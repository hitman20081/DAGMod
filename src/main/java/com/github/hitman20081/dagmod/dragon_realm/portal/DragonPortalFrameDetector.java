package com.github.hitman20081.dagmod.dragon_realm.portal;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects valid 7x7 Dragon Realm portal frames with a 5x5 interior.
 * Validates all Obsidian Portal Frame blocks are in correct position.
 *
 * Portal Structure (7x7 Frame):
 * OOOOOOO
 * O     O
 * O     O
 * O     O  (O = Obsidian Portal Frame, space = air/portal)
 * O     O
 * O     O
 * OOOOOOO
 */
public class DragonPortalFrameDetector {

    private final World world;
    private final BlockPos clickedPos;
    private BlockPos bottomLeft;
    private Direction.Axis axis;
    private final int width = 7;
    private final int height = 7;

    public DragonPortalFrameDetector(World world, BlockPos pos) {
        this.world = world;
        this.clickedPos = pos;
    }

    /**
     * Checks if a valid portal frame exists at the clicked position
     * @return true if valid 3x3 frame found
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
        while (isObsidianFrame(current.down())) {
            current = current.down();
        }

        // Go left to edge based on axis
        // If axis is X, the frame extends along X (east-west), so we move north-south to find edge
        // If axis is Z, the frame extends along Z (north-south), so we move east-west to find edge
        Direction leftDir = testAxis == Direction.Axis.X ? Direction.NORTH : Direction.WEST;
        while (isObsidianFrame(current.offset(leftDir))) {
            current = current.offset(leftDir);
        }

        // Check if this looks like a corner
        if (isObsidianFrame(current) &&
                isObsidianFrame(current.up()) &&
                isObsidianFrame(current.offset(leftDir.getOpposite()))) {
            return current;
        }

        return null;
    }

    private boolean validateFrame() {
        // Direction to move "right" along the frame
        Direction rightDir = axis == Direction.Axis.X ? Direction.SOUTH : Direction.EAST;

        // Check bottom edge
        for (int i = 0; i < width; i++) {
            if (!isObsidianFrame(bottomLeft.offset(rightDir, i))) {
                return false;
            }
        }

        // Check top edge
        BlockPos topLeft = bottomLeft.up(height - 1);
        for (int i = 0; i < width; i++) {
            if (!isObsidianFrame(topLeft.offset(rightDir, i))) {
                return false;
            }
        }

        // Check left edge
        for (int i = 0; i < height; i++) {
            if (!isObsidianFrame(bottomLeft.up(i))) {
                return false;
            }
        }

        // Check right edge
        BlockPos bottomRight = bottomLeft.offset(rightDir, width - 1);
        for (int i = 0; i < height; i++) {
            if (!isObsidianFrame(bottomRight.up(i))) {
                return false;
            }
        }

        // Check if interior 5x5 is empty (air blocks)
        for (int w = 1; w < width - 1; w++) {
            for (int h = 1; h < height - 1; h++) {
                if (!world.getBlockState(bottomLeft.offset(rightDir, w).up(h)).isAir()) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Gets list of all interior positions where portal blocks should be placed
     * For a 7x7 frame, this is a 5x5 area.
     */
    public List<BlockPos> getInteriorPositions() {
        List<BlockPos> positions = new ArrayList<>();
        Direction rightDir = axis == Direction.Axis.X ? Direction.SOUTH : Direction.EAST;
        BlockPos interiorBottomLeft = bottomLeft.offset(rightDir, 1).up(1);

        for (int w = 0; w < width - 2; w++) { // Interior width is 5
            for (int h = 0; h < height - 2; h++) { // Interior height is 5
                positions.add(interiorBottomLeft.offset(rightDir, w).up(h));
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

    private boolean isObsidianFrame(BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.getBlock() instanceof ObsidianPortalFrameBlock;
    }
}
