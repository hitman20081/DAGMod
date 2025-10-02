package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.client.QuestBookClientHandler;
import com.github.hitman20081.dagmod.quest.QuestData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class QuestBookItem extends Item {
    private final QuestData.QuestBookTier tier;

    public QuestBookItem(Settings settings, QuestData.QuestBookTier tier) {
        super(settings);
        this.tier = tier;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) {
            // Call client-side handler through a proxy
            QuestBookClientHandler.openQuestBook(tier);
        }
        return ActionResult.SUCCESS;
    }

    public QuestData.QuestBookTier getTier() {
        return tier;
    }

    @Override
    public Text getName(ItemStack stack) {
        return Text.literal(tier.getDisplayName());
    }

    public static QuestBookItem createNovice(Settings settings) {
        return new QuestBookItem(settings, QuestData.QuestBookTier.NOVICE);
    }

    public static QuestBookItem createApprentice(Settings settings) {
        return new QuestBookItem(settings, QuestData.QuestBookTier.APPRENTICE);
    }

    public static QuestBookItem createExpert(Settings settings) {
        return new QuestBookItem(settings, QuestData.QuestBookTier.EXPERT);
    }

    public static QuestBookItem createMaster(Settings settings) {
        return new QuestBookItem(settings, QuestData.QuestBookTier.MASTER);
    }
}