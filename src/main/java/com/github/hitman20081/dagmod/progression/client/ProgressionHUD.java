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

    // Background visibility toggle
    private static boolean backgroundEnabled = false; // Default: no background

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
    private static final int BAR_WIDTH = 120;  // Reduced from 182
    private static final int BAR_HEIGHT = 4;   // Reduced from 5
    private static final int PADDING = 10;

    // Colors (ARGB format) - Professional theme
    private static final int COLOR_BAR_BACKGROUND = 0xFF2A2A2A; // Dark gray
    private static final int COLOR_BAR_BORDER = 0xFF444444; // Medium gray
    private static final int COLOR_BAR_FILL = 0xFF4CAF50; // Material green
    private static final int COLOR_BAR_FILL_GLOW = 0xFF81C784; // Light green
    private static final int COLOR_TEXT_LEVEL = 0xFFFFD700; // Gold
    private static final int COLOR_TEXT_XP = 0xFFFFFFFF; // White
    private static final int COLOR_TEXT_SHADOW = 0x000000; // Black

    // Background color options (semi-transparent)
    private static final int BG_COLOR_BLACK = 0xC0000000;
    private static final int BG_COLOR_RED = 0xC0AA0000;
    private static final int BG_COLOR_GREEN = 0xC000AA00;
    private static final int BG_COLOR_BLUE = 0xC00000AA;
    private static final int BG_COLOR_YELLOW = 0xC0FFFF00;
    private static final int BG_COLOR_PURPLE = 0xC0AA00AA;
    private static final int BG_COLOR_CYAN = 0xC000AAAA;
    private static final int BG_COLOR_ORANGE = 0xC0FF6600;
    private static final int BG_COLOR_PINK = 0xC0FF66AA;
    private static final int BG_COLOR_GRAY = 0xC0555555;
    private static final int BG_COLOR_WHITE = 0xC0FFFFFF;

    // Current background color (default: black)
    private static int currentBackgroundColor = BG_COLOR_BLACK;

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
        // Draw background if enabled
        if (backgroundEnabled) {
            context.fill(x - 2, y - 15, x + BAR_WIDTH + 2, y + BAR_HEIGHT + 2, currentBackgroundColor);
        }

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
        int bgHeight = 26;  // Reduced from 32

        // Draw background if enabled
        if (backgroundEnabled) {
            context.fill(x - 2, y - 2, x + bgWidth, y + bgHeight, currentBackgroundColor);
        }

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
     * Helper method to draw a border
     */
    private static void drawBorder(DrawContext context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + 1, color); // Top
        context.fill(x, y + height - 1, x + width, y + height, color); // Bottom
        context.fill(x, y, x + 1, y + height, color); // Left
        context.fill(x + width - 1, y, x + width, y + height, color); // Right
    }

    /**
     * Render the XP progress bar
     */
    private static void renderXPBar(DrawContext context, int x, int y, PlayerProgressionData data) {
        // Draw background
        context.fill(x, y, x + BAR_WIDTH, y + BAR_HEIGHT, COLOR_BAR_BACKGROUND);

        // Draw border
        drawBorder(context, x, y, BAR_WIDTH, BAR_HEIGHT, COLOR_BAR_BORDER);

        // Calculate fill width
        double progress = data.getProgressToNextLevel();
        int fillWidth = (int) (BAR_WIDTH * progress);

        // Draw fill
        if (fillWidth > 0) {
            context.fill(x + 1, y + 1, x + fillWidth - 1, y + BAR_HEIGHT - 1, COLOR_BAR_FILL);
            context.fill(x + 1, y + 1, x + fillWidth - 1, y + 2, COLOR_BAR_FILL_GLOW);
        }
    }

    /**
     * Toggle the HUD background on/off
     * @return new state (true = enabled, false = disabled)
     */
    public static boolean toggleBackground() {
        backgroundEnabled = !backgroundEnabled;
        return backgroundEnabled;
    }

    /**
     * Check if HUD background is currently enabled
     * @return true if enabled, false if disabled
     */
    public static boolean isBackgroundEnabled() {
        return backgroundEnabled;
    }

    /**
     * Set HUD background visibility state
     * @param enabled true to show, false to hide
     */
    public static void setBackgroundEnabled(boolean enabled) {
        backgroundEnabled = enabled;
    }

    /**
     * Set the background color by name
     * @param color color name (black, red, green, blue, yellow, purple, cyan, orange, pink, gray, white)
     * @return true if color was valid and set, false otherwise
     */
    public static boolean setBackgroundColor(String color) {
        switch (color.toLowerCase()) {
            case "black":
                currentBackgroundColor = BG_COLOR_BLACK;
                return true;
            case "red":
                currentBackgroundColor = BG_COLOR_RED;
                return true;
            case "green":
                currentBackgroundColor = BG_COLOR_GREEN;
                return true;
            case "blue":
                currentBackgroundColor = BG_COLOR_BLUE;
                return true;
            case "yellow":
                currentBackgroundColor = BG_COLOR_YELLOW;
                return true;
            case "purple":
                currentBackgroundColor = BG_COLOR_PURPLE;
                return true;
            case "cyan":
                currentBackgroundColor = BG_COLOR_CYAN;
                return true;
            case "orange":
                currentBackgroundColor = BG_COLOR_ORANGE;
                return true;
            case "pink":
                currentBackgroundColor = BG_COLOR_PINK;
                return true;
            case "gray":
            case "grey":
                currentBackgroundColor = BG_COLOR_GRAY;
                return true;
            case "white":
                currentBackgroundColor = BG_COLOR_WHITE;
                return true;
            default:
                return false;
        }
    }

    /**
     * Get list of available background colors
     * @return array of color names
     */
    public static String[] getAvailableColors() {
        return new String[]{"black", "red", "green", "blue", "yellow", "purple", "cyan", "orange", "pink", "gray", "white"};
    }
}