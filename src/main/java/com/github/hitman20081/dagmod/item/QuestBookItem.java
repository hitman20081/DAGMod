package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.quest.QuestData;
import com.github.hitman20081.dagmod.quest.QuestManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.client.MinecraftClient;
import com.github.hitman20081.dagmod.gui.QuestBookScreen;

public class QuestBookItem extends Item {
    private final QuestData.QuestBookTier tier;

    public QuestBookItem(Settings settings, QuestData.QuestBookTier tier) {
        super(settings);
        this.tier = tier;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) {
            // Client side - open the GUI
            MinecraftClient.getInstance().setScreen(new QuestBookScreen(tier));
        } else {
            // Server side - sync quest data
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) user;
            QuestManager questManager = QuestManager.getInstance();
            QuestData playerData = questManager.getPlayerData(user);

            if (playerData.getQuestBookTier() != this.tier) {
                playerData.setQuestBookTier(this.tier);
                user.sendMessage(Text.literal("Quest book updated to: " + tier.getDisplayName()), false);
            }
        }
        return ActionResult.SUCCESS;
    }

    private void openQuestBookGUI(ServerPlayerEntity player, QuestManager questManager, QuestData playerData) {
        // Send packet to client to open book GUI
        // For now, we'll use a simple approach with client-side opening

        // This is a temporary server-side message until we implement full client-server communication
        player.sendMessage(Text.literal("Opening " + tier.getDisplayName() + "..."), false);
    }

    // Get the tier of this quest book
    public QuestData.QuestBookTier getTier() {
        return tier;
    }

    // Override item name to include tier
    @Override
    public Text getName(ItemStack stack) {
        return Text.literal(tier.getDisplayName());
    }

    // Add tooltip information


    // Static methods for easy creation
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