package com.github.hitman20081.dagmod.mixin;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.enchantment.CustomEnchantmentEffects;
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
 *
 * Priority 900 ensures this runs AFTER SoulBoundMixin (default 1000) at HEAD.
 * In Sponge Mixin, higher priority @Mixin is applied later, so its HEAD injection
 * runs first at runtime. We use a LOWER priority so SoulBound runs first.
 */
@Mixin(value = ServerPlayerEntity.class, priority = 900)
public class DeathGraveMixin {

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void captureInventoryForGrave(DamageSource damageSource, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        ServerWorld world = (ServerWorld) player.getEntityWorld();

        // Skip if keepInventory is enabled
        boolean keepInventory = world.getGameRules().getValue(GameRules.KEEP_INVENTORY);
        if (keepInventory) {
            DagMod.LOGGER.info("Grave system: keepInventory is ON for {}, skipping grave creation", player.getName().getString());
            return;
        }

        DagMod.LOGGER.info("Grave system: capturing inventory for {} ({} slots)", player.getName().getString(), player.getInventory().size());

        Map<Integer, ItemStack> items = new HashMap<>();

        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (!stack.isEmpty()) {
                // Skip soulbound items (safety check — SoulBoundMixin should have already removed them)
                if (CustomEnchantmentEffects.getEnchantmentLevel(stack, player.getEntityWorld(), "soul_bound") > 0) {
                    continue;
                }
                items.put(i, stack.copy());
                player.getInventory().setStack(i, ItemStack.EMPTY);
            }
        }

        DagMod.LOGGER.info("Grave system: captured {} items for {}", items.size(), player.getName().getString());

        if (!items.isEmpty()) {
            GraveManager.getInstance().createGrave(player, items);
        }
    }
}
