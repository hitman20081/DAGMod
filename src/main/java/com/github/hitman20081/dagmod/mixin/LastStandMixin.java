package com.github.hitman20081.dagmod.mixin;

import com.github.hitman20081.dagmod.event.LastStandHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.network.packet.s2c.play.PositionFlag;

import java.util.Set;

/**
 * Implements the Last Stand Powder mechanic: prevents one lethal hit and heals to 50% HP.
 * For void damage, the player is also teleported to the nearest solid block surface.
 */
@Mixin(LivingEntity.class)
public class LastStandMixin {

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void onDamageHead(ServerWorld world, DamageSource source, float amount,
                              CallbackInfoReturnable<Boolean> cir) {
        if (!((Object) this instanceof ServerPlayerEntity player)) return;
        if (player.getHealth() - amount > 0) return;

        if (!LastStandHandler.consume(player.getUuid())) return;

        cir.setReturnValue(false);
        player.setHealth(player.getMaxHealth() * 0.5f);

        // Void damage: teleport to nearest solid block surface at same X/Z
        if (source.isOf(DamageTypes.OUT_OF_WORLD)) {
            int topY = world.getTopY(Heightmap.Type.MOTION_BLOCKING, player.getBlockX(), player.getBlockZ());
            // Guard against empty/flat dimensions where topY could be at or below spawn floor
            double safeY = Math.max(topY, world.getBottomY() + 5);
            player.teleport(world, player.getX(), safeY, player.getZ(), Set.<PositionFlag>of(), player.getYaw(), player.getPitch(), false);
            player.sendMessage(
                    Text.literal("✝ Last Stand! Pulled back from the void at 50% HP!")
                            .formatted(Formatting.YELLOW),
                    false
            );
        } else {
            player.sendMessage(
                    Text.literal("✝ Last Stand activated! Revived at 50% HP!")
                            .formatted(Formatting.YELLOW),
                    false
            );
        }

        world.spawnParticles(
                ParticleTypes.TOTEM_OF_UNDYING,
                player.getX(), player.getY() + 1.0, player.getZ(),
                50, 0.5, 1.0, 0.5, 0.2
        );
        world.playSound(
                null, player.getBlockPos(),
                SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS,
                1.0f, 1.0f
        );
    }
}
