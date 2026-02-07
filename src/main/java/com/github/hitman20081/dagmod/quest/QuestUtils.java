package com.github.hitman20081.dagmod.quest;

import com.github.hitman20081.dagmod.item.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class QuestUtils {

    public static void giveQuestBookForTier(ServerPlayerEntity player, QuestData.QuestBookTier tier) {
        ItemStack newBook = switch (tier) {
            case NOVICE -> new ItemStack(ModItems.NOVICE_QUEST_BOOK);
            case APPRENTICE -> new ItemStack(ModItems.APPRENTICE_QUEST_BOOK);
            case EXPERT -> new ItemStack(ModItems.EXPERT_QUEST_BOOK);
            case MASTER -> new ItemStack(ModItems.MASTER_QUEST_TOME);
        };

        player.giveItemStack(newBook);
        player.sendMessage(Text.literal("You received: " + tier.getDisplayName() + "!")
                .formatted(Formatting.GOLD).formatted(Formatting.BOLD), false);
    }

    public static ItemStack createWelcomeBook() {
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);

        java.util.List<net.minecraft.text.RawFilteredPair<Text>> pages = new java.util.ArrayList<>();

        // Page 1: Welcome
        pages.add(net.minecraft.text.RawFilteredPair.of(
                Text.literal("§l§6Welcome to DAGMod!§r\n\n")
                        .append(Text.literal("This guide will help you get started on your adventure.\n\n"))
                        .append(Text.literal("§nGetting Started:§r\n"))
                        .append(Text.literal("1. Find the Hall of Champions\n"))
                        .append(Text.literal("2. Choose your Race\n"))
                        .append(Text.literal("3. Choose your Class"))
        ));

        // Page 2: Hall Locator
        pages.add(net.minecraft.text.RawFilteredPair.of(
                Text.literal("§l§dHall Locator§r\n\n")
                        .append(Text.literal("You've been given a Hall Locator item.\n\n"))
                        .append(Text.literal("§6Right-click§r it to see the command to locate the Hall of Champions.\n\n"))
                        .append(Text.literal("Follow the coordinates to reach the hall."))
        ));

        // Page 3: Races
        pages.add(net.minecraft.text.RawFilteredPair.of(
                Text.literal("§l§bRaces§r\n\n")
                        .append(Text.literal("§6Dwarf§r - Mining bonuses\n"))
                        .append(Text.literal("§aElf§r - Forest & archery bonuses\n"))
                        .append(Text.literal("§eHuman§r - Versatile bonuses\n"))
                        .append(Text.literal("§cOrc§r - Combat bonuses\n\n"))
                        .append(Text.literal("Choose wisely!"))
        ));

        // Page 4: Classes
        pages.add(net.minecraft.text.RawFilteredPair.of(
                Text.literal("§l§cClasses§r\n\n")
                        .append(Text.literal("§4Warrior§r - Melee combat\n\n"))
                        .append(Text.literal("§5Mage§r - Magic master\n\n"))
                        .append(Text.literal("§8Rogue§r - Stealth expert"))
        ));

        // Page 5: Quests
        pages.add(net.minecraft.text.RawFilteredPair.of(
                Text.literal("§l§2Quests§r\n\n")
                        .append(Text.literal("After choosing:\n\n"))
                        .append(Text.literal("1. Find Quest Blocks\n"))
                        .append(Text.literal("2. Right-click to browse\n"))
                        .append(Text.literal("3. Complete objectives\n"))
                        .append(Text.literal("4. Turn in for rewards!"))
        ));

        // Page 6: Commands
        pages.add(net.minecraft.text.RawFilteredPair.of(
                Text.literal("§l§9Commands§r\n\n")
                        .append(Text.literal("§6/dagmod info§r\n"))
                        .append(Text.literal("View your race/class\n\n"))
                        .append(Text.literal("§6/quest skip§r\n"))
                        .append(Text.literal("Skip to next quest\n\n"))
                        .append(Text.literal("Good luck!"))
        ));

        net.minecraft.component.type.WrittenBookContentComponent content =
                new net.minecraft.component.type.WrittenBookContentComponent(
                        net.minecraft.text.RawFilteredPair.of("DAGMod Team"),
                        "Welcome to DAGMod",
                        0,
                        pages,
                        true
                );

        book.set(net.minecraft.component.DataComponentTypes.WRITTEN_BOOK_CONTENT, content);
        book.set(net.minecraft.component.DataComponentTypes.CUSTOM_NAME,
                Text.literal("DAGMod Guide").formatted(Formatting.GOLD).formatted(Formatting.BOLD));

        return book;
    }
}