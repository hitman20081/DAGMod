package com.github.hitman20081.dagmod.quest.rewards;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.quest.QuestReward;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * Quest reward that unlocks a crafting recipe in the player's recipe book.
 * Use this to gate recipe knowledge behind quest completion.
 */
public class UnlockReward extends QuestReward {

    private final Identifier recipeId;
    private final String displayName;

    public UnlockReward(Identifier recipeId, String displayName) {
        super(RewardType.UNLOCK, "Recipe: " + displayName);
        this.recipeId = recipeId;
        this.displayName = displayName;
    }

    @Override
    public boolean giveReward(Player player, Level world) {
        if (!(player instanceof ServerPlayer serverPlayer) || !(world instanceof ServerLevel serverWorld)) {
            return true; // client side — nothing to do
        }

        List<RecipeHolder<?>> toUnlock = new ArrayList<>();
        for (RecipeHolder<?> entry : serverWorld.getServer().getRecipeManager().getRecipes()) {
            if (entry.id().identifier().equals(recipeId)) {
                toUnlock.add(entry);
                break;
            }
        }

        if (toUnlock.isEmpty()) {
            DagMod.LOGGER.warn("UnlockReward: recipe '{}' not found in recipe manager", recipeId);
            return false;
        }

        serverPlayer.awardRecipes(toUnlock);
        serverPlayer.sendSystemMessage(
            Component.literal("✦ Recipe Unlocked: ")
                .withStyle(ChatFormatting.GOLD)
                .append(Component.literal(displayName).withStyle(ChatFormatting.YELLOW)));
        DagMod.LOGGER.info("Unlocked recipe '{}' for player {}", recipeId, player.getName().getString());
        return true;
    }

    @Override
    public boolean canGiveReward(Player player) {
        return true; // Recipe unlocks never fail due to inventory space
    }

    @Override
    public Component getDisplayText() {
        return Component.literal("• Unlocks Recipe: ")
                .append(Component.literal(displayName).withStyle(ChatFormatting.YELLOW));
    }
}
