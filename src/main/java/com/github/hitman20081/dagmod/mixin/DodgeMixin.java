package com.github.hitman20081.dagmod.mixin;

import com.github.hitman20081.dagmod.event.DodgeHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
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

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void onDamageHead(ServerWorld world, DamageSource source, float amount,
                              CallbackInfoReturnable<Boolean> cir) {
        if (!((Object) this instanceof ServerPlayerEntity player)) return;

        if (DodgeHandler.tryDodge(player.getUuid(), world.getTime(), world.getRandom())) {
            world.spawnParticles(
                    ParticleTypes.SMOKE,
                    player.getX(), player.getY() + 1.0, player.getZ(),
                    10, 0.3, 0.3, 0.3, 0.05
            );
            player.sendMessage(Text.literal("Dodged!").formatted(Formatting.GRAY), true);
            cir.setReturnValue(false);
        }
    }
}
