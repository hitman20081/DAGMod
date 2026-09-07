package com.github.hitman20081.dagmod.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Sent when the player scrolls the mouse wheel over a Coin Pouch slot (see CoinPouchMouseActions).
 * containerId + slotIndex let the server resolve the exact server-side ItemStack the client was
 * looking at, since scrolling only mutates the client's own copy of the stack otherwise -- the
 * server-authoritative withdraw logic in CoinPouchItem.use() never sees that change without this.
 */
public record CoinPouchScrollPacket(int containerId, int slotIndex, int direction) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<CoinPouchScrollPacket> ID =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("dagmod", "coin_pouch_scroll"));

    public static final StreamCodec<FriendlyByteBuf, CoinPouchScrollPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, CoinPouchScrollPacket::containerId,
            ByteBufCodecs.INT, CoinPouchScrollPacket::slotIndex,
            ByteBufCodecs.INT, CoinPouchScrollPacket::direction,
            CoinPouchScrollPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
