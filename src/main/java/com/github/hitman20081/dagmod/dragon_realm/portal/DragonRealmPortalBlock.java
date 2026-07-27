package com.github.hitman20081.dagmod.dragon_realm.portal;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;

import java.util.List;

/**
 * Dragon Realm Portal Block - The actual portal that teleports entities
 *
 * Properties:
 * - Indestructible (-1.0f strength)
 * - No collision (walk through)
 * - Bright luminance (15)
 * - Teleports on entity collision
 * - Validates frame integrity
 */
public class DragonRealmPortalBlock extends Block {

    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

    protected static final VoxelShape X_SHAPE = Block.box(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
    protected static final VoxelShape Z_SHAPE = Block.box(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);

    public DragonRealmPortalBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.defaultBlockState().setValue(AXIS, Direction.Axis.X));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, net.minecraft.world.phys.shapes.CollisionContext context) {
        return state.getValue(AXIS) == Direction.Axis.X ? X_SHAPE : Z_SHAPE;
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        // Enchant particles (main portal effect - glowing purple)
        if (random.nextInt(3) == 0) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + random.nextDouble();
            double z = pos.getZ() + random.nextDouble();

            double vx = (random.nextDouble() - 0.5) * 0.1;
            double vy = (random.nextDouble() - 0.5) * 0.1;
            double vz = (random.nextDouble() - 0.5) * 0.1;

            world.addParticle(ParticleTypes.ENCHANT, x, y, z, vx, vy, vz);
        }

        // Portal particles (swirling effect)
        if (random.nextInt(2) == 0) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + random.nextDouble();
            double z = pos.getZ() + random.nextDouble();

            double vx = (random.nextDouble() - 0.5) * 0.5;
            double vy = -random.nextDouble() * 0.5;
            double vz = (random.nextDouble() - 0.5) * 0.5;

            world.addParticle(ParticleTypes.PORTAL, x, y, z, vx, vy, vz);
        }

        // End rod particles (sparkles)
        if (random.nextInt(5) == 0) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + random.nextDouble();
            double z = pos.getZ() + random.nextDouble();

            world.addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0.05, 0);
        }

        // Ambient portal sound
        if (random.nextInt(100) == 0) {
            world.playSound(
                    null,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    SoundEvents.PORTAL_AMBIENT,
                    SoundSource.BLOCKS,
                    0.3f,
                    random.nextFloat() * 0.4f + 0.8f
            );
        }
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        super.randomTick(state, world, pos, random);
        handleEntityCollisions(state, world, pos);
    }

    @Override
    protected void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        handleEntityCollisions(state, world, pos);
        // Schedule next tick
        world.scheduleTick(pos, this, 10);
    }

    @Override
    protected void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onPlace(state, world, pos, oldState, notify);
        // Schedule first tick when portal is created
        if (!world.isClientSide()) {
            world.scheduleTick(pos, this, 10);
        }
    }

    private void handleEntityCollisions(BlockState state, ServerLevel world, BlockPos pos) {
        // Get bounding box for this block
        AABB box = state.getShape(world, pos).bounds().move(pos);

        // Find all entities in this block
        List<Entity> entities = world.getEntitiesOfClass(Entity.class, box, entity -> true);

        for (Entity entity : entities) {
            if (entity instanceof ServerPlayer player) {
                teleportPlayer(player, world, pos);
            }
        }
    }

    private void teleportPlayer(ServerPlayer player, ServerLevel world, BlockPos pos) {
        // Skip if player already has cooldown
        if (player.isOnPortalCooldown()) {
            return;
        }

        // Get destination world
        ResourceKey<Level> destinationKey = world.dimension() == DragonRealmTeleporter.DRAGON_REALM
                ? DragonRealmTeleporter.OVERWORLD
                : DragonRealmTeleporter.DRAGON_REALM;

        ServerLevel destWorld = world.getServer().getLevel(destinationKey);

        if (destWorld == null) {
            player.sendSystemMessage(Component.literal("§cDestination dimension not found!"));
            return;
        }

        // Server-side sound BEFORE teleport
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                SoundEvents.PORTAL_TRIGGER, SoundSource.BLOCKS, 1.0f, 1.0f);

        // Particle burst BEFORE teleport
        for (int i = 0; i < 20; i++) {
            world.sendParticles(ParticleTypes.ENCHANT,
                    player.getX(), player.getY() + 1, player.getZ(),
                    1, 0.5, 0.5, 0.5, 0.1);
        }

        // Perform teleport
        DragonRealmTeleporter.teleport(player, destWorld);

        // Set cooldown IMMEDIATELY after teleport (80 ticks = 4 seconds)
        player.setPortalCooldown(80);
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            LevelReader world,
            ScheduledTickAccess tickView,
            BlockPos pos,
            Direction direction,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource random
    ) {
        Direction.Axis axis = state.getValue(AXIS);

        // Check if frame is still valid when a neighbor changes
        if (world instanceof LevelAccessor worldAccess) {
            // Only check if the neighbor that changed was an Obsidian Portal Frame that broke
            if (neighborState.isAir() && !isValidFrame(worldAccess, pos, axis)) {
                return Blocks.AIR.defaultBlockState();
            }
        }

        return state;
    }

    private boolean isValidFrame(LevelAccessor world, BlockPos pos, Direction.Axis axis) {
        // Check if we're still inside a valid portal frame
        // We need at least one Obsidian Portal Frame adjacent to us

        Direction dir1 = axis == Direction.Axis.X ? Direction.NORTH : Direction.WEST;
        Direction dir2 = dir1.getOpposite();

        BlockState state1 = world.getBlockState(pos.relative(dir1));
        BlockState state2 = world.getBlockState(pos.relative(dir2));
        BlockState stateUp = world.getBlockState(pos.above());
        BlockState stateDown = world.getBlockState(pos.below());

        // Count how many adjacent Obsidian Portal Frame or Portal blocks we have
        int validNeighbors = 0;
        if (state1.getBlock() instanceof ObsidianPortalFrameBlock || state1.getBlock() instanceof DragonRealmPortalBlock) validNeighbors++;
        if (state2.getBlock() instanceof ObsidianPortalFrameBlock || state2.getBlock() instanceof DragonRealmPortalBlock) validNeighbors++;
        if (stateUp.getBlock() instanceof ObsidianPortalFrameBlock || stateUp.getBlock() instanceof DragonRealmPortalBlock) validNeighbors++;
        if (stateDown.getBlock() instanceof ObsidianPortalFrameBlock || stateDown.getBlock() instanceof DragonRealmPortalBlock) validNeighbors++;

        // Need at least 2 valid neighbors to stay intact (prevents floating portal blocks)
        return validNeighbors >= 2;
    }
}
