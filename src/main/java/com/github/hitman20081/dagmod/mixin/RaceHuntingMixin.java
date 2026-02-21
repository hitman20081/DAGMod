package com.github.hitman20081.dagmod.mixin;

import com.github.hitman20081.dagmod.race_system.OrcHuntingHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class RaceHuntingMixin {

    @Inject(
            method = "onDeath",
            at = @At("HEAD")
    )
    private void onEntityDeath(DamageSource damageSource, CallbackInfo ci) {
        LivingEntity killed = (LivingEntity)(Object)this;

        // Check if killed by a player
        if (damageSource.getAttacker() instanceof ServerPlayerEntity player) {
            OrcHuntingHandler.handleOrcHunting(player, killed);
        }
    }
}