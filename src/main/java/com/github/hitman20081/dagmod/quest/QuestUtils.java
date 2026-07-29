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
                        .append(Component.literal("A race/class RPG built on top of Minecraft.\n\n"))
                        .append(Component.literal("§nFirst steps:§r\n"))
                        .append(Component.literal("1. Find the local Inn\n"))
                        .append(Component.literal("2. Meet Innkeeper Garrick\n"))
                        .append(Component.literal("3. Register your Race & Class\n"))
                        .append(Component.literal("4. Complete his 3 tasks\n"))
                        .append(Component.literal("5. Head to the Hall of Champions"))
        ));

        // Page 2: Hall Locator / Inn
        pages.add(net.minecraft.server.network.Filterable.passThrough(
                Component.literal("§l§dHall Locator§r\n\n")
                        .append(Component.literal("You have a §6Hall Locator§r in your inventory.\n\n"))
                        .append(Component.literal("§6Right-click§r it to find nearby structures.\n\n"))
                        .append(Component.literal("Your first stop is the §6Inn§r — §6Innkeeper Garrick§r is on the middle floor. He'll register your Race and Class and give you your first quests.\n\n"))
                        .append(Component.literal("The §6Hall of Champions§r comes later."))
        ));

        // Page 3: Races
        pages.add(net.minecraft.server.network.Filterable.passThrough(
                Component.literal("§l§bRaces§r\n\n")
                        .append(Component.literal("§6Dwarf§r\nMining & underground bonuses\n\n"))
                        .append(Component.literal("§aElf§r\nForest & archery bonuses\n\n"))
                        .append(Component.literal("§eHuman§r\n+25% XP, versatile bonuses\n\n"))
                        .append(Component.literal("§cOrc§r\nCombat & berserker bonuses"))
        ));

        // Page 4: Classes
        pages.add(net.minecraft.server.network.Filterable.passThrough(
                Component.literal("§l§cClasses§r\n\n")
                        .append(Component.literal("§4Warrior§r\nMelee tank, 6 abilities\n\n"))
                        .append(Component.literal("§5Mage§r\nRanged magic, 4 abilities + wands + scrolls\n\n"))
                        .append(Component.literal("§8Rogue§r\nStealth assassin, 7 abilities (dual system)"))
        ));

        // Page 5: Quest Block & Garrick
        pages.add(net.minecraft.server.network.Filterable.passThrough(
                Component.literal("§l§2Quest Blocks§r\n\n")
                        .append(Component.literal("§6Quest Blocks§r are glowing blocks found in the Hall of Champions and scattered buildings.\n\n"))
                        .append(Component.literal("They show §nMain§r and §nSide§r quests only.\n\n"))
                        .append(Component.literal("Complete Garrick's 3 tutorial tasks at the Inn to earn your §nNovice Quest Book§r — you need it before a Quest Block will open for you."))
        ));

        // Page 6: Class Abilities via Class Trainer
        pages.add(net.minecraft.server.network.Filterable.passThrough(
                Component.literal("§l§eClass Abilities§r\n\n")
                        .append(Component.literal("Your class abilities are unlocked through the §6Class Trainer NPC§r at the §6Hall of Champions§r.\n\n"))
                        .append(Component.literal("Each quest rewards an §nability item§r — hold it in your hotbar to activate that ability.\n\n"))
                        .append(Component.literal("The chain starts at level 10 and ends at level 100.\n\n"))
                        .append(Component.literal("Garrick will send you to the Class Trainer once your tutorial is done."))
        ));

        // Page 7: Dimensions
        pages.add(net.minecraft.server.network.Filterable.passThrough(
                Component.literal("§l§dDimensions§r\n\n")
                        .append(Component.literal("DAGMod adds 3 extra dimensions:\n\n"))
                        .append(Component.literal("§c• Bone Realm§r\nSkeleton-themed danger zone\n\n"))
                        .append(Component.literal("§5• Dragon Realm§r\nDragon boss encounter\n\n"))
                        .append(Component.literal("§9• Dungeon Realm§r\nProcedural dungeons\n\n"))
                        .append(Component.literal("Unlock through late-game quests."))
        ));

        // Page 8: Commands
        pages.add(net.minecraft.server.network.Filterable.passThrough(
                Component.literal("§l§9Commands§r\n\n")
                        .append(Component.literal("§6/info§r\nView race, class & stats\n\n"))
                        .append(Component.literal("§6/quest list§r\nList active quests\n\n"))
                        .append(Component.literal("§6/quest skip§r\nSkip current quest in menu\n\n"))
                        .append(Component.literal("§6/party create <name>§r\nGroup play\n\n"))
                        .append(Component.literal("Good luck, adventurer!"))
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