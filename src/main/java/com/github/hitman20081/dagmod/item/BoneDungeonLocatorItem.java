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
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BoneDungeonLocatorItem extends Item {

    // How far below terrain the dungeon entrance sits (matches start_height: absolute -15
    // combined with project_start_to_heightmap: WORLD_SURFACE_WG in bone_dungeon.json).
    private static final int SURFACE_DEPTH_OFFSET = 15;

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

        var structureSetRegistry = serverWorld.registryAccess().lookupOrThrow(Registries.STRUCTURE_SET);

        var setHolder = structureSetRegistry
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

        // Load competing structure placements for exclusion zone checks.
        RandomSpreadStructurePlacement hallSpawnPlacement = null;
        RandomSpreadStructurePlacement villagePlacement = null;
        var hallHolder = structureSetRegistry.get(Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "hall_spawn")).orElse(null);
        var villageHolder = structureSetRegistry.get(Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "village_npc_set")).orElse(null);
        if (hallHolder != null && hallHolder.value().placement() instanceof RandomSpreadStructurePlacement p) hallSpawnPlacement = p;
        if (villageHolder != null && villageHolder.value().placement() instanceof RandomSpreadStructurePlacement p) villagePlacement = p;

        final RandomSpreadStructurePlacement finalHall = hallSpawnPlacement;
        final RandomSpreadStructurePlacement finalVillage = villagePlacement;

        serverPlayer.sendSystemMessage(
                Component.literal("Consulting the ancient charts...").withStyle(ChatFormatting.YELLOW));

        // All pure math — no chunk loading, no server thread blocking.
        CompletableFuture.runAsync(() -> {
            try {
                int spacing = spreadPlacement.spacing();
                long worldSeed = serverWorld.getSeed();
                int playerChunkX = playerPos.getX() >> 4;
                int playerChunkZ = playerPos.getZ() >> 4;

                List<BlockPos> candidates = new ArrayList<>();
                for (int dr = -SEARCH_REGIONS; dr <= SEARCH_REGIONS; dr++) {
                    for (int dc = -SEARCH_REGIONS; dc <= SEARCH_REGIONS; dc++) {
                        ChunkPos cp = spreadPlacement.getPotentialStructureChunk(
                                worldSeed,
                                playerChunkX + dr * spacing,
                                playerChunkZ + dc * spacing);

                        // Filter 1: biome must be eligible (no chunk loading — pure noise).
                        var biome = serverWorld.getNoiseBiome(cp.getMiddleBlockX() >> 2, 16, cp.getMiddleBlockZ() >> 2);
                        if (!biome.is(BiomeTags.IS_BADLANDS) && !biome.is(Biomes.DESERT)) continue;

                        // Filter 2: exclusion zones — replicate bone_dungeon_set's exclusion logic.
                        // chunk_count=16 for hall_spawn, chunk_count=6 for village_npc_set.
                        if (isExcluded(cp, worldSeed, finalHall, 16)) continue;
                        if (isExcluded(cp, worldSeed, finalVillage, 6)) continue;

                        // Compute accurate entrance Y using terrain noise (no chunk loading).
                        int surfaceY = serverWorld.getChunkSource().getGenerator().getBaseHeight(
                                cp.getMiddleBlockX(), cp.getMiddleBlockZ(),
                                Heightmap.Types.WORLD_SURFACE_WG,
                                serverWorld,
                                serverWorld.getChunkSource().randomState());
                        int dungeonY = surfaceY - SURFACE_DEPTH_OFFSET;

                        candidates.add(new BlockPos(cp.getMiddleBlockX(), dungeonY, cp.getMiddleBlockZ()));
                    }
                }

                if (candidates.isEmpty()) {
                    server.execute(() -> {
                        serverPlayer.sendSystemMessage(
                                Component.literal("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━").withStyle(ChatFormatting.DARK_GRAY));
                        serverPlayer.sendSystemMessage(
                                Component.literal("  NO DUNGEON IN RANGE").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
                        serverPlayer.sendSystemMessage(
                                Component.literal("  No eligible location within ~6400 blocks.").withStyle(ChatFormatting.YELLOW));
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

    /**
     * Returns true if the bone dungeon candidate at {@code boneChunk} is within
     * {@code chunkCount} chunks (Chebyshev) of any candidate from {@code otherPlacement}.
     * Pure math — identical to the exclusion zone logic in MultiExclusionRandomSpreadPlacement.
     */
    private static boolean isExcluded(ChunkPos boneChunk, long seed,
                                       RandomSpreadStructurePlacement otherPlacement,
                                       int chunkCount) {
        if (otherPlacement == null) return false;
        int os = otherPlacement.spacing();
        // How many other-structure regions can have a candidate within chunkCount chunks of boneChunk?
        int range = (int) Math.ceil((chunkCount + os - 1.0) / os);
        // getMinBlockX() = chunkX * 16, so >> 4 gives the chunk coordinate.
        int cx = boneChunk.getMinBlockX() >> 4;
        int cz = boneChunk.getMinBlockZ() >> 4;
        for (int dr = -range; dr <= range; dr++) {
            for (int dc = -range; dc <= range; dc++) {
                ChunkPos other = otherPlacement.getPotentialStructureChunk(seed, cx + dr * os, cz + dc * os);
                int ox = other.getMinBlockX() >> 4;
                int oz = other.getMinBlockZ() >> 4;
                if (Math.max(Math.abs(ox - cx), Math.abs(oz - cz)) <= chunkCount) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void sendResult(ServerPlayer player, BlockPos nearest, int distance) {
        player.sendSystemMessage(
                Component.literal("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━").withStyle(ChatFormatting.DARK_GRAY));
        player.sendSystemMessage(
                Component.literal("  BONE DUNGEON LOCATED").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));

        String coordStr = "[" + nearest.getX() + ", " + nearest.getY() + ", " + nearest.getZ() + "]";
        player.sendSystemMessage(
                Component.literal("  Go to: ").withStyle(ChatFormatting.GRAY)
                        .append(Component.literal(coordStr).withStyle(ChatFormatting.AQUA))
                        .append(Component.literal("  (~" + distance + " blocks)").withStyle(ChatFormatting.GRAY)));

        player.sendSystemMessage(
                Component.literal("  Dungeon entrance is underground at that depth")
                        .withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(
                Component.literal("  Tip: /tp " + nearest.getX() + " " + nearest.getY() + " " + nearest.getZ())
                        .withStyle(ChatFormatting.GRAY));

        player.sendSystemMessage(
                Component.literal("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━").withStyle(ChatFormatting.DARK_GRAY));
    }
}
