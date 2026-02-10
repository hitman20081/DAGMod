package com.github.hitman20081.dagmod.class_system.armor;

import com.github.hitman20081.dagmod.item.ModItems;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Set;

public class SolarMendingHandler {

    private static final Set<Item> SOLARWEAVE_ITEMS = Set.of(
            ModItems.SOLARWEAVE_HELMET,
            ModItems.SOLARWEAVE_CHESTPLATE,
            ModItems.SOLARWEAVE_LEGGINGS,
            ModItems.SOLARWEAVE_BOOTS
    );

    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    private static int tickCounter = 0;
    private static boolean shouldRepairThisTick = false;

    /**
     * Call once per server tick (before iterating players) to update the counter.
     */
    public static void serverTick() {
        tickCounter++;
        if (tickCounter >= 100) { // Every 5 seconds
            tickCounter = 0;
            shouldRepairThisTick = true;
        } else {
            shouldRepairThisTick = false;
        }
    }

    /**
     * Call for each player during the tick loop.
     */
    public static void tick(ServerPlayerEntity player) {
        if (!shouldRepairThisTick) return;

        ServerWorld world = (ServerWorld) player.getEntityWorld();

        // All conditions must be true: daytime, sky visible, not raining
        if (!world.isDay()) return;
        if (!world.isSkyVisible(player.getBlockPos())) return;
        if (world.isRaining()) return;

        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack stack = player.getEquippedStack(slot);
            if (stack.isEmpty()) continue;
            if (!SOLARWEAVE_ITEMS.contains(stack.getItem())) continue;
            if (stack.getDamage() <= 0) continue;

            stack.setDamage(stack.getDamage() - 1);
        }
    }
}
