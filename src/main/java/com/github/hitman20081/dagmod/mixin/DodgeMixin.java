package com.github.hitman20081.dagmod.mixin;

import com.github.hitman20081.dagmod.event.DodgeHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Implements dodge mechanics for Phantom Dust (50%) and Perfect Dodge (100%).
 * Runs at HEAD of damage, before LastStandMixin.
 */
@Mixin(LivingEntity.class)
public class DodgeMixin {

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void onDamageHead(ServerLevel world, DamageSource source, float amount,
                              CallbackInfoReturnable<Boolean> cir) {
        if (!((Object) this instanceof ServerPlayer player)) return;

        if (DodgeHandler.tryDodge(player.getUUID(), world.getGameTime(), world.getRandom())) {
            world.sendParticles(
                    ParticleTypes.SMOKE,
                    player.getX(), player.getY() + 1.0, player.getZ(),
                    10, 0.3, 0.3, 0.3, 0.05
            );
            player.sendOverlayMessage(Component.literal("Dodged!").withStyle(ChatFormatting.GRAY));
            cir.setReturnValue(false);
        }
    }
}
