package com.github.hitman20081.dagmod.event;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.enchantment.CustomEnchantmentEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks active dodge effects (Phantom Dust = 50%, Perfect Dodge = 100%).
 */
public class DodgeHandler {

    private static final Map<UUID, Float> dodgeChance = new HashMap<>();
    private static final Map<UUID, Long> dodgeExpiry = new HashMap<>();

    /**
     * Activates a dodge effect for the given player.
     *
     * @param uuid          Player UUID
     * @param chance        Dodge chance (0.0 to 1.0)
     * @param worldTime     Current world tick
     * @param durationTicks Duration in ticks
     */
    public static void activate(UUID uuid, float chance, long worldTime, int durationTicks) {
        dodgeChance.put(uuid, chance);
        dodgeExpiry.put(uuid, worldTime + durationTicks);
    }

    /**
     * Rolls a dodge check for the given player.
     * Auto-removes expired entries.
     *
     * @return true if the attack should be dodged
     */
    public static boolean tryDodge(UUID uuid, long worldTime, RandomSource random) {
        Long expiry = dodgeExpiry.get(uuid);
        if (expiry == null) return false;
        if (worldTime >= expiry) {
            dodgeChance.remove(uuid);
            dodgeExpiry.remove(uuid);
            return false;
        }
        Float chance = dodgeChance.get(uuid);
        if (chance == null) return false;
        return random.nextFloat() < chance;
    }

    public static boolean isActive(UUID uuid) {
        return dodgeExpiry.containsKey(uuid);
    }

    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    /**
     * Rogue's Shadow Step passive: a chance to dodge entirely, independent of any timed dodge
     * buff (Phantom Dust / Perfect Dodge). Requires the Rogue class and the Shadow Step
     * enchantment on any piece of worn armor.
     */
    public static boolean tryPassiveDodge(ServerPlayer player, RandomSource random) {
        if (!"Rogue".equals(ClassSelectionAltarBlock.getPlayerClass(player.getUUID()))) {
            return false;
        }

        int level = 0;
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            level = Math.max(level, CustomEnchantmentEffects.getEnchantmentLevel(
                    player.getItemBySlot(slot), player.level(), "rogue_shadow_step"));
        }
        if (level <= 0) return false;

        return random.nextFloat() < 0.05f * level;
    }
}
