package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.client.QuestBookClientHandler;
import com.github.hitman20081.dagmod.quest.QuestData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

public class QuestBookItem extends Item {
    private final QuestData.QuestBookTier tier;

    public QuestBookItem(Properties settings, QuestData.QuestBookTier tier) {
        super(settings);
        this.tier = tier;
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        if (world.isClientSide()) {
            // Call client-side handler through a proxy
            QuestBookClientHandler.openQuestBook(tier);
        }
        return InteractionResult.SUCCESS;
    }

    public QuestData.QuestBookTier getTier() {
        return tier;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.literal(tier.getDisplayName());
    }

    public static QuestBookItem createNovice(Properties settings) {
        return new QuestBookItem(settings, QuestData.QuestBookTier.NOVICE);
    }

    public static QuestBookItem createApprentice(Properties settings) {
        return new QuestBookItem(settings, QuestData.QuestBookTier.APPRENTICE);
    }

    public static QuestBookItem createExpert(Properties settings) {
        return new QuestBookItem(settings, QuestData.QuestBookTier.EXPERT);
    }

    public static QuestBookItem createMaster(Properties settings) {
        return new QuestBookItem(settings, QuestData.QuestBookTier.MASTER);
    }
}