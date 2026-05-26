package com.github.hitman20081.dagmod.quest.rewards;

import com.github.hitman20081.dagmod.quest.QuestReward;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

/**
 * Gives a specific enchanted book (with enchantment component set) as a quest reward.
 * Use Identifier.withDefaultNamespace("enchantment_name") for vanilla enchantments.
 */
public class EnchantedBookReward extends QuestReward {

    private final Identifier enchantmentId;
    private final int level;

    public EnchantedBookReward(Identifier enchantmentId, int level) {
        super(RewardType.ITEM, enchantmentId.getPath().replace('_', ' ') + " " + toRoman(level) + " (book)");
        this.enchantmentId = enchantmentId;
        this.level = level;
    }

    @Override
    public boolean giveReward(Player player, Level world) {
        ItemStack book = createBook(world);
        if (book.isEmpty()) return false;

        if (!player.getInventory().add(book)) {
            player.drop(book, false);
        }
        player.sendSystemMessage(createSuccessMessage());
        return true;
    }

    @Override
    public boolean canGiveReward(Player player) {
        return true; // Books stack to 1; will drop on the ground if inventory is full
    }

    @Override
    public Component getDisplayText() {
        return Component.literal("• " + enchantmentId.getPath().replace('_', ' ') + " " + toRoman(level) + " (enchanted book)");
    }

    private ItemStack createBook(Level world) {
        var registry = world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        Holder<Enchantment> entry = registry
                .get(enchantmentId)
                .orElse(null);
        if (entry == null) return ItemStack.EMPTY;

        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
        ItemEnchantments.Mutable builder =
                new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        builder.set(entry, level);
        book.set(DataComponents.STORED_ENCHANTMENTS, builder.toImmutable());
        return book;
    }

    private static String toRoman(int level) {
        return switch (level) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            default -> String.valueOf(level);
        };
    }
}
