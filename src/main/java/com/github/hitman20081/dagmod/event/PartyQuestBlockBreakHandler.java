package com.github.hitman20081.dagmod.event;

import com.github.hitman20081.dagmod.party.quest.PartyQuestData;
import com.github.hitman20081.dagmod.party.quest.PartyQuestManager;
import com.github.hitman20081.dagmod.party.quest.PartyQuestObjectiveType;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerPlayer;

public class PartyQuestBlockBreakHandler {

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            // Ensure we're on the server and the player is a server player
            if (world.isClientSide() || !(player instanceof net.minecraft.server.level.ServerPlayer)) {
                return;
            }
            net.minecraft.server.level.ServerPlayer serverPlayer = (net.minecraft.server.level.ServerPlayer) player;

            PartyQuestData quest = PartyQuestManager.getInstance().getActiveQuest(serverPlayer);
            if (quest != null) {
                String blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();

                for (var objective : quest.getTemplate().getObjectives()) {
                    if (objective.getType() == PartyQuestObjectiveType.MINE_BLOCK) {
                        if (objective.getTarget().equals(blockId)) {
                            PartyQuestManager.getInstance().updateObjective(
                                quest.getPartyId(),
                                objective.getId(),
                                1
                            );
                        }
                    }
                }
            }
        });
    }
}
