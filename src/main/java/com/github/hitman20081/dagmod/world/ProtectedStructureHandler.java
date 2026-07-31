package com.github.hitman20081.dagmod.world;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.List;

public class ProtectedStructureHandler {

    // Add structure paths here to make them unbreakable in survival mode.
    private static final List<ResourceKey<Structure>> PROTECTED = List.of(
        structure("hall_of_champions")
    );

    public static void register() {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (player.isCreative()) return true;
            if (!(world instanceof ServerLevel serverLevel)) return true;

            for (ResourceKey<Structure> key : PROTECTED) {
                if (serverLevel.structureManager().getStructureWithPieceAt(pos, (Holder<Structure> h) -> h.is(key)).isValid()) {
                    player.sendSystemMessage(
                        Component.literal("The Hall of Champions is sacred ground — its stones cannot be moved.")
                            .withStyle(ChatFormatting.RED));
                    return false;
                }
            }
            return true;
        });
    }

    private static ResourceKey<Structure> structure(String path) {
        return ResourceKey.create(Registries.STRUCTURE, Identifier.fromNamespaceAndPath("dagmod", path));
    }
}
