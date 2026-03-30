package net.qiuyu.horrorcooked9.register;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import net.qiuyu.horrorcooked9.gameplay.juicing.JuicerMenu;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, HorrorCooked9.MODID);

    public static final RegistryObject<MenuType<JuicerMenu>> JUICER_MENU =
            MENUS.register("juicer_menu", () -> IForgeMenuType.create(JuicerMenu::new));

    private ModMenus() {
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
