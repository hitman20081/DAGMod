package com.github.hitman20081.dagmod.block;

import com.github.hitman20081.dagmod.block.entity.GemPolishingStationBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class GemPolishingStationBlock extends BaseEntityBlock {
    public static final MapCodec<GemPolishingStationBlock> CODEC = simpleCodec(GemPolishingStationBlock::new);

    // Define the shape based on the block model
    private static final VoxelShape SHAPE;

    static {
        // Four corner legs (2x4x2 each)
        VoxelShape legFrontLeft = Block.box(0, 0, 0, 2, 4, 2);
        VoxelShape legFrontRight = Block.box(14, 0, 0, 16, 4, 2);
        VoxelShape legBackLeft = Block.box(0, 0, 14, 2, 4, 16);
        VoxelShape legBackRight = Block.box(14, 0, 14, 16, 4, 16);

        // Base section (full width, height 4-12)
        VoxelShape base = Block.box(0, 4, 0, 16, 12, 16);

        // Tray section (centered 6x1x6, height 12-13)
        VoxelShape tray = Block.box(5, 12, 5, 11, 13, 11);

        // Combine all shapes
        SHAPE = Shapes.or(legFrontLeft, legFrontRight, legBackLeft, legBackRight, base, tray);
    }

    public GemPolishingStationBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GemPolishingStationBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (!world.isClientSide()) {
            MenuProvider screenHandlerFactory = (MenuProvider) world.getBlockEntity(pos);
            if (screenHandlerFactory != null) {
                player.openMenu(screenHandlerFactory);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        if (world.isClientSide()) {
            return null;
        }

        return createTickerHelper(type, com.github.hitman20081.dagmod.block.entity.ModBlockEntities.GEM_POLISHING_STATION,
                GemPolishingStationBlockEntity::tick);
    }
}
