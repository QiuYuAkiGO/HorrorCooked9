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
                        output.accept(ModItems.SIMPLE_FILTER.get());
                        output.accept(ModItems.JUICER.get());
                        output.accept(ModItems.CLEAVER.get());
                        output.accept(ModItems.FILTERED_WATER_BUCKET.get());
                        output.accept(ModItems.CLEAR_WATER_BUCKET.get());
                        output.accept(ModItems.PINEAPPLE_JUICE_BUCKET.get());
                        output.accept(ModItems.PINEAPPLE_JUICE_BOTTLE.get());
                        output.accept(ModItems.SHARPENING_STONE.get());
                        output.accept(ModItems.WOODEN_SALAD_SPOON.get());
                        output.accept(ModItems.SALAD_TONGS.get());
                        output.accept(ModItems.CRYSTAL_TOMATO.get());
                        output.accept(ModItems.CRYSTAL_TOMATO_SLICED.get());
                        output.accept(ModItems.CRYSTAL_SALAD.get());
                        output.accept(ModItems.SQUID_SALAD.get());
                        output.accept(ModItems.OCEAN_SALAD.get());
                        output.accept(ModItems.CRISPY_NORI.get());
                        output.accept(ModItems.CATFISH_SKEWER.get());
                        output.accept(ModItems.KALE.get());
                        output.accept(ModItems.KALE_LEAVES.get());
                        output.accept(ModItems.BRACKEN_FERN.get());
                        output.accept(ModItems.PICKLED_BRACKEN_FERN.get());
                        output.accept(ModItems.PINEAPPLE.get());
                        output.accept(ModItems.PINEAPPLE_CHUNKS.get());
                        output.accept(ModItems.PARASITIC_BEAN_SPROUTS.get());
                        output.accept(ModItems.HAPPY_RICE.get());
                        output.accept(ModItems.HAPPY_COOKED_RICE.get());
                        output.accept(ModItems.CAPTAIN_HAT.get());
                        output.accept(ModItems.FOODWORKS_TABLE.get());

                        // Carcasses
                        output.accept(ModItems.PIG_CARCASS.get());
                        output.accept(ModItems.COW_CARCASS.get());
                        output.accept(ModItems.SHEEP_CARCASS.get());
                        output.accept(ModItems.CHICKEN_CARCASS.get());

                        // Pig offal
                        output.accept(ModItems.PIG_LIVER.get());
                        output.accept(ModItems.PIG_STOMACH.get());
                        output.accept(ModItems.PIG_INTESTINE.get());
                        output.accept(ModItems.PIG_HEART.get());
                        output.accept(ModItems.PIG_KIDNEY.get());
                        output.accept(ModItems.PIG_LUNG.get());
                        output.accept(ModItems.PIG_TONGUE.get());
                        output.accept(ModItems.PIG_BRAIN.get());

                        // Beef offal
                        output.accept(ModItems.BEEF_TRIPE.get());
                        output.accept(ModItems.BEEF_OMASUM.get());
                        output.accept(ModItems.BEEF_HEART.get());
                        output.accept(ModItems.BEEF_LIVER.get());
                        output.accept(ModItems.BEEF_TONGUE.get());
                        output.accept(ModItems.BEEF_MARROW.get());
                        output.accept(ModItems.BEEF_TENDON.get());

                        // Lamb offal
                        output.accept(ModItems.LAMB_OFFAL_MIX.get());
                        output.accept(ModItems.LAMB_KIDNEY.get());
                        output.accept(ModItems.LAMB_TRIPE.get());
                        output.accept(ModItems.LAMB_LIVER.get());

                        // Chicken offal
                        output.accept(ModItems.CHICKEN_GIZZARD.get());
                        output.accept(ModItems.CHICKEN_HEART.get());
                        output.accept(ModItems.CHICKEN_LIVER.get());
                        output.accept(ModItems.CHICKEN_INTESTINE.get());
                        output.accept(ModItems.CHICKEN_BLOOD.get());

                        // Duck offal (placeholder)
                        output.accept(ModItems.DUCK_GIZZARD.get());
                        output.accept(ModItems.DUCK_LIVER.get());
                        output.accept(ModItems.DUCK_INTESTINE.get());
                        output.accept(ModItems.DUCK_BLOOD.get());
                        output.accept(ModItems.DUCK_HEART.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
