package com.github.hitman20081.dagmod.mixin;

import com.github.hitman20081.dagmod.class_system.MagePotionHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class MageStatusEffectMixin {

    @ModifyVariable(
            method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;)Z",
            at = @At("HEAD"),
            argsOnly = true
    )
    private StatusEffectInstance modifyPotionDuration(StatusEffectInstance effect) {
        // Check if this entity is a Mage player
        if ((Object)this instanceof ServerPlayerEntity player) {
            return MagePotionHandler.modifyPotionEffect(player, effect);
        }
        return effect;
    }
}