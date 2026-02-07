package com.github.hitman20081.dagmod.block;

import com.github.hitman20081.dagmod.block.entity.GemPolishingStationBlockEntity;
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
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GemPolishingStationBlock extends BlockWithEntity {
    public static final MapCodec<GemPolishingStationBlock> CODEC = createCodec(GemPolishingStationBlock::new);

    // Define the shape based on the block model
    private static final VoxelShape SHAPE;

    static {
        // Four corner legs (2x4x2 each)
        VoxelShape legFrontLeft = Block.createCuboidShape(0, 0, 0, 2, 4, 2);
        VoxelShape legFrontRight = Block.createCuboidShape(14, 0, 0, 16, 4, 2);
        VoxelShape legBackLeft = Block.createCuboidShape(0, 0, 14, 2, 4, 16);
        VoxelShape legBackRight = Block.createCuboidShape(14, 0, 14, 16, 4, 16);

        // Base section (full width, height 4-12)
        VoxelShape base = Block.createCuboidShape(0, 4, 0, 16, 12, 16);

        // Tray section (centered 6x1x6, height 12-13)
        VoxelShape tray = Block.createCuboidShape(5, 12, 5, 11, 13, 11);

        // Combine all shapes
        SHAPE = VoxelShapes.union(legFrontLeft, legFrontRight, legBackLeft, legBackRight, base, tray);
    }

    public GemPolishingStationBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
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
        return new GemPolishingStationBlockEntity(pos, state);
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

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient()) {
            return null;
        }

        return validateTicker(type, com.github.hitman20081.dagmod.block.entity.ModBlockEntities.GEM_POLISHING_STATION,
                GemPolishingStationBlockEntity::tick);
    }
}
