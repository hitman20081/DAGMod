package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.class_system.mage.ArcaneBarrierAbility;
import com.github.hitman20081.dagmod.class_system.mage.MageAbility;
import com.github.hitman20081.dagmod.class_system.mage.MageCooldownManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

/**
 * Barrier Charm - Activates the Arcane Barrier ability for Mages
 *
 * Effects:
 * - 10 absorption hearts
 * - Resistance II for 10 seconds
 * - Fire Resistance
 *
 * Cooldown: 60 seconds
 */
public class BarrierCharmItem extends Item {
    public BarrierCharmItem(Properties settings) {
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
                    Component.literal("✦ Only Mages can use Arcane Barrier!")
                            .withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }

        // Check cooldown
        if (MageCooldownManager.isOnCooldown(player, MageAbility.ARCANE_BARRIER)) {
            MageCooldownManager.sendCooldownMessage(player, MageAbility.ARCANE_BARRIER);
            return InteractionResult.FAIL;
        }

        // Activate Arcane Barrier
        boolean success = ArcaneBarrierAbility.activate(player);

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