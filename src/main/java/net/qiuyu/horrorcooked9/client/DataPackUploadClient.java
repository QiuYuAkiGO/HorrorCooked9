package net.qiuyu.horrorcooked9.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.qiuyu.horrorcooked9.network.develop.DataPackUploadCancelPacket;
import net.qiuyu.horrorcooked9.network.develop.DataPackUploadChunkPacket;
import net.qiuyu.horrorcooked9.network.develop.DataPackUploadFinishPacket;
import net.qiuyu.horrorcooked9.network.develop.DataPackUploadStartPacket;
import net.qiuyu.horrorcooked9.register.ModNetworking;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class DataPackUploadClient {

    private static final int CHUNK_SIZE = 32 * 1024;
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "horrorcooked9-datapack-upload");
        t.setDaemon(true);
        return t;
    });

    private DataPackUploadClient() {
    }

    public static void openFilePicker(long maxUploadBytes) {
        EXECUTOR.execute(() -> doUpload(maxUploadBytes));
    }

    private static void doUpload(long maxUploadBytes) {
        UUID sessionId = UUID.randomUUID();
        try {
            Path selectedPath = choosePath();
            if (selectedPath == null) {
                ModNetworking.CHANNEL.sendToServer(new DataPackUploadCancelPacket(sessionId, "用户取消选择。"));
                return;
            }

            PreparedUpload preparedUpload = prepareUpload(selectedPath);
            if (preparedUpload.zipBytes().length > maxUploadBytes) {
                notifyLocalPlayer("上传失败：文件大小超过服务器限制。");
                ModNetworking.CHANNEL.sendToServer(new DataPackUploadCancelPacket(sessionId, "文件大小超过限制。"));
                return;
            }

            if (!containsPackMcmeta(preparedUpload.zipBytes())) {
                notifyLocalPlayer("上传失败：所选内容不包含 pack.mcmeta。");
                ModNetworking.CHANNEL.sendToServer(new DataPackUploadCancelPacket(sessionId, "缺少 pack.mcmeta。"));
                return;
            }

            int totalChunks = (int) Math.ceil((double) preparedUpload.zipBytes().length / (double) CHUNK_SIZE);
            String sha256 = sha256Hex(preparedUpload.zipBytes());

            ModNetworking.CHANNEL.sendToServer(new DataPackUploadStartPacket(
                    sessionId,
                    preparedUpload.packName(),
                    preparedUpload.zipBytes().length,
                    totalChunks,
                    sha256
            ));

            for (int i = 0; i < totalChunks; i++) {
                int from = i * CHUNK_SIZE;
                int to = Math.min(from + CHUNK_SIZE, preparedUpload.zipBytes().length);
                byte[] chunk = new byte[to - from];
                System.arraycopy(preparedUpload.zipBytes(), from, chunk, 0, chunk.length);
                ModNetworking.CHANNEL.sendToServer(new DataPackUploadChunkPacket(sessionId, i, chunk));
            }

            ModNetworking.CHANNEL.sendToServer(new DataPackUploadFinishPacket(sessionId));
            notifyLocalPlayer("数据包上传请求已发送。");
        } catch (Exception ex) {
            notifyLocalPlayer("上传失败：" + ex.getMessage());
            ModNetworking.CHANNEL.sendToServer(new DataPackUploadCancelPacket(sessionId, ex.getMessage()));
        }
    }

    private static Path choosePath() throws Exception {
        final Path[] selected = new Path[1];
        SwingUtilities.invokeAndWait(() -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("选择数据包.zip或文件夹");
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            chooser.setAcceptAllFileFilterUsed(true);
            chooser.setFileFilter(new FileNameExtensionFilter("Data Pack Zip (*.zip)", "zip"));

            int result = chooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION && chooser.getSelectedFile() != null) {
                selected[0] = chooser.getSelectedFile().toPath();
            }
        });
        return selected[0];
    }

    private static PreparedUpload prepareUpload(Path selectedPath) throws IOException {
        if (Files.isDirectory(selectedPath)) {
            byte[] zipBytes = zipDirectory(selectedPath);
            String packName = selectedPath.getFileName() == null ? "datapack" : selectedPath.getFileName().toString();
            return new PreparedUpload(packName, zipBytes);
        }

        String fileName = selectedPath.getFileName() == null ? "datapack.zip" : selectedPath.getFileName().toString();
        if (!fileName.toLowerCase().endsWith(".zip")) {
            throw new IOException("仅支持 .zip 文件或文件夹。");
        }
        byte[] zipBytes = Files.readAllBytes(selectedPath);
        return new PreparedUpload(fileName, zipBytes);
    }

    private static byte[] zipDirectory(Path directory) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
            Files.walk(directory)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        Path relative = directory.relativize(path);
                        String zipEntryName = relative.toString().replace("\\", "/");
                        try (InputStream inputStream = Files.newInputStream(path)) {
                            zipOutputStream.putNextEntry(new ZipEntry(zipEntryName));
                            inputStream.transferTo(zipOutputStream);
                            zipOutputStream.closeEntry();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
            zipOutputStream.finish();
            return byteArrayOutputStream.toByteArray();
        } catch (RuntimeException ex) {
            if (ex.getCause() instanceof IOException ioException) {
                throw ioException;
            }
            throw ex;
        }
    }

    private static boolean containsPackMcmeta(byte[] zipBytes) throws IOException {
        try (InputStream inputStream = new java.io.ByteArrayInputStream(zipBytes);
             java.util.zip.ZipInputStream zipInputStream = new java.util.zip.ZipInputStream(inputStream)) {
            java.util.zip.ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (!entry.isDirectory() && "pack.mcmeta".equals(entry.getName())) {
                    return true;
                }
            }
            return false;
        }
    }

    private static String sha256Hex(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(bytes);
            StringBuilder builder = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Missing SHA-256", e);
        }
    }

    private static void notifyLocalPlayer(String text) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            minecraft.execute(() -> minecraft.player.sendSystemMessage(Component.literal(text)));
        }
    }

    private record PreparedUpload(String packName, byte[] zipBytes) {
    }
}
