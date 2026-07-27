package com.github.hitman20081.dagmod.bone_realm.chest;

import com.github.hitman20081.dagmod.bone_realm.BoneRealmRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

/**
 * Spawns locked chests when bosses are defeated
 * Drops the key to the player and spawns a floating chest
 */
public class BossChestSpawner {

    /**
     * Called when a boss is killed
     * @param boss The boss entity that died
     * @param killer The player who killed the boss (can be null)
     * @param world The world
     */
    public static void onBossDeath(LivingEntity boss, Player killer, Level world) {
        if (world.isClientSide()) {
            return;
        }

        ServerLevel serverWorld = (ServerLevel) world;
        BlockPos deathPos = boss.blockPosition();

        // Determine which boss died and spawn appropriate chest
        if (boss instanceof com.github.hitman20081.dagmod.bone_realm.entity.SkeletonLordEntity) {
            spawnBoneRealmChest(serverWorld, deathPos, killer);
        }
        // Add more boss types as needed
    }

    /**
     * Spawns a Bone Realm locked chest
     */
    private static void spawnBoneRealmChest(ServerLevel world, BlockPos pos, Player killer) {
        // Give key to player
        if (killer != null) {
            ItemStack key = new ItemStack(BoneRealmRegistry.BONE_REALM_CHEST_KEY);
            killer.addItem(key);

            killer.sendSystemMessage(
                    Component.literal("✦ A Bone Realm Chest Key has appeared! ✦")
                            .withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD));
        }

        // Find safe position for chest
        BlockPos chestPos = findSafeChestPosition(world, pos.above(2));

        // Spawn chest
        spawnChestWithEffects(
                world,
                chestPos,
                BoneRealmChestRegistry.BONE_REALM_LOCKED_CHEST,
                "dagmod:chests/bone_realm_locked_chest"
        );
    }

    /**
     * Spawns a chest with epic visual effects
     */
    private static void spawnChestWithEffects(ServerLevel world, BlockPos pos,
                                              net.minecraft.world.level.block.Block chestBlock,
                                              String lootTableId) {
        // Place the chest
        world.setBlock(pos, chestBlock.defaultBlockState()
                .setValue(LockedBoneChestBlock.FACING, Direction.NORTH), 3);

        // Set loot table
        if (world.getBlockEntity(pos) instanceof LockedBoneChestBlockEntity chestEntity) {
            chestEntity.setLootTable(
                    net.minecraft.resources.ResourceKey.create(
                            net.minecraft.core.registries.Registries.LOOT_TABLE,
                            net.minecraft.resources.Identifier.parse(lootTableId)
                    )
            );
            chestEntity.setLootTableSeed(world.getRandom().nextLong());

            chestEntity.setChanged();
        }

        // Epic spawn effects
        // Particle pillar from ground to chest
        for (int y = 0; y < 5; y++) {
            BlockPos particlePos = pos.below(y);
            for (int i = 0; i < 20; i++) {
                double offsetX = (world.getRandom().nextDouble() - 0.5) * 0.8;
                double offsetZ = (world.getRandom().nextDouble() - 0.5) * 0.8;

                world.sendParticles(
                        ParticleTypes.SOUL_FIRE_FLAME,
                        particlePos.getX() + 0.5 + offsetX,
                        particlePos.getY() + 0.5,
                        particlePos.getZ() + 0.5 + offsetZ,
                        1,
                        0, 0.2, 0,
                        0.02
                );
            }
        }

        // Explosion of particles at chest location
        for (int i = 0; i < 50; i++) {
            double offsetX = (world.getRandom().nextDouble() - 0.5) * 2;
            double offsetY = (world.getRandom().nextDouble() - 0.5) * 2;
            double offsetZ = (world.getRandom().nextDouble() - 0.5) * 2;

            world.sendParticles(
                    ParticleTypes.END_ROD,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    1,
                    offsetX * 0.3, offsetY * 0.3, offsetZ * 0.3,
                    0.1
            );
        }

        // Epic sounds
        world.playSound(
                null,
                pos,
                SoundEvents.ENDER_DRAGON_GROWL,
                SoundSource.BLOCKS,
                0.5f,
                1.5f
        );

        world.playSound(
                null,
                pos,
                SoundEvents.ENCHANTMENT_TABLE_USE,
                SoundSource.BLOCKS,
                1.0f,
                0.8f
        );
    }

    /**
     * Finds a safe position to spawn a chest
     * Searches down and around to find solid ground or valid position
     */
    private static BlockPos findSafeChestPosition(ServerLevel world, BlockPos startPos) {
        // First try: Directly at start position if it's air
        if (world.getBlockState(startPos).isAir() &&
                !world.getBlockState(startPos.below()).isAir()) {
            return startPos;
        }

        // Second try: Search down
        for (int y = 0; y < 10; y++) {
            BlockPos checkPos = startPos.below(y);
            if (world.getBlockState(checkPos).isAir() &&
                    world.getBlockState(checkPos.below()).isSolid()) {
                return checkPos;
            }
        }

        // Third try: Search around in a 5x5 area
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                BlockPos checkPos = startPos.offset(x, 0, z);
                if (world.getBlockState(checkPos).isAir() &&
                        world.getBlockState(checkPos.below()).isSolid()) {
                    return checkPos;
                }
            }
        }

        // Fallback: Just use start position and hope for the best
        return startPos;
    }

    /**
     * Utility method to check if an entity is a boss
     * You can customize this based on your boss naming/tagging system
     */
    public static boolean isBoss(LivingEntity entity) {
        String name = entity.getType().toString();
        return name.contains("skeleton_king") ||
                name.contains("bone_guardian") ||
                name.contains("summoner") ||
                entity.getCustomName() != null &&
                        entity.getCustomName().getString().toLowerCase().contains("boss");
    }
}