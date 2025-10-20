package com.github.hitman20081.dagmod.bone_realm.chest;

import com.github.hitman20081.dagmod.bone_realm.BoneRealmRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

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
    public static void onBossDeath(LivingEntity boss, PlayerEntity killer, World world) {
        if (world.isClient()) {
            return;
        }

        ServerWorld serverWorld = (ServerWorld) world;
        BlockPos deathPos = boss.getBlockPos();

        // Determine which boss died and spawn appropriate chest
        if (boss instanceof com.github.hitman20081.dagmod.bone_realm.entity.SkeletonKingEntity) {
            spawnSkeletonKingChest(serverWorld, deathPos, killer);
        } else if (boss instanceof com.github.hitman20081.dagmod.bone_realm.entity.SkeletonLordEntity) {
            spawnBoneRealmChest(serverWorld, deathPos, killer);
        }
        // Add more boss types as needed
    }

    /**
     * Spawns the Skeleton King's treasure chest
     */
    private static void spawnSkeletonKingChest(ServerWorld world, BlockPos pos, PlayerEntity killer) {
        // Give key to player
        if (killer != null) {
            ItemStack key = new ItemStack(BoneRealmRegistry.SKELETON_KING_KEY);
            killer.giveItemStack(key);

            killer.sendMessage(
                    Text.literal("✦ The Skeleton King's Key has been granted! ✦")
                            .formatted(Formatting.DARK_PURPLE, Formatting.BOLD),
                    false
            );
            killer.sendMessage(
                    Text.literal("A treasure chest materializes nearby...")
                            .formatted(Formatting.GRAY, Formatting.ITALIC),
                    false
            );
        }

        // Find safe position for chest (slightly above ground)
        BlockPos chestPos = findSafeChestPosition(world, pos.up(2));

        // Spawn chest
        spawnChestWithEffects(
                world,
                chestPos,
                BoneRealmChestRegistry.SKELETON_KING_CHEST,
                "dag005:chests/skeleton_king_chest"
        );
    }

    /**
     * Spawns a Bone Realm locked chest
     */
    private static void spawnBoneRealmChest(ServerWorld world, BlockPos pos, PlayerEntity killer) {
        // Give key to player
        if (killer != null) {
            ItemStack key = new ItemStack(BoneRealmRegistry.BONE_REALM_CHEST_KEY);
            killer.giveItemStack(key);

            killer.sendMessage(
                    Text.literal("✦ A Bone Realm Chest Key has appeared! ✦")
                            .formatted(Formatting.GRAY, Formatting.BOLD),
                    false
            );
        }

        // Find safe position for chest
        BlockPos chestPos = findSafeChestPosition(world, pos.up(2));

        // Spawn chest
        spawnChestWithEffects(
                world,
                chestPos,
                BoneRealmChestRegistry.BONE_REALM_LOCKED_CHEST,
                "dag005:chests/bone_realm_locked_chest"
        );
    }

    /**
     * Spawns a chest with epic visual effects
     */
    private static void spawnChestWithEffects(ServerWorld world, BlockPos pos,
                                              net.minecraft.block.Block chestBlock,
                                              String lootTableId) {
        // Place the chest
        world.setBlockState(pos, chestBlock.getDefaultState()
                .with(LockedBoneChestBlock.FACING, Direction.NORTH));

        // Set loot table
        if (world.getBlockEntity(pos) instanceof LockedBoneChestBlockEntity chestEntity) {
            chestEntity.setLootTable(
                    net.minecraft.registry.RegistryKey.of(
                            net.minecraft.registry.RegistryKeys.LOOT_TABLE,
                            net.minecraft.util.Identifier.of(lootTableId)
                    ),
                    world.getRandom().nextLong()
            );

            chestEntity.markDirty();
        }

        // Epic spawn effects
        // Particle pillar from ground to chest
        for (int y = 0; y < 5; y++) {
            BlockPos particlePos = pos.down(y);
            for (int i = 0; i < 20; i++) {
                double offsetX = (world.getRandom().nextDouble() - 0.5) * 0.8;
                double offsetZ = (world.getRandom().nextDouble() - 0.5) * 0.8;

                world.spawnParticles(
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

            world.spawnParticles(
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
                SoundEvents.ENTITY_ENDER_DRAGON_GROWL,
                SoundCategory.BLOCKS,
                0.5f,
                1.5f
        );

        world.playSound(
                null,
                pos,
                SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
                SoundCategory.BLOCKS,
                1.0f,
                0.8f
        );
    }

    /**
     * Finds a safe position to spawn a chest
     * Searches down and around to find solid ground or valid position
     */
    private static BlockPos findSafeChestPosition(ServerWorld world, BlockPos startPos) {
        // First try: Directly at start position if it's air
        if (world.getBlockState(startPos).isAir() &&
                !world.getBlockState(startPos.down()).isAir()) {
            return startPos;
        }

        // Second try: Search down
        for (int y = 0; y < 10; y++) {
            BlockPos checkPos = startPos.down(y);
            if (world.getBlockState(checkPos).isAir() &&
                    world.getBlockState(checkPos.down()).isSolidBlock(world, checkPos.down())) {
                return checkPos;
            }
        }

        // Third try: Search around in a 5x5 area
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                BlockPos checkPos = startPos.add(x, 0, z);
                if (world.getBlockState(checkPos).isAir() &&
                        world.getBlockState(checkPos.down()).isSolidBlock(world, checkPos.down())) {
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