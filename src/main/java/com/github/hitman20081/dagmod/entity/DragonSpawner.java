package com.github.hitman20081.dagmod.entity;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.block.DragonEggBlock;
import com.github.hitman20081.dagmod.block.ModBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BiomeTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.util.RandomSource;

import java.util.HashSet;

import java.util.Set;

/**
 * Handles spawning of Wild Dragons in mountain biomes
 * Wild Dragons (tameable) spawn very rarely (1 per ~5000 blocks) on high mountains (Y>160)
 * These are separate from the Dragon Guardian boss which will spawn in Dragon Realm
 *
 * LORE: Dragons are known to gather materials from around the world to build their nests,
 * hoarding precious metals, shiny blocks, and natural materials they find during their travels.
 *
 * Biome-specific dragon variants:
 * - Stony Peaks/Mountain biomes: Red Dragon (default)
 * - Frozen Peaks/Jagged Peaks (cold): Ice Dragon
 * - Future: Lava Dragon for volcanic biomes
 */
public class DragonSpawner {

    private static final int MIN_SPAWN_HEIGHT = 160;
    private static final int SPAWN_ATTEMPT_INTERVAL = 600; // 30 seconds (20 ticks/sec * 30)
    private static final int SPAWN_CHECK_RADIUS = 80; // Check 80 block radius from spawn attempt
    private static final int MIN_DRAGON_DISTANCE = 256; // Minimum 256 blocks between dragons
    private static final double INITIAL_SPAWN_CHANCE = 0.75; // 75% chance — fast initial/post-cooldown spawn (~40s avg)
    private static final int RESPAWN_COOLDOWN_TICKS = 12000; // 10 minutes after a dragon is killed
    private static final int MAX_WILD_DRAGONS = 8; // Maximum wild dragons in the overworld

    // Track spawned dragon locations to prevent overcrowding
    private static final Set<ChunkPos> dragonChunks = new HashSet<>();
    private static final RandomSource random = RandomSource.create();

    // -1 means no death recorded this session (use fast initial spawn)
    private static long lastDragonDeathTime = -1L;

    /**
     * Attempt to spawn a dragon in the world
     * Called periodically from server tick
     */
    public static void trySpawnDragon(ServerLevel world) {
        // Only spawn in Overworld
        if (world.dimension() != Level.OVERWORLD) {
            return;
        }

        // Enforce 10-minute respawn cooldown after a wild dragon death
        if (lastDragonDeathTime >= 0) {
            long elapsed = world.getGameTime() - lastDragonDeathTime;
            if (elapsed < RESPAWN_COOLDOWN_TICKS) {
                return; // Still cooling down
            }
            // Cooldown expired — reset so the next attempt uses the fast spawn chance
            lastDragonDeathTime = -1L;
        }

        // Fast spawn chance: quick on server start or after cooldown expires
        if (random.nextDouble() > INITIAL_SPAWN_CHANCE) {
            return;
        }

        // Global cap: count all wild dragons currently loaded in the world
        int currentDragons = world.getEntities(ModEntities.WILD_DRAGON, dragon -> true).size();
        if (currentDragons >= MAX_WILD_DRAGONS) {
            return;
        }

        // Get a random player to use as spawn center (ensures dragons spawn near active areas)
        if (world.players().isEmpty()) {
            return;
        }

        var players = world.players();
        var randomPlayer = players.get(random.nextInt(players.size()));
        BlockPos playerPos = randomPlayer.blockPosition();

        // Find a suitable spawn location near this player
        BlockPos spawnPos = findSuitableSpawnLocation(world, playerPos);

        if (spawnPos != null) {
            // Check if there's already a dragon nearby (tracked spawns)
            ChunkPos chunkPos = ChunkPos.containing(spawnPos);
            if (isDragonNearby(chunkPos)) {
                DagMod.LOGGER.debug("Dragon spawn attempt cancelled - tracked dragon nearby at chunk {}", chunkPos);
                return;
            }

            // Also check for ANY Wild Dragons in the world (including manually spawned ones)
            var nearbyDragons = world.getEntitiesOfClass(WildDragonEntity.class,
                    new net.minecraft.world.phys.AABB(spawnPos).inflate(MIN_DRAGON_DISTANCE, 128, MIN_DRAGON_DISTANCE),
                    dragon -> true);

            if (!nearbyDragons.isEmpty()) {
                DagMod.LOGGER.debug("Wild Dragon spawn attempt cancelled - found {} existing dragon(s) within {} blocks",
                        nearbyDragons.size(), MIN_DRAGON_DISTANCE);
                return;
            }

            // Spawn the Wild Dragon (tameable)
            WildDragonEntity dragon = new WildDragonEntity(ModEntities.WILD_DRAGON, world);
            dragon.snapTo(spawnPos, 0.0f, 0.0f);

            // Determine dragon variant based on biome
            Biome biome = world.getBiome(spawnPos).value();
            var biomeKey = world.getBiome(spawnPos).unwrapKey();

            DragonGuardianEntity.DragonVariant variant = null; // Default to null, so we only spawn if a condition is met
            String dragonType = "Unknown";

            if (biomeKey.isPresent()) {
                String biomeId = biomeKey.get().identifier().toString();

                // Check for cold mountain biomes → Ice Dragon
                if (biomeId.equals("minecraft:frozen_peaks") || biomeId.equals("minecraft:jagged_peaks")) {
                    variant = DragonGuardianEntity.DragonVariant.ICE;
                    dragonType = "Ice";
                    DagMod.LOGGER.info("Spawning ICE dragon variant in cold biome: {}", biomeId);
                }
                // Check for stony peaks → Wind Dragon
                else if (biomeId.equals("minecraft:stony_peaks")) {
                    variant = DragonGuardianEntity.DragonVariant.WIND;
                    dragonType = "Wind";
                    DagMod.LOGGER.info("Spawning WIND dragon variant in stony biome: {}", biomeId);
                }
                // Check for badlands → Lava Dragon (placeholder)
                else if (world.getBiome(spawnPos).is(BiomeTags.IS_BADLANDS)) {
                    variant = DragonGuardianEntity.DragonVariant.LAVA;
                    dragonType = "Lava";
                    DagMod.LOGGER.info("Spawning LAVA dragon variant in badlands biome: {}", biomeId);
                }
                // Default to Earth Dragon in other mountain biomes
                else if (world.getBiome(spawnPos).is(BiomeTags.IS_MOUNTAIN)) {
                    variant = DragonGuardianEntity.DragonVariant.EARTH;
                    dragonType = "Earth";
                    DagMod.LOGGER.info("Spawning EARTH dragon variant in mountain biome: {}", biomeId);
                }
            }

            if (variant == null) {
                DagMod.LOGGER.debug("No suitable dragon variant for biome, spawn cancelled.");
                return; // Do not spawn a dragon if no variant is chosen
            }

            dragon.setVariant(variant);
            dragon.finalizeSpawn(world, world.getCurrentDifficultyAt(spawnPos), EntitySpawnReason.NATURAL, null);

            if (world.addFreshEntity(dragon)) {
                dragonChunks.add(chunkPos);

                // Generate nest with eggs near the dragon
                BlockPos nestCenter = spawnPos.below(3); // Store the nest center
                generateDragonNest(world, nestCenter, variant); // Nest at ground level
                dragon.setNestPosition(nestCenter); // Set the nest position on the spawned dragon

                DagMod.LOGGER.info("{} Dragon spawned with nest at {} (Y: {}) near player {}",
                        dragonType, spawnPos, spawnPos.getY(), randomPlayer.getName().getString());
            }
        }
    }


    private static final int MAX_REALM_DRAGONS = 5; // Max ambient red dragons in Dragon Realm
    private static final int REALM_SPAWN_RADIUS = 120; // Spawn within 120 blocks of a player
    private static final double REALM_SPAWN_CHANCE = 0.15; // 15% chance per attempt

    /**
     * Attempt to spawn ambient Red DragonGuardianEntity in the Dragon Realm.
     * Called periodically from server tick alongside overworld spawner.
     */
    public static void trySpawnDragonInDragonRealm(ServerLevel world) {
        // Only spawn in the Dragon Realm
        if (world.dimension() != com.github.hitman20081.dagmod.dragon_realm.portal.DragonRealmTeleporter.DRAGON_REALM) {
            return;
        }

        if (world.players().isEmpty()) {
            return;
        }

        if (random.nextDouble() > REALM_SPAWN_CHANCE) {
            return;
        }

        // Count all DragonGuardianEntity in the realm (includes boss + ambient)
        int currentDragons = world.getEntities(ModEntities.DRAGON_GUARDIAN, dragon -> true).size();
        if (currentDragons >= MAX_REALM_DRAGONS) {
            return;
        }

        // Pick a random player as spawn center
        var players = world.players();
        var randomPlayer = players.get(random.nextInt(players.size()));
        BlockPos playerPos = randomPlayer.blockPosition();

        // Find a spawn location away from the player (40-120 blocks)
        for (int attempt = 0; attempt < 20; attempt++) {
            int offsetX = (40 + random.nextInt(REALM_SPAWN_RADIUS - 40)) * (random.nextBoolean() ? 1 : -1);
            int offsetZ = (40 + random.nextInt(REALM_SPAWN_RADIUS - 40)) * (random.nextBoolean() ? 1 : -1);
            BlockPos searchPos = playerPos.offset(offsetX, 0, offsetZ);

            BlockPos surfacePos = world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, searchPos);
            BlockPos spawnPos = surfacePos.above(8); // Spawn in the air

            // Check for clear space
            if (!world.getBlockState(spawnPos).isAir()) {
                continue;
            }

            DragonGuardianEntity dragon = ModEntities.DRAGON_GUARDIAN.create(world, EntitySpawnReason.NATURAL);
            if (dragon == null) {
                return;
            }

            dragon.snapTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, random.nextFloat() * 360f, 0f);
            dragon.setVariant(DragonGuardianEntity.DragonVariant.RED);
            dragon.finalizeSpawn(world, world.getCurrentDifficultyAt(spawnPos), EntitySpawnReason.NATURAL, null);

            if (world.addFreshEntity(dragon)) {
                DagMod.LOGGER.info("Ambient RED Dragon spawned in Dragon Realm at {} near player {}",
                        spawnPos, randomPlayer.getName().getString());
            }
            return;
        }
    }

    /**
     * Get a random precious block that dragons might hoard (very rare)
     */
    private static Block getRandomPreciousBlock() {
        int rand = random.nextInt(100);
        if (rand < 35) return Blocks.GOLD_BLOCK;
        if (rand < 60) return Blocks.IRON_BLOCK;
        if (rand < 75) return Blocks.COPPER_BLOCK;
        if (rand < 85) return Blocks.EMERALD_BLOCK;
        if (rand < 93) return Blocks.DIAMOND_BLOCK;
        return Blocks.LAPIS_BLOCK;
    }

    /**
     * Get a random shiny/metallic block that attracts dragons (rare)
     */
    private static Block getRandomShinyBlock() {
        int rand = random.nextInt(100);
        if (rand < 40) return Blocks.IRON_BLOCK;
        if (rand < 70) return Blocks.COPPER_BLOCK;
        if (rand < 85) return Blocks.GOLD_BLOCK;
        return Blocks.QUARTZ_BLOCK;
    }

    /**
     * Get a random natural material (common)
     */
    private static Block getRandomNaturalBlock() {
        int rand = random.nextInt(100);
        if (rand < 30) return Blocks.STONE;
        if (rand < 50) return Blocks.COBBLESTONE;
        if (rand < 65) return Blocks.STONE_BRICKS;
        if (rand < 75) return Blocks.ANDESITE;
        if (rand < 85) return Blocks.DIORITE;
        return Blocks.GRANITE;
    }

    /**
     * Get a random log type that dragons might gather (uncommon)
     */
    private static Block getRandomLog() {
        int rand = random.nextInt(100);
        if (rand < 30) return Blocks.OAK_LOG;
        if (rand < 50) return Blocks.BIRCH_LOG;
        if (rand < 65) return Blocks.SPRUCE_LOG;
        if (rand < 80) return Blocks.DARK_OAK_LOG;
        if (rand < 90) return Blocks.ACACIA_LOG;
        return Blocks.JUNGLE_LOG;
    }

    /**
     * Generate an enhanced dragon nest with eggs at the specified location
     * Creates a natural-looking bowl-shaped nest with varied materials and decorations
     * Dragons gather materials from around the world: precious metals, shiny blocks, logs, and stone
     * @param variant The dragon variant - determines the variant of eggs placed in the nest
     */
    private static void generateDragonNest(ServerLevel world, BlockPos centerPos, DragonGuardianEntity.DragonVariant variant) {
        // Build nest in multiple layers for depth and realism
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                double distance = Math.sqrt(x * x + z * z);

                // Only build within circular bounds (radius ~5)
                if (distance <= 5.0) {

                    // === LAYER 1: Foundation (buried layer for stability) ===
                    BlockPos foundationPos = centerPos.offset(x, -1, z);
                    if (distance <= 4.0) {
                        world.setBlock(foundationPos, Blocks.STONE.defaultBlockState(), 3);
                    }

                    // === LAYER 2: Main platform with bowl shape ===
                    BlockPos basePos = centerPos.offset(x, 0, z);

                    if (distance <= 4.5) {
                        // Inner bowl (center depression for eggs)
                        if (distance <= 1.5) {
                            // Very center: Magma Block (heat-resistant) or rare precious blocks
                            if (random.nextDouble() < 0.15) {
                                // 15% chance for precious block in center
                                world.setBlock(basePos, getRandomPreciousBlock().defaultBlockState(), 3);
                            } else {
                                world.setBlock(basePos, Blocks.MAGMA_BLOCK.defaultBlockState(), 3);
                            }
                        }
                        // Middle ring: Mixed materials (natural + some shiny blocks)
                        else if (distance <= 3.0) {
                            int rand = random.nextInt(100);
                            if (rand < 10) {
                                // 10% chance for shiny/metallic blocks
                                world.setBlock(basePos, getRandomShinyBlock().defaultBlockState(), 3);
                            } else if (rand < 20) {
                                // 10% chance for logs (gathered wood)
                                world.setBlock(basePos, getRandomLog().defaultBlockState(), 3);
                            } else {
                                // 80% natural stone materials
                                world.setBlock(basePos, getRandomNaturalBlock().defaultBlockState(), 3);
                            }
                        }
                        // Outer ring: Raised rim (mostly natural with occasional treasures)
                        else {
                            if (random.nextDouble() < 0.08) {
                                // 8% chance for precious blocks on rim
                                world.setBlock(basePos, getRandomShinyBlock().defaultBlockState(), 3);
                            } else {
                                world.setBlock(basePos, getRandomNaturalBlock().defaultBlockState(), 3);
                            }
                        }
                    }

                    // === LAYER 3: Raised rim around edges (creates bowl shape) ===
                    BlockPos rimPos = centerPos.offset(x, 1, z);

                    // Only on outer ring (creates raised edge)
                    if (distance > 3.0 && distance <= 4.5) {
                        int rand = random.nextInt(100);
                        if (rand < 5) {
                            // 5% chance for precious blocks on rim
                            world.setBlock(rimPos, getRandomPreciousBlock().defaultBlockState(), 3);
                        } else if (rand < 15) {
                            // 10% chance for logs (structural support)
                            world.setBlock(rimPos, getRandomLog().defaultBlockState(), 3);
                        } else {
                            // 85% natural stone
                            world.setBlock(rimPos, getRandomNaturalBlock().defaultBlockState(), 3);
                        }

                        // Occasional second layer on rim for height variation
                        if (random.nextDouble() < 0.3) {
                            world.setBlock(rimPos.above(), getRandomNaturalBlock().defaultBlockState(), 3);
                        }
                    }

                    // === LAYER 4: Decorative elements ===

                    // Scattered bone blocks around edges (dragon's prey remains)
                    if (distance > 3.5 && distance <= 5.0 && random.nextDouble() < 0.15) {
                        BlockPos bonePos = centerPos.offset(x, 1, z);
                        if (world.getBlockState(bonePos).isAir()) {
                            world.setBlock(bonePos, Blocks.BONE_BLOCK.defaultBlockState(), 3);
                        }
                    }

                    // Rare treasure chest (dragon's hoard) - very rare
                    if (distance > 3.0 && distance <= 4.5 && random.nextDouble() < 0.02) {
                        BlockPos chestPos = centerPos.offset(x, 1, z);
                        if (world.getBlockState(chestPos).isAir()) {
                            world.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 3);

                            // Fill chest with dragon treasure loot table
                            if (world.getBlockEntity(chestPos) instanceof ChestBlockEntity chest) {
                                ResourceKey<LootTable> lootTableKey = ResourceKey.create(Registries.LOOT_TABLE,
                                        Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "chests/dragon_nest_treasure"));
                                chest.setLootTable(lootTableKey, random.nextLong());
                                DagMod.LOGGER.debug("Dragon treasure chest placed at {} with loot table", chestPos);
                            }
                        }
                    }

                    // Scattered precious blocks (hoarded treasures) - rare
                    if (distance > 2.5 && distance <= 4.5 && random.nextDouble() < 0.05) {
                        BlockPos treasurePos = centerPos.offset(x, 1, z);
                        if (world.getBlockState(treasurePos).isAir()) {
                            world.setBlock(treasurePos, getRandomShinyBlock().defaultBlockState(), 3);
                        }
                    }

                    // Scattered gravel/coarse dirt for natural look
                    if (distance > 2.5 && distance <= 4.0 && random.nextDouble() < 0.1) {
                        BlockPos decorPos = centerPos.offset(x, 1, z);
                        if (world.getBlockState(decorPos).isAir()) {
                            world.setBlock(decorPos, random.nextBoolean() ?
                                Blocks.GRAVEL.defaultBlockState() : Blocks.COARSE_DIRT.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }

        // === Spawn 1-3 dragon eggs in the center bowl ===
        int eggCount = 1 + random.nextInt(3);
        for (int i = 0; i < eggCount; i++) {
            int attempts = 0;
            while (attempts < 15) {
                // Place eggs in the inner bowl area
                int x = random.nextInt(3) - 1; // -1, 0, or 1
                int z = random.nextInt(3) - 1;
                BlockPos eggPos = centerPos.offset(x, 1, z);

                // Check if position is valid (air block above magma block)
                if (world.getBlockState(eggPos).isAir() && world.getBlockState(eggPos.below()).getBlock() == Blocks.MAGMA_BLOCK) {
                    world.setBlock(eggPos, ModBlocks.DRAGON_EGG_BLOCK.defaultBlockState().setValue(DragonEggBlock.VARIANT, variant), 3);

                    // Set the egg's variant to match the parent dragon
                    if (world.getBlockEntity(eggPos) instanceof DragonEggBlockEntity eggEntity) {
                        eggEntity.setVariant(variant);
                        eggEntity.setChanged();
                    }

                    // Small particle effect when egg spawns (visual flair)
                    world.sendParticles(ParticleTypes.ENCHANT,
                        eggPos.getX() + 0.5, eggPos.getY() + 0.5, eggPos.getZ() + 0.5,
                        10, 0.3, 0.3, 0.3, 0.02);

                    DagMod.LOGGER.debug("{} dragon egg placed at {}", variant.name(), eggPos);
                    break;
                }
                attempts++;
            }
        }

        DagMod.LOGGER.info("Enhanced dragon nest generated at {} with {} eggs", centerPos, eggCount);
    }

    /**
     * Find a suitable location to spawn a dragon
     * Must be on a mountain, Y>180, with clear space
     */
    private static BlockPos findSuitableSpawnLocation(ServerLevel world, BlockPos center) {
        // Search in a radius around the center point
        for (int attempt = 0; attempt < 50; attempt++) {
            // RandomSource offset from center
            int offsetX = random.nextInt(SPAWN_CHECK_RADIUS * 2) - SPAWN_CHECK_RADIUS;
            int offsetZ = random.nextInt(SPAWN_CHECK_RADIUS * 2) - SPAWN_CHECK_RADIUS;

            BlockPos searchPos = center.offset(offsetX, 0, offsetZ);

            // Get surface height at this position
            BlockPos surfacePos = world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, searchPos);

            // Check height requirement
            if (surfacePos.getY() < MIN_SPAWN_HEIGHT) {
                continue;
            }

            // Check if biome is mountain
            Biome biome = world.getBiome(surfacePos).value();
            if (!world.getBiome(surfacePos).is(BiomeTags.IS_MOUNTAIN)) {
                continue;
            }

            // Check for clear space (5x5x5 area for dragon to spawn)
            BlockPos spawnPos = surfacePos.above(3); // Spawn slightly above ground
            if (hasEnoughClearSpace(world, spawnPos)) {
                return spawnPos;
            }
        }

        return null; // No suitable location found
    }

    /**
     * Check if there's enough clear space for a dragon to spawn
     */
    private static boolean hasEnoughClearSpace(ServerLevel world, BlockPos pos) {
        // Check a 5x5x5 area centered on the spawn position
        for (int x = -2; x <= 2; x++) {
            for (int y = 0; y <= 4; y++) {
                for (int z = -2; z <= 2; z++) {
                    BlockPos checkPos = pos.offset(x, y, z);
                    if (!world.getBlockState(checkPos).isAir() && !world.getBlockState(checkPos).canBeReplaced()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Check if there's already a dragon nearby
     */
    private static boolean isDragonNearby(ChunkPos pos) {
        int chunkDistance = MIN_DRAGON_DISTANCE / 16; // Convert blocks to chunks

        for (ChunkPos dragonChunk : dragonChunks) {
            int dx = Math.abs(dragonChunk.x() - pos.x());
            int dz = Math.abs(dragonChunk.z() - pos.z());

            if (dx < chunkDistance && dz < chunkDistance) {
                return true;
            }
        }

        return false;
    }

    /**
     * Remove a dragon location when it dies
     * This allows new dragons to spawn in that area eventually
     */
    public static void removeDragonLocation(BlockPos pos) {
        ChunkPos chunkPos = ChunkPos.containing(pos);
        dragonChunks.remove(chunkPos);
        DagMod.LOGGER.debug("Dragon location removed at chunk {}", chunkPos);
    }

    /**
     * Called when a natural wild dragon dies. Starts the 10-minute respawn cooldown.
     * Not triggered by quest-specific subclasses (e.g. RedDragonEntity).
     */
    public static void recordDragonDeath(long worldTime) {
        lastDragonDeathTime = worldTime;
        DagMod.LOGGER.info("Wild Dragon died — respawn cooldown started ({} minutes)", RESPAWN_COOLDOWN_TICKS / 1200);
    }

    /**
     * Clear all dragon locations (for world reload/restart)
     */
    public static void clearDragonLocations() {
        dragonChunks.clear();
    }

    /**
     * Get spawn attempt interval in ticks
     */
    public static int getSpawnInterval() {
        return SPAWN_ATTEMPT_INTERVAL;
    }
}
