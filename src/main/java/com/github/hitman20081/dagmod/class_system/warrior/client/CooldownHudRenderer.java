package com.github.hitman20081.dagmod.class_system.warrior.client;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.class_system.warrior.WarriorAbility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.DeltaTracker;
import net.minecraft.network.chat.Component;

public class CooldownHudRenderer {

    // Matches the mana/energy bar dimensions exactly
    private static final int BAR_SLOT_W = 81;
    private static final int BAR_H      = 9;
    private static final int BOX_GAP    = 1;
    private static final int N          = WarriorAbility.values().length; // 6
    private static final int BOX_W      = (BAR_SLOT_W - (N - 1) * BOX_GAP) / N; // 12px

    public void onHudRender(GuiGraphicsExtractor drawContext, DeltaTracker tickCounter) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;

        String playerClass = ClassSelectionAltarBlock.getPlayerClass(client.player.getUUID());
        if (!"Warrior".equals(playerClass)) return;

        int screenWidth  = client.getWindow().getGuiScaledWidth();
        int screenHeight = client.getWindow().getGuiScaledHeight();

        // Same anchor as mana bar
        int x = screenWidth / 2 + 10;
        int y = screenHeight - 49;

        // Outer border around the whole block (matches mana bar border style)
        drawBorder(drawContext, x - 1, y - 1, BAR_SLOT_W + 2, BAR_H + 2, 0xFF000000);

        WarriorAbility[] abilities = WarriorAbility.values();
        for (int i = 0; i < N; i++) {
            int bx = x + i * (BOX_W + BOX_GAP);
            renderBox(drawContext, client, abilities[i], bx, y);
        }
    }

    private void renderBox(GuiGraphicsExtractor ctx, Minecraft client,
                           WarriorAbility ability, int x, int y) {
        boolean ready   = ClientCooldownData.isReady(ability);
        int baseColor   = ability.getColor();
        int bgColor     = ready ? (0xFF000000 | baseColor) : darken(baseColor);

        ctx.fill(x, y, x + BOX_W, y + BAR_H, bgColor);

        if (!ready) {
            int secs = ClientCooldownData.getRemainingSeconds(ability);
            String label = secs >= 100 ? (secs / 60 + 1) + "m" : String.valueOf(secs);
            int tx = x + (BOX_W - client.font.width(label)) / 2;
            ctx.text(client.font, Component.literal(label), tx, y + 1, 0xFFFFFFFF, true);
        }
    }

    private void drawBorder(GuiGraphicsExtractor ctx, int x, int y,
                            int w, int h, int color) {
        ctx.fill(x, y,         x + w, y + 1,     color);
        ctx.fill(x, y + h - 1, x + w, y + h,     color);
        ctx.fill(x, y,         x + 1, y + h,     color);
        ctx.fill(x + w - 1, y, x + w, y + h,     color);
    }

    private static int darken(int rgb) {
        int r = (int) (((rgb >> 16) & 0xFF) * 0.3f);
        int g = (int) (((rgb >> 8)  & 0xFF) * 0.3f);
        int b = (int) ((rgb         & 0xFF) * 0.3f);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
}
