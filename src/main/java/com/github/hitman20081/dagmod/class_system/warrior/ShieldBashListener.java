package com.github.hitman20081.dagmod.class_system.warrior;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

/**
 * Listens for shield right-clicks to activate Shield Bash
 */
public class ShieldBashListener {

    /**
     * Register the shield bash callback
     */
    public static void register() {
        UseItemCallback.EVENT.register(ShieldBashListener::onUseItem);
    }

    private static InteractionResult onUseItem(Player player, Level world, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Only process shield items
        if (!(stack.getItem() instanceof ShieldItem)) {
            return InteractionResult.PASS;
        }

        // Only on server side
        if (world.isClientSide()) {
            return InteractionResult.PASS;
        }

        // Check if player is sneaking (sneak + right-click activates Shield Bash)
        if (!player.isShiftKeyDown()) {
            return InteractionResult.PASS; // Normal shield use
        }

        // Check if player is a Warrior
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUUID());
        if (!"warrior".equalsIgnoreCase(playerClass)) {
            return InteractionResult.PASS; // Not a warrior, allow normal shield use
        }

        // Check cooldown
        if (CooldownManager.isOnCooldown(player, WarriorAbility.SHIELD_BASH)) {
            CooldownManager.sendCooldownMessage(player, WarriorAbility.SHIELD_BASH);
            return InteractionResult.FAIL;
        }

        // Activate Shield Bash
        boolean success = ShieldBashAbility.activate(player);

        if (success) {
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }
}