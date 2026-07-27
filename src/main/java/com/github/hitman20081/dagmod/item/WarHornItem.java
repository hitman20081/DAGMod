package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.class_system.warrior.CooldownManager;
import com.github.hitman20081.dagmod.class_system.warrior.WarCryAbility;
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
 * War Horn - Activates the War Cry ability for Warriors
 */
public class WarHornItem extends Item {
    public WarHornItem(Properties settings) {
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
                    Component.literal("⚔ Only Warriors can use War Cry!")
                            .withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }

        // Check cooldown
        if (CooldownManager.isOnCooldown(player, WarriorAbility.WAR_CRY)) {
            CooldownManager.sendCooldownMessage(player, WarriorAbility.WAR_CRY);
            return InteractionResult.FAIL;
        }

        // Activate War Cry
        boolean success = WarCryAbility.activate(player);

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