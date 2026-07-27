package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.class_system.mage.MageAbility;
import com.github.hitman20081.dagmod.class_system.mage.MageCooldownManager;
import com.github.hitman20081.dagmod.class_system.mage.TimeWarpAbility;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

/**
 * Temporal Crystal - Activates the Time Warp ability for Mages
 *
 * Effects:
 * - Slows all enemies in 10 block radius
 * - Slowness IV + Weakness II for 8 seconds
 *
 * Cooldown: 45 seconds
 */
public class TemporalCrystalItem extends Item {
    public TemporalCrystalItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        // Check if player is a Mage
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUUID());
        if (!"Mage".equalsIgnoreCase(playerClass)) {
            player.sendOverlayMessage(
                    Component.literal("✦ Only Mages can use Time Warp!")
                            .withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }

        // Check cooldown
        if (MageCooldownManager.isOnCooldown(player, MageAbility.TIME_WARP)) {
            MageCooldownManager.sendCooldownMessage(player, MageAbility.TIME_WARP);
            return InteractionResult.FAIL;
        }

        // Activate Time Warp
        boolean success = TimeWarpAbility.activate(player);

        if (success) {
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true; // Enchanted appearance
    }
}