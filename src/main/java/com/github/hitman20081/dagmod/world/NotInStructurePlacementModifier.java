package com.github.hitman20081.dagmod.world;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.stream.Stream;

/**
 * Placement modifier that prevents a feature from generating inside any
 * structure's bounding box. Used to stop basalt columns from growing
 * inside enclosed structures in the Bone Realm.
 */
public class NotInStructurePlacementModifier extends PlacementModifier {

    public static final NotInStructurePlacementModifier INSTANCE = new NotInStructurePlacementModifier();
    public static final MapCodec<NotInStructurePlacementModifier> CODEC = MapCodec.unit(INSTANCE);

    public static PlacementModifierType<NotInStructurePlacementModifier> TYPE;

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
        WorldGenLevel world = context.getLevel();

        // Check every structure start registered in this chunk
        for (StructureStart start : world.getChunk(pos).getAllStarts().values()) {
            if (!start.getPieces().isEmpty() && start.getBoundingBox().isInside(pos.getX(), pos.getY(), pos.getZ())) {
                return Stream.empty();
            }
        }

        return Stream.of(pos);
    }

    @Override
    public PlacementModifierType<?> type() {
        return TYPE;
    }
}
