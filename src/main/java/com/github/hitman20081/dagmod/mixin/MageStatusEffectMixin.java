package com.github.hitman20081.dagmod.mixin;

import com.github.hitman20081.dagmod.class_system.MagePotionHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class MageStatusEffectMixin {

    @ModifyVariable(
            method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z",
            at = @At("HEAD"),
            argsOnly = true
    )
    private MobEffectInstance modifyPotionDuration(MobEffectInstance effect) {
        // Check if this entity is a Mage player
        if ((Object)this instanceof ServerPlayer player) {
            return MagePotionHandler.modifyPotionEffect(player, effect);
        }
        return effect;
    }
}