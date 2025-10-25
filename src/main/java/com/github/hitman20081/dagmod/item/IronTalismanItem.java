package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.class_system.warrior.CooldownManager;
import com.github.hitman20081.dagmod.class_system.warrior.IronSkinAbility;
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
 * Iron Talisman - Activates the Iron Skin ability for Warriors
 *
 * Effects:
 * - +8 absorption hearts
 * - 60% damage reduction for 15 seconds
 * - Fire resistance
 * - Movement slowness (trade-off)
 *
 * Cooldown: 120 seconds (2 minutes)
 */
public class IronTalismanItem extends Item {
    public IronTalismanItem(Settings settings) {
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
                    Text.literal("⚔ Only Warriors can use Iron Skin!")
                            .formatted(Formatting.RED),
                    true
            );
            return ActionResult.FAIL;
        }

        // Check cooldown
        if (CooldownManager.isOnCooldown(player, WarriorAbility.IRON_SKIN)) {
            CooldownManager.sendCooldownMessage(player, WarriorAbility.IRON_SKIN);
            return ActionResult.FAIL;
        }

        // Activate Iron Skin
        boolean success = IronSkinAbility.activate(player);

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