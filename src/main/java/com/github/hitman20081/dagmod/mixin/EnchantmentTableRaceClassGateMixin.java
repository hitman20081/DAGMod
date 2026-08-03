package com.github.hitman20081.dagmod.mixin;

import com.github.hitman20081.dagmod.enchantment.RaceClassEnchantmentGate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Strips any race/class-gated enchantment (see RaceClassEnchantmentGate) that the enchanting
 * player doesn't qualify for, right after vanilla applies the roll to the output item.
 */
@Mixin(EnchantmentMenu.class)
public class EnchantmentTableRaceClassGateMixin {

    @Shadow @Final private Container enchantSlots;

    @Inject(method = "clickMenuButton", at = @At("RETURN"))
    private void onClickMenuButton(Player player, int id, CallbackInfoReturnable<Boolean> cir) {
        boolean success = cir.getReturnValue();
        if (!success) return;
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        ItemStack result = this.enchantSlots.getItem(0);
        RaceClassEnchantmentGate.stripDisallowed(serverPlayer, result);
    }
}
