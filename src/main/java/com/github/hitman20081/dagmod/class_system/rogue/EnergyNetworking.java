package com.github.hitman20081.dagmod.class_system.rogue;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.Identifier;

/**
 * Handles client-server synchronization for Rogue energy
 */
public class EnergyNetworking {
    public static final Identifier ENERGY_SYNC_ID = Identifier.fromNamespaceAndPath("dagmod", "energy_sync");

    /**
     * Sync energy packet - sent from server to client
     */
    public record EnergySyncPayload(int energy) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<EnergySyncPayload> ID =
                new CustomPacketPayload.Type<>(ENERGY_SYNC_ID);

        public static final StreamCodec<RegistryFriendlyByteBuf, EnergySyncPayload> CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.INT, EnergySyncPayload::energy,
                        EnergySyncPayload::new
                );

        @Override
        public Type<? extends CustomPacketPayload> type() {
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
    public static void syncEnergyToClient(ServerPlayer player, int energy) {
        ServerPlayNetworking.send(player, new EnergySyncPayload(energy));
    }
}