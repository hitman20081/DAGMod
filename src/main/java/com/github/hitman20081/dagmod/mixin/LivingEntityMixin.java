package com.github.hitman20081.dagmod.mixin;

import com.github.hitman20081.dagmod.progression.XPEventHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to award XP when players kill mobs
 */
@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "die", at = @At("HEAD"))
    private void die(DamageSource damageSource, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity)(Object)this;

        // Direct hit (melee/projectile) first. Falls back to vanilla's own kill-credit
        // tracking (getLastHurtByPlayer, with its short memory window) for deaths whose
        // final damage source isn't the player themselves -- e.g. a mob set alight by
        // Blazing Strike/Fire Aspect that dies from the burn a couple seconds later, or any
        // other enchantment effect (poison, etc.) that finishes a mob off on a later tick
        // rather than the triggering hit. Without this fallback those kills granted no XP at
        // all, since the death's DamageSource.getEntity() for on-fire/DoT damage is never
        // the igniting player.
        ServerPlayer player = damageSource.getEntity() instanceof ServerPlayer directAttacker
                ? directAttacker
                : entity.getLastHurtByPlayer() instanceof ServerPlayer recentAttacker ? recentAttacker : null;

        if (player != null) {
            XPEventHandler.onMobKilled(player, entity);
        }
    }
}