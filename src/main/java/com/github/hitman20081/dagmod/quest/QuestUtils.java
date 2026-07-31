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

        // Page 3: Race - Human
        pages.add(net.minecraft.server.network.Filterable.passThrough(
                Component.literal("§l§fRace 1/4 — Human§r\n")
                        .append(Component.literal("§7\"The Balanced\"§r\n\n"))
                        .append(Component.literal("§eKey Bonuses:§r\n"))
                        .append(Component.literal("• +25% XP from all sources\n"))
                        .append(Component.literal("• Bonus XP from fishing & farming\n"))
                        .append(Component.literal("• No gathering penalties\n\n"))
                        .append(Component.literal("§7Versatile — suits any playstyle. Best choice for fast levelling.§r"))
        ));

        // Page 4: Race - Dwarf
        pages.add(net.minecraft.server.network.Filterable.passThrough(
                Component.literal("§l§6Race 2/4 — Dwarf§r\n")
                        .append(Component.literal("§7\"The Miner\"§r\n\n"))
                        .append(Component.literal("§eKey Bonuses:§r\n"))
                        .append(Component.literal("• +20% mining speed\n"))
                        .append(Component.literal("• +1 heart (max health)\n"))
                        .append(Component.literal("• Bonus ore drops underground\n"))
                        .append(Component.literal("• Underground Resistance synergy\n\n"))
                        .append(Component.literal("§cPenalty: -5% movement speed§r\n\n"))
                        .append(Component.literal("§7Best for cave explorers and resource gatherers.§r"))
        ));

        // Page 5: Race - Elf
        pages.add(net.minecraft.server.network.Filterable.passThrough(
                Component.literal("§l§aRace 3/4 — Elf§r\n")
                        .append(Component.literal("§7\"The Ranger\"§r\n\n"))
                        .append(Component.literal("§eKey Bonuses:§r\n"))
                        .append(Component.literal("• +15% movement speed\n"))
                        .append(Component.literal("• +0.5 block reach\n"))
                        .append(Component.literal("• Woodcutting & archery bonuses\n"))
                        .append(Component.literal("• Hero of the Village trades\n\n"))
                        .append(Component.literal("§7Best for explorers, archers, and forest fighters.§r"))
        ));

        // Page 6: Race - Orc
        pages.add(net.minecraft.server.network.Filterable.passThrough(
                Component.literal("§l§cRace 4/4 — Orc§r\n")
                        .append(Component.literal("§7\"The Warrior\"§r\n\n"))
                        .append(Component.literal("§eKey Bonuses:§r\n"))
                        .append(Component.literal("• +15% melee attack damage\n"))
                        .append(Component.literal("• +2 hearts (max health)\n"))
                        .append(Component.literal("• Hunting & fishing bonuses\n"))
                        .append(Component.literal("• Bonus damage to animals\n\n"))
                        .append(Component.literal("§7Best for frontline fighters and hunters.§r"))
        ));

        // Page 7: Class - Warrior
        pages.add(net.minecraft.server.network.Filterable.passThrough(
                Component.literal("§l§4Class 1/3 — Warrior§r\n")
                        .append(Component.literal("§7\"Strength and honor\"§r\n\n"))
                        .append(Component.literal("§eKey Bonuses:§r\n"))
                        .append(Component.literal("• +20% melee damage\n"))
                        .append(Component.literal("• +2 max hearts\n"))
                        .append(Component.literal("• Heavy armor proficiency\n"))
                        .append(Component.literal("• 5 combat abilities\n\n"))
                        .append(Component.literal("§eResource: §rCooldowns\n\n"))
                        .append(Component.literal("§7Frontline tank. Melee dominance.§r"))
        ));

        // Page 8: Class - Mage
        pages.add(net.minecraft.server.network.Filterable.passThrough(
                Component.literal("§l§5Class 2/3 — Mage§r\n")
                        .append(Component.literal("§7\"Knowledge is power\"§r\n\n"))
                        .append(Component.literal("§eKey Bonuses:§r\n"))
                        .append(Component.literal("• +50% potion duration\n"))
                        .append(Component.literal("• 100 mana pool\n"))
                        .append(Component.literal("• Mana regeneration\n"))
                        .append(Component.literal("• 4 abilities + wands + scrolls\n\n"))
                        .append(Component.literal("§eResource: §rMana\n\n"))
                        .append(Component.literal("§7Ranged magic. Battlefield control.§r"))
        ));

        // Page 9: Class - Rogue
        pages.add(net.minecraft.server.network.Filterable.passThrough(
                Component.literal("§l§2Class 3/3 — Rogue§r\n")
                        .append(Component.literal("§7\"Strike from the shadows\"§r\n\n"))
                        .append(Component.literal("§eKey Bonuses:§r\n"))
                        .append(Component.literal("• +30% movement speed\n"))
                        .append(Component.literal("• 25% critical hit chance\n"))
                        .append(Component.literal("• Backstab bonus damage\n"))
                        .append(Component.literal("• 7 energy abilities\n\n"))
                        .append(Component.literal("§eResource: §rEnergy\n\n"))
                        .append(Component.literal("§7Stealth assassin. Burst damage.§r"))
        ));

        // Page 10: Quest Block & Garrick
        pages.add(net.minecraft.server.network.Filterable.passThrough(
                Component.literal("§l§2Quest Blocks§r\n\n")
                        .append(Component.literal("§6Quest Blocks§r are glowing blocks found in the Hall of Champions and scattered buildings.\n\n"))
                        .append(Component.literal("They show §nMain§r and §nSide§r quests only.\n\n"))
                        .append(Component.literal("Complete Garrick's 3 tutorial tasks at the Inn to earn your §nNovice Quest Book§r — you need it before a Quest Block will open for you."))
        ));

        // Page 11: Class Abilities via Class Trainer
        pages.add(net.minecraft.server.network.Filterable.passThrough(
                Component.literal("§l§eClass Abilities§r\n\n")
                        .append(Component.literal("Your class abilities are unlocked through the §6Class Trainer NPC§r at the §6Hall of Champions§r.\n\n"))
                        .append(Component.literal("Each quest rewards an §nability item§r — hold it in your hotbar to activate that ability.\n\n"))
                        .append(Component.literal("The chain starts at level 10 and ends at level 100.\n\n"))
                        .append(Component.literal("Garrick will send you to the Class Trainer once your tutorial is done."))
        ));

        // Page 12: Dimensions
        pages.add(net.minecraft.server.network.Filterable.passThrough(
                Component.literal("§l§dDimensions§r\n\n")
                        .append(Component.literal("§c• Bone Realm§r\n"))
                        .append(Component.literal("Undead danger zone.\n"))
                        .append(Component.literal("Boss: Skeleton King.\n"))
                        .append(Component.literal("Enter via Necrotic Key.\n\n"))
                        .append(Component.literal("§5• Dragon Realm§r\n"))
                        .append(Component.literal("Volcanic boss arena.\n"))
                        .append(Component.literal("Boss: Dragon Guardian.\n"))
                        .append(Component.literal("Set your spawn before entering!\n\n"))
                        .append(Component.literal("Portals unlock through\n"))
                        .append(Component.literal("late-game progression."))
        ));

        // Page 13: Core Commands
        pages.add(net.minecraft.server.network.Filterable.passThrough(
                Component.literal("§l§9Commands (1/2)§r\n\n")
                        .append(Component.literal("§6/info§r\n"))
                        .append(Component.literal(" Race, class & level stats\n\n"))
                        .append(Component.literal("§6/quest list§r\n"))
                        .append(Component.literal(" View active quests\n\n"))
                        .append(Component.literal("§6/quest skip§r\n"))
                        .append(Component.literal(" Skip in quest menu\n\n"))
                        .append(Component.literal("§6/resetclass§r\n"))
                        .append(Component.literal(" Reset your class choice\n\n"))
                        .append(Component.literal("§6/guildreg§r\n"))
                        .append(Component.literal(" Re-open Guild Registry"))
        ));

        // Page 14: Party Commands
        pages.add(net.minecraft.server.network.Filterable.passThrough(
                Component.literal("§l§9Commands (2/2)§r\n\n")
                        .append(Component.literal("§6/party create <name>§r\n"))
                        .append(Component.literal(" Form a new party\n\n"))
                        .append(Component.literal("§6/party invite <player>§r\n"))
                        .append(Component.literal(" Invite to your party\n\n"))
                        .append(Component.literal("§6/party accept§r\n"))
                        .append(Component.literal(" Accept an invite\n\n"))
                        .append(Component.literal("§6/party leave§r\n"))
                        .append(Component.literal(" Leave your party\n\n"))
                        .append(Component.literal("§6/pc <msg>§r\n"))
                        .append(Component.literal(" Party chat shorthand"))
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