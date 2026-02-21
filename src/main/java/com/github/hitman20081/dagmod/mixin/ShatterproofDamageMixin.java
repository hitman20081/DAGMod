package com.github.hitman20081.dagmod.mixin;

import com.github.hitman20081.dagmod.enchantment.ShatterproofHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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
    private float dagmod$shatterproofWeaponDebuff(float amount, ServerWorld world, DamageSource source) {
        if (source.getAttacker() instanceof ServerPlayerEntity attacker) {
            ItemStack weapon = attacker.getMainHandStack();
            if (ShatterproofHelper.isShatterproofBroken(weapon)) {
                return amount * 0.01f;
            }
        }
        return amount;
    }
}
