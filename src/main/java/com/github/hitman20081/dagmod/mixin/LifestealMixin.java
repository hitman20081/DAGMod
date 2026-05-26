package com.github.hitman20081.dagmod.mixin;

import com.github.hitman20081.dagmod.event.VampireDustHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Grants 10% lifesteal to Vampire Dust users on successful hits.
 * Capped at 2.5 hearts (5.0 HP) per hit.
 */
@Mixin(LivingEntity.class)
public class LifestealMixin {

    @Inject(method = "damage", at = @At("RETURN"))
    private void onDamageReturn(ServerLevel world, DamageSource source, float amount,
                                CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() != Boolean.TRUE) return;

        if (source.getEntity() instanceof ServerPlayer attacker) {
            if (VampireDustHandler.isActive(attacker.getUUID(), world.getGameTime())) {
                attacker.heal(Math.min(amount * 0.1f, 5.0f));
            }
        }
    }
}
