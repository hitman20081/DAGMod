package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.class_system.warrior.BattleShoutAbility;
import com.github.hitman20081.dagmod.class_system.warrior.CooldownManager;
import com.github.hitman20081.dagmod.class_system.warrior.WarriorAbility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

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
    public BattleStandardItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        // Check if player is a Warrior
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUuid());
        if (!"warrior".equalsIgnoreCase(playerClass)) {
            player.sendMessage(
                    Text.literal("⚔ Only Warriors can use Battle Shout!")
                            .formatted(Formatting.RED),
                    true
            );
            return ActionResult.FAIL;
        }

        // Check cooldown
        if (CooldownManager.isOnCooldown(player, WarriorAbility.BATTLE_SHOUT)) {
            CooldownManager.sendCooldownMessage(player, WarriorAbility.BATTLE_SHOUT);
            return ActionResult.FAIL;
        }

        // Activate Battle Shout
        boolean success = BattleShoutAbility.activate(player);

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