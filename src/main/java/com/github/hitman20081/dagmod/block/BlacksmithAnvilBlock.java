package com.github.hitman20081.dagmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlacksmithAnvilBlock extends Block {

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape BASE   = Block.box( 2.0,  0.0,  2.0, 14.0,  4.0, 14.0);
    private static final VoxelShape X_LEG1 = Block.box( 4.0,  4.0,  3.0, 12.0,  5.0, 13.0);
    private static final VoxelShape X_LEG2 = Block.box( 5.0,  5.0,  4.0, 11.0, 10.0, 12.0);
    private static final VoxelShape X_TOP  = Block.box( 0.0, 10.0,  4.0, 16.0, 16.0, 12.0);
    private static final VoxelShape Z_LEG1 = Block.box( 3.0,  4.0,  4.0, 13.0,  5.0, 12.0);
    private static final VoxelShape Z_LEG2 = Block.box( 4.0,  5.0,  5.0, 12.0, 10.0, 11.0);
    private static final VoxelShape Z_TOP  = Block.box( 4.0, 10.0,  0.0, 12.0, 16.0, 16.0);
    private static final VoxelShape X_SHAPE = Shapes.or(BASE, X_LEG1, X_LEG2, X_TOP);
    private static final VoxelShape Z_SHAPE = Shapes.or(BASE, Z_LEG1, Z_LEG2, Z_TOP);

    public BlacksmithAnvilBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        if (player.isCreative()) {
            return super.getDestroyProgress(state, player, level, pos);
        }
        return 0.0F;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        return facing.getAxis() == Direction.Axis.X ? X_SHAPE : Z_SHAPE;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        player.openMenu(new SimpleMenuProvider(
                (syncId, inventory, p) -> new AnvilMenu(syncId, inventory, ContainerLevelAccess.create(level, pos)) {
                    @Override
                    public boolean stillValid(Player player) {
                        return true;
                    }
                },
                Component.translatable("container.repair")
        ));
        return InteractionResult.CONSUME;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
