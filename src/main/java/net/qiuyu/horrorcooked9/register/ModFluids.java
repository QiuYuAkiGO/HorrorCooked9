package net.qiuyu.horrorcooked9.register;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.qiuyu.horrorcooked9.HorrorCooked9;

import java.util.function.Consumer;

public class ModFluids {
    public static final ResourceLocation STILL_WATER_TEXTURE = ResourceLocation.parse("minecraft:block/water_still");
    public static final ResourceLocation FLOWING_WATER_TEXTURE = ResourceLocation.parse("minecraft:block/water_flow");
    public static final ResourceLocation WATER_OVERLAY_TEXTURE = ResourceLocation.parse("minecraft:block/water_overlay");

    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, HorrorCooked9.MODID);
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, HorrorCooked9.MODID);

    public static final RegistryObject<FluidType> CLEAR_WATER_TYPE = FLUID_TYPES.register("clear_water_type",
            () -> createFluidType(0xFF8BE3FF));

    public static final RegistryObject<Fluid> CLEAR_WATER = FLUIDS.register("clear_water",
            () -> new ForgeFlowingFluid.Source(clearWaterProperties()) {
                @Override
                protected boolean canConvertToSource(Level level) {
                    return false;
                }
            });
    public static final RegistryObject<Fluid> FLOWING_CLEAR_WATER = FLUIDS.register("flowing_clear_water",
            () -> new ForgeFlowingFluid.Flowing(clearWaterProperties()));

    public static final RegistryObject<FluidType> FILTERED_WATER_TYPE = FLUID_TYPES.register("filtered_water_type",
            () -> createFluidType(0xFF6FA19E));

    public static final RegistryObject<Fluid> FILTERED_WATER = FLUIDS.register("filtered_water",
            () -> new ForgeFlowingFluid.Source(filteredWaterProperties()) {
                @Override
                protected boolean canConvertToSource(Level level) {
                    return false;
                }
            });
    public static final RegistryObject<Fluid> FLOWING_FILTERED_WATER = FLUIDS.register("flowing_filtered_water",
            () -> new ForgeFlowingFluid.Flowing(filteredWaterProperties()));

    public static final RegistryObject<FluidType> PINEAPPLE_JUICE_TYPE = FLUID_TYPES.register("pineapple_juice_type",
            () -> createFluidType(0xFFE8A731));

    public static final RegistryObject<Fluid> PINEAPPLE_JUICE = FLUIDS.register("pineapple_juice",
            () -> new ForgeFlowingFluid.Source(pineappleJuiceProperties()) {
                @Override
                protected boolean canConvertToSource(Level level) {
                    return false;
                }
            });
    public static final RegistryObject<Fluid> FLOWING_PINEAPPLE_JUICE = FLUIDS.register("flowing_pineapple_juice",
            () -> new ForgeFlowingFluid.Flowing(pineappleJuiceProperties()));

    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
        FLUIDS.register(eventBus);
    }

    private static FluidType createFluidType(int tintColor) {
        return new FluidType(FluidType.Properties.create()
                .canSwim(true)
                .canDrown(true)
                .supportsBoating(true)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)) {
            public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                consumer.accept(new IClientFluidTypeExtensions() {
                    @Override
                    public ResourceLocation getStillTexture() {
                        return STILL_WATER_TEXTURE;
                    }

                    @Override
                    public ResourceLocation getFlowingTexture() {
                        return FLOWING_WATER_TEXTURE;
                    }

                    @Override
                    public ResourceLocation getOverlayTexture() {
                        return WATER_OVERLAY_TEXTURE;
                    }

                    @Override
                    public int getTintColor() {
                        return tintColor;
                    }
                });
            }
        };
    }

    private static ForgeFlowingFluid.Properties clearWaterProperties() {
        return new ForgeFlowingFluid.Properties(CLEAR_WATER_TYPE, CLEAR_WATER, FLOWING_CLEAR_WATER)
                .bucket(ModItems.CLEAR_WATER_BUCKET)
                .block(ModBlocks.CLEAR_WATER_BLOCK)
                .slopeFindDistance(2)
                .levelDecreasePerBlock(2);
    }

    private static ForgeFlowingFluid.Properties filteredWaterProperties() {
        return new ForgeFlowingFluid.Properties(FILTERED_WATER_TYPE, FILTERED_WATER, FLOWING_FILTERED_WATER)
                .bucket(ModItems.FILTERED_WATER_BUCKET)
                .block(ModBlocks.FILTERED_WATER_BLOCK)
                .slopeFindDistance(2)
                .levelDecreasePerBlock(2);
    }

    private static ForgeFlowingFluid.Properties pineappleJuiceProperties() {
        return new ForgeFlowingFluid.Properties(PINEAPPLE_JUICE_TYPE, PINEAPPLE_JUICE, FLOWING_PINEAPPLE_JUICE)
                .bucket(ModItems.PINEAPPLE_JUICE_BUCKET)
                .block(ModBlocks.PINEAPPLE_JUICE_BLOCK)
                .slopeFindDistance(2)
                .levelDecreasePerBlock(2);
    }

    private ModFluids() {
    }
}
