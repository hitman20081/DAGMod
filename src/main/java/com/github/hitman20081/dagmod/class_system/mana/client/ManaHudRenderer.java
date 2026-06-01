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

        float manaPercentage = ClientManaData.getManaPercentage();
        int manaBarWidth = (int) (81 * manaPercentage);

        // Draw mana bar background (dark blue/purple)
        drawContext.fill(x, y, x + 81, y + 5, 0xFF000033);

        // Draw mana bar foreground (bright blue)
        drawContext.fill(x, y, x + manaBarWidth, y + 5, 0xFF00AAFF);

        // Draw border
        drawBorder(drawContext, x - 1, y - 1, 83, 7, 0xFF000000);

        // Draw mana count to the right of the bar
        String manaText = (int) ClientManaData.getCurrentMana() + "/" + ClientManaData.getMaxMana();
        drawContext.text(client.font, Component.literal(manaText), x + 85, y - 1, 0x00AAFF, true);
    }

    private void drawBorder(GuiGraphicsExtractor context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + 1, color); // Top
        context.fill(x, y + height - 1, x + width, y + height, color); // Bottom
        context.fill(x, y, x + 1, y + height, color); // Left
        context.fill(x + width - 1, y, x + width, y + height, color); // Right
    }
}