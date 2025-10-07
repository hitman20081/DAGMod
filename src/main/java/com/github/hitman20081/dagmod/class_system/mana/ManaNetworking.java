package com.github.hitman20081.dagmod.class_system.mana;

import com.github.hitman20081.dagmod.DagMod;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ManaNetworking {
    public static final Identifier MANA_UPDATE_ID = Identifier.of(DagMod.MOD_ID, "mana_update");

    // ADD THIS METHOD
    public static void registerPayloads() {
        PayloadTypeRegistry.playS2C().register(ManaUpdatePayload.ID, ManaUpdatePayload.CODEC);
    }

    public static void sendManaUpdate(ServerPlayerEntity player, float currentMana, int maxMana) {
        ServerPlayNetworking.send(player, new ManaUpdatePayload(currentMana, maxMana));
    }

    public record ManaUpdatePayload(float currentMana, int maxMana) implements CustomPayload {
        public static final CustomPayload.Id<ManaUpdatePayload> ID = new CustomPayload.Id<>(MANA_UPDATE_ID);
        public static final PacketCodec<RegistryByteBuf, ManaUpdatePayload> CODEC = PacketCodec.of(
                (value, buf) -> {
                    buf.writeFloat(value.currentMana);
                    buf.writeInt(value.maxMana);
                },
                (buf) -> new ManaUpdatePayload(buf.readFloat(), buf.readInt())
        );

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
}