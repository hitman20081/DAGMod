package com.github.hitman20081.dagmod.bone_realm;

import com.github.hitman20081.dagmod.block.ModBlocks;
import com.github.hitman20081.dagmod.bone_realm.entity.BoneRealmEntityRegistry;
import com.github.hitman20081.dagmod.bone_realm.entity.SkeletonKingEntity;
import com.github.hitman20081.dagmod.bone_realm.entity.SkeletonLordEntity;
import com.github.hitman20081.dagmod.bone_realm.portal.BoneRealmTeleporter;
import com.github.hitman20081.dagmod.pale_garden.entity.PaleGardenEntityRegistry;
import com.github.hitman20081.dagmod.pale_garden.entity.SpiderQueenEntity;
import com.github.hitman20081.dagmod.pale_garden.portal.PaleGardenTeleporter;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class BossRoomSpawnHandler {

    private static final int TRIGGER_RADIUS = 12;
    private static final int CHECK_INTERVAL = 20; // ticks (1 second)

    private static int tickCounter = 0;

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(BossRoomSpawnHandler::onTick);
    }

    private static void onTick(MinecraftServer server) {
        if (++tickCounter < CHECK_INTERVAL) return;
        tickCounter = 0;

        for (ServerLevel world : server.getAllLevels()) {
            // The Skeleton Lord's trigger sits in bone_dungeon_portal_room, part of the
            // Overworld bone_dungeon jigsaw structure (desert/badlands biomes) -- it guards the
            // portal itself, before the player ever steps into the Bone Realm. Everything else
            // (Skeleton King, Spider Queen) lives inside their respective custom dimensions.
            if (world.dimension() != Level.OVERWORLD
                    && world.dimension() != BoneRealmTeleporter.BONE_REALM
                    && world.dimension() != PaleGardenTeleporter.PALE_GARDEN) continue;

            for (ServerPlayer player : world.players()) {
                checkNearby(world, player);
            }
        }
    }

    private static void checkNearby(ServerLevel world, ServerPlayer player) {
        if (player.isCreative()) return;
        BlockPos center = player.blockPosition();
        int r = TRIGGER_RADIUS;

        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-r, -r, -r),
                center.offset(r, r, r))) {

            if (world.getBlockState(pos).getBlock() == ModBlocks.BOSS_SPAWN_TRIGGER) {
                spawnSkeletonLord(world, pos);
                return;
            }
            if (world.getBlockState(pos).getBlock() == ModBlocks.SKELETON_KING_SPAWN_TRIGGER) {
                spawnSkeletonKing(world, pos);
                return;
            }
            if (world.getBlockState(pos).getBlock() == ModBlocks.SPIDER_QUEEN_SPAWN_TRIGGER) {
                spawnSpiderQueen(world, pos);
                return;
            }
        }
    }

    private static void spawnSkeletonLord(ServerLevel world, BlockPos triggerPos) {
        world.setBlock(triggerPos, Blocks.AIR.defaultBlockState(), 3);

        BlockPos spawnPos = triggerPos.above();
        SkeletonLordEntity boss = new SkeletonLordEntity(BoneRealmEntityRegistry.SKELETON_LORD, world);
        boss.snapTo(
                spawnPos.getX() + 0.5,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5,
                world.getRandom().nextFloat() * 360f,
                0f
        );
        world.addFreshEntity(boss);

        world.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                spawnPos.getX() + 0.5, spawnPos.getY() + 1.0, spawnPos.getZ() + 0.5,
                60, 0.8, 1.0, 0.8, 0.05);
        world.playSound(null, triggerPos, SoundEvents.WITHER_SPAWN,
                SoundSource.HOSTILE, 1.0f, 1.0f);
    }

    private static void spawnSkeletonKing(ServerLevel world, BlockPos triggerPos) {
        world.setBlock(triggerPos, Blocks.AIR.defaultBlockState(), 3);

        // Seal the doorway: replace all light blocks in the room area with barriers
        sealRoom(world, triggerPos);

        BlockPos spawnPos = triggerPos.above();
        SkeletonKingEntity boss = new SkeletonKingEntity(BoneRealmEntityRegistry.SKELETON_KING, world);
        boss.snapTo(
                spawnPos.getX() + 0.5,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5,
                world.getRandom().nextFloat() * 360f,
                0f
        );
        world.addFreshEntity(boss);

        world.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                spawnPos.getX() + 0.5, spawnPos.getY() + 1.0, spawnPos.getZ() + 0.5,
                80, 1.0, 1.2, 1.0, 0.05);
        world.playSound(null, triggerPos, SoundEvents.WITHER_SPAWN,
                SoundSource.HOSTILE, 1.5f, 0.8f);
    }

    private static void spawnSpiderQueen(ServerLevel world, BlockPos triggerPos) {
        world.setBlock(triggerPos, Blocks.AIR.defaultBlockState(), 3);

        // Lair envelope is bigger than the throne room, so use a larger seal radius.
        sealRoom(world, triggerPos, 45, 25, 45);

        BlockPos spawnPos = triggerPos.above();
        SpiderQueenEntity boss = new SpiderQueenEntity(PaleGardenEntityRegistry.SPIDER_QUEEN, world);
        boss.snapTo(
                spawnPos.getX() + 0.5,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5,
                world.getRandom().nextFloat() * 360f,
                0f
        );
        world.addFreshEntity(boss);

        world.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR,
                spawnPos.getX() + 0.5, spawnPos.getY() + 1.0, spawnPos.getZ() + 0.5,
                80, 1.0, 1.2, 1.0, 0.05);
        world.playSound(null, triggerPos, SoundEvents.SPIDER_AMBIENT,
                SoundSource.HOSTILE, 2.0f, 0.5f);
    }

    /**
     * Scans an area around the trigger for light blocks (level 0 used as invisible door markers)
     * and replaces them with barrier blocks to lock players in the boss room.
     */
    public static void sealRoom(ServerLevel world, BlockPos center) {
        sealRoom(world, center, 35, 15, 35);
    }

    public static void sealRoom(ServerLevel world, BlockPos center, int radiusX, int radiusY, int radiusZ) {
        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-radiusX, -radiusY, -radiusZ),
                center.offset(radiusX, radiusY, radiusZ))) {
            var state = world.getBlockState(pos);
            if (state.getBlock() == Blocks.LIGHT && state.getValue(LightBlock.LEVEL) == 0) {
                world.setBlock(pos, Blocks.BARRIER.defaultBlockState(), 3);
            }
        }
    }

    /**
     * Reverses sealRoom: scans the same area and removes all barrier blocks back to air.
     */
    public static void unsealRoom(ServerLevel world, BlockPos center) {
        unsealRoom(world, center, 35, 15, 35);
    }

    public static void unsealRoom(ServerLevel world, BlockPos center, int radiusX, int radiusY, int radiusZ) {
        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-radiusX, -radiusY, -radiusZ),
                center.offset(radiusX, radiusY, radiusZ))) {
            if (world.getBlockState(pos).getBlock() == Blocks.BARRIER) {
                world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            }
        }
    }
}
