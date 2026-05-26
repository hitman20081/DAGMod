package com.github.hitman20081.dagmod.event;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ShadowBlendHandler {

    // Track which players have Shadow Blend active
    private static final Set<UUID> shadowBlendPlayers = new HashSet<>();

    public static void register() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                if (serverPlayer.hasEffect(MobEffects.INVISIBILITY)) {
                    UUID playerId = serverPlayer.getUUID();

                    // Check if this player has Shadow Blend active
                    if (shadowBlendPlayers.contains(playerId)) {
                        // Remove invisibility
                        serverPlayer.removeEffect(MobEffects.INVISIBILITY);

                        // Clear the flag
                        shadowBlendPlayers.remove(playerId);

                        // Notify player
                        serverPlayer.sendOverlayMessage(Component.literal("🌑 Shadow Blend broken by attack! 🌑")
                                .withStyle(ChatFormatting.DARK_GRAY));
                    }
                }
            }
            return InteractionResult.PASS;
        });
    }

    public static void activateShadowBlend(UUID playerId) {
        shadowBlendPlayers.add(playerId);
    }

    public static void deactivateShadowBlend(UUID playerId) {
        shadowBlendPlayers.remove(playerId);
    }
}