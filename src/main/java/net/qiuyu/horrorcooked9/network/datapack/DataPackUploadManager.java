package net.qiuyu.horrorcooked9.network.datapack;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import net.qiuyu.horrorcooked9.config.ModServerConfig;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class DataPackUploadManager {

    // Must match client chunk strategy and stay below 32767-byte payload cap.
    private static final int MAX_CHUNK_SIZE = 24 * 1024;
    private static final int MAX_EXTRACT_FILE_COUNT = 10_000;
    private static final long MAX_EXTRACT_TOTAL_SIZE = 256L * 1024L * 1024L;

    private static final Map<UUID, UploadSession> SESSIONS = new ConcurrentHashMap<>();

    private DataPackUploadManager() {
    }

    public static void startUpload(ServerPlayer player, UUID sessionId, String packName, int totalSize, int totalChunks, String sha256) {
        if (!validateServerState(player)) {
            return;
        }
        if (sessionId == null || packName == null || sha256 == null) {
            player.sendSystemMessage(Component.literal("上传初始化失败：参数无效。"));
            return;
        }
        long maxUploadSize = ModServerConfig.getMaxUploadSizeBytes();
        if (totalSize <= 0 || totalSize > maxUploadSize) {
            player.sendSystemMessage(Component.literal("上传初始化失败：文件大小超出限制。"));
            return;
        }
        if (totalChunks <= 0) {
            player.sendSystemMessage(Component.literal("上传初始化失败：分片数量无效。"));
            return;
        }
        if (totalChunks > (int) Math.ceil((double) totalSize / (double) MAX_CHUNK_SIZE) + 4) {
            player.sendSystemMessage(Component.literal("上传初始化失败：分片信息异常。"));
            return;
        }

        String normalizedPackName = sanitizePackName(packName);
        if (normalizedPackName.isBlank()) {
            player.sendSystemMessage(Component.literal("上传初始化失败：数据包名称无效。"));
            return;
        }

        SESSIONS.put(sessionId, new UploadSession(player.getUUID(), normalizedPackName, totalSize, totalChunks, sha256.toLowerCase()));
        player.sendSystemMessage(Component.literal("开始接收数据包：" + normalizedPackName));
    }

    public static void appendChunk(ServerPlayer player, UUID sessionId, int chunkIndex, byte[] chunkData) {
        UploadSession session = SESSIONS.get(sessionId);
        if (session == null || !session.playerId().equals(player.getUUID())) {
            return;
        }
        if (chunkData == null || chunkData.length == 0 || chunkData.length > MAX_CHUNK_SIZE) {
            cancelWithMessage(player, sessionId, "上传失败：分片大小无效。");
            return;
        }
        if (chunkIndex < 0 || chunkIndex >= session.totalChunks()) {
            cancelWithMessage(player, sessionId, "上传失败：分片序号无效。");
            return;
        }
        if (!session.chunks().containsKey(chunkIndex)) {
            session.chunks().put(chunkIndex, Arrays.copyOf(chunkData, chunkData.length));
            session.receivedBytes().addAndGet(chunkData.length);
        }
    }

    public static void finishUpload(ServerPlayer player, UUID sessionId) {
        UploadSession session = SESSIONS.remove(sessionId);
        if (session == null || !session.playerId().equals(player.getUUID())) {
            return;
        }

        if (session.chunks().size() != session.totalChunks()) {
            player.sendSystemMessage(Component.literal("上传失败：分片不完整。"));
            return;
        }
        if (session.receivedBytes().get() != session.totalSize()) {
            player.sendSystemMessage(Component.literal("上传失败：文件总大小不匹配。"));
            return;
        }

        byte[] allBytes = new byte[session.totalSize()];
        int offset = 0;
        for (int i = 0; i < session.totalChunks(); i++) {
            byte[] part = session.chunks().get(i);
            if (part == null) {
                player.sendSystemMessage(Component.literal("上传失败：存在缺失分片。"));
                return;
            }
            if (offset + part.length > allBytes.length) {
                player.sendSystemMessage(Component.literal("上传失败：分片拼接越界。"));
                return;
            }
            System.arraycopy(part, 0, allBytes, offset, part.length);
            offset += part.length;
        }

        String currentHash = sha256Hex(allBytes);
        if (!session.sha256().equals(currentHash)) {
            player.sendSystemMessage(Component.literal("上传失败：校验和不匹配。"));
            return;
        }

        try {
            Path installed = installToDatapacks(player.getServer(), session.packName(), allBytes);
            player.sendSystemMessage(Component.literal("数据包上传完成：" + installed.getFileName()));
        } catch (IOException ex) {
            player.sendSystemMessage(Component.literal("上传失败：" + ex.getMessage()));
        }
    }

    public static void cancelUpload(ServerPlayer player, UUID sessionId, String reason) {
        UploadSession session = SESSIONS.remove(sessionId);
        if (session != null && session.playerId().equals(player.getUUID())) {
            String message = (reason == null || reason.isBlank()) ? "已取消上传。" : "已取消上传：" + reason;
            player.sendSystemMessage(Component.literal(message));
        }
    }

    public static List<String> listDatapackNames(MinecraftServer server) {
        Path datapacksDir = server.getWorldPath(LevelResource.DATAPACK_DIR);
        List<String> candidates = new ArrayList<>();
        try {
            Files.createDirectories(datapacksDir);
            try (var stream = Files.list(datapacksDir)) {
                stream.filter(path -> Files.isDirectory(path) || Files.isRegularFile(path))
                        .map(path -> path.getFileName().toString())
                        .filter(DataPackUploadManager::isValidDatapackEntryName)
                        .sorted(String::compareToIgnoreCase)
                        .forEach(candidates::add);
            }
        } catch (IOException ignored) {
            return List.of();
        }
        return candidates;
    }

    public static boolean deleteDatapack(MinecraftServer server, String packName) throws IOException {
        String candidateName = packName == null ? "" : packName.trim();
        if (!isValidDatapackEntryName(candidateName)) {
            throw new IOException("数据包名称无效。");
        }

        Path datapacksDir = server.getWorldPath(LevelResource.DATAPACK_DIR).normalize();
        Files.createDirectories(datapacksDir);

        Path targetPath = datapacksDir.resolve(candidateName).normalize();
        if (!targetPath.startsWith(datapacksDir)) {
            throw new IOException("数据包名称无效。");
        }
        if (!Files.exists(targetPath)) {
            return false;
        }

        if (Files.isDirectory(targetPath)) {
            deleteDirectory(targetPath);
        } else {
            Files.delete(targetPath);
        }
        return true;
    }

    private static boolean validateServerState(ServerPlayer player) {
        if (!ModServerConfig.isDatapackUploadEnabled()) {
            player.sendSystemMessage(Component.literal("数据包上传功能已禁用。"));
            return false;
        }
        if (!player.getServer().isDedicatedServer()) {
            player.sendSystemMessage(Component.literal("该功能仅支持多人专用服务器。"));
            return false;
        }
        return true;
    }

    private static void cancelWithMessage(ServerPlayer player, UUID sessionId, String message) {
        SESSIONS.remove(sessionId);
        player.sendSystemMessage(Component.literal(message));
    }

    private static Path installToDatapacks(MinecraftServer server, String packName, byte[] zipBytes) throws IOException {
        Path datapacksDir = server.getWorldPath(LevelResource.DATAPACK_DIR);
        Files.createDirectories(datapacksDir);

        Path targetDir = datapacksDir.resolve(packName).normalize();
        if (Files.exists(targetDir)) {
            throw new IOException("同名数据包已存在：" + packName);
        }

        Path tempDir = server.getServerDirectory().toPath().resolve("horrorcooked9_upload_tmp");
        Files.createDirectories(tempDir);

        Path tempZip = Files.createTempFile(tempDir, "upload-", ".zip");
        try {
            Files.write(tempZip, zipBytes, StandardOpenOption.TRUNCATE_EXISTING);
            unzipSecure(tempZip, targetDir);
            if (!Files.exists(targetDir.resolve("pack.mcmeta"))) {
                deleteDirectory(targetDir);
                throw new IOException("上传内容缺少 pack.mcmeta。");
            }
            return targetDir;
        } finally {
            Files.deleteIfExists(tempZip);
        }
    }

    private static void unzipSecure(Path zipPath, Path targetDir) throws IOException {
        Files.createDirectories(targetDir);
        int fileCount = 0;
        long totalExtracted = 0L;
        byte[] buffer = new byte[8192];

        try (InputStream inputStream = Files.newInputStream(zipPath);
             ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String entryName = entry.getName();
                if (entryName == null || entryName.isBlank()) {
                    zipInputStream.closeEntry();
                    continue;
                }
                Path outPath = targetDir.resolve(entryName).normalize();
                if (!outPath.startsWith(targetDir)) {
                    throw new IOException("非法压缩条目路径：" + entryName);
                }

                if (entry.isDirectory()) {
                    Files.createDirectories(outPath);
                    zipInputStream.closeEntry();
                    continue;
                }

                fileCount++;
                if (fileCount > MAX_EXTRACT_FILE_COUNT) {
                    throw new IOException("解压文件数量超出限制。");
                }

                Files.createDirectories(outPath.getParent());
                long written = 0L;
                try (var output = Files.newOutputStream(outPath, StandardOpenOption.CREATE_NEW)) {
                    int read;
                    while ((read = zipInputStream.read(buffer)) >= 0) {
                        if (read == 0) {
                            continue;
                        }
                        output.write(buffer, 0, read);
                        written += read;
                        totalExtracted += read;
                        if (totalExtracted > MAX_EXTRACT_TOTAL_SIZE) {
                            throw new IOException("解压总大小超出限制。");
                        }
                    }
                }
                if (written == 0L && entry.getSize() > 0) {
                    throw new IOException("解压过程中出现空文件异常。");
                }
                zipInputStream.closeEntry();
            }
        } catch (IOException ex) {
            deleteDirectory(targetDir);
            throw ex;
        }
    }

    private static void deleteDirectory(Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }
        try (var walk = Files.walk(path)) {
            walk.sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException ignored) {
                }
            });
        }
    }

    private static boolean isValidDatapackEntryName(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        if (".".equals(value) || "..".equals(value)) {
            return false;
        }
        if (value.contains("/") || value.contains("\\")) {
            return false;
        }
        if (value.startsWith(".")) {
            return false;
        }
        if (value.matches(".*[\\x00-\\x1F\\x7F].*")) {
            return false;
        }
        return !value.matches(".*[<>:\"|?*].*");
    }

    private static String sanitizePackName(String packName) {
        String base = packName.trim();
        if (base.toLowerCase().endsWith(".zip")) {
            base = base.substring(0, base.length() - 4);
        }
        // Keep Unicode names (e.g. Chinese), only replace illegal path/file characters.
        base = base.replace('/', '_').replace('\\', '_');
        base = base.replaceAll("[<>:\"|?*]", "_");
        base = base.replaceAll("[\\x00-\\x1F\\x7F]", "_");
        // Windows does not allow trailing spaces or dots in directory names.
        base = base.replaceAll("[\\s.]+$", "");
        if (".".equals(base) || "..".equals(base)) {
            base = "_";
        }
        return base.length() > 64 ? base.substring(0, 64) : base;
    }

    private static String sha256Hex(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(data);
            StringBuilder builder = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Missing SHA-256", e);
        }
    }
}
