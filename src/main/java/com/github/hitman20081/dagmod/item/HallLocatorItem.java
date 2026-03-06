package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.DagMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.util.concurrent.CompletableFuture;

public class HallLocatorItem extends Item {

    public HallLocatorItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        if (!world.isClient() && player instanceof ServerPlayerEntity serverPlayer) {
            ServerWorld serverWorld = (ServerWorld) player.getEntityWorld();
            MinecraftServer server = serverWorld.getServer();
            BlockPos playerPos = serverPlayer.getBlockPos();

            serverPlayer.sendMessage(
                Text.literal("Consulting the ancient records...")
                    .formatted(Formatting.YELLOW), false);

            CompletableFuture.runAsync(() -> {
                try {
                    var registry = serverWorld.getRegistryManager().getOrThrow(RegistryKeys.STRUCTURE);
                    var entry = registry.getEntry(Identifier.of(DagMod.MOD_ID, "hall_of_champions")).orElse(null);

                    if (entry == null) {
                        server.execute(() -> serverPlayer.sendMessage(
                            Text.literal("The records seem incomplete. (Missing hall_of_champions structure)")
                                .formatted(Formatting.RED), false));
                        return;
                    }

                    var structures = RegistryEntryList.of(entry);
                    ChunkGenerator chunkGenerator = serverWorld.getChunkManager().getChunkGenerator();
                    var result = chunkGenerator.locateStructure(serverWorld, structures, playerPos, 500, false);

                    server.execute(() -> {
                        if (result == null) {
                            serverPlayer.sendMessage(
                                Text.literal("No Hall of Champions found within range. Explore further and try again.")
                                    .formatted(Formatting.RED), false);
                        } else {
                            BlockPos hallPos = result.getFirst();
                            int distance = (int) Math.sqrt(playerPos.getSquaredDistance(hallPos));

                            serverPlayer.sendMessage(
                                Text.literal("═══════════════════════════════")
                                    .formatted(Formatting.GOLD), false);
                            serverPlayer.sendMessage(
                                Text.literal("  HALL OF CHAMPIONS LOCATED")
                                    .formatted(Formatting.LIGHT_PURPLE).formatted(Formatting.BOLD), false);
                            serverPlayer.sendMessage(
                                Text.literal("  Coordinates: ")
                                    .formatted(Formatting.GRAY)
                                    .append(Text.literal("[" + hallPos.getX() + ", ~, " + hallPos.getZ() + "]")
                                        .formatted(Formatting.AQUA)), false);
                            serverPlayer.sendMessage(
                                Text.literal("  Distance: ~" + distance + " blocks")
                                    .formatted(Formatting.GRAY), false);
                            serverPlayer.sendMessage(
                                Text.literal("═══════════════════════════════")
                                    .formatted(Formatting.GOLD), false);
                            serverPlayer.sendMessage(
                                Text.literal("  *Lost your locator? Use: /locate structure dagmod:hall_of_champions")
                                    .formatted(Formatting.DARK_GRAY), false);
                        }
                    });
                } catch (Exception e) {
                    server.execute(() -> serverPlayer.sendMessage(
                        Text.literal("The records are unreadable. (" + e.getMessage() + ")")
                            .formatted(Formatting.RED), false));
                }
            });
        }

        return ActionResult.SUCCESS;
    }
}
