package com.github.hitman20081.dagmod.class_system.mana.client;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.DeltaTracker;
import net.minecraft.resources.Identifier;
import net.minecraft.network.chat.Component;

public class ManaHudRenderer {
    private static final Identifier MANA_BAR_TEXTURE = Identifier.fromNamespaceAndPath("dagmod", "textures/gui/mana_bar.png");

    public void onHudRender(GuiGraphicsExtractor drawContext, DeltaTracker tickCounter) {
        Minecraft client = Minecraft.getInstance();

        if (client.player == null) return;

        // Only show for Mages
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(client.player.getUUID());
        if (!"Mage".equals(playerClass)) return;

        int screenWidth = client.getWindow().getGuiScaledWidth();
        int screenHeight = client.getWindow().getGuiScaledHeight();

        // Position: Over hunger bar (right side)
        int x = screenWidth / 2 + 10;
        int y = screenHeight - 49;

        int currentMana = (int) ClientManaData.getCurrentMana();
        int maxMana = ClientManaData.getMaxMana();
        float manaPercentage = ClientManaData.getManaPercentage();

        // Bar is 9px tall so text fits inside
        int barWidth = 81;
        int barHeight = 9;
        int fillWidth = (int) (barWidth * manaPercentage);

        // Draw mana bar background (dark blue)
        drawContext.fill(x, y, x + barWidth, y + barHeight, 0xFF000033);

        // Draw mana bar foreground (bright blue)
        if (fillWidth > 0) {
            drawContext.fill(x, y, x + fillWidth, y + barHeight, 0xFF00AAFF);
        }

        // Draw border
        drawBorder(drawContext, x - 1, y - 1, barWidth + 2, barHeight + 2, 0xFF000000);

        // Draw mana count centered inside the bar
        String manaText = currentMana + "/" + maxMana;
        int textX = x + (barWidth - client.font.width(manaText)) / 2;
        int textY = y + 1;
        drawContext.text(client.font, Component.literal(manaText), textX, textY, 0xFFFFFFFF, true);
    }

    private void drawBorder(GuiGraphicsExtractor context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + 1, color); // Top
        context.fill(x, y + height - 1, x + width, y + height, color); // Bottom
        context.fill(x, y, x + 1, y + height, color); // Left
        context.fill(x + width - 1, y, x + width, y + height, color); // Right
    }
}