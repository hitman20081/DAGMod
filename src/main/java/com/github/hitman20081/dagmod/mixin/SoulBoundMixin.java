package com.github.hitman20081.dagmod.mixin;

import com.github.hitman20081.dagmod.enchantment.CustomEnchantmentEffects;
import com.github.hitman20081.dagmod.enchantment.SoulBoundStorage;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Preserves items with the Soul Bound enchantment across death.
 * On death: removes soulbound items from inventory and stores them.
 * On respawn: items are returned via the AFTER_RESPAWN handler in DagMod.java.
 */
@Mixin(ServerPlayerEntity.class)
public class SoulBoundMixin {

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void saveSoulBoundItems(DamageSource damageSource, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        List<ItemStack> saved = new ArrayList<>();

        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (!stack.isEmpty() && CustomEnchantmentEffects.getEnchantmentLevel(stack, player.getEntityWorld(), "soul_bound") > 0) {
                saved.add(stack.copy());
                player.getInventory().setStack(i, ItemStack.EMPTY);
            }
        }

        if (!saved.isEmpty()) {
            SoulBoundStorage.store(player.getUuid(), saved);
        }
    }
}
