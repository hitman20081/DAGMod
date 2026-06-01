package com.github.hitman20081.dagmod.class_system.warrior;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public class CooldownNetworking {

    public static final Identifier COOLDOWN_SYNC_ID = Identifier.fromNamespaceAndPath("dagmod", "cooldown_sync");

    /**
     * Remaining ticks for each ability in WarriorAbility enum order:
     * rage, shieldBash, warCry, battleShout, whirlwind, ironSkin
     * 0 = ready.
     */
    public record CooldownSyncPayload(
            int rage, int shieldBash, int warCry,
            int battleShout, int whirlwind, int ironSkin
    ) implements CustomPacketPayload {

        public static final Type<CooldownSyncPayload> ID = new Type<>(COOLDOWN_SYNC_ID);

        public static final StreamCodec<RegistryFriendlyByteBuf, CooldownSyncPayload> CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.INT, CooldownSyncPayload::rage,
                        ByteBufCodecs.INT, CooldownSyncPayload::shieldBash,
                        ByteBufCodecs.INT, CooldownSyncPayload::warCry,
                        ByteBufCodecs.INT, CooldownSyncPayload::battleShout,
                        ByteBufCodecs.INT, CooldownSyncPayload::whirlwind,
                        ByteBufCodecs.INT, CooldownSyncPayload::ironSkin,
                        CooldownSyncPayload::new
                );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return ID;
        }
    }

    public static void registerPayloads() {
        PayloadTypeRegistry.clientboundPlay().register(CooldownSyncPayload.ID, CooldownSyncPayload.CODEC);
    }

    public static void syncCooldownsToClient(ServerPlayer player) {
        ServerPlayNetworking.send(player, new CooldownSyncPayload(
                CooldownManager.getRemainingCooldown(player, WarriorAbility.RAGE),
                CooldownManager.getRemainingCooldown(player, WarriorAbility.SHIELD_BASH),
                CooldownManager.getRemainingCooldown(player, WarriorAbility.WAR_CRY),
                CooldownManager.getRemainingCooldown(player, WarriorAbility.BATTLE_SHOUT),
                CooldownManager.getRemainingCooldown(player, WarriorAbility.WHIRLWIND),
                CooldownManager.getRemainingCooldown(player, WarriorAbility.IRON_SKIN)
        ));
    }
}
