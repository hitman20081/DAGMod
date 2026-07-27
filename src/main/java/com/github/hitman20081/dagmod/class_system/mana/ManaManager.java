package com.github.hitman20081.dagmod.class_system.mana;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.progression.ProgressionManager;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ManaManager {
    private static final Map<UUID, ManaData> playerManaData = new ConcurrentHashMap<>();
    private static final Map<UUID, Integer> regenTicks = new ConcurrentHashMap<>();

    public static ManaData getManaData(ServerPlayer player) {
        return playerManaData.computeIfAbsent(player.getUUID(), uuid -> new ManaData());
    }

    public static void tick(ServerPlayer player) {
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUUID());

        if (!"Mage".equals(playerClass)) {
            return; // Only Mages have mana
        }

        ManaData data = getManaData(player);

        // Regenerate mana every 20 ticks (1 second)
        if (player.level().getGameTime() % 20 == 0) {
            if (data.getCurrentMana() < data.getMaxMana()) {
                int level = 1;
                var progData = ProgressionManager.getPlayerData(player);
                if (progData != null) level = progData.getCurrentLevel();

                float baseRegen = calculateRegenRate(level);

                // Apply armor set mana regen bonus
                float armorBonus = com.github.hitman20081.dagmod.class_system.armor.CustomArmorSetBonus
                        .getManaRegenBonus(player);
                float totalRegen = baseRegen * (1.0f + armorBonus);

                data.addMana(totalRegen);

                // Sync to client
                ManaNetworking.sendManaUpdate(player, data.getCurrentMana(), data.getMaxMana());
            }
        }
    }

    public static float calculateRegenRate(int level) {
        return 2.0f + level * 0.04f;
    }

    public static int calculateMaxMana(int level) {
        return ManaData.BASE_MAX_MANA + (level - 1) * 2;
    }

    public static void updateMaxManaForLevel(ServerPlayer player, int level) {
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUUID());
        if (!"Mage".equals(playerClass)) return;

        ManaData data = getManaData(player);
        data.setMaxMana(calculateMaxMana(level));
        ManaNetworking.sendManaUpdate(player, data.getCurrentMana(), data.getMaxMana());
    }

    public static void clearPlayerData(UUID playerId) {
        playerManaData.remove(playerId);
        regenTicks.remove(playerId);
    }
}