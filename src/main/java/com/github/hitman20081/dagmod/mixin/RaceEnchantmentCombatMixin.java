package com.github.hitman20081.dagmod.mixin;

import com.github.hitman20081.dagmod.race_system.RaceCombatEnchantmentHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Applies the Dwarf/Elf/Orc combat enchantment damage bonuses (Deep Striker, Forest's Blessing,
 * Berserker's Fury) to the ATTACKER's outgoing damage. Same hurtServer hook shape as
 * RogueDamageMixin, but keys off damageSource.getEntity() (the attacker) rather than `this`
 * (the defender) — multiple mixins may each modify this variable, and Mixin composes them.
 */
@Mixin(LivingEntity.class)
public class RaceEnchantmentCombatMixin {

    @ModifyVariable(
            method = "hurtServer",
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true
    )
    private float onIncomingDamage(float amount, ServerLevel world, DamageSource source) {
        if (source.getEntity() instanceof ServerPlayer attacker) {
            float multiplier = RaceCombatEnchantmentHandler.getDamageMultiplier(attacker, world);
            return amount * multiplier;
        }
        return amount;
    }
}
