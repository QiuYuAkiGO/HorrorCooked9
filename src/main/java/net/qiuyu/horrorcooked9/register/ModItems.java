package net.qiuyu.horrorcooked9.register;


import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.world.item.ArmorItem;
import net.minecraftforge.registries.RegistryObject;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import net.qiuyu.horrorcooked9.armor.custom.CaptainHat;
import net.qiuyu.horrorcooked9.items.custom.Cleaver;
import net.qiuyu.horrorcooked9.items.custom.CrystalTomato;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, HorrorCooked9.MODID);

    public static final RegistryObject<Item> CHOPPING_BOARD = ITEMS.register("chopping_board",
            () -> new BlockItem(ModBlocks.CHOPPING_BOARD.get(), new Item.Properties()));

    public static final RegistryObject<Item> CLEAVER = ITEMS.register("cleaver",
            () -> new Cleaver(new Item.Properties().stacksTo(1).durability(512)));

    public static final RegistryObject<Item> CRYSTAL_TOMATO = ITEMS.register("crystal_tomato",
            () -> new CrystalTomato(new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.5f).build())));

    public static final RegistryObject<Item> CRYSTAL_TOMATO_SLICED = ITEMS.register("crystal_tomato_sliced",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.5f).build())));

    public static final RegistryObject<Item> CAPTAIN_HAT = ITEMS.register("captain_hat",
            () -> new CaptainHat(CaptainHat.MATERIAL, ArmorItem.Type.HELMET, new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
