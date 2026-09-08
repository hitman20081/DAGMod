package com.github.hitman20081.dagmod.mixin;

import com.github.hitman20081.dagmod.economy.CoinPouchItem;
import com.github.hitman20081.dagmod.enchantment.CustomEnchantmentEffects;
import com.github.hitman20081.dagmod.enchantment.SoulBoundStorage;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Preserves items with the Soul Bound enchantment -- and the Coin Pouch, unconditionally, by item
 * identity rather than enchantment (it's a utility item, not gear; it shouldn't need an
 * enchantment glint to explain why it survives death) -- across death.
 * On death: removes soulbound items from inventory and stores them with slot indices.
 * On respawn: items are returned to original slots via the AFTER_RESPAWN handler in DagMod.java.
 */
@Mixin(ServerPlayer.class)
public class SoulBoundMixin {

    @Inject(method = "die", at = @At("HEAD"))
    private void saveSoulBoundItems(DamageSource damageSource, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        List<ItemStack> saved = new ArrayList<>();
        List<Integer> slots = new ArrayList<>();

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && (stack.getItem() instanceof CoinPouchItem
                    || CustomEnchantmentEffects.getEnchantmentLevel(stack, player.level(), "soul_bound") > 0)) {
                saved.add(stack.copy());
                slots.add(i);
                player.getInventory().setItem(i, ItemStack.EMPTY);
            }
        }

        if (!saved.isEmpty()) {
            SoulBoundStorage.store(player.getUUID(), saved, slots);
        }
    }
}
