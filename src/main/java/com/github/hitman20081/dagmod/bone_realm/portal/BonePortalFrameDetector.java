package com.github.hitman20081.dagmod.bone_realm.portal;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects valid 5x6 Bone Realm portal frames
 * Validates all Ancient Bone Blocks are in correct position
 */
public class BonePortalFrameDetector {

    private final Level world;
    private final BlockPos clickedPos;
    private BlockPos bottomLeft;
    private Direction.Axis axis;
    private int width;
    private int height;

    public BonePortalFrameDetector(Level world, BlockPos pos) {
        this.world = world;
        this.clickedPos = pos;
    }

    /**
     * Checks if a valid portal frame exists at the clicked position
     * @return true if valid 5x6 frame found
     */
    public boolean isValidFrame() {
        // Try both X and Z axes
        if (tryFindFrame(Direction.Axis.Z)) {
            return true;
        }
        return tryFindFrame(Direction.Axis.X);
    }

    private boolean tryFindFrame(Direction.Axis testAxis) {
        this.axis = testAxis;

        // Portal must be 5 wide x 6 tall
        this.width = 5;
        this.height = 6;

        // Try to find bottom-left corner from clicked position
        BlockPos testPos = findBottomLeft(clickedPos, testAxis);
        if (testPos == null) {
            return false;
        }

        this.bottomLeft = testPos;

        // Validate entire frame
        return validateFrame();
    }

    private BlockPos findBottomLeft(BlockPos start, Direction.Axis testAxis) {
        // Search down and left to find corner
        BlockPos current = start;

        // Go down to bottom
        while (isAncientBone(current.below())) {
            current = current.below();
        }

        // Go left to edge based on axis
        // If axis is X, the frame extends along X (east-west), so we move north-south to find edge
        // If axis is Z, the frame extends along Z (north-south), so we move east-west to find edge
        Direction leftDir = testAxis == Direction.Axis.X ? Direction.NORTH : Direction.WEST;
        while (isAncientBone(current.relative(leftDir))) {
            current = current.relative(leftDir);
        }

        // Check if this looks like a corner
        if (isAncientBone(current) &&
                isAncientBone(current.above()) &&
                isAncientBone(current.relative(leftDir.getOpposite()))) {
            return current;
        }

        return null;
    }

    private boolean validateFrame() {
        // Direction to move "right" along the frame
        Direction rightDir = axis == Direction.Axis.X ? Direction.SOUTH : Direction.EAST;

        // Check bottom edge
        for (int i = 0; i < width; i++) {
            if (!isAncientBone(bottomLeft.relative(rightDir, i))) {
                return false;
            }
        }

        // Check top edge
        BlockPos topLeft = bottomLeft.above(height - 1);
        for (int i = 0; i < width; i++) {
            if (!isAncientBone(topLeft.relative(rightDir, i))) {
                return false;
            }
        }

        // Check left edge
        for (int i = 0; i < height; i++) {
            if (!isAncientBone(bottomLeft.above(i))) {
                return false;
            }
        }

        // Check right edge
        BlockPos bottomRight = bottomLeft.relative(rightDir, width - 1);
        for (int i = 0; i < height; i++) {
            if (!isAncientBone(bottomRight.above(i))) {
                return false;
            }
        }

        // Check interior is empty (air blocks)
        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                BlockPos interiorPos = bottomLeft.relative(rightDir, x).above(y);
                if (!world.getBlockState(interiorPos).isAir()) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Gets list of all interior positions where portal blocks should be placed
     */
    public List<BlockPos> getInteriorPositions() {
        List<BlockPos> positions = new ArrayList<>();
        Direction rightDir = axis == Direction.Axis.X ? Direction.SOUTH : Direction.EAST;

        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
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

    private boolean isAncientBone(BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.getBlock() instanceof AncientBoneBlock;
    }
}