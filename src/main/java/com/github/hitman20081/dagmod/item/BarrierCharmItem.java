package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.class_system.mage.ArcaneBarrierAbility;
import com.github.hitman20081.dagmod.class_system.mage.MageAbility;
import com.github.hitman20081.dagmod.class_system.mage.MageCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

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
    public BarrierCharmItem(Settings settings) {
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
                    Text.literal("✦ Only Mages can use Arcane Barrier!")
                            .formatted(Formatting.RED),
                    true
            );
            return ActionResult.FAIL;
        }

        // Check cooldown
        if (MageCooldownManager.isOnCooldown(player, MageAbility.ARCANE_BARRIER)) {
            MageCooldownManager.sendCooldownMessage(player, MageAbility.ARCANE_BARRIER);
            return ActionResult.FAIL;
        }

        // Activate Arcane Barrier
        boolean success = ArcaneBarrierAbility.activate(player);

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