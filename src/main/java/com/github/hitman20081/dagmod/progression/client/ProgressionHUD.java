package com.github.hitman20081.dagmod.progression.client;

import com.github.hitman20081.dagmod.progression.PlayerProgressionData;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

/**
 * Renders the progression HUD on screen
 * Shows player level and XP progress bar
 */
public class ProgressionHUD {

    // Position options - change this to move the HUD
    private static final HUDPosition POSITION = HUDPosition.TOP_LEFT;

    public enum HUDPosition {
        ABOVE_HOTBAR,   // Centered above hotbar
        TOP_LEFT,       // Top-left corner
        TOP_RIGHT,      // Top-right corner
        BOTTOM_LEFT,    // Bottom-left corner (above inventory)
        BOTTOM_RIGHT    // Bottom-right corner
    }

    // Bar dimensions
    private static final int BAR_WIDTH = 182;
    private static final int BAR_HEIGHT = 5;
    private static final int PADDING = 10;

    // Colors (ARGB format) - Professional theme
    private static final int COLOR_BAR_BACKGROUND = 0xFF2A2A2A; // Dark gray
    private static final int COLOR_BAR_BORDER = 0xFF444444; // Medium gray
    private static final int COLOR_BAR_FILL = 0xFF4CAF50; // Material green
    private static final int COLOR_BAR_FILL_GLOW = 0xFF81C784; // Light green
    private static final int COLOR_TEXT_LEVEL = 0xFFFFD700; // Gold
    private static final int COLOR_TEXT_XP = 0xFFFFFFFF; // White
    private static final int COLOR_BACKGROUND = 0xC0000000; // Semi-transparent black
    private static final int COLOR_TEXT_SHADOW = 0x000000; // Black

    /**
     * Register the HUD renderer
     */
    public static void register() {
        HudRenderCallback.EVENT.register(ProgressionHUD::render);
    }

    /**
     * Render the progression HUD
     */
    private static void render(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.options.hudHidden) return;
        if (client.player == null) return;

        PlayerProgressionData data = ClientProgressionData.getLocalPlayerData();
        if (data == null) return;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        // Calculate position based on setting
        int x, y;
        switch (POSITION) {
            case ABOVE_HOTBAR:
                x = (screenWidth - BAR_WIDTH) / 2;
                y = screenHeight - 70; // Above hotbar
                renderCentered(context, client, x, y, data);
                break;

            case TOP_LEFT:
                x = PADDING;
                y = PADDING;
                renderCompact(context, client, x, y, data);
                break;

            case TOP_RIGHT:
                x = screenWidth - BAR_WIDTH - PADDING;
                y = PADDING;
                renderCompact(context, client, x, y, data);
                break;

            case BOTTOM_LEFT:
                x = PADDING;
                y = screenHeight - 60; // Above inventory when open
                renderCompact(context, client, x, y, data);
                break;

            case BOTTOM_RIGHT:
                x = screenWidth - BAR_WIDTH - PADDING;
                y = screenHeight - 60;
                renderCompact(context, client, x, y, data);
                break;
        }
    }

    /**
     * Render centered style (for above hotbar)
     */
    private static void renderCentered(DrawContext context, MinecraftClient client, int x, int y, PlayerProgressionData data) {
        // Background behind text and bar
        context.fill(x - 2, y - 15, x + BAR_WIDTH + 2, y + BAR_HEIGHT + 2, COLOR_BACKGROUND);

        // Level text (gold, left side)
        String levelText = "Level " + data.getCurrentLevel();
        context.drawText(
                client.textRenderer,
                Text.literal(levelText),
                x,
                y - 12,
                COLOR_TEXT_LEVEL,
                true
        );

        // XP text (white, right side)
        String xpText;
        if (data.isMaxLevel()) {
            xpText = "MAX LEVEL";
        } else {
            xpText = String.format("%,d/%,d (%d%%)",
                    data.getCurrentXP(),
                    data.getXPRequiredForNextLevel(),
                    data.getProgressPercentage());
        }

        int xpTextWidth = client.textRenderer.getWidth(xpText);
        context.drawText(
                client.textRenderer,
                Text.literal(xpText),
                x + BAR_WIDTH - xpTextWidth,
                y - 12,
                COLOR_TEXT_XP,
                true
        );

        renderXPBar(context, x, y, data);
    }

    /**
     * Render compact style (for corners)
     */
    private static void renderCompact(DrawContext context, MinecraftClient client, int x, int y, PlayerProgressionData data) {
        // Calculate background size
        int bgWidth = BAR_WIDTH + 4;
        int bgHeight = 32;

        // Draw semi-transparent background
        context.fill(x - 2, y - 2, x + bgWidth, y + bgHeight, COLOR_BACKGROUND);

        // Level text (gold color)
        String levelText = "Level " + data.getCurrentLevel();
        context.drawText(
                client.textRenderer,
                Text.literal(levelText),
                x + 2,
                y + 2,
                COLOR_TEXT_LEVEL,
                true // shadow
        );

        // XP text (white color)
        String xpText;
        if (data.isMaxLevel()) {
            xpText = "MAX LEVEL";
        } else {
            xpText = String.format("%,d/%,d (%d%%)",
                    data.getCurrentXP(),
                    data.getXPRequiredForNextLevel(),
                    data.getProgressPercentage());
        }

        context.drawText(
                client.textRenderer,
                Text.literal(xpText),
                x + 2,
                y + 12,
                COLOR_TEXT_XP,
                true // shadow
        );

        // Draw XP bar below text
        renderXPBar(context, x + 2, y + 22, data);
    }

    /**
     * Render the XP progress bar
     */
    private static void renderXPBar(DrawContext context, int x, int y, PlayerProgressionData data) {
        // Draw background
        context.fill(x, y, x + BAR_WIDTH, y + BAR_HEIGHT, COLOR_BAR_BACKGROUND);

        // Draw border
        context.drawBorder(x, y, BAR_WIDTH, BAR_HEIGHT, COLOR_BAR_BORDER);

        // Calculate fill width
        double progress = data.getProgressToNextLevel();
        int fillWidth = (int) (BAR_WIDTH * progress);

        // Draw fill
        if (fillWidth > 0) {
            context.fill(x + 1, y + 1, x + fillWidth - 1, y + BAR_HEIGHT - 1, COLOR_BAR_FILL);
            context.fill(x + 1, y + 1, x + fillWidth - 1, y + 2, COLOR_BAR_FILL_GLOW);
        }
    }
}