package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.class_system.rogue.RogueAbility;
import com.github.hitman20081.dagmod.class_system.rogue.RogueCooldownManager;
import com.github.hitman20081.dagmod.class_system.rogue.VanishAbility;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

/**
 * Vanish Cloak - Activates Vanish ability for Rogues
 * Invisibility escape
 */
public class VanishCloakItem extends Item {
    public VanishCloakItem(Properties settings) {
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
                    Component.literal("✦ Only Rogues can use Vanish!")
                            .withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }

        if (RogueCooldownManager.isOnCooldown(player, RogueAbility.VANISH)) {
            RogueCooldownManager.sendCooldownMessage(player, RogueAbility.VANISH);
            return InteractionResult.FAIL;
        }

        boolean success = VanishAbility.activate(player);
        return success ? InteractionResult.SUCCESS : InteractionResult.FAIL;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}