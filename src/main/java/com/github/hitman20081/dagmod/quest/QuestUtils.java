package com.github.hitman20081.dagmod.quest;

import com.github.hitman20081.dagmod.item.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public class QuestUtils {

    public static void giveQuestBookForTier(ServerPlayer player, QuestData.QuestBookTier tier) {
        ItemStack newBook = switch (tier) {
            case NOVICE -> new ItemStack(ModItems.NOVICE_QUEST_BOOK);
            case APPRENTICE -> new ItemStack(ModItems.APPRENTICE_QUEST_BOOK);
            case EXPERT -> new ItemStack(ModItems.EXPERT_QUEST_BOOK);
            case MASTER -> new ItemStack(ModItems.MASTER_QUEST_TOME);
        };

        player.addItem(newBook);
        player.sendOverlayMessage(Component.literal("You received: " + tier.getDisplayName() + "!")
                .withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.BOLD));
    }

    /**
     * Remove the old tier's quest book from inventory and give the new one.
     * Called on chain-based upgrades so the player doesn't accumulate old books.
     */
    public static void swapQuestBook(ServerPlayer player, QuestData.QuestBookTier oldTier, QuestData.QuestBookTier newTier) {
        // Remove old book
        Item oldBook = switch (oldTier) {
            case NOVICE -> ModItems.NOVICE_QUEST_BOOK;
            case APPRENTICE -> ModItems.APPRENTICE_QUEST_BOOK;
            case EXPERT -> ModItems.EXPERT_QUEST_BOOK;
            case MASTER -> ModItems.MASTER_QUEST_TOME;
        };
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.getItem() == oldBook) {
                player.getInventory().removeItem(i, 1);
                break;
            }
        }
        player.getInventory().setChanged();

        // Give new book
        giveQuestBookForTier(player, newTier);
    }

    public static ItemStack createWelcomeBook() {
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);

        java.util.List<net.minecraft.server.network.Filterable<Component>> pages = new java.util.ArrayList<>();

        // Page 1: Welcome
        pages.add(net.minecraft.server.network.Filterable.passThrough(
                Component.literal("§l§6Welcome to DAGMod!§r\n\n")
                        .append(Component.literal("This guide will help you get started on your adventure.\n\n"))
                        .append(Component.literal("§nGetting Started:§r\n"))
                        .append(Component.literal("1. Find the Hall of Champions\n"))
                        .append(Component.literal("2. Choose your Race\n"))
                        .append(Component.literal("3. Choose your Class"))
        ));

        // Page 2: Hall Locator
        pages.add(net.minecraft.server.network.Filterable.passThrough(
                Component.literal("§l§dHall Locator§r\n\n")
                        .append(Component.literal("You've been given a Hall Locator item.\n\n"))
                        .append(Component.literal("§6Right-click§r it to see the command to locate the Hall of Champions.\n\n"))
                        .append(Component.literal("Follow the coordinates to reach the hall."))
        ));

        // Page 3: Races
        pages.add(net.minecraft.server.network.Filterable.passThrough(
                Component.literal("§l§bRaces§r\n\n")
                        .append(Component.literal("§6Dwarf§r - Mining bonuses\n"))
                        .append(Component.literal("§aElf§r - Forest & archery bonuses\n"))
                        .append(Component.literal("§eHuman§r - Versatile bonuses\n"))
                        .append(Component.literal("§cOrc§r - Combat bonuses\n\n"))
                        .append(Component.literal("Choose wisely!"))
        ));

        // Page 4: Classes
        pages.add(net.minecraft.server.network.Filterable.passThrough(
                Component.literal("§l§cClasses§r\n\n")
                        .append(Component.literal("§4Warrior§r - Melee combat\n\n"))
                        .append(Component.literal("§5Mage§r - Magic master\n\n"))
                        .append(Component.literal("§8Rogue§r - Stealth expert"))
        ));

        // Page 5: Quests
        pages.add(net.minecraft.server.network.Filterable.passThrough(
                Component.literal("§l§2Quests§r\n\n")
                        .append(Component.literal("After choosing:\n\n"))
                        .append(Component.literal("1. Find Quest Blocks\n"))
                        .append(Component.literal("2. Right-click to browse\n"))
                        .append(Component.literal("3. Complete objectives\n"))
                        .append(Component.literal("4. Turn in for rewards!"))
        ));

        // Page 6: Commands
        pages.add(net.minecraft.server.network.Filterable.passThrough(
                Component.literal("§l§9Commands§r\n\n")
                        .append(Component.literal("§6/dagmod info§r\n"))
                        .append(Component.literal("View your race/class\n\n"))
                        .append(Component.literal("§6/quest skip§r\n"))
                        .append(Component.literal("Skip to next quest\n\n"))
                        .append(Component.literal("Good luck!"))
        ));

        net.minecraft.world.item.component.WrittenBookContent content =
                new net.minecraft.world.item.component.WrittenBookContent(
                        net.minecraft.server.network.Filterable.passThrough("DAGMod Team"),
                        "Welcome to DAGMod",
                        0,
                        pages,
                        false);

        book.set(net.minecraft.core.component.DataComponents.WRITTEN_BOOK_CONTENT, content);
        book.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,
                Component.literal("DAGMod Guide").withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.BOLD));

        return book;
    }
}