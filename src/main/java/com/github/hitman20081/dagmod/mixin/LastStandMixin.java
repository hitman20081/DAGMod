package com.github.hitman20081.dagmod.mixin;

import com.github.hitman20081.dagmod.event.LastStandHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.Relative;

import java.util.Set;

/**
 * Implements the Last Stand Powder mechanic: prevents one lethal hit and heals to 50% HP.
 * For void damage, the player is also teleported to the nearest solid block surface.
 */
@Mixin(LivingEntity.class)
public class LastStandMixin {

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void onDamageHead(ServerLevel world, DamageSource source, float amount,
                              CallbackInfoReturnable<Boolean> cir) {
        if (!((Object) this instanceof ServerPlayer player)) return;
        if (player.getHealth() - amount > 0) return;

        if (!LastStandHandler.consume(player.getUUID())) return;

        cir.setReturnValue(false);
        player.setHealth(player.getMaxHealth() * 0.5f);

        // Void damage: teleport to nearest solid block surface at same X/Z
        if (source.is(DamageTypes.FELL_OUT_OF_WORLD)) {
            int topY = world.getHeight(Heightmap.Types.MOTION_BLOCKING, player.getBlockX(), player.getBlockZ());
            // Guard against empty/flat dimensions where topY could be at or below spawn floor
            double safeY = Math.max(topY, world.getMinY() + 5);
            player.teleportTo(world, player.getX(), safeY, player.getZ(), Set.<Relative>of(), player.getYRot(), player.getXRot(), false);
            player.sendSystemMessage(
                    Component.literal("✝ Last Stand! Pulled back from the void at 50% HP!")
                            .withStyle(ChatFormatting.YELLOW));
        } else {
            player.sendSystemMessage(
                    Component.literal("✝ Last Stand activated! Revived at 50% HP!")
                            .withStyle(ChatFormatting.YELLOW));
        }

        world.sendParticles(
                ParticleTypes.TOTEM_OF_UNDYING,
                player.getX(), player.getY() + 1.0, player.getZ(),
                50, 0.5, 1.0, 0.5, 0.2
        );
        world.playSound(
                null, player.blockPosition(),
                SoundEvents.TOTEM_USE, SoundSource.PLAYERS,
                1.0f, 1.0f
        );
    }
}
