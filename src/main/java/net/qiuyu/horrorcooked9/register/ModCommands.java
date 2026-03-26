package net.qiuyu.horrorcooked9.register;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.qiuyu.horrorcooked9.commands.RandomValueCommand;
import net.qiuyu.horrorcooked9.commands.UploadDataPackCommand;

@Mod.EventBusSubscriber
public class ModCommands {
    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        RandomValueCommand.register(dispatcher);
        UploadDataPackCommand.register(dispatcher);
    }
}
