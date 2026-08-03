package com.github.hitman20081.dagmod.mixin;

import com.github.hitman20081.dagmod.enchantment.RaceClassEnchantmentGate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Strips any race/class-gated enchantment (see RaceClassEnchantmentGate) that the player
 * taking the anvil result doesn't qualify for, before it reaches their inventory.
 */
@Mixin(AnvilMenu.class)
public class AnvilRaceClassGateMixin {

    @Inject(method = "onTake", at = @At("HEAD"))
    private void onTake(Player player, ItemStack stack, CallbackInfo ci) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        RaceClassEnchantmentGate.stripDisallowed(serverPlayer, stack);
    }
}
