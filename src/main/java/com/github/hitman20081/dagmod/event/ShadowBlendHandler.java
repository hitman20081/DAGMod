package com.github.hitman20081.dagmod.event;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ShadowBlendHandler {

    // Track which players have Shadow Blend active
    private static final Set<UUID> shadowBlendPlayers = new HashSet<>();

    public static void register() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClient() && player instanceof ServerPlayerEntity serverPlayer) {
                if (serverPlayer.hasStatusEffect(StatusEffects.INVISIBILITY)) {
                    UUID playerId = serverPlayer.getUuid();

                    // Check if this player has Shadow Blend active
                    if (shadowBlendPlayers.contains(playerId)) {
                        // Remove invisibility
                        serverPlayer.removeStatusEffect(StatusEffects.INVISIBILITY);

                        // Clear the flag
                        shadowBlendPlayers.remove(playerId);

                        // Notify player
                        serverPlayer.sendMessage(Text.literal("🌑 Shadow Blend broken by attack! 🌑")
                                .formatted(Formatting.DARK_GRAY), true);
                    }
                }
            }
            return ActionResult.PASS;
        });
    }

    public static void activateShadowBlend(UUID playerId) {
        shadowBlendPlayers.add(playerId);
    }

    public static void deactivateShadowBlend(UUID playerId) {
        shadowBlendPlayers.remove(playerId);
    }
}