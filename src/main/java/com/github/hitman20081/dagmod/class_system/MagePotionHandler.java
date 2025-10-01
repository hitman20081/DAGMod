package com.github.hitman20081.dagmod.class_system;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;

public class MagePotionHandler {

    /**
     * Extend potion duration for Mages by 50%
     */
    public static StatusEffectInstance modifyPotionEffect(ServerPlayerEntity player, StatusEffectInstance effect) {
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUuid());

        if (!"Mage".equals(playerClass)) {
            return effect;
        }

        // Mages get 50% longer potion durations
        int originalDuration = effect.getDuration();
        int newDuration = (int)(originalDuration * 1.5);

        // Create new effect with extended duration
        return new StatusEffectInstance(
                effect.getEffectType(),
                newDuration,
                effect.getAmplifier(),
                effect.isAmbient(),
                effect.shouldShowParticles(),
                effect.shouldShowIcon()
        );
    }
}