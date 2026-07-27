package com.github.hitman20081.dagmod.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record QuestRequestPacket() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<QuestRequestPacket> ID =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("dagmod", "quest_request"));

    public static final StreamCodec<FriendlyByteBuf, QuestRequestPacket> CODEC =
            StreamCodec.of((value, buf) -> {}, buf -> new QuestRequestPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}