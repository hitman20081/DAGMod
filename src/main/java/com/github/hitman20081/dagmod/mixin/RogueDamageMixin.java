package com.github.hitman20081.dagmod.mixin;

import com.github.hitman20081.dagmod.class_system.RogueCombatHandler;
import com.github.hitman20081.dagmod.class_system.armor.CustomArmorSetBonus;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class RogueDamageMixin {

    /**
     * Modify damage when a player attacks (handles Rogue backstab + weapon synergy bonuses)
     */
    @ModifyVariable(
            method = "hurtServer",
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true
    )
    private float modifyPlayerDamage(float amount, ServerLevel world, DamageSource source) {
        LivingEntity target = (LivingEntity)(Object)this;

        // Check if damage source is a player attacking
        if (source.getEntity() instanceof ServerPlayer attacker) {
            // Handle Rogue backstab (includes weapon synergy backstab bonus)
            float rogueDamage = RogueCombatHandler.handleRogueDamage(attacker, target, amount);

            // Apply weapon synergy damage bonus (for all classes with matching weapon+armor)
            float weaponSynergyBonus = CustomArmorSetBonus.getWeaponSynergyDamageBonus(attacker);
            if (weaponSynergyBonus > 0) {
                rogueDamage *= (1.0f + weaponSynergyBonus);
            }

            return rogueDamage;
        }

        // Check if this entity is a Rogue player taking fall damage
        if ((Object)this instanceof ServerPlayer player &&
                source.is(net.minecraft.world.damagesource.DamageTypes.FALL)) {
            return RogueCombatHandler.modifyFallDamage(player, amount);
        }

        return amount;
    }
}