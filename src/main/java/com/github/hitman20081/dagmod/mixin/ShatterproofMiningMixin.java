package com.github.hitman20081.dagmod.mixin;

import com.github.hitman20081.dagmod.enchantment.ShatterproofHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Reduces mining speed by 99% when the player's held tool is shatterproof-broken
 * (at 1 durability, kept alive by the Shatterproof enchantment).
 */
@Mixin(PlayerEntity.class)
public class ShatterproofMiningMixin {

    @Inject(method = "getBlockBreakingSpeed", at = @At("RETURN"), cancellable = true)
    private void dagmod$shatterproofToolDebuff(BlockState state, CallbackInfoReturnable<Float> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        ItemStack tool = player.getMainHandStack();
        if (ShatterproofHelper.isShatterproofBroken(tool)) {
            cir.setReturnValue(cir.getReturnValue() * 0.01f);
        }
    }
}
