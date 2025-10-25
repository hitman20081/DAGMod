package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.class_system.warrior.CooldownManager;
import com.github.hitman20081.dagmod.class_system.warrior.WarriorAbility;
import com.github.hitman20081.dagmod.class_system.warrior.WhirlwindAbility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * Whirlwind Axe - Activates the Whirlwind ability for Warriors
 *
 * Effects:
 * - AoE spin attack in 5 block radius
 * - 8 damage (4 hearts) to all enemies
 * - Knocks back enemies
 *
 * Cooldown: 30 seconds
 */
public class WhirlwindAxeItem extends Item {
    public WhirlwindAxeItem(Settings settings) {
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
                    Text.literal("⚔ Only Warriors can use Whirlwind!")
                            .formatted(Formatting.RED),
                    true
            );
            return ActionResult.FAIL;
        }

        // Check cooldown
        if (CooldownManager.isOnCooldown(player, WarriorAbility.WHIRLWIND)) {
            CooldownManager.sendCooldownMessage(player, WarriorAbility.WHIRLWIND);
            return ActionResult.FAIL;
        }

        // Activate Whirlwind
        boolean success = WhirlwindAbility.activate(player);

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