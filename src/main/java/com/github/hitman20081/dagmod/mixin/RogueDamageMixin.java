package com.github.hitman20081.dagmod.mixin;

import com.github.hitman20081.dagmod.class_system.RogueCombatHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class RogueDamageMixin {

    /**
     * Modify damage when a Rogue attacks (critical hits and backstab)
     */
    @ModifyVariable(
            method = "damage",
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true
    )
    private float modifyRogueDamage(float amount, ServerWorld world, DamageSource source) {
        LivingEntity target = (LivingEntity)(Object)this;

        // Check if damage source is a Rogue player attacking
        if (source.getAttacker() instanceof ServerPlayerEntity attacker) {
            return RogueCombatHandler.handleRogueDamage(attacker, target, amount);
        }

        // Check if this entity is a Rogue player taking fall damage
        if ((Object)this instanceof ServerPlayerEntity player &&
                source.isOf(net.minecraft.entity.damage.DamageTypes.FALL)) {
            return RogueCombatHandler.modifyFallDamage(player, amount);
        }

        return amount;
    }
}