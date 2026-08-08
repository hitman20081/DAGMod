package com.github.hitman20081.dagmod.bone_realm.portal;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.shapes.CollisionContext;
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
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.*;
import net.minecraft.world.level.ScheduledTickAccess;

import java.util.List;

/**
 * Bone Realm Portal Block - The actual portal
 * Players step in and teleport after brief delay
 */
public class BoneRealmPortalBlock extends Block {

    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

    protected static final VoxelShape X_SHAPE = Block.box(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
    protected static final VoxelShape Z_SHAPE = Block.box(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);

    public BoneRealmPortalBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(AXIS, Direction.Axis.X));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return state.getValue(AXIS) == Direction.Axis.Z ? Z_SHAPE : X_SHAPE;
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        // Ambient portal sounds
        if (random.nextInt(100) == 0) {
            world.playSound(
                    null,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    SoundEvents.PORTAL_AMBIENT,
                    SoundSource.BLOCKS,
                    0.5f,
                    random.nextFloat() * 0.4f + 0.8f
            );
        }

        // Particle effects
        for (int i = 0; i < 4; i++) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + random.nextDouble();
            double z = pos.getZ() + random.nextDouble();
            double vx = (random.nextDouble() - 0.5) * 0.5;
            double vy = (random.nextDouble() - 0.5) * 0.5;
            double vz = (random.nextDouble() - 0.5) * 0.5;

            // Soul fire particles
            world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, vx, vy, vz);

            // Occasional soul particles
            if (random.nextInt(3) == 0) {
                world.addParticle(ParticleTypes.SOUL, x, y, z, vx, vy, vz);
            }
        }
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        // Check for entities every random tick
        handleEntityCollisions(state, world, pos);
    }

    @Override
    protected void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        // Also check on scheduled ticks
        handleEntityCollisions(state, world, pos);
        // A multi-block portal has many interior blocks, each independently
        // rescheduling this same entity-collision check — 20 ticks (vs. the
        // previous 10) halves the total redundant scan volume.
        world.scheduleTick(pos, this, 20);
    }

    @Override
    protected void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onPlace(state, world, pos, oldState, notify);
        // Schedule first tick when portal is created
        if (!world.isClientSide()) {
            world.scheduleTick(pos, this, 20);
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
        ResourceKey<Level> destinationKey = world.dimension() == BoneRealmTeleporter.BONE_REALM
                ? BoneRealmTeleporter.OVERWORLD
                : BoneRealmTeleporter.BONE_REALM;

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
            world.sendParticles(ParticleTypes.SOUL,
                    player.getX(), player.getY() + 1, player.getZ(),
                    1, 0.5, 0.5, 0.5, 0.1);
        }

        // Perform teleport
        BoneRealmTeleporter.teleport(player, destWorld);

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
            // Only check if the neighbor that changed was an Ancient Bone Block that broke
            if (neighborState.isAir() && !isValidFrame(worldAccess, pos, axis)) {
                return Blocks.AIR.defaultBlockState();
            }
        }

        return state;
    }

    private boolean isValidFrame(LevelAccessor world, BlockPos pos, Direction.Axis axis) {
        // Check if we're still inside a valid portal frame
        // We need at least one Ancient Bone Block adjacent to us

        Direction dir1 = axis == Direction.Axis.X ? Direction.NORTH : Direction.WEST;
        Direction dir2 = dir1.getOpposite();

        BlockState state1 = world.getBlockState(pos.relative(dir1));
        BlockState state2 = world.getBlockState(pos.relative(dir2));
        BlockState stateUp = world.getBlockState(pos.above());
        BlockState stateDown = world.getBlockState(pos.below());

        // Count how many adjacent Ancient Bone or Portal blocks we have
        int validNeighbors = 0;
        if (state1.getBlock() instanceof AncientBoneBlock || state1.getBlock() instanceof BoneRealmPortalBlock) validNeighbors++;
        if (state2.getBlock() instanceof AncientBoneBlock || state2.getBlock() instanceof BoneRealmPortalBlock) validNeighbors++;
        if (stateUp.getBlock() instanceof AncientBoneBlock || stateUp.getBlock() instanceof BoneRealmPortalBlock) validNeighbors++;
        if (stateDown.getBlock() instanceof AncientBoneBlock || stateDown.getBlock() instanceof BoneRealmPortalBlock) validNeighbors++;

        // Need at least 2 valid neighbors to stay intact (prevents floating portal blocks)
        return validNeighbors >= 2;
    }
}