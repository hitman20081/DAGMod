package com.github.hitman20081.dagmod.class_system.mana;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ManaManager {
    private static final Map<UUID, ManaData> playerManaData = new HashMap<>();
    private static final Map<UUID, Integer> regenTicks = new HashMap<>();
    private static final float MANA_REGEN_PER_SECOND = 2.0f;

    public static ManaData getManaData(ServerPlayerEntity player) {
        return playerManaData.computeIfAbsent(player.getUuid(), uuid -> new ManaData());
    }

    public static void tick(ServerPlayerEntity player) {
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUuid());
        if (!"Mage".equals(playerClass)) {
            return;
        }

        ManaData manaData = getManaData(player);
        int ticks = regenTicks.getOrDefault(player.getUuid(), 0);
        ticks++;

        if (ticks >= 20) { // Every second
            ticks = 0;
            if (manaData.getCurrentMana() < manaData.getMaxMana()) {
                manaData.addMana(MANA_REGEN_PER_SECOND);
                // Sync to client (we'll add this next)
                ManaNetworking.sendManaUpdate(player, manaData.getCurrentMana(), manaData.getMaxMana());
            }
        }

        regenTicks.put(player.getUuid(), ticks);
    }

    public static void clearPlayerData(UUID playerId) {
        playerManaData.remove(playerId);
        regenTicks.remove(playerId);
    }
}