package com.github.hitman20081.dagmod.bone_realm;

import com.github.hitman20081.dagmod.block.ModBlocks;
import com.github.hitman20081.dagmod.bone_realm.entity.BoneRealmEntityRegistry;
import com.github.hitman20081.dagmod.bone_realm.entity.SkeletonLordEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
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
                spawnBoss(world, pos);
                return;
            }
        }
    }

    private static void spawnBoss(ServerWorld world, BlockPos triggerPos) {
        // Remove the trigger first so it cannot fire again
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
}
