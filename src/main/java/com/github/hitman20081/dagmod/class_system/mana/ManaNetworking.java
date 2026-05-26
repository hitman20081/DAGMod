package com.github.hitman20081.dagmod.class_system.mana;

import com.github.hitman20081.dagmod.DagMod;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.Identifier;

public class ManaNetworking {
    public static final Identifier MANA_UPDATE_ID = Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "mana_update");

    // ADD THIS METHOD
    public static void registerPayloads() {
        PayloadTypeRegistry.clientboundPlay().register(ManaUpdatePayload.ID, ManaUpdatePayload.CODEC);
    }

    public static void sendManaUpdate(ServerPlayer player, float currentMana, int maxMana) {
        ServerPlayNetworking.send(player, new ManaUpdatePayload(currentMana, maxMana));
    }

    public record ManaUpdatePayload(float currentMana, int maxMana) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<ManaUpdatePayload> ID = new CustomPacketPayload.Type<>(MANA_UPDATE_ID);
        public static final StreamCodec<RegistryFriendlyByteBuf, ManaUpdatePayload> CODEC = StreamCodec.of(
                (buf, value) -> {
                    buf.writeFloat(value.currentMana);
                    buf.writeInt(value.maxMana);
                },
                (buf) -> new ManaUpdatePayload(buf.readFloat(), buf.readInt())
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return ID;
        }
    }
}