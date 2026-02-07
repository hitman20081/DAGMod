package com.github.hitman20081.dagmod.bone_realm.portal;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.*;
import net.minecraft.world.tick.ScheduledTickView;

import java.util.List;

/**
 * Bone Realm Portal Block - The actual portal
 * Players step in and teleport after brief delay
 */
public class BoneRealmPortalBlock extends Block {

    public static final EnumProperty<Direction.Axis> AXIS = Properties.HORIZONTAL_AXIS;

    protected static final VoxelShape X_SHAPE = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
    protected static final VoxelShape Z_SHAPE = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);

    public BoneRealmPortalBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(AXIS, Direction.Axis.X));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(AXIS) == Direction.Axis.Z ? Z_SHAPE : X_SHAPE;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        // Ambient portal sounds
        if (random.nextInt(100) == 0) {
            world.playSound(
                    null,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    SoundEvents.BLOCK_PORTAL_AMBIENT,
                    SoundCategory.BLOCKS,
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
            world.addParticleClient(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, vx, vy, vz);

            // Occasional soul particles
            if (random.nextInt(3) == 0) {
                world.addParticleClient(ParticleTypes.SOUL, x, y, z, vx, vy, vz);
            }
        }
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        // Check for entities every random tick
        handleEntityCollisions(state, world, pos);
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        // Also check on scheduled ticks
        handleEntityCollisions(state, world, pos);
        // Schedule next tick
        world.scheduleBlockTick(pos, this, 10);
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        // Schedule first tick when portal is created
        if (!world.isClient()) {
            world.scheduleBlockTick(pos, this, 10);
        }
    }

    private void handleEntityCollisions(BlockState state, ServerWorld world, BlockPos pos) {
        // Get bounding box for this block
        Box box = state.getOutlineShape(world, pos).getBoundingBox().offset(pos);

        // Find all entities in this block
        List<Entity> entities = world.getEntitiesByClass(Entity.class, box, entity -> true);

        for (Entity entity : entities) {
            if (entity instanceof ServerPlayerEntity player) {
                teleportPlayer(player, world, pos);
            }
        }
    }

    private void teleportPlayer(ServerPlayerEntity player, ServerWorld world, BlockPos pos) {
        // Skip if player already has cooldown
        if (player.hasPortalCooldown()) {
            return;
        }

        // Get destination world
        RegistryKey<World> destinationKey = world.getRegistryKey() == BoneRealmTeleporter.BONE_REALM
                ? BoneRealmTeleporter.OVERWORLD
                : BoneRealmTeleporter.BONE_REALM;

        ServerWorld destWorld = world.getServer().getWorld(destinationKey);

        if (destWorld == null) {
            player.sendMessage(Text.literal("§cDestination dimension not found!"), false);
            return;
        }

        // Server-side sound BEFORE teleport
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                SoundEvents.BLOCK_PORTAL_TRIGGER, SoundCategory.BLOCKS, 1.0f, 1.0f);

        // Particle burst BEFORE teleport
        for (int i = 0; i < 20; i++) {
            world.spawnParticles(ParticleTypes.SOUL,
                    player.getX(), player.getY() + 1, player.getZ(),
                    1, 0.5, 0.5, 0.5, 0.1);
        }

        // Perform teleport
        BoneRealmTeleporter.teleport(player, destWorld);

        // Set cooldown IMMEDIATELY after teleport (80 ticks = 4 seconds)
        player.setPortalCooldown(80);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(
            BlockState state,
            WorldView world,
            ScheduledTickView tickView,
            BlockPos pos,
            Direction direction,
            BlockPos neighborPos,
            BlockState neighborState,
            Random random
    ) {
        Direction.Axis axis = state.get(AXIS);

        // Check if frame is still valid when a neighbor changes
        if (world instanceof WorldAccess worldAccess) {
            // Only check if the neighbor that changed was an Ancient Bone Block that broke
            if (neighborState.isAir() && !isValidFrame(worldAccess, pos, axis)) {
                return Blocks.AIR.getDefaultState();
            }
        }

        return state;
    }

    private boolean isValidFrame(WorldAccess world, BlockPos pos, Direction.Axis axis) {
        // Check if we're still inside a valid portal frame
        // We need at least one Ancient Bone Block adjacent to us

        Direction dir1 = axis == Direction.Axis.X ? Direction.NORTH : Direction.WEST;
        Direction dir2 = dir1.getOpposite();

        BlockState state1 = world.getBlockState(pos.offset(dir1));
        BlockState state2 = world.getBlockState(pos.offset(dir2));
        BlockState stateUp = world.getBlockState(pos.up());
        BlockState stateDown = world.getBlockState(pos.down());

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