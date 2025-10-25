package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.class_system.mage.MageAbility;
import com.github.hitman20081.dagmod.class_system.mage.MageCooldownManager;
import com.github.hitman20081.dagmod.class_system.mage.TimeWarpAbility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * Temporal Crystal - Activates the Time Warp ability for Mages
 *
 * Effects:
 * - Slows all enemies in 10 block radius
 * - Slowness IV + Weakness II for 8 seconds
 *
 * Cooldown: 45 seconds
 */
public class TemporalCrystalItem extends Item {
    public TemporalCrystalItem(Settings settings) {
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
                    Text.literal("✦ Only Mages can use Time Warp!")
                            .formatted(Formatting.RED),
                    true
            );
            return ActionResult.FAIL;
        }

        // Check cooldown
        if (MageCooldownManager.isOnCooldown(player, MageAbility.TIME_WARP)) {
            MageCooldownManager.sendCooldownMessage(player, MageAbility.TIME_WARP);
            return ActionResult.FAIL;
        }

        // Activate Time Warp
        boolean success = TimeWarpAbility.activate(player);

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