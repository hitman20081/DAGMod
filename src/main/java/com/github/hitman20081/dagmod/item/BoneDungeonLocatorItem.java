package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.DagMod;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BoneDungeonLocatorItem extends Item {

    // Dungeon always starts at absolute Y=-15 because STRUCTURE_STARTS runs before
    // terrain generation, so project_start_to_heightmap: WORLD_SURFACE_WG returns 0.
    static final int DUNGEON_START_Y = -15;

    // spacing=40 × SEARCH_REGIONS=10 = ±400 chunks = ±6400 blocks from player
    private static final int SEARCH_REGIONS = 10;

    public BoneDungeonLocatorItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        if (!world.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            locate((ServerLevel) player.level(), serverPlayer);
        }
        return InteractionResult.SUCCESS;
    }

    public static void locate(ServerLevel serverWorld, ServerPlayer serverPlayer) {
        MinecraftServer server = serverWorld.getServer();
        BlockPos playerPos = serverPlayer.blockPosition();

        var setHolder = serverWorld.registryAccess()
                .lookupOrThrow(Registries.STRUCTURE_SET)
                .get(Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "bone_dungeon_set"))
                .orElse(null);
        if (setHolder == null) {
            serverPlayer.sendSystemMessage(
                    Component.literal("The charts seem incomplete. (bone_dungeon_set not found)")
                            .withStyle(ChatFormatting.RED));
            return;
        }

        StructurePlacement placement = setHolder.value().placement();
        if (!(placement instanceof RandomSpreadStructurePlacement spreadPlacement)) {
            serverPlayer.sendSystemMessage(
                    Component.literal("The charts seem incomplete. (Unexpected placement type)")
                            .withStyle(ChatFormatting.RED));
            return;
        }

        serverPlayer.sendSystemMessage(
                Component.literal("Consulting the ancient charts...").withStyle(ChatFormatting.YELLOW));

        // Pure math — no chunk loading, no server thread blocking.
        CompletableFuture.runAsync(() -> {
            try {
                int spacing = spreadPlacement.spacing();
                long worldSeed = serverWorld.getSeed();
                int playerChunkX = playerPos.getX() >> 4;
                int playerChunkZ = playerPos.getZ() >> 4;

                // For every grid region within SEARCH_REGIONS, compute the candidate chunk
                // and verify the biome there is actually eligible (desert/badlands). This
                // filters out candidates that fall outside the biome, which would otherwise
                // point to empty underground and look like vanilla dungeon spawner rooms.
                List<BlockPos> candidates = new ArrayList<>();
                for (int dr = -SEARCH_REGIONS; dr <= SEARCH_REGIONS; dr++) {
                    for (int dc = -SEARCH_REGIONS; dc <= SEARCH_REGIONS; dc++) {
                        ChunkPos cp = spreadPlacement.getPotentialStructureChunk(
                                worldSeed,
                                playerChunkX + dr * spacing,
                                playerChunkZ + dc * spacing);
                        // getNoiseBiome uses quart coords (>> 2); Y=16 quarts = block Y=64 (surface)
                        var biome = serverWorld.getNoiseBiome(cp.getMiddleBlockX() >> 2, 16, cp.getMiddleBlockZ() >> 2);
                        if (biome.is(BiomeTags.IS_BADLANDS) || biome.is(Biomes.DESERT)) {
                            candidates.add(new BlockPos(cp.getMiddleBlockX(), DUNGEON_START_Y, cp.getMiddleBlockZ()));
                        }
                    }
                }

                if (candidates.isEmpty()) {
                    server.execute(() -> {
                        serverPlayer.sendSystemMessage(
                                Component.literal("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━").withStyle(ChatFormatting.DARK_GRAY));
                        serverPlayer.sendSystemMessage(
                                Component.literal("  NO DUNGEON IN RANGE").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
                        serverPlayer.sendSystemMessage(
                                Component.literal("  No badlands or desert within ~6400 blocks.").withStyle(ChatFormatting.YELLOW));
                        serverPlayer.sendSystemMessage(
                                Component.literal("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━").withStyle(ChatFormatting.DARK_GRAY));
                    });
                    return;
                }

                candidates.sort(Comparator.comparingDouble(p -> playerPos.distSqr(p)));
                BlockPos nearest = candidates.get(0);
                final int distance = (int) Math.sqrt(playerPos.distSqr(nearest));

                server.execute(() -> sendResult(serverPlayer, nearest, distance));

            } catch (Exception e) {
                DagMod.LOGGER.error("BoneDungeonLocator: search failed", e);
                server.execute(() -> serverPlayer.sendSystemMessage(
                        Component.literal("The charts are unreadable. (" + e.getMessage() + ")")
                                .withStyle(ChatFormatting.RED)));
            }
        });
    }

    public static void sendResult(ServerPlayer player, BlockPos nearest, int distance) {
        player.sendSystemMessage(
                Component.literal("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━").withStyle(ChatFormatting.DARK_GRAY));
        player.sendSystemMessage(
                Component.literal("  BONE DUNGEON LOCATED").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));

        String coordStr = "[" + nearest.getX() + ", " + DUNGEON_START_Y + ", " + nearest.getZ() + "]";
        player.sendSystemMessage(
                Component.literal("  Go to: ").withStyle(ChatFormatting.GRAY)
                        .append(Component.literal(coordStr).withStyle(ChatFormatting.AQUA))
                        .append(Component.literal("  (~" + distance + " blocks)").withStyle(ChatFormatting.GRAY)));

        player.sendSystemMessage(
                Component.literal("  Dungeon is underground — use spectator or dig down")
                        .withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(
                Component.literal("  Tip: /tp " + nearest.getX() + " " + DUNGEON_START_Y + " " + nearest.getZ())
                        .withStyle(ChatFormatting.GRAY));

        player.sendSystemMessage(
                Component.literal("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━").withStyle(ChatFormatting.DARK_GRAY));
    }
}
