
package com.github.hitman20081.dagmod.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;

public class ClassSelectionTomeItem extends Item {

    public ClassSelectionTomeItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        // Check if already has class
        String existingClass = ClassSelectionAltarBlock.getPlayerClass(player.getUuid());
        if (!existingClass.equals("none")) {
            player.sendMessage(Text.literal("You have already chosen your path.")
                    .formatted(Formatting.YELLOW), false);
            return ActionResult.FAIL;
        }

        // Display tome content
        displayTomeContent(player);

        // Give class tokens
        player.giveItemStack(new ItemStack(ModItems.WARRIOR_TOKEN));
        player.giveItemStack(new ItemStack(ModItems.MAGE_TOKEN));
        player.giveItemStack(new ItemStack(ModItems.ROGUE_TOKEN));

        // Play sound
        world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_BOOK_PAGE_TURN,
                SoundCategory.PLAYERS, 1.0f, 1.0f);

        // Consume tome
        stack.decrement(1);

        return ActionResult.SUCCESS;
    }

    private void displayTomeContent(PlayerEntity player) {
        player.sendMessage(Text.empty(), false);
        player.sendMessage(Text.literal("╔═══════════════════════════════════╗")
                .formatted(Formatting.GOLD), false);
        player.sendMessage(Text.literal("║     CLASS SELECTION TOME      ║")
                .formatted(Formatting.GOLD).formatted(Formatting.BOLD), false);
        player.sendMessage(Text.literal("╚═══════════════════════════════════╝")
                .formatted(Formatting.GOLD), false);
        player.sendMessage(Text.empty(), false);
        player.sendMessage(Text.literal("Long ago, three legendary heroes")
                .formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("defended these lands. Their power lives")
                .formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("on through those who choose their path...")
                .formatted(Formatting.GRAY), false);
        player.sendMessage(Text.empty(), false);

        // WARRIOR
        player.sendMessage(Text.literal("─────────────────────────────────────")
                .formatted(Formatting.GOLD), false);
        player.sendMessage(Text.literal("⚔ THE WARRIOR")
                .formatted(Formatting.RED).formatted(Formatting.BOLD), false);
        player.sendMessage(Text.literal("\"Strength and honor guide my blade\"")
                .formatted(Formatting.GRAY), false);
        player.sendMessage(Text.empty(), false);
        player.sendMessage(Text.literal("Masters of combat, Warriors stand at the")
                .formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("front lines, protecting allies with steel")
                .formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("and unwavering courage.")
                .formatted(Formatting.WHITE), false);
        player.sendMessage(Text.empty(), false);
        player.sendMessage(Text.literal("⚔ Class Benefits:")
                .formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.literal("  • +20% Melee Damage")
                .formatted(Formatting.RED), false);
        player.sendMessage(Text.literal("  • +2 Maximum Hearts")
                .formatted(Formatting.RED), false);
        player.sendMessage(Text.literal("  • Heavy Armor Proficiency")
                .formatted(Formatting.RED), false);
        player.sendMessage(Text.empty(), false);

        // MAGE
        player.sendMessage(Text.literal("─────────────────────────────────────")
                .formatted(Formatting.GOLD), false);
        player.sendMessage(Text.literal("✦ THE MAGE")
                .formatted(Formatting.AQUA).formatted(Formatting.BOLD), false);
        player.sendMessage(Text.literal("\"Knowledge is the truest power\"")
                .formatted(Formatting.GRAY), false);
        player.sendMessage(Text.empty(), false);
        player.sendMessage(Text.literal("Scholars of the arcane, Mages wield")
                .formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("devastating magical power to control")
                .formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("the battlefield from afar.")
                .formatted(Formatting.WHITE), false);
        player.sendMessage(Text.empty(), false);
        player.sendMessage(Text.literal("⚔ Class Benefits:")
                .formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.literal("  • +50% Potion Duration")
                .formatted(Formatting.AQUA), false);
        player.sendMessage(Text.literal("  • 100 Mana Pool")
                .formatted(Formatting.AQUA), false);
        player.sendMessage(Text.literal("  • Mana Regeneration")
                .formatted(Formatting.AQUA), false);
        player.sendMessage(Text.empty(), false);

        // ROGUE
        player.sendMessage(Text.literal("─────────────────────────────────────")
                .formatted(Formatting.GOLD), false);
        player.sendMessage(Text.literal("⚡ THE ROGUE")
                .formatted(Formatting.DARK_GREEN).formatted(Formatting.BOLD), false);
        player.sendMessage(Text.literal("\"Strike from the shadows, vanish in smoke\"")
                .formatted(Formatting.GRAY), false);
        player.sendMessage(Text.empty(), false);
        player.sendMessage(Text.literal("Masters of stealth and precision, Rogues")
                .formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("strike quickly and disappear before the")
                .formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("enemy can retaliate.")
                .formatted(Formatting.WHITE), false);
        player.sendMessage(Text.empty(), false);
        player.sendMessage(Text.literal("⚔ Class Benefits:")
                .formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.literal("  • +30% Movement Speed")
                .formatted(Formatting.DARK_GREEN), false);
        player.sendMessage(Text.literal("  • 25% Critical Hit Chance")
                .formatted(Formatting.DARK_GREEN), false);
        player.sendMessage(Text.literal("  • Backstab Bonus Damage")
                .formatted(Formatting.DARK_GREEN), false);
        player.sendMessage(Text.empty(), false);

        // INSTRUCTIONS
        player.sendMessage(Text.literal("─────────────────────────────────────")
                .formatted(Formatting.GOLD), false);
        player.sendMessage(Text.literal("HOW TO CHOOSE:")
                .formatted(Formatting.YELLOW).formatted(Formatting.BOLD), false);
        player.sendMessage(Text.empty(), false);
        player.sendMessage(Text.literal("You have received three Class Tokens.")
                .formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("Hold the token of your chosen class and")
                .formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.literal("right-click the Class Selection Altar.")
                .formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.empty(), false);
        player.sendMessage(Text.literal("⚠ WARNING: This choice is permanent!")
                .formatted(Formatting.RED).formatted(Formatting.BOLD), false);
        player.sendMessage(Text.empty(), false);
        player.sendMessage(Text.literal("╚═══════════════════════════════════╝")
                .formatted(Formatting.GOLD), false);
        player.sendMessage(Text.empty(), false);
    }
}