package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.class_system.mage.MageAbility;
import com.github.hitman20081.dagmod.class_system.mage.MageCooldownManager;
import com.github.hitman20081.dagmod.class_system.mage.ManaBurstAbility;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

/**
 * Mana Catalyst - Activates the Mana Burst ability for Mages
 *
 * Effects:
 * - AoE explosion in 7 block radius
 * - 10 damage + knockback to all enemies
 *
 * Cooldown: 30 seconds
 */
public class ManaCatalystItem extends Item {
    public ManaCatalystItem(Properties settings) {
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
                    Component.literal("✦ Only Mages can use Mana Burst!")
                            .withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }

        // Check cooldown
        if (MageCooldownManager.isOnCooldown(player, MageAbility.MANA_BURST)) {
            MageCooldownManager.sendCooldownMessage(player, MageAbility.MANA_BURST);
            return InteractionResult.FAIL;
        }

        // Activate Mana Burst
        boolean success = ManaBurstAbility.activate(player);

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