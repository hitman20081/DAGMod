package com.github.hitman20081.dagmod.pale_garden.portal;

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

public class PaleGardenPortalBlock extends Block {

    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

    protected static final VoxelShape X_SHAPE = Block.box(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
    protected static final VoxelShape Z_SHAPE = Block.box(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);

    public PaleGardenPortalBlock(Properties settings) {
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
        if (random.nextInt(100) == 0) {
            world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS,
                    0.5f, random.nextFloat() * 0.4f + 0.8f);
        }

        for (int i = 0; i < 4; i++) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + random.nextDouble();
            double z = pos.getZ() + random.nextDouble();
            double vx = (random.nextDouble() - 0.5) * 0.5;
            double vy = (random.nextDouble() - 0.5) * 0.5;
            double vz = (random.nextDouble() - 0.5) * 0.5;

            world.addParticle(ParticleTypes.PORTAL, x, y, z, vx, vy, vz);

            if (random.nextInt(3) == 0) {
                world.addParticle(ParticleTypes.END_ROD, x, y, z, 0.0, 0.015, 0.0);
            }
        }
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        handleEntityCollisions(state, world, pos);
    }

    @Override
    protected void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        handleEntityCollisions(state, world, pos);
        world.scheduleTick(pos, this, 10);
    }

    @Override
    protected void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onPlace(state, world, pos, oldState, notify);
        if (!world.isClientSide()) {
            world.scheduleTick(pos, this, 10);
        }
    }

    private void handleEntityCollisions(BlockState state, ServerLevel world, BlockPos pos) {
        AABB box = state.getShape(world, pos).bounds().move(pos);
        List<Entity> entities = world.getEntitiesOfClass(Entity.class, box, entity -> true);

        for (Entity entity : entities) {
            if (entity instanceof ServerPlayer player) {
                teleportPlayer(player, world, pos);
            }
        }
    }

    private void teleportPlayer(ServerPlayer player, ServerLevel world, BlockPos pos) {
        if (player.isOnPortalCooldown()) return;

        ResourceKey<Level> destinationKey = world.dimension() == PaleGardenTeleporter.PALE_GARDEN
                ? PaleGardenTeleporter.OVERWORLD
                : PaleGardenTeleporter.PALE_GARDEN;

        ServerLevel destWorld = world.getServer().getLevel(destinationKey);
        if (destWorld == null) {
            player.sendSystemMessage(Component.literal("§cDestination dimension not found!"));
            return;
        }

        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                SoundEvents.PORTAL_TRIGGER, SoundSource.BLOCKS, 1.0f, 1.0f);

        for (int i = 0; i < 20; i++) {
            world.sendParticles(ParticleTypes.END_ROD,
                    player.getX(), player.getY() + 1, player.getZ(),
                    1, 0.5, 0.5, 0.5, 0.05);
        }

        PaleGardenTeleporter.teleport(player, destWorld);
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

        if (world instanceof LevelAccessor worldAccess) {
            if (neighborState.isAir() && !isValidFrame(worldAccess, pos, axis)) {
                return Blocks.AIR.defaultBlockState();
            }
        }

        return state;
    }

    private boolean isValidFrame(LevelAccessor world, BlockPos pos, Direction.Axis axis) {
        Direction dir1 = axis == Direction.Axis.X ? Direction.NORTH : Direction.WEST;
        Direction dir2 = dir1.getOpposite();

        int validNeighbors = 0;
        if (isFrameOrPortal(world.getBlockState(pos.relative(dir1)))) validNeighbors++;
        if (isFrameOrPortal(world.getBlockState(pos.relative(dir2)))) validNeighbors++;
        if (isFrameOrPortal(world.getBlockState(pos.above()))) validNeighbors++;
        if (isFrameOrPortal(world.getBlockState(pos.below()))) validNeighbors++;

        return validNeighbors >= 2;
    }

    private boolean isFrameOrPortal(BlockState state) {
        return state.getBlock() instanceof PaleHeartstoneBlock || state.getBlock() instanceof PaleGardenPortalBlock;
    }
}
