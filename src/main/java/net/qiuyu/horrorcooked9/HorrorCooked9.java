package net.qiuyu.horrorcooked9;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.qiuyu.horrorcooked9.armor.renderer.CaptainHatRenderer;
import net.qiuyu.horrorcooked9.blocks.renderer.ChoppingBoardRenderer;
import net.qiuyu.horrorcooked9.blocks.renderer.SaladBowlRenderer;
import net.qiuyu.horrorcooked9.client.ClientRuntimeBridgeImpl;
import net.qiuyu.horrorcooked9.common.ClientRuntimeBridge;
import net.qiuyu.horrorcooked9.config.ModServerConfig;
import net.qiuyu.horrorcooked9.network.ModNetworking;
import net.qiuyu.horrorcooked9.register.ModBlockEntities;
import net.qiuyu.horrorcooked9.register.ModBlocks;
import net.qiuyu.horrorcooked9.register.ModCreativeModeTabs;
import net.qiuyu.horrorcooked9.register.ModEffects;
import net.qiuyu.horrorcooked9.register.ModGameRules;
import net.qiuyu.horrorcooked9.register.ModItems;
import net.qiuyu.horrorcooked9.register.ModRecipes;

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
        ModEffects.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModRecipes.register(modEventBus);

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
            event.enqueueWork(() -> ClientRuntimeBridge.install(new ClientRuntimeBridgeImpl()));
        }

        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event)
        {
            event.registerBlockEntityRenderer(ModBlockEntities.CHOPPING_BOARD_BE.get(), ChoppingBoardRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntities.SALAD_BOWL_BE.get(), SaladBowlRenderer::new);
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

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientForgeEvents {
        private static int shakeTicks = 0;
        private static boolean wasDiarrheaProcActive = false;

        @SubscribeEvent
        public static void onRenderGuiPost(RenderGuiEvent.Post event) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player == null || minecraft.level == null) {
                return;
            }
            if (!minecraft.player.hasEffect(ModEffects.DIARRHEA.get())) {
                return;
            }
            MobEffectInstance slowness = minecraft.player.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
            MobEffectInstance fatigue = minecraft.player.getEffect(MobEffects.DIG_SLOWDOWN);
            boolean diarrheaProcActive = slowness != null && slowness.getAmplifier() >= 6;
            if (diarrheaProcActive && !wasDiarrheaProcActive) {
                shakeTicks = 20;
            }
            wasDiarrheaProcActive = diarrheaProcActive;
            if (slowness == null && fatigue == null) {
                return;
            }

            int amplifier = minecraft.player.getEffect(ModEffects.DIARRHEA.get()).getAmplifier();
            int alpha = Math.min(180, 90 + amplifier * 20);
            int color = (alpha << 24);

            GuiGraphics guiGraphics = event.getGuiGraphics();
            int width = minecraft.getWindow().getGuiScaledWidth();
            int height = minecraft.getWindow().getGuiScaledHeight();
            guiGraphics.fill(0, 0, width, height, color);
        }

        @SubscribeEvent
        public static void onComputeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
            if (shakeTicks <= 0) {
                return;
            }

            float progress = shakeTicks / 20.0F;
            float tickTime = (float) (20 - shakeTicks + event.getPartialTick());
            float wave = Mth.sin(tickTime * 1.8F);
            float yawOffset = wave * 2.5F * progress;
            float pitchOffset = Mth.cos(tickTime * 2.2F) * 1.6F * progress;

            event.setYaw(event.getYaw() + yawOffset);
            event.setPitch(event.getPitch() + pitchOffset);
            shakeTicks--;
        }
    }
}
