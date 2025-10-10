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
            return; // Only Mages have mana
        }

        ManaData data = getManaData(player);

        // Regenerate mana every 20 ticks (1 second)
        if (player.getWorld().getTime() % 20 == 0) {
            if (data.getCurrentMana() < data.getMaxMana()) {
                float baseRegen = 2.0f; // Base 2 mana per second

                // Apply armor set mana regen bonus
                float armorBonus = com.github.hitman20081.dagmod.class_system.armor.CustomArmorSetBonus
                        .getManaRegenBonus(player);
                float totalRegen = baseRegen * (1.0f + armorBonus);

                data.addMana((int)totalRegen);

                // Sync to client
                ManaNetworking.sendManaUpdate(player, data.getCurrentMana(), data.getMaxMana());
            }
        }
    }

    public static void clearPlayerData(UUID playerId) {
        playerManaData.remove(playerId);
        regenTicks.remove(playerId);
    }
}