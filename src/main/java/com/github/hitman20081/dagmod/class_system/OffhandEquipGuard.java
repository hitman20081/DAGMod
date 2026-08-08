package com.github.hitman20081.dagmod.class_system;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.util.ModTags;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

/**
 * Off-hand weapons (dagger/sword, see ModTags.Items.OFFHAND_DAGGERS/OFFHAND_SWORDS) are only
 * usable by Warriors and Rogues -- see OffhandStrikeHandler for the actual attack. Nothing in
 * vanilla stops any item going into the off-hand slot (swap-hands key, inventory drag, etc.),
 * and there's no single choke point to intercept every path an item could land there through, so
 * this sweeps every online player once a tick and corrects it after the fact instead. Cheap (one
 * ItemStack getter + tag check per player per tick) and robust against any equip path, including
 * ones added later. Shields, and everything that isn't a tagged offhand weapon, are never
 * touched -- only items in OFFHAND_DAGGERS/OFFHAND_SWORDS are ever evaluated at all.
 */
public class OffhandEquipGuard {

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                checkPlayer(player);
            }
        });
    }

    private static void checkPlayer(ServerPlayer player) {
        ItemStack offhand = player.getOffhandItem();
        if (offhand.isEmpty()) return;
        if (!offhand.is(ModTags.Items.OFFHAND_DAGGERS) && !offhand.is(ModTags.Items.OFFHAND_SWORDS)) return;

        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUUID());
        if ("Warrior".equals(playerClass) || "Rogue".equals(playerClass)) return;

        player.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
        if (!player.getInventory().add(offhand)) {
            player.drop(offhand, false);
        }

        player.sendOverlayMessage(Component.literal("Only Warriors and Rogues can wield a weapon off-hand!")
                .withStyle(ChatFormatting.RED));
    }
}
