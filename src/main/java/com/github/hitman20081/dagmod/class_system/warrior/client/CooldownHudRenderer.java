package com.github.hitman20081.dagmod.class_system.warrior.client;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.class_system.warrior.WarriorAbility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.DeltaTracker;
import net.minecraft.network.chat.Component;

public class CooldownHudRenderer {

    // Labels shown inside each box (4 chars max)
    private static final String[] LABELS = {"RAGE", "BASH", "WAR", "SHOUT", "WHIRL", "IRON"};

    private static final int BOX_W   = 26;
    private static final int BOX_H   = 22;
    private static final int GAP     = 3;
    private static final int TOTAL_W = WarriorAbility.values().length * BOX_W
                                     + (WarriorAbility.values().length - 1) * GAP;

    public void onHudRender(GuiGraphicsExtractor drawContext, DeltaTracker tickCounter) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;

        String playerClass = ClassSelectionAltarBlock.getPlayerClass(client.player.getUUID());
        if (!"Warrior".equals(playerClass)) return;

        int screenWidth  = client.getWindow().getGuiScaledWidth();
        int screenHeight = client.getWindow().getGuiScaledHeight();

        int startX = (screenWidth - TOTAL_W) / 2;
        int y      = screenHeight - 70;

        WarriorAbility[] abilities = WarriorAbility.values();
        for (int i = 0; i < abilities.length; i++) {
            WarriorAbility ability = abilities[i];
            int x = startX + i * (BOX_W + GAP);
            renderAbilityBox(drawContext, client, ability, LABELS[i], x, y);
        }
    }

    private void renderAbilityBox(GuiGraphicsExtractor ctx, Minecraft client,
                                  WarriorAbility ability, String label, int x, int y) {
        boolean ready    = ClientCooldownData.isReady(ability);
        int baseColor    = ability.getColor();
        int bgColor      = ready ? (0xFF000000 | baseColor) : darken(baseColor);
        int borderColor  = ready ? 0xFFFFFFFF : 0xFF666666;

        // Background
        ctx.fill(x, y, x + BOX_W, y + BOX_H, bgColor);

        // Border
        ctx.fill(x - 1, y - 1, x + BOX_W + 1, y,          borderColor); // top
        ctx.fill(x - 1, y + BOX_H, x + BOX_W + 1, y + BOX_H + 1, borderColor); // bottom
        ctx.fill(x - 1, y, x,          y + BOX_H, borderColor); // left
        ctx.fill(x + BOX_W, y, x + BOX_W + 1, y + BOX_H, borderColor); // right

        // Ability label (top line)
        int labelX = x + (BOX_W - client.font.width(label)) / 2;
        ctx.text(client.font, Component.literal(label), labelX, y + 2, 0xFFFFFFFF, true);

        // Cooldown time or ready indicator (bottom line)
        String timeText = ready ? "RDY" : ClientCooldownData.getRemainingSeconds(ability) + "s";
        int timeColor   = ready ? 0xFF00FF00 : 0xFFFFAA00;
        int timeX = x + (BOX_W - client.font.width(timeText)) / 2;
        ctx.text(client.font, Component.literal(timeText), timeX, y + 12, timeColor, true);
    }

    /** Darken an RGB color to ~35% brightness for on-cooldown state. */
    private static int darken(int rgb) {
        int r = (int) (((rgb >> 16) & 0xFF) * 0.35f);
        int g = (int) (((rgb >> 8)  & 0xFF) * 0.35f);
        int b = (int) ((rgb         & 0xFF) * 0.35f);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
}
