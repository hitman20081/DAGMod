package com.github.hitman20081.dagmod.block;

import com.github.hitman20081.dagmod.block.entity.GemInfusingStationBlockEntity;
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

public class GemInfusingStationBlock extends BaseEntityBlock {
    public static final MapCodec<GemInfusingStationBlock> CODEC = simpleCodec(GemInfusingStationBlock::new);

    // Define the shape based on the block model (same shape as Gem Polishing Station)
    private static final VoxelShape SHAPE;

    static {
        // Four corner legs (2x4x2 each)
        VoxelShape legFrontLeft = Block.box(0, 0, 0, 2, 4, 2);
        VoxelShape legFrontRight = Block.box(14, 0, 0, 16, 4, 2);
        VoxelShape legBackLeft = Block.box(0, 0, 14, 2, 4, 16);
        VoxelShape legBackRight = Block.box(14, 0, 14, 16, 4, 16);

        // Base section (full width, height 4-10)
        VoxelShape base = Block.box(0, 4, 0, 16, 10, 16);

        // Tray section (centered 4x1x4, height 10-11)
        VoxelShape tray = Block.box(6, 10, 6, 10, 11, 10);

        // Combine all shapes
        SHAPE = Shapes.or(legFrontLeft, legFrontRight, legBackLeft, legBackRight, base, tray);
    }

    public GemInfusingStationBlock(Properties settings) {
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
        return new GemInfusingStationBlockEntity(pos, state);
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

        return createTickerHelper(type, com.github.hitman20081.dagmod.block.entity.ModBlockEntities.GEM_INFUSING_STATION,
                GemInfusingStationBlockEntity::tick);
    }
}
