package net.qiuyu.horrorcooked9.register;


import net.minecraft.world.food.FoodProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
import net.qiuyu.horrorcooked9.items.custom.BrackenSproutsBowlItem;
import net.qiuyu.horrorcooked9.items.custom.CandiedCrystalTomatoSlicesItem;
import net.qiuyu.horrorcooked9.items.custom.Cleaver;
import net.qiuyu.horrorcooked9.items.custom.CrystalTomato;
import net.qiuyu.horrorcooked9.items.custom.CrystalTomatoKetchupItem;
import net.qiuyu.horrorcooked9.items.custom.CrystalTomatoSlicedItem;
import net.qiuyu.horrorcooked9.items.custom.DriedBeanSproutsItem;
import net.qiuyu.horrorcooked9.items.custom.FarRealmGemBowlItem;
import net.qiuyu.horrorcooked9.items.custom.FriedPineappleFriedRiceBaseItem;
import net.qiuyu.horrorcooked9.items.custom.FriedTranquilBaseItem;
import net.qiuyu.horrorcooked9.items.custom.KaleItem;
import net.qiuyu.horrorcooked9.items.custom.KaleLeavesItem;
import net.qiuyu.horrorcooked9.items.custom.KalePureeItem;
import net.qiuyu.horrorcooked9.items.custom.ParasiticBeanSproutsItem;
import net.qiuyu.horrorcooked9.items.custom.PineappleFriedRiceItem;
import net.qiuyu.horrorcooked9.items.custom.PlainMixedBowlItem;
import net.qiuyu.horrorcooked9.items.custom.PickledBrackenFernItem;
import net.qiuyu.horrorcooked9.items.custom.RoastedCrystalTomatoItem;
import net.qiuyu.horrorcooked9.items.custom.HappyRiceItem;
import net.qiuyu.horrorcooked9.items.custom.HappyCookedRiceItem;
import net.qiuyu.horrorcooked9.items.custom.PineappleChunksItem;
import net.qiuyu.horrorcooked9.items.custom.PineappleItem;
import net.qiuyu.horrorcooked9.items.custom.SaladBowlItem;
import net.qiuyu.horrorcooked9.items.custom.RoastedPineappleChunksItem;
import net.qiuyu.horrorcooked9.items.custom.ShitItem;
import net.qiuyu.horrorcooked9.gameplay.food.FoodRuntimeConfigs;
import net.qiuyu.horrorcooked9.items.custom.TranquilMixedRiceItem;
import net.qiuyu.horrorcooked9.items.custom.TranquilityItem;
import net.qiuyu.horrorcooked9.items.custom.TwinCorpseParasiticSaladItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, HorrorCooked9.MODID);

    public static final RegistryObject<Item> CHOPPING_BOARD = ITEMS.register("chopping_board",
            () -> new BlockItem(ModBlocks.CHOPPING_BOARD.get(), new Item.Properties()));

    public static final RegistryObject<Item> SALAD_BOWL = ITEMS.register("salad_bowl",
            () -> new SaladBowlItem(ModBlocks.SALAD_BOWL.get(), new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> CLEAVER = ITEMS.register("cleaver",
            () -> new Cleaver(new Item.Properties().stacksTo(1).durability(512)));

    public static final RegistryObject<Item> SHARPENING_STONE = ITEMS.register("sharpening_stone",
            () -> new Item(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> WOODEN_SALAD_SPOON = ITEMS.register("wooden_salad_spoon",
            () -> new Item(new Item.Properties().stacksTo(1).durability(192)));

    public static final RegistryObject<Item> SALAD_TONGS = ITEMS.register("salad_tongs",
            () -> new Item(new Item.Properties().stacksTo(1).durability(384)));

    public static final RegistryObject<Item> CRYSTAL_TOMATO = ITEMS.register("crystal_tomato",
            () -> new CrystalTomato(new Item.Properties().food(jsonFood("horrorcooked9:crystal_tomato", 3, 0.5f))));

    public static final RegistryObject<Item> CRYSTAL_TOMATO_SLICED = ITEMS.register("crystal_tomato_sliced",
            () -> new CrystalTomatoSlicedItem(new Item.Properties().food(jsonFood("horrorcooked9:crystal_tomato_sliced", 3, 0.5f))));

    public static final RegistryObject<Item> CANDIED_CRYSTAL_TOMATO_SLICES = ITEMS.register("candied_crystal_tomato_slices",
            () -> new CandiedCrystalTomatoSlicesItem(new Item.Properties().food(jsonFood("horrorcooked9:candied_crystal_tomato_slices", 4, 0.55f))));

    public static final RegistryObject<Item> ROASTED_CRYSTAL_TOMATO = ITEMS.register("roasted_crystal_tomato",
            () -> new RoastedCrystalTomatoItem(new Item.Properties().food(jsonFood("horrorcooked9:roasted_crystal_tomato", 5, 0.65f))));

    public static final RegistryObject<Item> CRYSTAL_SALAD = ITEMS.register("crystal_salad",
            () -> new Item(new Item.Properties().food(jsonFood("horrorcooked9:crystal_salad", 6, 0.8f))));

    public static final RegistryObject<Item> CRYSTAL_TOMATO_KETCHUP = ITEMS.register("crystal_tomato_ketchup",
            () -> new CrystalTomatoKetchupItem(new Item.Properties().food(jsonFood("horrorcooked9:crystal_tomato_ketchup", 2, 0.3f))));

    public static final RegistryObject<Item> SQUID_SALAD = ITEMS.register("squid_salad",
            () -> new Item(new Item.Properties().food(jsonFood("horrorcooked9:squid_salad", 7, 0.9f))));

    public static final RegistryObject<Item> TWIN_CORPSE_PARASITIC_SALAD = ITEMS.register("twin_corpse_parasitic_salad",
            () -> new TwinCorpseParasiticSaladItem(new Item.Properties().food(new FoodProperties.Builder()
                    .nutrition(7)
                    .saturationMod(0.9f)
                    .effect(() -> new MobEffectInstance(ModEffects.DIARRHEA.get(), 240 * 20, 0), 1.0f)
                    .effect(() -> new MobEffectInstance(MobEffects.BLINDNESS, 30 * 20, 0), 1.0f)
                    .build())));

    public static final RegistryObject<Item> CRISPY_NORI = ITEMS.register("crispy_nori",
            () -> new Item(new Item.Properties().food(jsonFood("horrorcooked9:crispy_nori", 5, 0.6f))));

    public static final RegistryObject<Item> CATFISH_SKEWER = ITEMS.register("catfish_skewer",
            () -> new Item(new Item.Properties().food(jsonFood("horrorcooked9:catfish_skewer", 8, 1.0f))));

    public static final RegistryObject<Item> FAR_REALM_GEM_BOWL = ITEMS.register("far_realm_gem_bowl",
            () -> new FarRealmGemBowlItem(new Item.Properties().stacksTo(1)
                    .food(jsonFood("horrorcooked9:far_realm_gem_bowl", 4, 0.6f))));

    public static final RegistryObject<Item> BRACKEN_SPROUTS_BOWL = ITEMS.register("bracken_sprouts_bowl",
            () -> new BrackenSproutsBowlItem(new Item.Properties().stacksTo(1)
                    .food(new FoodProperties.Builder().nutrition(8).saturationMod(0.8f).build())));

    public static final RegistryObject<Item> TRANQUIL_MIXED_RICE = ITEMS.register("tranquil_mixed_rice",
            () -> new TranquilMixedRiceItem(new Item.Properties().stacksTo(1)
                    .food(new FoodProperties.Builder().nutrition(6).saturationMod(0.8f).build())));

    public static final RegistryObject<Item> COLORFUL_PALETTE = ITEMS.register("colorful_palette",
            () -> new PlainMixedBowlItem(new Item.Properties().stacksTo(1)
                    .food(new FoodProperties.Builder().nutrition(6).saturationMod(0.8f).build())));

    public static final RegistryObject<Item> TRANQUIL_BASE = ITEMS.register("tranquil_base",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> FRIED_TRANQUIL_BASE = ITEMS.register("fried_tranquil_base",
            () -> new FriedTranquilBaseItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> TRANQUILITY = ITEMS.register("tranquility",
            () -> new TranquilityItem(new Item.Properties().stacksTo(1)
                    .food(new FoodProperties.Builder()
                            .nutrition(2)
                            .saturationMod(0.1f)
                            .effect(() -> new MobEffectInstance(MobEffects.SATURATION, 10 * 20, 0), 1.0f)
                            .build())));

    public static final RegistryObject<Item> KALE = ITEMS.register("kale",
            () -> new KaleItem(new Item.Properties().food(jsonFood("horrorcooked9:kale", 2, 0.2f))));

    public static final RegistryObject<Item> KALE_LEAVES = ITEMS.register("kale_leaves",
            () -> new KaleLeavesItem(new Item.Properties().food(jsonFood("horrorcooked9:kale_leaves", 3, 0.35f))));

    public static final RegistryObject<Item> KALE_PUREE = ITEMS.register("kale_puree",
            () -> new KalePureeItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.35f).build())));

    public static final RegistryObject<Item> BRACKEN_FERN = ITEMS.register("bracken_fern",
            () -> new BrackenFernItem(new Item.Properties().food(jsonFood("horrorcooked9:bracken_fern", 2, 0.2f))));

    public static final RegistryObject<Item> PICKLED_BRACKEN_FERN = ITEMS.register("pickled_bracken_fern",
            () -> new PickledBrackenFernItem(new Item.Properties().food(jsonFood("horrorcooked9:pickled_bracken_fern", 4, 0.6f))));

    public static final RegistryObject<Item> PINEAPPLE = ITEMS.register("pineapple",
            () -> new PineappleItem(new Item.Properties().food(jsonFood("horrorcooked9:pineapple", 3, 0.3f))));

    public static final RegistryObject<Item> PINEAPPLE_CHUNKS = ITEMS.register("pineapple_chunks",
            () -> new PineappleChunksItem(new Item.Properties().food(jsonFood("horrorcooked9:pineapple_chunks", 4, 0.5f))));

    public static final RegistryObject<Item> ROASTED_PINEAPPLE_CHUNKS = ITEMS.register("roasted_pineapple_chunks",
            () -> new RoastedPineappleChunksItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(5).saturationMod(0.7f).build())));

    public static final RegistryObject<Item> PINEAPPLE_FRIED_RICE_BASE = ITEMS.register("pineapple_fried_rice_base",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> FRIED_PINEAPPLE_FRIED_RICE_BASE = ITEMS.register("fried_pineapple_fried_rice_base",
            () -> new FriedPineappleFriedRiceBaseItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> PINEAPPLE_FRIED_RICE = ITEMS.register("pineapple_fried_rice",
            () -> new PineappleFriedRiceItem(new Item.Properties().stacksTo(1)
                    .food(new FoodProperties.Builder().nutrition(4).saturationMod(0.6f).build())));

    public static final RegistryObject<Item> PARASITIC_BEAN_SPROUTS = ITEMS.register("parasitic_bean_sprouts",
            () -> new ParasiticBeanSproutsItem(new Item.Properties().food(jsonFood("horrorcooked9:parasitic_bean_sprouts", 7, 1.0f))));

    public static final RegistryObject<Item> DRIED_BEAN_SPROUTS = ITEMS.register("dried_bean_sprouts",
            () -> new DriedBeanSproutsItem(new Item.Properties().food(jsonFood("horrorcooked9:dried_bean_sprouts", 3, 0.4f))));

    public static final RegistryObject<Item> HAPPY_RICE = ITEMS.register("happy_rice",
            () -> new HappyRiceItem(new Item.Properties().food(jsonFood("horrorcooked9:happy_rice", 5, 0.8f))));

    public static final RegistryObject<Item> HAPPY_COOKED_RICE = ITEMS.register("happy_cooked_rice",
            () -> new HappyCookedRiceItem(new Item.Properties().food(jsonFood("horrorcooked9:happy_cooked_rice", 8, 1.2f))));

    public static final RegistryObject<Item> SHIT = ITEMS.register("shit",
            () -> new ShitItem(new Item.Properties()));

    public static final RegistryObject<Item> CAPTAIN_HAT = ITEMS.register("captain_hat",
            () -> new CaptainHat(CaptainHat.MATERIAL, ArmorItem.Type.HELMET, new Item.Properties()));

 public static final RegistryObject<Item> FOODWORKS_TABLE = ITEMS.register("foodworks_table",
            () -> new BlockItem(ModBlocks.FOODWORKS_TABLE.get(), new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    private static FoodProperties jsonFood(String itemId, int fallbackNutrition, float fallbackSaturation) {
        return FoodRuntimeConfigs.resolveRegistrationFoodProperties(
                ResourceLocation.parse(itemId),
                fallbackNutrition,
                fallbackSaturation
        );
    }
}
