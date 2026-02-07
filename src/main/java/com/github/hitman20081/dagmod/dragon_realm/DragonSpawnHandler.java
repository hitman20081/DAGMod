package com.github.hitman20081.dagmod.dragon_realm;

import com.github.hitman20081.dagmod.dragon_realm.portal.DragonRealmTeleporter;
import com.github.hitman20081.dagmod.entity.DragonGuardianEntity;
import com.github.hitman20081.dagmod.entity.ModEntities;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnLocationTypes;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;

/**
 * Handles natural dragon spawning in the Dragon Realm
 *
 * Features:
 * - Configurable spawn rates and weights
 * - Spawn restrictions (light level, height, biome)
 * - Integration with Minecraft's mob spawning system
 */
public class DragonSpawnHandler {

    // Spawn configuration
    public static final int SPAWN_WEIGHT = 5;           // Relative spawn weight (5 = uncommon)
    public static final int MIN_GROUP_SIZE = 1;         // Min dragons per spawn
    public static final int MAX_GROUP_SIZE = 1;         // Max dragons per spawn

    // Biome configuration
    private static final RegistryKey<Biome> DRAGON_REALM_BIOME = RegistryKey.of(
            RegistryKeys.BIOME,
            Identifier.of("dagmod", "dragon_realm")
    );

    /**
     * Register dragon spawning rules
     * Call this during mod initialization
     */
    public static void register() {
        // Register spawn restrictions (when/where dragons can spawn)
        SpawnRestriction.register(
                ModEntities.DRAGON_GUARDIAN,
                SpawnLocationTypes.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                DragonSpawnHandler::canSpawn
        );

        // Add dragons to Dragon Realm biome spawns
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(DRAGON_REALM_BIOME),
                SpawnGroup.MONSTER,
                ModEntities.DRAGON_GUARDIAN,
                SPAWN_WEIGHT,
                MIN_GROUP_SIZE,
                MAX_GROUP_SIZE
        );
    }

    /**
     * Check if a dragon can spawn at this location
     * Returns true if spawn conditions are met
     */
    private static boolean canSpawn(
            net.minecraft.entity.EntityType<DragonGuardianEntity> type,
            net.minecraft.world.ServerWorldAccess world,
            net.minecraft.entity.SpawnReason spawnReason,
            net.minecraft.util.math.BlockPos pos,
            net.minecraft.util.math.random.Random random
    ) {
        // Only spawn in Dragon Realm dimension
        if (world.toServerWorld().getRegistryKey() != DragonRealmTeleporter.DRAGON_REALM) {
            return false;
        }

        // Only spawn on the surface (above Y=60 to prevent cave/underground spawning)
        // Dragons should spawn in open skies, not in dark caves
        if (pos.getY() < 60) {
            return false;
        }

        // Use standard hostile mob spawn conditions (dark areas)
        if (!HostileEntity.canSpawnInDark(type, world, spawnReason, pos, random)) {
            return false;
        }

        // Don't spawn too close to the boss arena (within 128 blocks of 0,64,0)
        double distanceToArena = Math.sqrt(
                pos.getX() * pos.getX() +
                (pos.getY() - 64) * (pos.getY() - 64) +
                pos.getZ() * pos.getZ()
        );

        if (distanceToArena < 128) {
            return false;
        }

        // Require at least 16 blocks of vertical clearance for flying
        for (int i = 1; i <= 16; i++) {
            if (!world.getBlockState(pos.up(i)).isAir()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Get the spawn weight for dragons
     * Can be called to adjust spawn rates dynamically
     */
    public static int getSpawnWeight() {
        return SPAWN_WEIGHT;
    }

    /**
     * Get minimum group size
     */
    public static int getMinGroupSize() {
        return MIN_GROUP_SIZE;
    }

    /**
     * Get maximum group size
     */
    public static int getMaxGroupSize() {
        return MAX_GROUP_SIZE;
    }
}
