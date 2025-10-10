package com.github.hitman20081.dagmod.client;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;

public class EnergyHudRenderer {

    private static int clientEnergy = 100;
    private static final int MAX_ENERGY = 100;
    private static final int BAR_WIDTH = 81;
    private static final int BAR_HEIGHT = 9;

    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();

            if (client.player == null || client.options.hudHidden) {
                return;
            }

            // FIX: use getUuid()
            if (!ClassSelectionAltarBlock.getPlayerClass(client.player.getUuid()).equals("rogue")) {
                return;
            }

            renderEnergyBar(drawContext, client.player);
        });
    }

    private static void renderEnergyBar(DrawContext context, PlayerEntity player) {
        int screenWidth = context.getScaledWindowWidth();
        int screenHeight = context.getScaledWindowHeight();

        int x = screenWidth / 2 + 10;
        int y = screenHeight - 49;

        // Background
        context.fill(x, y, x + BAR_WIDTH, y + BAR_HEIGHT, 0xFF000000);

        // Border
        context.fill(x - 1, y - 1, x + BAR_WIDTH + 1, y, 0xFFFFFFFF);
        context.fill(x - 1, y + BAR_HEIGHT, x + BAR_WIDTH + 1, y + BAR_HEIGHT + 1, 0xFFFFFFFF);
        context.fill(x - 1, y, x, y + BAR_HEIGHT, 0xFFFFFFFF);
        context.fill(x + BAR_WIDTH, y, x + BAR_WIDTH + 1, y + BAR_HEIGHT, 0xFFFFFFFF);

        // Energy fill
        int fillWidth = (int) ((clientEnergy / (float) MAX_ENERGY) * (BAR_WIDTH - 2));

        if (fillWidth > 0) {
            int color1 = 0xFF4B0082;
            int color2 = 0xFF8B00FF;

            for (int i = 0; i < fillWidth; i++) {
                float ratio = i / (float) fillWidth;
                int r = (int) (((color1 >> 16) & 0xFF) * (1 - ratio) + ((color2 >> 16) & 0xFF) * ratio);
                int g = (int) (((color1 >> 8) & 0xFF) * (1 - ratio) + ((color2 >> 8) & 0xFF) * ratio);
                int b = (int) ((color1 & 0xFF) * (1 - ratio) + (color2 & 0xFF) * ratio);
                int color = 0xFF000000 | (r << 16) | (g << 8) | b;

                context.fill(x + 1 + i, y + 1, x + 2 + i, y + BAR_HEIGHT - 1, color);
            }
        }

        // Text
        String energyText = clientEnergy + "/" + MAX_ENERGY;
        int textWidth = MinecraftClient.getInstance().textRenderer.getWidth(energyText);
        int textX = x + (BAR_WIDTH - textWidth) / 2;
        int textY = y + 1;

        context.drawTextWithShadow(
                MinecraftClient.getInstance().textRenderer,
                energyText,
                textX,
                textY,
                0xFFFFFF
        );
    }

    public static void setClientEnergy(int energy) {
        clientEnergy = Math.max(0, Math.min(energy, MAX_ENERGY));
    }

    public static int getClientEnergy() {
        return clientEnergy;
    }
}