package com.github.hitman20081.dagmod.event;

import com.github.hitman20081.dagmod.party.quest.PartyQuestData;
import com.github.hitman20081.dagmod.party.quest.PartyQuestManager;
import com.github.hitman20081.dagmod.party.quest.PartyQuestObjectiveType;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PartyQuestBlockBreakHandler {

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            // Ensure we're on the server and the player is a server player
            if (world.isClient() || !(player instanceof net.minecraft.server.network.ServerPlayerEntity)) {
                return;
            }
            net.minecraft.server.network.ServerPlayerEntity serverPlayer = (net.minecraft.server.network.ServerPlayerEntity) player;

            PartyQuestData quest = PartyQuestManager.getInstance().getActiveQuest(serverPlayer);
            if (quest != null) {
                String blockId = Registries.BLOCK.getId(state.getBlock()).toString();

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
