
package com.github.hitman20081.dagmod.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;

public class ClassSelectionTomeItem extends Item {

    public ClassSelectionTomeItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        // Check if already has class
        String existingClass = ClassSelectionAltarBlock.getPlayerClass(player.getUUID());
        if (!existingClass.equals("none")) {
            player.sendSystemMessage(Component.literal("You have already chosen your path.")
                    .withStyle(ChatFormatting.YELLOW));
            return InteractionResult.FAIL;
        }

        // Display tome content
        displayTomeContent(player);

        // Give class tokens
        player.addItem(new ItemStack(ModItems.WARRIOR_TOKEN));
        player.addItem(new ItemStack(ModItems.MAGE_TOKEN));
        player.addItem(new ItemStack(ModItems.ROGUE_TOKEN));

        // Play sound
        world.playSound(null, player.blockPosition(), SoundEvents.BOOK_PAGE_TURN,
                SoundSource.PLAYERS, 1.0f, 1.0f);

        // Consume tome
        stack.shrink(1);

        return InteractionResult.SUCCESS;
    }

    private void displayTomeContent(Player player) {
        player.sendSystemMessage(Component.empty());
        player.sendSystemMessage(Component.literal("╔═══════════════════════════════════╗")
                .withStyle(ChatFormatting.GOLD));
        player.sendSystemMessage(Component.literal("║     CLASS SELECTION TOME      ║")
                .withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.BOLD));
        player.sendSystemMessage(Component.literal("╚═══════════════════════════════════╝")
                .withStyle(ChatFormatting.GOLD));
        player.sendSystemMessage(Component.empty());
        player.sendSystemMessage(Component.literal("Long ago, three legendary heroes")
                .withStyle(ChatFormatting.GRAY));
        player.sendSystemMessage(Component.literal("defended these lands. Their power lives")
                .withStyle(ChatFormatting.GRAY));
        player.sendSystemMessage(Component.literal("on through those who choose their path...")
                .withStyle(ChatFormatting.GRAY));
        player.sendSystemMessage(Component.empty());

        // WARRIOR
        player.sendSystemMessage(Component.literal("─────────────────────────────────────")
                .withStyle(ChatFormatting.GOLD));
        player.sendSystemMessage(Component.literal("⚔ THE WARRIOR")
                .withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
        player.sendSystemMessage(Component.literal("\"Strength and honor guide my blade\"")
                .withStyle(ChatFormatting.GRAY));
        player.sendSystemMessage(Component.empty());
        player.sendSystemMessage(Component.literal("Masters of combat, Warriors stand at the")
                .withStyle(ChatFormatting.WHITE));
        player.sendSystemMessage(Component.literal("front lines, protecting allies with steel")
                .withStyle(ChatFormatting.WHITE));
        player.sendSystemMessage(Component.literal("and unwavering courage.")
                .withStyle(ChatFormatting.WHITE));
        player.sendSystemMessage(Component.empty());
        player.sendSystemMessage(Component.literal("⚔ Class Benefits:")
                .withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(Component.literal("  • +20% Melee Damage")
                .withStyle(ChatFormatting.RED));
        player.sendSystemMessage(Component.literal("  • +2 Maximum Hearts")
                .withStyle(ChatFormatting.RED));
        player.sendSystemMessage(Component.literal("  • Heavy Armor Proficiency")
                .withStyle(ChatFormatting.RED));
        player.sendSystemMessage(Component.empty());

        // MAGE
        player.sendSystemMessage(Component.literal("─────────────────────────────────────")
                .withStyle(ChatFormatting.GOLD));
        player.sendSystemMessage(Component.literal("✦ THE MAGE")
                .withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.BOLD));
        player.sendSystemMessage(Component.literal("\"Knowledge is the truest power\"")
                .withStyle(ChatFormatting.GRAY));
        player.sendSystemMessage(Component.empty());
        player.sendSystemMessage(Component.literal("Scholars of the arcane, Mages wield")
                .withStyle(ChatFormatting.WHITE));
        player.sendSystemMessage(Component.literal("devastating magical power to control")
                .withStyle(ChatFormatting.WHITE));
        player.sendSystemMessage(Component.literal("the battlefield from afar.")
                .withStyle(ChatFormatting.WHITE));
        player.sendSystemMessage(Component.empty());
        player.sendSystemMessage(Component.literal("⚔ Class Benefits:")
                .withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(Component.literal("  • +50% Potion Duration")
                .withStyle(ChatFormatting.AQUA));
        player.sendSystemMessage(Component.literal("  • 100 Mana Pool")
                .withStyle(ChatFormatting.AQUA));
        player.sendSystemMessage(Component.literal("  • Mana Regeneration")
                .withStyle(ChatFormatting.AQUA));
        player.sendSystemMessage(Component.empty());

        // ROGUE
        player.sendSystemMessage(Component.literal("─────────────────────────────────────")
                .withStyle(ChatFormatting.GOLD));
        player.sendSystemMessage(Component.literal("⚡ THE ROGUE")
                .withStyle(ChatFormatting.DARK_GREEN).withStyle(ChatFormatting.BOLD));
        player.sendSystemMessage(Component.literal("\"Strike from the shadows, vanish in smoke\"")
                .withStyle(ChatFormatting.GRAY));
        player.sendSystemMessage(Component.empty());
        player.sendSystemMessage(Component.literal("Masters of stealth and precision, Rogues")
                .withStyle(ChatFormatting.WHITE));
        player.sendSystemMessage(Component.literal("strike quickly and disappear before the")
                .withStyle(ChatFormatting.WHITE));
        player.sendSystemMessage(Component.literal("enemy can retaliate.")
                .withStyle(ChatFormatting.WHITE));
        player.sendSystemMessage(Component.empty());
        player.sendSystemMessage(Component.literal("⚔ Class Benefits:")
                .withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(Component.literal("  • +30% Movement Speed")
                .withStyle(ChatFormatting.DARK_GREEN));
        player.sendSystemMessage(Component.literal("  • 25% Critical Hit Chance")
                .withStyle(ChatFormatting.DARK_GREEN));
        player.sendSystemMessage(Component.literal("  • Backstab Bonus Damage")
                .withStyle(ChatFormatting.DARK_GREEN));
        player.sendSystemMessage(Component.empty());

        // INSTRUCTIONS
        player.sendSystemMessage(Component.literal("─────────────────────────────────────")
                .withStyle(ChatFormatting.GOLD));
        player.sendSystemMessage(Component.literal("HOW TO CHOOSE:")
                .withStyle(ChatFormatting.YELLOW).withStyle(ChatFormatting.BOLD));
        player.sendSystemMessage(Component.empty());
        player.sendSystemMessage(Component.literal("You have received three Class Tokens.")
                .withStyle(ChatFormatting.WHITE));
        player.sendSystemMessage(Component.literal("Hold the token of your chosen class and")
                .withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(Component.literal("right-click the Class Selection Altar.")
                .withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(Component.empty());
        player.sendSystemMessage(Component.literal("⚠ WARNING: This choice is permanent!")
                .withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
        player.sendSystemMessage(Component.empty());
        player.sendSystemMessage(Component.literal("╚═══════════════════════════════════╝")
                .withStyle(ChatFormatting.GOLD));
        player.sendSystemMessage(Component.empty());
    }
}