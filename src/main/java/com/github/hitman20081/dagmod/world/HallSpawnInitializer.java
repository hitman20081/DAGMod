package com.github.hitman20081.dagmod.world;

import com.github.hitman20081.dagmod.DagMod;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HallSpawnInitializer {

    public static void register() {
        ServerLifecycleEvents.SERVER_STARTED.register(HallSpawnInitializer::onServerStarted);
    }

    private static void onServerStarted(MinecraftServer server) {
        // Marker file ensures this only runs once per world
        Path markerFile = server.getSavePath(WorldSavePath.ROOT).resolve("dagmod_hall_spawn.flag");
        if (Files.exists(markerFile)) return;

        ServerWorld overworld = server.getOverworld();

        BlockPos hallPos = locateHall(overworld);
        if (hallPos == null) {
            DagMod.LOGGER.warn("[DAGMod] Could not locate Hall of Champions — world spawn not adjusted");
            return;
        }

        BlockPos spawnPos = findSafeSpawn(overworld, hallPos);
        setWorldSpawn(server, spawnPos);

        try {
            Files.writeString(markerFile, "initialized");
        } catch (IOException e) {
            DagMod.LOGGER.error("[DAGMod] Failed to write hall spawn marker", e);
        }

        DagMod.LOGGER.info("[DAGMod] World spawn set near Hall of Champions at " + spawnPos);
    }

    @Nullable
    private static BlockPos locateHall(ServerWorld overworld) {
        // Same pattern as BoneDungeonLocatorItem
        var registry = overworld.getRegistryManager().getOrThrow(RegistryKeys.STRUCTURE);
        var entry = registry.getEntry(Identifier.of(DagMod.MOD_ID, "hall_of_champions")).orElse(null);
        if (entry == null) return null;

        var result = overworld.getChunkManager().getChunkGenerator()
                .locateStructure(overworld, RegistryEntryList.of(entry), BlockPos.ORIGIN, 100, false);

        return result != null ? result.getFirst() : null;
    }

    /**
     * Walks offsets around the Hall center until it finds solid ground with 2 air blocks above.
     */
    private static BlockPos findSafeSpawn(ServerWorld world, BlockPos hallPos) {
        int[] offsets = {20, -20, 25, -25, 15, -15, 0};
        for (int dx : offsets) {
            for (int dz : offsets) {
                int x = hallPos.getX() + dx;
                int z = hallPos.getZ() + dz;
                int y = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
                BlockPos feet = new BlockPos(x, y, z);
                if (world.getBlockState(feet).isAir() && world.getBlockState(feet.up()).isAir()) {
                    return feet;
                }
            }
        }
        // Fallback: surface at Hall center
        int y = world.getTopY(Heightmap.Type.WORLD_SURFACE, hallPos.getX(), hallPos.getZ());
        return new BlockPos(hallPos.getX(), y, hallPos.getZ());
    }

    /**
     * Sets world spawn via the Brigadier dispatcher — avoids version-specific world property APIs.
     */
    private static void setWorldSpawn(MinecraftServer server, BlockPos pos) {
        try {
            var source = server.getCommandSource();
            var dispatcher = server.getCommandManager().getDispatcher();
            String cmd = "setworldspawn " + pos.getX() + " " + pos.getY() + " " + pos.getZ();
            dispatcher.execute(dispatcher.parse(cmd, source));
        } catch (Exception e) {
            DagMod.LOGGER.error("[DAGMod] Failed to set world spawn", e);
        }
    }
}
