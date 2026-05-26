package com.github.hitman20081.dagmod.class_system.armor;

import com.github.hitman20081.dagmod.item.ModItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;

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
    public static void tick(ServerPlayer player) {
        if (!shouldRepairThisTick) return;

        ServerLevel world = (ServerLevel) player.level();

        // All conditions must be true: daytime, sky visible, not raining
        if (!world.isBrightOutside()) return;
        if (!world.canSeeSky(player.blockPosition())) return;
        if (world.isRaining()) return;

        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.isEmpty()) continue;
            if (!SOLARWEAVE_ITEMS.contains(stack.getItem())) continue;
            if (stack.getDamageValue() <= 0) continue;

            stack.setDamageValue(stack.getDamageValue() - 1);
        }
    }
}
