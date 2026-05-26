package com.github.hitman20081.dagmod.mixin;

import com.github.hitman20081.dagmod.enchantment.ShatterproofHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Reduces weapon damage by 99% when the attacker's weapon is shatterproof-broken
 * (at 1 durability, kept alive by the Shatterproof enchantment).
 */
@Mixin(LivingEntity.class)
public class ShatterproofDamageMixin {

    @ModifyVariable(
            method = "damage",
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true
    )
    private float dagmod$shatterproofWeaponDebuff(float amount, ServerLevel world, DamageSource source) {
        if (source.getEntity() instanceof ServerPlayer attacker) {
            ItemStack weapon = attacker.getMainHandItem();
            if (ShatterproofHelper.isShatterproofBroken(weapon)) {
                return amount * 0.01f;
            }
        }
        return amount;
    }
}
