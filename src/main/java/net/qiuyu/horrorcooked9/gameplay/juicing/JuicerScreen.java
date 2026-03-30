package net.qiuyu.horrorcooked9.gameplay.juicing;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.qiuyu.horrorcooked9.HorrorCooked9;

public class JuicerScreen extends AbstractContainerScreen<JuicerMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.parse("minecraft:textures/gui/container/furnace.png");

    public JuicerScreen(JuicerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        int left = leftPos;
        int top = topPos;
        guiGraphics.blit(TEXTURE, left, top, 0, 0, imageWidth, imageHeight);

        int fluidHeight = 48;
        int fluidScaled = (int) ((menu.getFluidAmount() / (float) Math.max(1, menu.getTankCapacity())) * fluidHeight);
        if (fluidScaled > 0) {
            guiGraphics.fill(left + 80, top + 20 + (fluidHeight - fluidScaled), left + 92, top + 20 + fluidHeight, 0xAAE8A731);
        }

        int progressWidth = 24;
        int progressScaled = (int) ((menu.getManualProgressTicks() / (float) menu.getManualTargetTicks()) * progressWidth);
        if (progressScaled > 0) {
            guiGraphics.fill(left + 74, top + 70, left + 74 + progressScaled, top + 74, 0xFF4DD0E1);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, title, 8, 6, 0x404040, false);
        guiGraphics.drawString(font, playerInventoryTitle, 8, imageHeight - 94, 0x404040, false);
        guiGraphics.drawString(font, Component.translatable("screen." + HorrorCooked9.MODID + ".juicer.fluid", menu.getFluidAmount()), 60, 10, 0x2E2E2E, false);
        guiGraphics.drawString(font, Component.translatable("screen." + HorrorCooked9.MODID + ".juicer.pulp", menu.getPulpRemainingMb()), 60, 22, 0x2E2E2E, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
