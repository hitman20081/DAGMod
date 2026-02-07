package com.github.hitman20081.dagmod.block;

import com.github.hitman20081.dagmod.block.entity.GemCuttingStationBlockEntity;
import com.github.hitman20081.dagmod.block.entity.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

public class GemCuttingStationBlock extends BlockWithEntity {
    public static final MapCodec<GemCuttingStationBlock> CODEC = createCodec(GemCuttingStationBlock::new);
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;

    // Define the shape based on the block model (table with legs)
    private static final VoxelShape SHAPE;

    static {
        // Four corner legs (2x4x2 each)
        VoxelShape legFrontLeft = Block.createCuboidShape(0, 0, 0, 2, 4, 2);
        VoxelShape legFrontRight = Block.createCuboidShape(14, 0, 0, 16, 4, 2);
        VoxelShape legBackLeft = Block.createCuboidShape(0, 0, 14, 2, 4, 16);
        VoxelShape legBackRight = Block.createCuboidShape(14, 0, 14, 16, 4, 16);

        // Base section (full width, height 4-10)
        VoxelShape base = Block.createCuboidShape(0, 4, 0, 16, 10, 16);

        // Tray section (centered 4x1x4, height 10-11)
        VoxelShape tray = Block.createCuboidShape(6, 10, 6, 10, 11, 10);

        // Combine all shapes
        SHAPE = VoxelShapes.union(legFrontLeft, legFrontRight, legBackLeft, legBackRight, base, tray);
    }

    public GemCuttingStationBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GemCuttingStationBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient()) {
            NamedScreenHandlerFactory screenHandlerFactory = (NamedScreenHandlerFactory) world.getBlockEntity(pos);
            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof GemCuttingStationBlockEntity gemCuttingStation) {
            ItemScatterer.spawn(world, pos, gemCuttingStation);
            world.updateComparators(pos, this);
        }
        super.onStateReplaced(state, world, pos, moved);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient()) {
            return null;
        }
        return validateTicker(type, ModBlockEntities.GEM_CUTTING_STATION,
                GemCuttingStationBlockEntity::tick);
    }
}
