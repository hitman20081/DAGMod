package com.github.hitman20081.dagmod.class_system.rogue.client;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.DeltaTracker;
import net.minecraft.network.chat.Component;

public class EnergyHudRenderer {

    public void onHudRender(GuiGraphicsExtractor drawContext, DeltaTracker tickCounter) {
        Minecraft client = Minecraft.getInstance();

        if (client.player == null) return;

        String playerClass = ClassSelectionAltarBlock.getPlayerClass(client.player.getUUID());
        if (!"Rogue".equals(playerClass)) return;

        int screenWidth  = client.getWindow().getGuiScaledWidth();
        int screenHeight = client.getWindow().getGuiScaledHeight();

        int barWidth  = 81;
        int barHeight = 9;

        // Same position as mana bar — right side of hotbar center
        int x = screenWidth / 2 + 10;
        int y = screenHeight - 49;

        int currentEnergy = ClientEnergyData.getCurrentEnergy();
        int maxEnergy     = ClientEnergyData.getMaxEnergy();
        int fillWidth     = (int) (barWidth * ClientEnergyData.getEnergyPercentage());

        // Background (dark gold)
        drawContext.fill(x, y, x + barWidth, y + barHeight, 0xFF332200);

        // Fill (bright gold/yellow)
        if (fillWidth > 0) {
            drawContext.fill(x, y, x + fillWidth, y + barHeight, 0xFFFFAA00);
        }

        // Border
        drawBorder(drawContext, x - 1, y - 1, barWidth + 2, barHeight + 2, 0xFF000000);

        // Count centered inside bar
        String energyText = currentEnergy + "/" + maxEnergy;
        int textX = x + (barWidth - client.font.width(energyText)) / 2;
        drawContext.text(client.font, Component.literal(energyText), textX, y + 1, 0xFFFFFFFF, true);
    }

    private void drawBorder(GuiGraphicsExtractor context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + 1, color);
        context.fill(x, y + height - 1, x + width, y + height, color);
        context.fill(x, y, x + 1, y + height, color);
        context.fill(x + width - 1, y, x + width, y + height, color);
    }
}
