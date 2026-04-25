package net.qiuyu.horrorcooked9.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.qiuyu.horrorcooked9.HorrorCooked9;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, HorrorCooked9.MODID);

    public static final RegistryObject<CreativeModeTab> HORROR_COOKED_TAB = CREATIVE_MODE_TABS.register("horrorcooked9_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.CLEAVER.get()))
                    .title(Component.translatable("creativetab.horrorcooked9_tab"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.CHOPPING_BOARD.get());
                        output.accept(ModItems.SALAD_BOWL.get());
                        output.accept(ModItems.CLEAVER.get());
                        output.accept(ModItems.SHARPENING_STONE.get());
                        output.accept(ModItems.WOODEN_SALAD_SPOON.get());
                        output.accept(ModItems.SALAD_TONGS.get());
                        output.accept(ModItems.CRYSTAL_TOMATO.get());
                        output.accept(ModItems.CRYSTAL_TOMATO_SLICED.get());
                        output.accept(ModItems.CANDIED_CRYSTAL_TOMATO_SLICES.get());
                        output.accept(ModItems.ROASTED_CRYSTAL_TOMATO.get());
                        output.accept(ModItems.CRYSTAL_SALAD.get());
                        output.accept(ModItems.CRYSTAL_TOMATO_KETCHUP.get());
                        output.accept(ModItems.SQUID_SALAD.get());
                        output.accept(ModItems.TWIN_CORPSE_PARASITIC_SALAD.get());
                        output.accept(ModItems.CRISPY_NORI.get());
                        output.accept(ModItems.CATFISH_SKEWER.get());
                        output.accept(ModItems.FAR_REALM_GEM_BOWL.get());
                        output.accept(ModItems.BRACKEN_SPROUTS_BOWL.get());
                        output.accept(ModItems.TRANQUIL_MIXED_RICE.get());
                        output.accept(ModItems.COLORFUL_PALETTE.get());
                        output.accept(ModItems.TRANQUIL_BASE.get());
                        output.accept(ModItems.FRIED_TRANQUIL_BASE.get());
                        output.accept(ModItems.TRANQUILITY.get());
                        output.accept(ModItems.KALE.get());
                        output.accept(ModItems.KALE_LEAVES.get());
                        output.accept(ModItems.KALE_PUREE.get());
                        output.accept(ModItems.BRACKEN_FERN.get());
                        output.accept(ModItems.PICKLED_BRACKEN_FERN.get());
                        output.accept(ModItems.PINEAPPLE.get());
                        output.accept(ModItems.PINEAPPLE_CHUNKS.get());
                        output.accept(ModItems.ROASTED_PINEAPPLE_CHUNKS.get());
                        output.accept(ModItems.PINEAPPLE_FRIED_RICE_BASE.get());
                        output.accept(ModItems.FRIED_PINEAPPLE_FRIED_RICE_BASE.get());
                        output.accept(ModItems.PINEAPPLE_FRIED_RICE.get());
                        output.accept(ModItems.PARASITIC_BEAN_SPROUTS.get());
                        output.accept(ModItems.DRIED_BEAN_SPROUTS.get());
                        output.accept(ModItems.HAPPY_RICE.get());
                        output.accept(ModItems.HAPPY_COOKED_RICE.get());
                        output.accept(ModItems.SHIT.get());
                        output.accept(ModItems.FERTILIZER.get());
                        output.accept(ModItems.GOLDEN_SHIT.get());
                        output.accept(ModItems.CAPTAIN_HAT.get());
                        output.accept(ModItems.FOODWORKS_TABLE.get());
                        output.accept(ModItems.HOOK_MONSTER_SPAWN_EGG.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
