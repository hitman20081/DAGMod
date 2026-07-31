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
                        .append(Component.literal("§8A race/class RPG built on top of Minecraft.\n\n"))
                        .append(Component.literal("§8§nFirst steps:§r\n"))
                        .append(Component.literal("§81. Find the local Inn\n"))
                        .append(Component.literal("§82. Meet Innkeeper Garrick\n"))
                        .append(Component.literal("§83. Register your Race & Class\n"))
                        .append(Component.literal("§84. Complete his 3 tasks\n"))
                        .append(Component.literal("§85. Head to the Hall of Champions"))
        ));

        // Page 2: Hall Locator / Inn
        pages.add(net.minecraft.server.network.Filterable.passThrough(
                Component.literal("§l§5The Inn & Garrick§r\n\n")
                        .append(Component.literal("§8Your first stop is the §6Inn§8.\n\n"))
                        .append(Component.literal("§6Innkeeper Garrick§8 is on the middle floor. Talk to him to register your Race and Class and begin your three tutorial tasks.\n\n"))
                        .append(Component.literal("§8Once you complete his tasks, Garrick will give you a §6Hall Locator§8 to find the §6Hall of Champions§8.\n\n"))
                        .append(Component.literal("§8Use /info to check your race, class & level at any time."))
        ));

        // Page 3: Race - Human
        pages.add(net.minecraft.server.network.Filterable.passThrough(
                Component.literal("§l§8Race 1/4 — Human§r\n")
                        .append(Component.literal("§8\"The Balanced\"\n\n"))
                        .append(Component.literal("§6Key Bonuses:§8\n"))
                        .append(Component.literal("§8• +25% XP from all sources\n"))
                        .append(Component.literal("§8• Bonus XP from fishing & farming\n"))
                        .append(Component.literal("§8• No gathering penalties\n\n"))
                        .append(Component.literal("§8Versatile — suits any playstyle. Best choice for fast levelling."))
        ));

        // Page 4: Race - Dwarf
        pages.add(net.minecraft.server.network.Filterable.passThrough(
                Component.literal("§l§6Race 2/4 — Dwarf§r\n")
                        .append(Component.literal("§8\"The Miner\"\n\n"))
                        .append(Component.literal("§6Key Bonuses:§8\n"))
                        .append(Component.literal("§8• +20% mining speed\n"))
                        .append(Component.literal("§8• +1 heart (max health)\n"))
                        .append(Component.literal("§8• Bonus ore drops underground\n"))
                        .append(Component.literal("§8• Underground Resistance synergy\n\n"))
                        .append(Component.literal("§cPenalty: -5% movement speed§8\n\n"))
                        .append(Component.literal("§8Best for cave explorers and resource gatherers."))
        ));

        // Page 5: Race - Elf
        pages.add(net.minecraft.server.network.Filterable.passThrough(
                Component.literal("§l§2Race 3/4 — Elf§r\n")
                        .append(Component.literal("§8\"The Ranger\"\n\n"))
                        .append(Component.literal("§6Key Bonuses:§8\n"))
                        .append(Component.literal("§8• +15% movement speed\n"))
                        .append(Component.literal("§8• +0.5 block reach\n"))
                        .append(Component.literal("§8• Woodcutting & archery bonuses\n"))
                        .append(Component.literal("§8• Hero of the Village trades\n\n"))
                        .append(Component.literal("§8Best for explorers, archers, and forest fighters."))
        ));

        // Page 6: Race - Orc
        pages.add(net.minecraft.server.network.Filterable.passThrough(
                Component.literal("§l§4Race 4/4 — Orc§r\n")
                        .append(Component.literal("§8\"The Warrior\"\n\n"))
                        .append(Component.literal("§6Key Bonuses:§8\n"))
                        .append(Component.literal("§8• +15% melee attack damage\n"))
                        .append(Component.literal("§8• +2 hearts (max health)\n"))
                        .append(Component.literal("§8• Hunting & fishing bonuses\n"))
                        .append(Component.literal("§8• Bonus damage to animals\n\n"))
                        .append(Component.literal("§8Best for frontline fighters and hunters."))
        ));

        // Page 7: Class - Warrior
        pages.add(net.minecraft.server.network.Filterable.passThrough(
                Component.literal("§l§4Class 1/3 — Warrior§r\n")
                        .append(Component.literal("§8\"Strength and honor\"\n\n"))
                        .append(Component.literal("§6Key Bonuses:§8\n"))
                        .append(Component.literal("§8• +20% melee damage\n"))
                        .append(Component.literal("§8• +2 max hearts\n"))
                        .append(Component.literal("§8• Heavy armor proficiency\n"))
                        .append(Component.literal("§8• 5 combat abilities\n\n"))
                        .append(Component.literal("§6Resource: §8Cooldowns\n\n"))
                        .append(Component.literal("§8Frontline tank. Melee dominance."))
        ));

        // Page 8: Class - Mage
        pages.add(net.minecraft.server.network.Filterable.passThrough(
                Component.literal("§l§5Class 2/3 — Mage§r\n")
                        .append(Component.literal("§8\"Knowledge is power\"\n\n"))
                        .append(Component.literal("§6Key Bonuses:§8\n"))
                        .append(Component.literal("§8• +50% potion duration\n"))
                        .append(Component.literal("§8• 100 mana pool\n"))
                        .append(Component.literal("§8• Mana regeneration\n"))
                        .append(Component.literal("§8• 4 abilities + wands + scrolls\n\n"))
                        .append(Component.literal("§6Resource: §8Mana\n\n"))
                        .append(Component.literal("§8Ranged magic. Battlefield control."))
        ));

        // Page 9: Class - Rogue
        pages.add(net.minecraft.server.network.Filterable.passThrough(
                Component.literal("§l§2Class 3/3 — Rogue§r\n")
                        .append(Component.literal("§8\"Strike from the shadows\"\n\n"))
                        .append(Component.literal("§6Key Bonuses:§8\n"))
                        .append(Component.literal("§8• +30% movement speed\n"))
                        .append(Component.literal("§8• 25% critical hit chance\n"))
                        .append(Component.literal("§8• Backstab bonus damage\n"))
                        .append(Component.literal("§8• 7 energy abilities\n\n"))
                        .append(Component.literal("§6Resource: §8Energy\n\n"))
                        .append(Component.literal("§8Stealth assassin. Burst damage."))
        ));

        // Page 10: Quest Block & Garrick
        pages.add(net.minecraft.server.network.Filterable.passThrough(
                Component.literal("§l§2Quest Blocks§r\n\n")
                        .append(Component.literal("§6Quest Blocks§8 are glowing blocks found in the Hall of Champions and scattered buildings.\n\n"))
                        .append(Component.literal("§8They show Main and Side quests only.\n\n"))
                        .append(Component.literal("§8Complete Garrick's 3 tutorial tasks at the Inn to earn your §6Novice Quest Book§8 — you need it before a Quest Block will open for you."))
        ));

        // Page 11: Class Abilities via Class Trainer
        pages.add(net.minecraft.server.network.Filterable.passThrough(
                Component.literal("§l§6Class Abilities§r\n\n")
                        .append(Component.literal("§8Your class abilities are unlocked through the §6Class Trainer NPC§8 at the §6Hall of Champions§8.\n\n"))
                        .append(Component.literal("§8Each quest rewards an ability item — hold it in your hotbar to activate that ability.\n\n"))
                        .append(Component.literal("§8The chain starts at level 10 and ends at level 100.\n\n"))
                        .append(Component.literal("§8Garrick will send you to the Class Trainer once your tutorial is done."))
        ));

        // Page 12: Dimensions
        pages.add(net.minecraft.server.network.Filterable.passThrough(
                Component.literal("§l§5Dimensions§r\n\n")
                        .append(Component.literal("§c• Bone Realm§8\n"))
                        .append(Component.literal("§8Undead danger zone.\n"))
                        .append(Component.literal("§8Boss: Skeleton King.\n"))
                        .append(Component.literal("§8Enter via Necrotic Key.\n\n"))
                        .append(Component.literal("§5• Dragon Realm§8\n"))
                        .append(Component.literal("§8Volcanic boss arena.\n"))
                        .append(Component.literal("§8Boss: Dragon Guardian.\n"))
                        .append(Component.literal("§8Set your spawn before entering!\n\n"))
                        .append(Component.literal("§8Portals unlock through\n"))
                        .append(Component.literal("§8late-game progression."))
        ));

        // Page 13: Core Commands
        pages.add(net.minecraft.server.network.Filterable.passThrough(
                Component.literal("§l§9Commands (1/2)§r\n\n")
                        .append(Component.literal("§6/info§r\n"))
                        .append(Component.literal("§8 Race, class & level stats\n\n"))
                        .append(Component.literal("§6/quest list§r\n"))
                        .append(Component.literal("§8 View active quests\n\n"))
                        .append(Component.literal("§6/quest skip§r\n"))
                        .append(Component.literal("§8 Skip in quest menu\n\n"))
                        .append(Component.literal("§6/resetclass§r\n"))
                        .append(Component.literal("§8 Reset your class choice\n\n"))
                        .append(Component.literal("§6/guildreg§r\n"))
                        .append(Component.literal("§8 Re-open Guild Registry"))
        ));

        // Page 14: Party Commands
        pages.add(net.minecraft.server.network.Filterable.passThrough(
                Component.literal("§l§9Commands (2/2)§r\n\n")
                        .append(Component.literal("§6/party create <name>§r\n"))
                        .append(Component.literal("§8 Form a new party\n\n"))
                        .append(Component.literal("§6/party invite <player>§r\n"))
                        .append(Component.literal("§8 Invite to your party\n\n"))
                        .append(Component.literal("§6/party accept§r\n"))
                        .append(Component.literal("§8 Accept an invite\n\n"))
                        .append(Component.literal("§6/party leave§r\n"))
                        .append(Component.literal("§8 Leave your party\n\n"))
                        .append(Component.literal("§6/pc <msg>§r\n"))
                        .append(Component.literal("§8 Party chat shorthand"))
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