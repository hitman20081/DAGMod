package com.github.hitman20081.dagmod.class_system.warrior;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

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

    private static ActionResult onUseItem(PlayerEntity player, World world, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        // Only process shield items
        if (!(stack.getItem() instanceof ShieldItem)) {
            return ActionResult.PASS;
        }

        // Only on server side
        if (world.isClient) {
            return ActionResult.PASS;
        }

        // Check if player is sneaking (sneak + right-click activates Shield Bash)
        if (!player.isSneaking()) {
            return ActionResult.PASS; // Normal shield use
        }

        // Check if player is a Warrior
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUuid());
        if (!"warrior".equalsIgnoreCase(playerClass)) {
            return ActionResult.PASS; // Not a warrior, allow normal shield use
        }

        // Check cooldown
        if (CooldownManager.isOnCooldown(player, WarriorAbility.SHIELD_BASH)) {
            CooldownManager.sendCooldownMessage(player, WarriorAbility.SHIELD_BASH);
            return ActionResult.FAIL;
        }

        // Activate Shield Bash
        boolean success = ShieldBashAbility.activate(player);

        if (success) {
            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;
    }
}