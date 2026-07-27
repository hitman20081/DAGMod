package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.class_system.rogue.PoisonStrikeAbility;
import com.github.hitman20081.dagmod.class_system.rogue.RogueAbility;
import com.github.hitman20081.dagmod.class_system.rogue.RogueCooldownManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

/**
 * Poison Vial - Activates Poison Strike ability for Rogues
 *
 * Effects:
 * - Apply deadly poison to target
 * - DoT damage over time
 *
 * Cooldown: 20 seconds
 */
public class PoisonVialItem extends Item {
    public PoisonVialItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        // Check if player is a Rogue
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUUID());
        if (!"Rogue".equalsIgnoreCase(playerClass)) {
            player.sendOverlayMessage(
                    Component.literal("✦ Only Rogues can use Poison Strike!")
                            .withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }

        // Check cooldown
        if (RogueCooldownManager.isOnCooldown(player, RogueAbility.POISON_STRIKE)) {
            RogueCooldownManager.sendCooldownMessage(player, RogueAbility.POISON_STRIKE);
            return InteractionResult.FAIL;
        }

        // Activate Poison Strike
        boolean success = PoisonStrikeAbility.activate(player);

        if (success) {
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}