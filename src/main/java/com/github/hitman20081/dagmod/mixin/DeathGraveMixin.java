package com.github.hitman20081.dagmod.mixin;

import com.github.hitman20081.dagmod.grave.GraveManager;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.rule.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Captures remaining inventory items on death (after SoulBoundMixin has removed soulbound items)
 * and stores them in a grave. Clears inventory so vanilla dropAll() drops nothing.
 */
@Mixin(value = ServerPlayerEntity.class, priority = 1100)
public class DeathGraveMixin {

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void captureInventoryForGrave(DamageSource damageSource, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        ServerWorld world = (ServerWorld) player.getEntityWorld();

        // Skip if keepInventory is enabled
        if (world.getGameRules().getValue(GameRules.KEEP_INVENTORY)) {
            return;
        }

        Map<Integer, ItemStack> items = new HashMap<>();

        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (!stack.isEmpty()) {
                items.put(i, stack.copy());
                player.getInventory().setStack(i, ItemStack.EMPTY);
            }
        }

        if (!items.isEmpty()) {
            GraveManager.getInstance().createGrave(player, items);
        }
    }
}
