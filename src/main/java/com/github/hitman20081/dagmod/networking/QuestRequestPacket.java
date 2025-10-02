package com.github.hitman20081.dagmod.networking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record QuestRequestPacket() implements CustomPayload {

    public static final CustomPayload.Id<QuestRequestPacket> ID =
            new CustomPayload.Id<>(Identifier.of("dagmod", "quest_request"));

    public static final PacketCodec<PacketByteBuf, QuestRequestPacket> CODEC =
            PacketCodec.of((value, buf) -> {}, buf -> new QuestRequestPacket());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}