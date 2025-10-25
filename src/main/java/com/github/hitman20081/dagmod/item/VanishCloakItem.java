package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.class_system.rogue.RogueAbility;
import com.github.hitman20081.dagmod.class_system.rogue.RogueCooldownManager;
import com.github.hitman20081.dagmod.class_system.rogue.VanishAbility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * Vanish Cloak - Activates Vanish ability for Rogues
 * Invisibility escape
 */
public class VanishCloakItem extends Item {
    public VanishCloakItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUuid());
        if (!"Rogue".equalsIgnoreCase(playerClass)) {
            player.sendMessage(
                    Text.literal("✦ Only Rogues can use Vanish!")
                            .formatted(Formatting.RED),
                    true
            );
            return ActionResult.FAIL;
        }

        if (RogueCooldownManager.isOnCooldown(player, RogueAbility.VANISH)) {
            RogueCooldownManager.sendCooldownMessage(player, RogueAbility.VANISH);
            return ActionResult.FAIL;
        }

        boolean success = VanishAbility.activate(player);
        return success ? ActionResult.SUCCESS : ActionResult.FAIL;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}