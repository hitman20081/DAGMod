package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.class_system.mage.ArcaneMissilesAbility;
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
 * Arcane Orb - Activates the Arcane Missiles ability for Mages
 *
 * Effects:
 * - Fires 5 homing arcane missiles
 * - Each deals 3 damage (15 total)
 * - Auto-targets nearest enemies
 *
 * Cooldown: 20 seconds
 */
public class ArcaneOrbItem extends Item {
    public ArcaneOrbItem(Settings settings) {
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
                    Text.literal("✦ Only Mages can use Arcane Missiles!")
                            .formatted(Formatting.RED),
                    true
            );
            return ActionResult.FAIL;
        }

        // Check cooldown
        if (MageCooldownManager.isOnCooldown(player, MageAbility.ARCANE_MISSILES)) {
            MageCooldownManager.sendCooldownMessage(player, MageAbility.ARCANE_MISSILES);
            return ActionResult.FAIL;
        }

        // Activate Arcane Missiles
        boolean success = ArcaneMissilesAbility.activate(player);

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