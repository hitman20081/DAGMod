package com.github.hitman20081.dagmod.quest.rewards;

import com.github.hitman20081.dagmod.quest.QuestReward;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

/**
 * Gives a specific enchanted book (with enchantment component set) as a quest reward.
 * Use Identifier.ofVanilla("enchantment_name") for vanilla enchantments.
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
    public boolean giveReward(PlayerEntity player, World world) {
        ItemStack book = createBook(world);
        if (book.isEmpty()) return false;

        if (!player.getInventory().insertStack(book)) {
            player.dropItem(book, false);
        }
        player.sendMessage(createSuccessMessage(), false);
        return true;
    }

    @Override
    public boolean canGiveReward(PlayerEntity player) {
        return true; // Books stack to 1; will drop on the ground if inventory is full
    }

    @Override
    public Text getDisplayText() {
        return Text.literal("• " + enchantmentId.getPath().replace('_', ' ') + " " + toRoman(level) + " (enchanted book)");
    }

    private ItemStack createBook(World world) {
        var registry = world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);
        RegistryEntry<Enchantment> entry = registry
                .getEntry(enchantmentId)
                .orElse(null);
        if (entry == null) return ItemStack.EMPTY;

        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
        ItemEnchantmentsComponent.Builder builder =
                new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
        builder.add(entry, level);
        book.set(DataComponentTypes.STORED_ENCHANTMENTS, builder.build());
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
