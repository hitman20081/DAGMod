package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.class_system.rogue.BlinkStrikeAbility;
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
 * Void Blade - Activates Blink Strike ability for Rogues
 * Teleport behind nearest enemy
 */
public class VoidBladeItem extends Item {
    public VoidBladeItem(Settings settings) {
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
                    Text.literal("✦ Only Rogues can use Blink Strike!")
                            .formatted(Formatting.RED),
                    true
            );
            return ActionResult.FAIL;
        }

        if (RogueCooldownManager.isOnCooldown(player, RogueAbility.BLINK_STRIKE)) {
            RogueCooldownManager.sendCooldownMessage(player, RogueAbility.BLINK_STRIKE);
            return ActionResult.FAIL;
        }

        boolean success = BlinkStrikeAbility.activate(player);
        return success ? ActionResult.SUCCESS : ActionResult.FAIL;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}