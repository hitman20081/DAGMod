package com.github.hitman20081.dagmod.dragon_realm.boss;

import com.github.hitman20081.dagmod.dragon_realm.portal.DragonRealmTeleporter;
import com.github.hitman20081.dagmod.entity.DragonGuardianEntity;
import com.github.hitman20081.dagmod.entity.ModEntities;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.util.RandomSource;

import java.util.List;

/**
 * Handles Dragon Guardian boss spawning in Dragon Realm
 *
 * Features:
 * - Spawns boss on first dimension entry
 * - Checks for existing boss before spawning
 * - Spawns at center arena (0, 64, 0)
 * - RandomSource variant selection (RED, ICE, LAVA)
 * - Global spawn announcements
 */
public class DragonGuardianSpawner {

    // Arena spawn location (X, Z coordinates - Y will be found dynamically)
    private static final int ARENA_X = 0;
    private static final int ARENA_Z = 0;
    private static final int SEARCH_RADIUS = 256; // Search this far for existing boss

    /**
     * Attempt to spawn Dragon Guardian when player enters Dragon Realm
     * Only spawns if no boss currently exists AND no active respawn timer
     */
    public static void trySpawnBoss(ServerLevel dragonRealm, ServerPlayer enteringPlayer) {
        // Only spawn in Dragon Realm dimension
        if (dragonRealm.dimension() != DragonRealmTeleporter.DRAGON_REALM) {
            return;
        }

        // Check if boss already exists
        if (bossExists(dragonRealm)) {
            return;
        }

        // Check if respawn timer is active - don't spawn if waiting for respawn
        if (DragonRespawnTimerManager.get(dragonRealm.getServer()).hasActiveTimer(dragonRealm)) {
            return;
        }

        // Spawn the boss
        spawnBoss(dragonRealm, enteringPlayer);
    }

    /**
     * Check if Dragon Guardian boss already exists in dimension
     */
    public static boolean bossExists(ServerLevel world) {
        // Search for any DragonGuardianEntity in the dimension
        List<DragonGuardianEntity> bosses = world.getEntitiesOfClass(
            DragonGuardianEntity.class,
            new net.minecraft.world.phys.AABB(
                ARENA_X - SEARCH_RADIUS,
                0,
                ARENA_Z - SEARCH_RADIUS,
                ARENA_X + SEARCH_RADIUS,
                256,
                ARENA_Z + SEARCH_RADIUS
            ),
            entity -> true
        );

        return !bosses.isEmpty();
    }

    /**
     * Spawn Dragon Guardian boss at arena center
     * Public for use by respawn timer
     */
    public static void spawnBoss(ServerLevel world, ServerPlayer triggeringPlayer) {
        // Create boss entity
        DragonGuardianEntity boss = ModEntities.DRAGON_GUARDIAN.create(world, EntitySpawnReason.TRIGGERED);

        if (boss == null) {
            System.err.println("Failed to create Dragon Guardian entity!");
            return;
        }

        // Find surface level and spawn above it
        int surfaceY = findSurfaceLevel(world, ARENA_X, ARENA_Z);
        int spawnY = surfaceY + 10; // Spawn 10 blocks above surface for flying start

        // Set position at arena center above surface
        boss.snapTo(
            ARENA_X + 0.5,
            spawnY,
            ARENA_Z + 0.5,
            0, 0
        );

        // Mark as boss - this makes the entity display as "Dragon Guardian" instead of variant name
        // Boss will use RED variant texture but display name "Dragon Guardian"
        boss.setBoss(true);

        // Spawn the boss
        world.addFreshEntity(boss);

        // Epic spawn effects
        BlockPos spawnPos = new BlockPos(ARENA_X, spawnY, ARENA_Z);
        spawnEffects(world, spawnPos);

        // Global announcements - boss uses generic "Dragon Guardian" title
        announceSpawn(world, null); // Pass null for boss (not a variant-specific dragon)
    }

    /**
     * Find surface level at given X/Z coordinates
     * Searches downward from high altitude to find solid ground
     */
    private static int findSurfaceLevel(ServerLevel world, int x, int z) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(x, 256, z);

        // Search downward for solid ground from Y=256
        for (int y = 256; y >= 0; y--) {
            mutable.setY(y);
            if (world.getBlockState(mutable).isSolid()) {
                return y + 1; // Return position above solid block
            }
        }

        // Fallback if no ground found (shouldn't happen in normal terrain)
        return 80;
    }

    /**
     * Spawn dramatic particle effects and sounds
     */
    private static void spawnEffects(ServerLevel world, BlockPos pos) {
        // Massive explosion of particles
        for (int i = 0; i < 100; i++) {
            double offsetX = (world.getRandom().nextDouble() - 0.5) * 10;
            double offsetY = (world.getRandom().nextDouble() - 0.5) * 10;
            double offsetZ = (world.getRandom().nextDouble() - 0.5) * 10;

            world.sendParticles(
                ParticleTypes.ENCHANT,
                pos.getX() + offsetX,
                pos.getY() + offsetY,
                pos.getZ() + offsetZ,
                5,
                0.5, 0.5, 0.5,
                0.1
            );

            world.sendParticles(
                ParticleTypes.END_ROD,
                pos.getX() + offsetX,
                pos.getY() + offsetY,
                pos.getZ() + offsetZ,
                3,
                0.3, 0.3, 0.3,
                0.05
            );
        }

        // Lightning effect particles
        for (int i = 0; i < 50; i++) {
            world.sendParticles(
                ParticleTypes.ELECTRIC_SPARK,
                pos.getX(),
                pos.getY() + i * 0.5,
                pos.getZ(),
                1,
                0.1, 0, 0.1,
                0
            );
        }

        // Epic sounds
        world.playSound(
            null,
            pos.getX(),
            pos.getY(),
            pos.getZ(),
            SoundEvents.ENDER_DRAGON_GROWL,
            SoundSource.HOSTILE,
            5.0f,
            0.8f
        );

        world.playSound(
            null,
            pos.getX(),
            pos.getY(),
            pos.getZ(),
            SoundEvents.LIGHTNING_BOLT_THUNDER,
            SoundSource.HOSTILE,
            3.0f,
            1.2f
        );
    }

    /**
     * Announce boss spawn to all players in dimension
     * @param variant Dragon variant (null for generic "Dragon Guardian" boss)
     */
    private static void announceSpawn(ServerLevel world, DragonGuardianEntity.DragonVariant variant) {
        // Get variant-specific title, or generic boss title if null
        String variantName;
        if (variant == null) {
            variantName = "Dragon Guardian"; // Generic boss name
        } else {
            variantName = switch (variant) {
                case RED -> "Red Dragon Guardian";
                case ICE -> "Ice Dragon Guardian";
                case LAVA -> "Lava Dragon Guardian";
                case EARTH -> "Earth Dragon Guardian";
                case WIND -> "Wind Dragon Guardian";
            };
        }

        // Announcement messages
        Component title = Component.literal("☆ " + variantName + " ☆")
                .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD);
        Component subtitle = Component.literal("has awakened in the Dragon Realm!")
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);

        // Send to all players in Dragon Realm
        for (ServerPlayer player : world.players()) {
            player.sendSystemMessage(title);
            player.sendSystemMessage(subtitle);
        }

        // Also broadcast to Overworld (so all players know)
        ServerLevel overworld = world.getServer().getLevel(Level.OVERWORLD);
        if (overworld != null) {
            Component overworldMsg = Component.literal("The " + variantName + " has awakened!")
                    .withStyle(ChatFormatting.LIGHT_PURPLE);
            for (ServerPlayer player : overworld.players()) {
                player.sendSystemMessage(overworldMsg);
            }
        }
    }

    /**
     * Handle boss death - mark for respawn timer
     * Called by death event handler
     */
    public static void onBossDeath(DragonGuardianEntity boss, ServerLevel world) {
        // Dramatic death effects
        BlockPos pos = boss.blockPosition();

        // Explosion of particles
        for (int i = 0; i < 200; i++) {
            world.sendParticles(
                ParticleTypes.ENCHANT,
                pos.getX(),
                pos.getY() + 2,
                pos.getZ(),
                1,
                2, 2, 2,
                0.2
            );
        }

        // Death sound
        world.playSound(
            null,
            pos.getX(),
            pos.getY(),
            pos.getZ(),
            SoundEvents.ENDER_DRAGON_DEATH,
            SoundSource.HOSTILE,
            5.0f,
            1.0f
        );

        // Global death announcement
        Component deathMsg = Component.literal("☆ The Dragon Guardian has been slain! ☆")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);

        for (ServerPlayer player : world.getServer().getPlayerList().getPlayers()) {
            player.sendSystemMessage(deathMsg);
        }

        // Start respawn timer
        DragonRespawnTimerManager manager = DragonRespawnTimerManager.get(world.getServer());
        manager.startTimer(world);
    }
}
