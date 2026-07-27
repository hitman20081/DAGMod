package com.github.hitman20081.dagmod.class_system;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.server.level.ServerPlayer;

public class MagePotionHandler {

    /**
     * Extend potion duration for Mages by 50%
     */
    public static MobEffectInstance modifyPotionEffect(ServerPlayer player, MobEffectInstance effect) {
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUUID());

        if (!"Mage".equals(playerClass)) {
            return effect;
        }

        // Mages get 50% longer potion durations
        int originalDuration = effect.getDuration();
        int newDuration = (int)(originalDuration * 1.5);

        // Create new effect with extended duration
        return new MobEffectInstance(
                effect.getEffect(),
                newDuration,
                effect.getAmplifier(),
                effect.isAmbient(),
                effect.isVisible(),
                effect.showIcon()
        );
    }
}