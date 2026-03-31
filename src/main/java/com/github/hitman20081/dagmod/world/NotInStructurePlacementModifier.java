package com.github.hitman20081.dagmod.world;

import com.mojang.serialization.MapCodec;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.FeaturePlacementContext;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;

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
    public Stream<BlockPos> getPositions(FeaturePlacementContext context, Random random, BlockPos pos) {
        StructureWorldAccess world = context.getWorld();

        // Check every structure start registered in this chunk
        for (StructureStart start : world.getChunk(pos).getStructureStarts().values()) {
            if (start.hasChildren() && start.getBoundingBox().contains(pos.getX(), pos.getY(), pos.getZ())) {
                return Stream.empty();
            }
        }

        return Stream.of(pos);
    }

    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}
