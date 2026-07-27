package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.class_system.rogue.AssassinateAbility;
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
 * Assassin's Mark - Activates Assassinate ability for Rogues
 * Manual backstab burst
 */
public class AssassinsMarkItem extends Item {
    public AssassinsMarkItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUUID());
        if (!"Rogue".equalsIgnoreCase(playerClass)) {
            player.sendOverlayMessage(
                    Component.literal("✦ Only Rogues can use Assassinate!")
                            .withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }

        if (RogueCooldownManager.isOnCooldown(player, RogueAbility.ASSASSINATE)) {
            RogueCooldownManager.sendCooldownMessage(player, RogueAbility.ASSASSINATE);
            return InteractionResult.FAIL;
        }

        boolean success = AssassinateAbility.activate(player);
        return success ? InteractionResult.SUCCESS : InteractionResult.FAIL;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}