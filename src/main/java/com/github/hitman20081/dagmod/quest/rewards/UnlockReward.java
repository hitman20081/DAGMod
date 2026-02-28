package com.github.hitman20081.dagmod.quest.rewards;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.quest.QuestReward;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

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
    public boolean giveReward(PlayerEntity player, World world) {
        if (!(player instanceof ServerPlayerEntity serverPlayer) || !(world instanceof ServerWorld serverWorld)) {
            return true; // client side — nothing to do
        }

        List<RecipeEntry<?>> toUnlock = new ArrayList<>();
        for (RecipeEntry<?> entry : serverWorld.getServer().getRecipeManager().values()) {
            if (entry.id().getValue().equals(recipeId)) {
                toUnlock.add(entry);
                break;
            }
        }

        if (toUnlock.isEmpty()) {
            DagMod.LOGGER.warn("UnlockReward: recipe '{}' not found in recipe manager", recipeId);
            return false;
        }

        serverPlayer.unlockRecipes(toUnlock);
        serverPlayer.sendMessage(
            Text.literal("✦ Recipe Unlocked: ")
                .formatted(Formatting.GOLD)
                .append(Text.literal(displayName).formatted(Formatting.YELLOW)),
            false
        );
        DagMod.LOGGER.info("Unlocked recipe '{}' for player {}", recipeId, player.getName().getString());
        return true;
    }

    @Override
    public boolean canGiveReward(PlayerEntity player) {
        return true; // Recipe unlocks never fail due to inventory space
    }

    @Override
    public Text getDisplayText() {
        return Text.literal("• Unlocks Recipe: ")
                .append(Text.literal(displayName).formatted(Formatting.YELLOW));
    }
}
