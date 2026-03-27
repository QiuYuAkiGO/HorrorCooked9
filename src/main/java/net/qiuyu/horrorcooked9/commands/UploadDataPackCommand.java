package net.qiuyu.horrorcooked9.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import net.qiuyu.horrorcooked9.config.ModServerConfig;
import net.qiuyu.horrorcooked9.network.ModNetworking;
import net.qiuyu.horrorcooked9.network.datapack.DataPackUploadManager;
import net.qiuyu.horrorcooked9.network.datapack.OpenDataPackPickerPacket;

import java.io.IOException;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public final class UploadDataPackCommand {

    private UploadDataPackCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                literal("datapack")
                        .then(literal("upload")
                                .requires(source -> source.hasPermission(2))
                                .executes(context -> executeUpload(context.getSource()))
                        )
                        .then(literal("delete")
                                .requires(source -> source.hasPermission(2))
                                .then(argument("packName", StringArgumentType.string())
                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                                DataPackUploadManager.listDatapackNames(context.getSource().getServer()),
                                                builder
                                        ))
                                        .executes(context -> executeDelete(
                                                context.getSource(),
                                                StringArgumentType.getString(context, "packName")
                                        ))
                                )
                        )
        );
    }

    private static int executeUpload(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        MinecraftServer server = source.getServer();

        if (!ModServerConfig.isDatapackUploadEnabled()) {
            source.sendFailure(Component.literal("数据包上传功能已在配置文件中禁用。"));
            return 0;
        }

        if (!server.isDedicatedServer()) {
            source.sendFailure(Component.literal("该功能仅在多人专用服务器可用，单人模式下不可使用。"));
            return 0;
        }

        long maxUploadSize = ModServerConfig.getMaxUploadSizeBytes();
        ModNetworking.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new OpenDataPackPickerPacket(maxUploadSize));
        source.sendSuccess(() -> Component.literal("请在客户端选择要上传的数据包.zip 或文件夹。"), false);
        return 1;
    }

    private static int executeDelete(CommandSourceStack source, String packName) {
        MinecraftServer server = source.getServer();

        if (!ModServerConfig.isDatapackUploadEnabled()) {
            source.sendFailure(Component.literal("数据包管理功能已在配置文件中禁用。"));
            return 0;
        }

        if (!server.isDedicatedServer()) {
            source.sendFailure(Component.literal("该功能仅在多人专用服务器可用，单人模式下不可使用。"));
            return 0;
        }

        try {
            boolean deleted = DataPackUploadManager.deleteDatapack(server, packName);
            if (!deleted) {
                source.sendFailure(Component.literal("未找到数据包：" + packName));
                return 0;
            }
            source.sendSuccess(() -> Component.literal("已删除数据包：" + packName + "。如需立即生效，请执行 /reload。"), true);
            return 1;
        } catch (IOException ex) {
            source.sendFailure(Component.literal("删除失败：" + ex.getMessage()));
            return 0;
        }
    }
}
