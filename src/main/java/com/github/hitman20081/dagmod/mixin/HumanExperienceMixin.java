package com.github.hitman20081.dagmod.mixin;

import com.github.hitman20081.dagmod.race_system.HumanBonusHandler;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class HumanExperienceMixin {

    @Shadow
    public abstract void addExperience(int experience);

    @Inject(
            method = "addExperience",
            at = @At("HEAD"),
            cancellable = true
    )
    private void modifyHumanExperience(int experience, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;

        if (player instanceof ServerPlayerEntity serverPlayer) {
            int modifiedXP = HumanBonusHandler.modifyExperienceGain(serverPlayer, experience);

            // If XP was modified, cancel original and add modified amount
            if (modifiedXP != experience) {
                ci.cancel();
                // We need to manually add the XP to avoid infinite loop
                serverPlayer.experienceProgress += (float)modifiedXP / (float)serverPlayer.getNextLevelExperience();

                while (serverPlayer.experienceProgress >= 1.0F) {
                    serverPlayer.experienceProgress = (serverPlayer.experienceProgress - 1.0F) * (float)serverPlayer.getNextLevelExperience();
                    serverPlayer.addExperienceLevels(1);
                    serverPlayer.experienceProgress /= (float)serverPlayer.getNextLevelExperience();
                }
            }
        }
    }
}