package com.github.hitman20081.dagmod.bone_realm;

import com.github.hitman20081.dagmod.block.ModBlocks;
import com.github.hitman20081.dagmod.bone_realm.entity.BoneRealmEntityRegistry;
import com.github.hitman20081.dagmod.bone_realm.entity.SkeletonKingEntity;
import com.github.hitman20081.dagmod.bone_realm.entity.SkeletonLordEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.block.LightBlock;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

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

        for (ServerWorld world : server.getWorlds()) {
            for (ServerPlayerEntity player : world.getPlayers()) {
                checkNearby(world, player);
            }
        }
    }

    private static void checkNearby(ServerWorld world, ServerPlayerEntity player) {
        if (player.isCreative()) return;
        BlockPos center = player.getBlockPos();
        int r = TRIGGER_RADIUS;

        for (BlockPos pos : BlockPos.iterate(
                center.add(-r, -r, -r),
                center.add(r, r, r))) {

            if (world.getBlockState(pos).isOf(ModBlocks.BOSS_SPAWN_TRIGGER)) {
                spawnSkeletonLord(world, pos);
                return;
            }
            if (world.getBlockState(pos).isOf(ModBlocks.SKELETON_KING_SPAWN_TRIGGER)) {
                spawnSkeletonKing(world, pos);
                return;
            }
        }
    }

    private static void spawnSkeletonLord(ServerWorld world, BlockPos triggerPos) {
        world.setBlockState(triggerPos, Blocks.AIR.getDefaultState());

        BlockPos spawnPos = triggerPos.up();
        SkeletonLordEntity boss = new SkeletonLordEntity(BoneRealmEntityRegistry.SKELETON_LORD, world);
        boss.refreshPositionAndAngles(
                spawnPos.getX() + 0.5,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5,
                world.getRandom().nextFloat() * 360f,
                0f
        );
        world.spawnEntity(boss);

        world.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME,
                spawnPos.getX() + 0.5, spawnPos.getY() + 1.0, spawnPos.getZ() + 0.5,
                60, 0.8, 1.0, 0.8, 0.05);
        world.playSound(null, triggerPos, SoundEvents.ENTITY_WITHER_SPAWN,
                SoundCategory.HOSTILE, 1.0f, 1.0f);
    }

    private static void spawnSkeletonKing(ServerWorld world, BlockPos triggerPos) {
        world.setBlockState(triggerPos, Blocks.AIR.getDefaultState());

        // Seal the doorway: replace all light blocks in the room area with barriers
        sealRoom(world, triggerPos);

        BlockPos spawnPos = triggerPos.up();
        SkeletonKingEntity boss = new SkeletonKingEntity(BoneRealmEntityRegistry.SKELETON_KING, world);
        boss.refreshPositionAndAngles(
                spawnPos.getX() + 0.5,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5,
                world.getRandom().nextFloat() * 360f,
                0f
        );
        world.spawnEntity(boss);

        world.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME,
                spawnPos.getX() + 0.5, spawnPos.getY() + 1.0, spawnPos.getZ() + 0.5,
                80, 1.0, 1.2, 1.0, 0.05);
        world.playSound(null, triggerPos, SoundEvents.ENTITY_WITHER_SPAWN,
                SoundCategory.HOSTILE, 1.5f, 0.8f);
    }

    /**
     * Scans a 70x30x70 area around the trigger for light blocks (level 0 used as door markers)
     * and replaces them with barrier blocks to lock players in the boss room.
     */
    public static void sealRoom(ServerWorld world, BlockPos center) {
        for (BlockPos pos : BlockPos.iterate(
                center.add(-35, -15, -35),
                center.add(35, 15, 35))) {
            var state = world.getBlockState(pos);
            if (state.isOf(Blocks.LIGHT) && state.get(LightBlock.LEVEL_15) == 0) {
                world.setBlockState(pos.toImmutable(), Blocks.BARRIER.getDefaultState());
            }
        }
    }

    /**
     * Reverses sealRoom: scans the same area and removes all barrier blocks back to air.
     */
    public static void unsealRoom(ServerWorld world, BlockPos center) {
        for (BlockPos pos : BlockPos.iterate(
                center.add(-35, -15, -35),
                center.add(35, 15, 35))) {
            if (world.getBlockState(pos).isOf(Blocks.BARRIER)) {
                world.setBlockState(pos.toImmutable(), Blocks.AIR.getDefaultState());
            }
        }
    }
}
