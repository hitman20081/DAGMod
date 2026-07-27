package com.github.hitman20081.dagmod.progression;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.Identifier;

/**
 * Handles network communication for progression system
 * Server → Client packets to sync progression data
 */
public class ProgressionPackets {

    // Packet identifier for progression sync
    public static final Identifier SYNC_PROGRESSION_ID = Identifier.fromNamespaceAndPath("dagmod", "sync_progression");

    /**
     * Custom payload for progression sync
     */
    public record ProgressionSyncPayload(CompoundTag data) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<ProgressionSyncPayload> ID = new CustomPacketPayload.Type<>(SYNC_PROGRESSION_ID);

        public static final StreamCodec<RegistryFriendlyByteBuf, ProgressionSyncPayload> CODEC =
                StreamCodec.of(
                        (buf, value) -> buf.writeNbt(value.data),
                        buf -> new ProgressionSyncPayload(buf.readNbt())
                );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return ID;
        }
    }

    /**
     * Send progression data to client
     * @param player The player to send to
     * @param data The progression data to sync
     */
    public static void sendProgressionData(ServerPlayer player, PlayerProgressionData data) {
        CompoundTag nbt = data.toNbt();
        ServerPlayNetworking.send(player, new ProgressionSyncPayload(nbt));
    }

    /**
     * Register server-side packet handlers
     * Call this during mod initialization
     */
    public static void registerServerPackets() {
        // Register the payload type for server→client communication
        net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.clientboundPlay().register(
                ProgressionSyncPayload.ID,
                ProgressionSyncPayload.CODEC
        );
    }
}