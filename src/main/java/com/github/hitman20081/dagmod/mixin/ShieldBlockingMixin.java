package com.github.hitman20081.dagmod.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to allow custom shields to block damage.
 * Vanilla only checks for Items.SHIELD in some places, this extends it to any ShieldItem.
 */
@Mixin(LivingEntity.class)
public class ShieldBlockingMixin {

    /**
     * Modifies the shield blocking check to accept any ShieldItem, not just vanilla shield.
     */
    @Inject(method = "isBlocking", at = @At("HEAD"), cancellable = true)
    private void dagmod$allowCustomShieldBlocking(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity.isUsingItem()) {
            ItemStack activeStack = entity.getActiveItem();
            if (!activeStack.isEmpty() && activeStack.getItem() instanceof ShieldItem) {
                cir.setReturnValue(true);
            }
        }
    }
}
