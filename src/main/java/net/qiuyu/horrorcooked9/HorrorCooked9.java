package net.qiuyu.horrorcooked9;

import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.qiuyu.horrorcooked9.client.renderer.CaptainHatRenderer;
import net.qiuyu.horrorcooked9.client.renderer.ChoppingBoardRenderer;
import net.qiuyu.horrorcooked9.client.renderer.ExcrementRenderer;
import net.qiuyu.horrorcooked9.client.renderer.HookMonsterRenderer;
import net.qiuyu.horrorcooked9.client.renderer.HookRenderer;
import net.qiuyu.horrorcooked9.client.renderer.SaladBowlRenderer;
import net.qiuyu.horrorcooked9.client.ClientItemExtensionRegistry;
import net.qiuyu.horrorcooked9.client.ClientRuntimeBridgeImpl;
import net.qiuyu.horrorcooked9.common.ClientRuntimeBridge;
import net.qiuyu.horrorcooked9.config.ModServerConfig;
import net.qiuyu.horrorcooked9.entity.custom.ExcrementEntity;
import net.qiuyu.horrorcooked9.entity.custom.HookMonsterEntity;
import net.qiuyu.horrorcooked9.network.ModNetworking;
import net.qiuyu.horrorcooked9.register.ModBlockEntities;
import net.qiuyu.horrorcooked9.register.ModBlocks;
import net.qiuyu.horrorcooked9.register.ModCreativeModeTabs;
import net.qiuyu.horrorcooked9.register.ModEffects;
import net.qiuyu.horrorcooked9.register.ModEntities;
import net.qiuyu.horrorcooked9.register.ModGameRules;
import net.qiuyu.horrorcooked9.register.ModItems;
import net.qiuyu.horrorcooked9.register.ModRecipes;
import net.qiuyu.horrorcooked9.register.ModSounds;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(HorrorCooked9.MODID)
public class HorrorCooked9
{
    public static final String MODID = "horrorcooked9";

    public HorrorCooked9(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        context.registerConfig(net.minecraftforge.fml.config.ModConfig.Type.SERVER, ModServerConfig.SPEC);

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModEntities.register(modEventBus);
        ModEffects.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModSounds.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(ModGameRules::bootstrap);
        ModNetworking.register();
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(final FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                ClientRuntimeBridge.install(new ClientRuntimeBridgeImpl());
                ClientItemExtensionRegistry.install();
            });
        }

        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event)
        {
            event.registerBlockEntityRenderer(ModBlockEntities.CHOPPING_BOARD_BE.get(), ChoppingBoardRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntities.SALAD_BOWL_BE.get(), SaladBowlRenderer::new);
            event.registerEntityRenderer(ModEntities.HOOK_MONSTER.get(), HookMonsterRenderer::new);
            event.registerEntityRenderer(ModEntities.EXCREMENT.get(), ExcrementRenderer::new);
            event.registerEntityRenderer(ModEntities.HOOK.get(), HookRenderer::new);
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        @SubscribeEvent
        public static void addLayers(EntityRenderersEvent.AddLayers event)
        {
            // Player skins (default + slim)
            for (String skin : event.getSkins()) {
                LivingEntityRenderer renderer = event.getSkin(skin);
                if (renderer != null) {
                    renderer.addLayer(new CaptainHatRenderer(renderer));
                }
            }

            // Non-player entities that can wear helmets
            addLayerIfLivingRenderer(event.getRenderer(EntityType.ZOMBIE));
            addLayerIfLivingRenderer(event.getRenderer(EntityType.HUSK));
            addLayerIfLivingRenderer(event.getRenderer(EntityType.DROWNED));
            addLayerIfLivingRenderer(event.getRenderer(EntityType.ZOMBIFIED_PIGLIN));
            addLayerIfLivingRenderer(event.getRenderer(EntityType.ZOMBIE_VILLAGER));
            addLayerIfLivingRenderer(event.getRenderer(EntityType.ARMOR_STAND));
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        private static void addLayerIfLivingRenderer(EntityRenderer<?> renderer) {
            if (renderer instanceof LivingEntityRenderer livingRenderer) {
                livingRenderer.addLayer(new CaptainHatRenderer(livingRenderer));
            }
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {
        @SubscribeEvent
        public static void entityAttributeCreation(net.minecraftforge.event.entity.EntityAttributeCreationEvent event) {
            event.put(ModEntities.HOOK_MONSTER.get(), HookMonsterEntity.createAttributes().build());
            event.put(ModEntities.EXCREMENT.get(), ExcrementEntity.createAttributes().build());
        }
    }

}
