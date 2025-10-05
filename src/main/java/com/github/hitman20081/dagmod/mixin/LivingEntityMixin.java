package com.github.hitman20081.dagmod.mixin;

import com.github.hitman20081.dagmod.progression.XPEventHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to award XP when players kill mobs
 */
@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeath(DamageSource damageSource, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity)(Object)this;

        // Check if killed by a player
        if (damageSource.getAttacker() instanceof ServerPlayerEntity player) {
            // Award XP to the player
            XPEventHandler.onMobKilled(player, entity);
        }
    }
}