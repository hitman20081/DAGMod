package com.github.hitman20081.dagmod.progression;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * Handles network communication for progression system
 * Server → Client packets to sync progression data
 */
public class ProgressionPackets {

    // Packet identifier for progression sync
    public static final Identifier SYNC_PROGRESSION_ID = Identifier.of("dagmod", "sync_progression");

    /**
     * Custom payload for progression sync
     */
    public record ProgressionSyncPayload(NbtCompound data) implements CustomPayload {
        public static final Id<ProgressionSyncPayload> ID = new Id<>(SYNC_PROGRESSION_ID);

        public static final PacketCodec<RegistryByteBuf, ProgressionSyncPayload> CODEC =
                PacketCodec.of(
                        (value, buf) -> buf.writeNbt(value.data),
                        buf -> new ProgressionSyncPayload(buf.readNbt())
                );

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    /**
     * Send progression data to client
     * @param player The player to send to
     * @param data The progression data to sync
     */
    public static void sendProgressionData(ServerPlayerEntity player, PlayerProgressionData data) {
        NbtCompound nbt = data.toNbt();
        ServerPlayNetworking.send(player, new ProgressionSyncPayload(nbt));
    }

    /**
     * Register server-side packet handlers
     * Call this during mod initialization
     */
    public static void registerServerPackets() {
        // Register the payload type for server→client communication
        net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.playS2C().register(
                ProgressionSyncPayload.ID,
                ProgressionSyncPayload.CODEC
        );
    }
}