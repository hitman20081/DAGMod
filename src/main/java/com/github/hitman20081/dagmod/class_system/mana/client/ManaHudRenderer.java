package com.github.hitman20081.dagmod.class_system.mana.client;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

public class ManaHudRenderer implements HudRenderCallback {
    private static final Identifier MANA_BAR_TEXTURE = Identifier.of("dagmod", "textures/gui/mana_bar.png");

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null) return;

        // Only show for Mages
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(client.player.getUuid());
        if (!"Mage".equals(playerClass)) return;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

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

        // Draw mana text
        String manaText = String.format("%.0f/%.0f", ClientManaData.getCurrentMana(), (float) ClientManaData.getMaxMana());
        drawContext.drawText(client.textRenderer, manaText, x + 41 - client.textRenderer.getWidth(manaText) / 2, y - 10, 0x00AAFF, true);
    }

    private void drawBorder(DrawContext context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + 1, color); // Top
        context.fill(x, y + height - 1, x + width, y + height, color); // Bottom
        context.fill(x, y, x + 1, y + height, color); // Left
        context.fill(x + width - 1, y, x + width, y + height, color); // Right
    }
}