package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.class_system.rogue.PoisonStrikeAbility;
import com.github.hitman20081.dagmod.class_system.rogue.RogueAbility;
import com.github.hitman20081.dagmod.class_system.rogue.RogueCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

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
    public PoisonVialItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        // Check if player is a Rogue
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUuid());
        if (!"Rogue".equalsIgnoreCase(playerClass)) {
            player.sendMessage(
                    Text.literal("✦ Only Rogues can use Poison Strike!")
                            .formatted(Formatting.RED),
                    true
            );
            return ActionResult.FAIL;
        }

        // Check cooldown
        if (RogueCooldownManager.isOnCooldown(player, RogueAbility.POISON_STRIKE)) {
            RogueCooldownManager.sendCooldownMessage(player, RogueAbility.POISON_STRIKE);
            return ActionResult.FAIL;
        }

        // Activate Poison Strike
        boolean success = PoisonStrikeAbility.activate(player);

        if (success) {
            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}