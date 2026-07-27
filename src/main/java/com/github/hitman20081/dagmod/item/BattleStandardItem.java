package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.class_system.warrior.BattleShoutAbility;
import com.github.hitman20081.dagmod.class_system.warrior.CooldownManager;
import com.github.hitman20081.dagmod.class_system.warrior.WarriorAbility;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

/**
 * Battle Standard - Activates the Battle Shout ability for Warriors
 *
 * Effects:
 * - Heal 6 hearts immediately
 * - +20% attack damage for 12 seconds
 * - +10% movement speed for 12 seconds
 * - Removes negative effects
 *
 * Cooldown: 45 seconds
 */
public class BattleStandardItem extends Item {
    public BattleStandardItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        // Check if player is a Warrior
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUUID());
        if (!"warrior".equalsIgnoreCase(playerClass)) {
            player.sendOverlayMessage(
                    Component.literal("⚔ Only Warriors can use Battle Shout!")
                            .withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }

        // Check cooldown
        if (CooldownManager.isOnCooldown(player, WarriorAbility.BATTLE_SHOUT)) {
            CooldownManager.sendCooldownMessage(player, WarriorAbility.BATTLE_SHOUT);
            return InteractionResult.FAIL;
        }

        // Activate Battle Shout
        boolean success = BattleShoutAbility.activate(player);

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