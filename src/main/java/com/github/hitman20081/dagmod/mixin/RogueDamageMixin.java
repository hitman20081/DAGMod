package com.github.hitman20081.dagmod.mixin;

import com.github.hitman20081.dagmod.class_system.RogueCombatHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class RogueDamageMixin {

    /**
     * Modify damage dealt by Rogue players (critical hits and backstab)

    @ModifyVariable(
            method = "damage",
            at = @At("HEAD"),
            argsOnly = true
    )
    private ServerWorld modifyRogueDamageDealt(ServerWorld value) {
        LivingEntity target = (LivingEntity)(Object)this;

        // Check if damage source is a Rogue player
        LivingEntity source = null;
        if (source.getAttacker() instanceof ServerPlayerEntity attacker) {
            return RogueCombatHandler.handleRogueDamage(attacker, target, amount);
        }

        return amount;
    }*/

    /**
     * Reduce fall damage for Rogue players

    @ModifyVariable(
            method = "damage",
            at = @At("HEAD"),
            argsOnly = true
    )
    private ServerWorld modifyRogueFallDamage(ServerWorld value) {
        // Check if this entity is a Rogue player taking fall damage
        if ((Object)this instanceof ServerPlayerEntity player &&
                source.getName().equals("fall")) {
            return RogueCombatHandler.modifyFallDamage(player, amount);
        }

        return amount;
    }*/
}