package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.class_system.mage.MageAbility;
import com.github.hitman20081.dagmod.class_system.mage.MageCooldownManager;
import com.github.hitman20081.dagmod.class_system.mage.ManaBurstAbility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

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
    public ManaCatalystItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        // Check if player is a Mage
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUuid());
        if (!"Mage".equalsIgnoreCase(playerClass)) {
            player.sendMessage(
                    Text.literal("✦ Only Mages can use Mana Burst!")
                            .formatted(Formatting.RED),
                    true
            );
            return ActionResult.FAIL;
        }

        // Check cooldown
        if (MageCooldownManager.isOnCooldown(player, MageAbility.MANA_BURST)) {
            MageCooldownManager.sendCooldownMessage(player, MageAbility.MANA_BURST);
            return ActionResult.FAIL;
        }

        // Activate Mana Burst
        boolean success = ManaBurstAbility.activate(player);

        if (success) {
            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true; // Enchanted appearance
    }
}