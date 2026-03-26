package net.qiuyu.horrorcooked9.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import net.qiuyu.horrorcooked9.config.ModServerConfig;
import net.qiuyu.horrorcooked9.register.ModNetworking;
import net.qiuyu.horrorcooked9.network.develop.OpenDataPackPickerPacket;

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
}
