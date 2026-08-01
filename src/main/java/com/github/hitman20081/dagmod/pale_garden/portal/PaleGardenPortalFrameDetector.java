package com.github.hitman20081.dagmod.pale_garden.portal;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class PaleGardenPortalFrameDetector {

    private final Level world;
    private final BlockPos clickedPos;
    private BlockPos bottomLeft;
    private Direction.Axis axis;
    private static final int WIDTH = 5;
    private static final int HEIGHT = 5;

    public PaleGardenPortalFrameDetector(Level world, BlockPos pos) {
        this.world = world;
        this.clickedPos = pos;
    }

    public boolean isValidFrame() {
        if (tryFindFrame(Direction.Axis.Z)) return true;
        return tryFindFrame(Direction.Axis.X);
    }

    private boolean tryFindFrame(Direction.Axis testAxis) {
        this.axis = testAxis;
        BlockPos testPos = findBottomLeft(clickedPos, testAxis);
        if (testPos == null) return false;
        this.bottomLeft = testPos;
        return validateFrame();
    }

    private BlockPos findBottomLeft(BlockPos start, Direction.Axis testAxis) {
        BlockPos current = start;

        while (isPaleHeartstone(current.below())) {
            current = current.below();
        }

        Direction leftDir = testAxis == Direction.Axis.X ? Direction.NORTH : Direction.WEST;
        while (isPaleHeartstone(current.relative(leftDir))) {
            current = current.relative(leftDir);
        }

        if (isPaleHeartstone(current) &&
                isPaleHeartstone(current.above()) &&
                isPaleHeartstone(current.relative(leftDir.getOpposite()))) {
            return current;
        }

        return null;
    }

    private boolean validateFrame() {
        Direction rightDir = axis == Direction.Axis.X ? Direction.SOUTH : Direction.EAST;

        for (int i = 0; i < WIDTH; i++) {
            if (!isPaleHeartstone(bottomLeft.relative(rightDir, i))) return false;
        }

        BlockPos topLeft = bottomLeft.above(HEIGHT - 1);
        for (int i = 0; i < WIDTH; i++) {
            if (!isPaleHeartstone(topLeft.relative(rightDir, i))) return false;
        }

        for (int i = 0; i < HEIGHT; i++) {
            if (!isPaleHeartstone(bottomLeft.above(i))) return false;
        }

        BlockPos bottomRight = bottomLeft.relative(rightDir, WIDTH - 1);
        for (int i = 0; i < HEIGHT; i++) {
            if (!isPaleHeartstone(bottomRight.above(i))) return false;
        }

        for (int x = 1; x < WIDTH - 1; x++) {
            for (int y = 1; y < HEIGHT - 1; y++) {
                BlockPos interiorPos = bottomLeft.relative(rightDir, x).above(y);
                BlockState state = world.getBlockState(interiorPos);
                if (!state.isAir() && !(state.getBlock() instanceof PaleGardenPortalBlock)) {
                    return false;
                }
            }
        }

        return true;
    }

    public List<BlockPos> getInteriorPositions() {
        List<BlockPos> positions = new ArrayList<>();
        Direction rightDir = axis == Direction.Axis.X ? Direction.SOUTH : Direction.EAST;

        for (int x = 1; x < WIDTH - 1; x++) {
            for (int y = 1; y < HEIGHT - 1; y++) {
                positions.add(bottomLeft.relative(rightDir, x).above(y));
            }
        }

        return positions;
    }

    public Direction.Axis getAxis() {
        return axis;
    }

    public BlockPos getBottomLeft() {
        return bottomLeft;
    }

    private boolean isPaleHeartstone(BlockPos pos) {
        return world.getBlockState(pos).getBlock() instanceof PaleHeartstoneBlock;
    }
}
