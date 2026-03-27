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
import net.qiuyu.horrorcooked9.items.custom.BrackenFernItem;
import net.qiuyu.horrorcooked9.items.custom.Cleaver;
import net.qiuyu.horrorcooked9.items.custom.CrystalTomato;
import net.qiuyu.horrorcooked9.items.custom.CrystalTomatoSlicedItem;
import net.qiuyu.horrorcooked9.items.custom.KaleItem;
import net.qiuyu.horrorcooked9.items.custom.KaleLeavesItem;
import net.qiuyu.horrorcooked9.items.custom.ParasiticBeanSproutsItem;
import net.qiuyu.horrorcooked9.items.custom.PickledBrackenFernItem;
import net.qiuyu.horrorcooked9.items.custom.HappyRiceItem;
import net.qiuyu.horrorcooked9.items.custom.HappyCookedRiceItem;
import net.qiuyu.horrorcooked9.items.custom.PineappleChunksItem;
import net.qiuyu.horrorcooked9.items.custom.PineappleItem;
import net.qiuyu.horrorcooked9.items.custom.SaladBowlItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, HorrorCooked9.MODID);

    public static final RegistryObject<Item> CHOPPING_BOARD = ITEMS.register("chopping_board",
            () -> new BlockItem(ModBlocks.CHOPPING_BOARD.get(), new Item.Properties()));

    public static final RegistryObject<Item> SALAD_BOWL = ITEMS.register("salad_bowl",
            () -> new SaladBowlItem(ModBlocks.SALAD_BOWL.get(), new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> CLEAVER = ITEMS.register("cleaver",
            () -> new Cleaver(new Item.Properties().stacksTo(1).durability(512)));

    public static final RegistryObject<Item> WOODEN_SALAD_SPOON = ITEMS.register("wooden_salad_spoon",
            () -> new Item(new Item.Properties().stacksTo(1).durability(192)));

    public static final RegistryObject<Item> SALAD_TONGS = ITEMS.register("salad_tongs",
            () -> new Item(new Item.Properties().stacksTo(1).durability(384)));

    public static final RegistryObject<Item> CRYSTAL_TOMATO = ITEMS.register("crystal_tomato",
            () -> new CrystalTomato(new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.5f).build())));

    public static final RegistryObject<Item> CRYSTAL_TOMATO_SLICED = ITEMS.register("crystal_tomato_sliced",
            () -> new CrystalTomatoSlicedItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.5f).build())));

    public static final RegistryObject<Item> CRYSTAL_SALAD = ITEMS.register("crystal_salad",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.8f).build())));

    public static final RegistryObject<Item> KALE = ITEMS.register("kale",
            () -> new KaleItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2f).build())));

    public static final RegistryObject<Item> KALE_LEAVES = ITEMS.register("kale_leaves",
            () -> new KaleLeavesItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.35f).build())));

    public static final RegistryObject<Item> BRACKEN_FERN = ITEMS.register("bracken_fern",
            () -> new BrackenFernItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2f).build())));

    public static final RegistryObject<Item> PICKLED_BRACKEN_FERN = ITEMS.register("pickled_bracken_fern",
            () -> new PickledBrackenFernItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.6f).build())));

    public static final RegistryObject<Item> PINEAPPLE = ITEMS.register("pineapple",
            () -> new PineappleItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3f).build())));

    public static final RegistryObject<Item> PINEAPPLE_CHUNKS = ITEMS.register("pineapple_chunks",
            () -> new PineappleChunksItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.5f).build())));

    public static final RegistryObject<Item> PARASITIC_BEAN_SPROUTS = ITEMS.register("parasitic_bean_sprouts",
            () -> new ParasiticBeanSproutsItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(7).saturationMod(1.0f).build())));

    public static final RegistryObject<Item> HAPPY_RICE = ITEMS.register("happy_rice",
            () -> new HappyRiceItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(5).saturationMod(0.8f).build())));

    public static final RegistryObject<Item> HAPPY_COOKED_RICE = ITEMS.register("happy_cooked_rice",
            () -> new HappyCookedRiceItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(8).saturationMod(1.2f).build())));

    public static final RegistryObject<Item> CAPTAIN_HAT = ITEMS.register("captain_hat",
            () -> new CaptainHat(CaptainHat.MATERIAL, ArmorItem.Type.HELMET, new Item.Properties()));

 public static final RegistryObject<Item> FOODWORKS_TABLE = ITEMS.register("foodworks_table",
            () -> new BlockItem(ModBlocks.FOODWORKS_TABLE.get(), new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
