package com.github.hitman20081.dagmod.party.client;

import com.github.hitman20081.dagmod.party.PartyData;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Renders party member information on the HUD
 */
public class PartyHUD {

    private static final int MEMBER_HEIGHT = 20;
    private static final int MEMBER_WIDTH = 150;
    private static final int PADDING = 5;
    private static final int START_X = 5;
    private static final int START_Y = 100;

    // Colors
    private static final int COLOR_BACKGROUND = 0xC0000000; // Semi-transparent black
    private static final int COLOR_HEALTH_BAR_BG = 0xFF333333; // Dark gray
    private static final int COLOR_HEALTH_BAR_FULL = 0xFF00FF00; // Green
    private static final int COLOR_HEALTH_BAR_HALF = 0xFFFFFF00; // Yellow
    private static final int COLOR_HEALTH_BAR_LOW = 0xFFFF0000; // Red
    private static final int COLOR_LEADER = 0xFFFFD700; // Gold
    private static final int COLOR_MEMBER = 0xFFFFFFFF; // White
    private static final int COLOR_XP_BONUS = 0xFF00FFFF; // Cyan

    /**
     * Register the HUD renderer
     */
    public static void register() {
        HudRenderCallback.EVENT.register(PartyHUD::render);
    }

    /**
     * Render the party HUD
     */
    private static void render(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.options.hudHidden) return;
        if (client.player == null) return;

        PartyData party = ClientPartyData.getLocalPartyData();
        if (party == null) return;

        // Don't render if solo
        if (party.getSize() <= 1) return;

        int y = START_Y;

        // Render party header
        context.drawText(
                client.textRenderer,
                Text.literal("Party (" + party.getSize() + "/5)").formatted(Formatting.GOLD),
                START_X,
                y,
                COLOR_LEADER,
                true
        );
        y += 12;

        // Render XP bonus
        int xpBonus = party.getXpBonusPercentage();
        if (xpBonus > 0) {
            context.drawText(
                    client.textRenderer,
                    Text.literal("XP Bonus: +" + xpBonus + "%").formatted(Formatting.AQUA),
                    START_X,
                    y,
                    COLOR_XP_BONUS,
                    true
            );
            y += 12;
        }

        y += 5;

        // Render party members
        List<PartyMemberInfo> members = ClientPartyData.getPartyMembers();
        for (PartyMemberInfo member : members) {
            // Skip self
            if (member.uuid.equals(client.player.getUuid())) {
                continue;
            }

            renderMember(context, client, member, START_X, y, party.isLeader(member.uuid));
            y += MEMBER_HEIGHT + PADDING;
        }
    }

    /**
     * Render a single party member
     */
    private static void renderMember(DrawContext context, MinecraftClient client, PartyMemberInfo member, int x, int y, boolean isLeader) {
        // Background
        context.fill(x, y, x + MEMBER_WIDTH, y + MEMBER_HEIGHT, COLOR_BACKGROUND);

        // Leader star
        if (isLeader) {
            context.drawText(
                    client.textRenderer,
                    Text.literal("★").formatted(Formatting.GOLD),
                    x + 2,
                    y + 2,
                    COLOR_LEADER,
                    true
            );
        }

        // Name
        int nameX = x + (isLeader ? 12 : 4);
        context.drawText(
                client.textRenderer,
                Text.literal(member.name),
                nameX,
                y + 2,
                isLeader ? COLOR_LEADER : COLOR_MEMBER,
                true
        );

        // Health bar
        int barY = y + 12;
        int barWidth = MEMBER_WIDTH - 8;
        int barHeight = 4;

        // Background
        context.fill(x + 4, barY, x + 4 + barWidth, barY + barHeight, COLOR_HEALTH_BAR_BG);

        // Health fill
        if (member.health > 0 && member.maxHealth > 0) {
            float healthPercent = member.health / member.maxHealth;
            int fillWidth = (int) (barWidth * healthPercent);

            int fillColor;
            if (healthPercent > 0.5f) {
                fillColor = COLOR_HEALTH_BAR_FULL;
            } else if (healthPercent > 0.25f) {
                fillColor = COLOR_HEALTH_BAR_HALF;
            } else {
                fillColor = COLOR_HEALTH_BAR_LOW;
            }

            context.fill(x + 4, barY, x + 4 + fillWidth, barY + barHeight, fillColor);
        }
    }

    /**
     * Party member information for rendering
     */
    public static class PartyMemberInfo {
        public final UUID uuid;
        public final String name;
        public float health;
        public float maxHealth;
        public boolean online;

        public PartyMemberInfo(UUID uuid, String name, float health, float maxHealth, boolean online) {
            this.uuid = uuid;
            this.name = name;
            this.health = health;
            this.maxHealth = maxHealth;
            this.online = online;
        }
    }
}