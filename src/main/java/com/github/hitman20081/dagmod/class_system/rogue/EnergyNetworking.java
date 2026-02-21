package com.github.hitman20081.dagmod.class_system.rogue;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * Handles client-server synchronization for Rogue energy
 */
public class EnergyNetworking {
    public static final Identifier ENERGY_SYNC_ID = Identifier.of("dagmod", "energy_sync");

    /**
     * Sync energy packet - sent from server to client
     */
    public record EnergySyncPayload(int energy) implements CustomPayload {
        public static final CustomPayload.Id<EnergySyncPayload> ID =
                new CustomPayload.Id<>(ENERGY_SYNC_ID);

        public static final PacketCodec<RegistryByteBuf, EnergySyncPayload> CODEC =
                PacketCodec.tuple(
                        PacketCodecs.INTEGER, EnergySyncPayload::energy,
                        EnergySyncPayload::new
                );

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    /**
     * Register networking handlers
     */
    public static void registerServerHandlers() {
        // Server doesn't need to receive energy sync packets
        // Energy is managed server-side and synced to clients
    }

    /**
     * Sync player's energy to their client
     */
    public static void syncEnergyToClient(ServerPlayerEntity player, int energy) {
        ServerPlayNetworking.send(player, new EnergySyncPayload(energy));
    }
}