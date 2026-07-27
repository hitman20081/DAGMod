package com.github.hitman20081.dagmod.enchantment;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HeartArmorHandler {

    private static final EquipmentSlot[] ARMOR_SLOTS = {
        EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    private static final int CHECK_INTERVAL = 20;         // run logic every second
    private static final long COMBAT_COOLDOWN = 400L;     // 20 sec without damage = out of combat
    private static final float REGEN_PER_SECOND = 2f;     // 1 yellow heart restored per second OOC

    private static long currentTick = 0;

    // Tracks absorption amount from previous check to detect damage taken
    private static final Map<UUID, Float> prevAbsorption = new HashMap<>();
    // Tracks the last tick a player's absorption was reduced by damage
    private static final Map<UUID, Long> lastDamageTick = new HashMap<>();

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(HeartArmorHandler::onTick);
    }

    private static void onTick(MinecraftServer server) {
        currentTick++;
        if (currentTick % CHECK_INTERVAL != 0) return;

        for (ServerLevel world : server.getAllLevels()) {
            for (ServerPlayer player : world.players()) {
                updateAbsorption(player);
            }
        }
    }

    private static void updateAbsorption(ServerPlayer player) {
        int totalLevels = 0;
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack stack = player.getItemBySlot(slot);
            totalLevels += CustomEnchantmentEffects.getEnchantmentLevel(stack, player.level(), "heart_armor");
        }

        UUID uuid = player.getUUID();

        if (totalLevels <= 0) {
            player.removeEffect(MobEffects.ABSORPTION);
            prevAbsorption.remove(uuid);
            lastDamageTick.remove(uuid);
            return;
        }

        // amp 0 = 2 hearts, amp 1 = 4, amp 2 = 6, amp 3 = 8, amp 4 = 10
        int amplifier = Math.min((totalLevels - 1) / 4, 4);
        float maxAbsorption = (amplifier + 1) * 4f;

        // Maintain the effect at the correct amplifier
        var current = player.getEffect(MobEffects.ABSORPTION);
        if (current == null || current.getAmplifier() != amplifier) {
            player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, -1, amplifier, false, false, true));
        }

        float currentAbs = player.getAbsorptionAmount();
        float prev = prevAbsorption.getOrDefault(uuid, maxAbsorption);

        // Detect damage: absorption went down since last check
        if (currentAbs < prev) {
            lastDamageTick.put(uuid, currentTick);
        }
        prevAbsorption.put(uuid, currentAbs);

        // Only restore yellow hearts when out of combat
        long lastHit = lastDamageTick.getOrDefault(uuid, 0L);
        boolean outOfCombat = (currentTick - lastHit) > COMBAT_COOLDOWN;

        if (outOfCombat && currentAbs < maxAbsorption) {
            player.setAbsorptionAmount(Math.min(currentAbs + REGEN_PER_SECOND, maxAbsorption));
        }
    }
}
