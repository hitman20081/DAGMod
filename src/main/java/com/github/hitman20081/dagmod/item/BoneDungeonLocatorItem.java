package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.DagMod;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.HolderSet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.concurrent.CompletableFuture;

public class BoneDungeonLocatorItem extends Item {

    public BoneDungeonLocatorItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        if (!world.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            ServerLevel serverWorld = (ServerLevel) player.level();
            MinecraftServer server = serverWorld.getServer();
            BlockPos playerPos = serverPlayer.blockPosition();

            serverPlayer.sendSystemMessage(
                Component.literal("Consulting the ancient charts...")
                    .withStyle(ChatFormatting.YELLOW));

            CompletableFuture.runAsync(() -> {
                try {
                    var registry = serverWorld.registryAccess().lookupOrThrow(Registries.STRUCTURE);
                    var entry = registry.get(Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "bone_dungeon")).orElse(null);

                    if (entry == null) {
                        server.execute(() -> serverPlayer.sendSystemMessage(
                            Component.literal("The charts seem incomplete. (Missing bone_dungeon structure)")
                                .withStyle(ChatFormatting.RED)));
                        return;
                    }

                    var structures = HolderSet.direct(entry);
                    ChunkGenerator chunkGenerator = serverWorld.getChunkSource().getGenerator();
                    var result = chunkGenerator.findNearestMapStructure(serverWorld, structures, playerPos, 500, false);

                    server.execute(() -> {
                        if (result == null) {
                            serverPlayer.sendSystemMessage(
                                Component.literal("No bone dungeon found within range. Explore further and try again.")
                                    .withStyle(ChatFormatting.RED));
                        } else {
                            BlockPos dungeonPos = result.getFirst();
                            int distance = (int) Math.sqrt(playerPos.distSqr(dungeonPos));

                            serverPlayer.sendSystemMessage(
                                Component.literal("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                                    .withStyle(ChatFormatting.DARK_GRAY));
                            serverPlayer.sendSystemMessage(
                                Component.literal("  BONE DUNGEON LOCATED")
                                    .withStyle(ChatFormatting.WHITE).withStyle(ChatFormatting.BOLD));
                            serverPlayer.sendSystemMessage(
                                Component.literal("  Coordinates: ")
                                    .withStyle(ChatFormatting.GRAY)
                                    .append(Component.literal("[" + dungeonPos.getX() + ", ~, " + dungeonPos.getZ() + "]")
                                        .withStyle(ChatFormatting.AQUA)));
                            serverPlayer.sendSystemMessage(
                                Component.literal("  Distance: ~" + distance + " blocks")
                                    .withStyle(ChatFormatting.GRAY));
                            serverPlayer.sendSystemMessage(
                                Component.literal("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                                    .withStyle(ChatFormatting.DARK_GRAY));
                        }
                    });
                } catch (Exception e) {
                    server.execute(() -> serverPlayer.sendSystemMessage(
                        Component.literal("The charts are unreadable. (" + e.getMessage() + ")")
                            .withStyle(ChatFormatting.RED)));
                }
            });
        }
        return InteractionResult.SUCCESS;
    }
}
