package com.github.hitman20081.dagmod.bone_realm;

import com.github.hitman20081.dagmod.block.ModBlocks;
import com.github.hitman20081.dagmod.bone_realm.entity.BoneRealmEntityRegistry;
import com.github.hitman20081.dagmod.bone_realm.entity.SkeletonKingEntity;
import com.github.hitman20081.dagmod.bone_realm.entity.SkeletonLordEntity;
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

    /**
     * Scans a 70x30x70 area around the trigger for light blocks (level 0 used as door markers)
     * and replaces them with barrier blocks to lock players in the boss room.
     */
    public static void sealRoom(ServerLevel world, BlockPos center) {
        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-35, -15, -35),
                center.offset(35, 15, 35))) {
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
        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-35, -15, -35),
                center.offset(35, 15, 35))) {
            if (world.getBlockState(pos).getBlock() == Blocks.BARRIER) {
                world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            }
        }
    }
}
