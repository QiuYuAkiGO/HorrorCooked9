package net.qiuyu.horrorcooked9.register;


import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.world.item.ArmorItem;
import net.minecraftforge.registries.RegistryObject;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import net.qiuyu.horrorcooked9.armor.custom.CaptainHat;
import net.qiuyu.horrorcooked9.items.custom.BrackenFernItem;
import net.qiuyu.horrorcooked9.items.custom.ChoppingBoardItem;
import net.qiuyu.horrorcooked9.items.custom.Cleaver;
import net.qiuyu.horrorcooked9.items.custom.CrystalTomato;
import net.qiuyu.horrorcooked9.items.custom.CrystalTomatoSlicedItem;
import net.qiuyu.horrorcooked9.items.custom.KaleItem;
import net.qiuyu.horrorcooked9.items.custom.KaleLeavesItem;
import net.qiuyu.horrorcooked9.items.custom.CarcassItem;
import net.qiuyu.horrorcooked9.items.custom.ConfigurableFoodItem;
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
            () -> new ChoppingBoardItem(ModBlocks.CHOPPING_BOARD.get(), new Item.Properties().stacksTo(1).durability(336)));

    public static final RegistryObject<Item> SALAD_BOWL = ITEMS.register("salad_bowl",
            () -> new SaladBowlItem(ModBlocks.SALAD_BOWL.get(), new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> SIMPLE_FILTER = ITEMS.register("simple_filter",
            () -> new BlockItem(ModBlocks.SIMPLE_FILTER.get(), new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> JUICER = ITEMS.register("juicer",
            () -> new BlockItem(ModBlocks.JUICER.get(), new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> CLEAR_WATER_BUCKET = ITEMS.register("clear_water_bucket",
            () -> new BucketItem(ModFluids.CLEAR_WATER, new Item.Properties().stacksTo(1).craftRemainder(net.minecraft.world.item.Items.BUCKET)));

    public static final RegistryObject<Item> FILTERED_WATER_BUCKET = ITEMS.register("filtered_water_bucket",
            () -> new BucketItem(ModFluids.FILTERED_WATER, new Item.Properties().stacksTo(1).craftRemainder(net.minecraft.world.item.Items.BUCKET)));

    public static final RegistryObject<Item> PINEAPPLE_JUICE_BUCKET = ITEMS.register("pineapple_juice_bucket",
            () -> new BucketItem(ModFluids.PINEAPPLE_JUICE, new Item.Properties().stacksTo(1).craftRemainder(net.minecraft.world.item.Items.BUCKET)));

    public static final RegistryObject<Item> PINEAPPLE_JUICE_BOTTLE = ITEMS.register("pineapple_juice_bottle",
            () -> new ConfigurableFoodItem(new Item.Properties().stacksTo(16).craftRemainder(net.minecraft.world.item.Items.GLASS_BOTTLE)));

    public static final RegistryObject<Item> CLEAVER = ITEMS.register("cleaver",
            () -> new Cleaver(new Item.Properties().stacksTo(1).durability(512)));

    public static final RegistryObject<Item> SHARPENING_STONE = ITEMS.register("sharpening_stone",
            () -> new Item(new Item.Properties().stacksTo(64)));

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

    public static final RegistryObject<Item> SQUID_SALAD = ITEMS.register("squid_salad",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(7).saturationMod(0.9f).build())));

    public static final RegistryObject<Item> OCEAN_SALAD = ITEMS.register("ocean_salad",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(7).saturationMod(0.9f).build())));

    public static final RegistryObject<Item> CRISPY_NORI = ITEMS.register("crispy_nori",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(5).saturationMod(0.6f).build())));

    public static final RegistryObject<Item> CATFISH_SKEWER = ITEMS.register("catfish_skewer",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(8).saturationMod(1.0f).build())));

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

    // Carcasses
    public static final RegistryObject<Item> PIG_CARCASS = registerCarcass("pig_carcass");
    public static final RegistryObject<Item> COW_CARCASS = registerCarcass("cow_carcass");
    public static final RegistryObject<Item> SHEEP_CARCASS = registerCarcass("sheep_carcass");
    public static final RegistryObject<Item> CHICKEN_CARCASS = registerCarcass("chicken_carcass");

    // Pig offal
    public static final RegistryObject<Item> PIG_LIVER = registerOrganFood("pig_liver");
    public static final RegistryObject<Item> PIG_STOMACH = registerOrganFood("pig_stomach");
    public static final RegistryObject<Item> PIG_INTESTINE = registerOrganFood("pig_intestine");
    public static final RegistryObject<Item> PIG_HEART = registerOrganFood("pig_heart");
    public static final RegistryObject<Item> PIG_KIDNEY = registerOrganFood("pig_kidney");
    public static final RegistryObject<Item> PIG_LUNG = registerOrganFood("pig_lung");
    public static final RegistryObject<Item> PIG_TONGUE = registerOrganFood("pig_tongue");
    public static final RegistryObject<Item> PIG_BRAIN = registerOrganFood("pig_brain");

    // Beef offal
    public static final RegistryObject<Item> BEEF_TRIPE = registerOrganFood("beef_tripe");
    public static final RegistryObject<Item> BEEF_OMASUM = registerOrganFood("beef_omasum");
    public static final RegistryObject<Item> BEEF_HEART = registerOrganFood("beef_heart");
    public static final RegistryObject<Item> BEEF_LIVER = registerOrganFood("beef_liver");
    public static final RegistryObject<Item> BEEF_TONGUE = registerOrganFood("beef_tongue");
    public static final RegistryObject<Item> BEEF_MARROW = registerOrganFood("beef_marrow");
    public static final RegistryObject<Item> BEEF_TENDON = registerOrganFood("beef_tendon");

    // Lamb offal
    public static final RegistryObject<Item> LAMB_OFFAL_MIX = registerOrganFood("lamb_offal_mix");
    public static final RegistryObject<Item> LAMB_KIDNEY = registerOrganFood("lamb_kidney");
    public static final RegistryObject<Item> LAMB_TRIPE = registerOrganFood("lamb_tripe");
    public static final RegistryObject<Item> LAMB_LIVER = registerOrganFood("lamb_liver");

    // Chicken offal
    public static final RegistryObject<Item> CHICKEN_GIZZARD = registerOrganFood("chicken_gizzard");
    public static final RegistryObject<Item> CHICKEN_HEART = registerOrganFood("chicken_heart");
    public static final RegistryObject<Item> CHICKEN_LIVER = registerOrganFood("chicken_liver");
    public static final RegistryObject<Item> CHICKEN_INTESTINE = registerOrganFood("chicken_intestine");
    public static final RegistryObject<Item> CHICKEN_BLOOD = registerOrganFood("chicken_blood");

    // Duck offal (placeholder, currently no vanilla duck drops)
    public static final RegistryObject<Item> DUCK_GIZZARD = registerOrganFood("duck_gizzard");
    public static final RegistryObject<Item> DUCK_LIVER = registerOrganFood("duck_liver");
    public static final RegistryObject<Item> DUCK_INTESTINE = registerOrganFood("duck_intestine");
    public static final RegistryObject<Item> DUCK_BLOOD = registerOrganFood("duck_blood");
    public static final RegistryObject<Item> DUCK_HEART = registerOrganFood("duck_heart");

    private static RegistryObject<Item> registerCarcass(String name) {
        return ITEMS.register(name, () -> new CarcassItem(new Item.Properties().stacksTo(16)));
    }

    private static RegistryObject<Item> registerOrganFood(String name) {
        return ITEMS.register(name, () -> new ConfigurableFoodItem(new Item.Properties().stacksTo(64)));
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
