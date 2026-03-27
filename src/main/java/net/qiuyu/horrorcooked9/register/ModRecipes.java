package net.qiuyu.horrorcooked9.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import net.qiuyu.horrorcooked9.gameplay.chopping.ChopperMinigameRecipe;
import net.qiuyu.horrorcooked9.gameplay.salad.SaladBowlRecipe;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, HorrorCooked9.MODID);

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, HorrorCooked9.MODID);

    public static final RegistryObject<RecipeSerializer<SaladBowlRecipe>> SALAD_BOWL_SERIALIZER =
            RECIPE_SERIALIZERS.register("salad_bowl", SaladBowlRecipe.Serializer::new);

    public static final RegistryObject<RecipeSerializer<ChopperMinigameRecipe>> CHOPPER_MINIGAME_SERIALIZER =
            RECIPE_SERIALIZERS.register("chopper_minigame", ChopperMinigameRecipe.Serializer::new);

    public static final RegistryObject<RecipeType<SaladBowlRecipe>> SALAD_BOWL_TYPE =
            RECIPE_TYPES.register("salad_bowl", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return HorrorCooked9.MODID + ":salad_bowl";
                }
            });

    public static final RegistryObject<RecipeType<ChopperMinigameRecipe>> CHOPPER_MINIGAME_TYPE =
            RECIPE_TYPES.register("chopper_minigame", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return HorrorCooked9.MODID + ":chopper_minigame";
                }
            });

    public static void register(IEventBus eventBus) {
        RECIPE_SERIALIZERS.register(eventBus);
        RECIPE_TYPES.register(eventBus);
    }
}
