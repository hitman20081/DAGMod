package com.github.hitman20081.dagmod.grave;

import com.github.hitman20081.dagmod.DagMod;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.core.BlockPos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GraveManager {
    private static final GraveManager INSTANCE = new GraveManager();
    private static final long LOOT_DELAY_TICKS = 5 * 60 * 20; // 5 minutes in ticks

    private MinecraftServer server;
    private final ConcurrentHashMap<UUID, GraveData> graves = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, UUID> positionLookup = new ConcurrentHashMap<>();

    private GraveManager() {}

    public static GraveManager getInstance() {
        return INSTANCE;
    }

    public void initialize(MinecraftServer server) {
        this.server = server;
        loadAllGraves();
        DagMod.LOGGER.info("Grave system initialized with {} graves", graves.size());
    }

    public void shutdown() {
        graves.clear();
        positionLookup.clear();
        server = null;
    }

    public void createGrave(ServerPlayer player, Map<Integer, ItemStack> items) {
        if (items.isEmpty()) return;

        UUID playerId = player.getUUID();
        String playerName = player.getName().getString();

        // If player has existing grave, drop those old items at the old location
        GraveData existing = graves.get(playerId);
        if (existing != null) {
            dropOldGraveItems(existing);
            removePositionLookup(existing);
        }

        ServerLevel world = (ServerLevel) player.level();
        BlockPos deathPos = player.blockPosition();
        Identifier dimension = world.dimension().identifier();
        long currentTick = server.overworld().getGameTime();

        // If the player died in the void, snap the search origin to the surface
        if (deathPos.getY() < world.getMinY()) {
            int surfaceY = world.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, deathPos.getX(), deathPos.getZ());
            deathPos = new BlockPos(deathPos.getX(), Math.max(surfaceY, world.getMinY() + 1), deathPos.getZ());
            DagMod.LOGGER.info("Grave system: void death detected for {}, snapping grave origin to surface Y={}", playerName, deathPos.getY());
        }

        // Find a suitable position for the grave
        BlockPos gravePos = findGravePosition(world, deathPos);

        GraveData grave = new GraveData(playerId, playerName, items, gravePos, dimension, currentTick);

        // Place lodestone block
        world.setBlock(gravePos, Blocks.LODESTONE.defaultBlockState(), 3);

        // Store in memory
        graves.put(playerId, grave);
        addPositionLookup(grave);

        // Persist to disk
        saveGrave(playerId, grave);

        player.sendSystemMessage(
                Component.literal("Your items have been stored in a grave at ")
                        .withStyle(ChatFormatting.GRAY)
                        .append(Component.literal("[" + gravePos.getX() + ", " + gravePos.getY() + ", " + gravePos.getZ() + "]")
                                .withStyle(ChatFormatting.YELLOW)));

        DagMod.LOGGER.info("Created grave for {} at {} in {}", playerName, gravePos, dimension);
    }

    /**
     * Attempt to collect a grave at the given position.
     * Returns true if the grave was collected (or rejected with message), false if no grave exists at this position.
     */
    public boolean collectGrave(ServerPlayer player, BlockPos pos) {
        ServerLevel world = (ServerLevel) player.level();
        Identifier dimension = world.dimension().identifier();
        String key = positionKey(dimension, pos);
        UUID graveOwner = positionLookup.get(key);

        if (graveOwner == null) return false;

        GraveData grave = graves.get(graveOwner);
        if (grave == null) {
            positionLookup.remove(key);
            return false;
        }

        // Verify position matches
        if (!grave.getPosition().equals(pos) || !grave.getDimension().equals(dimension)) {
            return false;
        }

        UUID playerId = player.getUUID();
        boolean isOwner = playerId.equals(graveOwner);

        // Check loot delay for non-owners
        if (!isOwner) {
            long currentTick = server.overworld().getGameTime();
            long elapsed = currentTick - grave.getCreatedAt();

            if (elapsed < LOOT_DELAY_TICKS) {
                long remainingTicks = LOOT_DELAY_TICKS - elapsed;
                long remainingSeconds = remainingTicks / 20;
                long minutes = remainingSeconds / 60;
                long seconds = remainingSeconds % 60;

                player.sendSystemMessage(
                        Component.literal("This grave belongs to " + grave.getOwnerName() + "! You can loot it in "
                                + minutes + "m " + seconds + "s.")
                                .withStyle(ChatFormatting.RED));
                return true; // Handled (rejected), don't pass through to normal interaction
            }
        }

        // Drop all items at the player's feet
        for (ItemStack stack : grave.getItems().values()) {
            ItemEntity itemEntity = new ItemEntity(
                    player.level(),
                    player.getX(), player.getY() + 0.5, player.getZ(),
                    stack.copy()
            );
            itemEntity.setNoPickUpDelay();
            player.level().addFreshEntity(itemEntity);
        }

        // Remove the lodestone block
        world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);

        // Clean up data
        removePositionLookup(grave);
        graves.remove(graveOwner);
        deleteGraveFile(graveOwner);

        if (isOwner) {
            player.sendSystemMessage(
                    Component.literal("You recovered your items from the grave!")
                            .withStyle(ChatFormatting.GREEN));
        } else {
            player.sendSystemMessage(
                    Component.literal("You looted an abandoned grave!")
                            .withStyle(ChatFormatting.YELLOW));
        }

        DagMod.LOGGER.info("{} collected grave at {}", player.getName().getString(), pos);
        return true;
    }

    public GraveData getGraveForPlayer(UUID playerId) {
        return graves.get(playerId);
    }

    public long getLootDelayTicks() {
        return LOOT_DELAY_TICKS;
    }

    public long getCurrentTick() {
        return server != null ? server.overworld().getGameTime() : 0;
    }

    public boolean hasGraveAt(BlockPos pos, Identifier dimension) {
        return positionLookup.containsKey(positionKey(dimension, pos));
    }

    private void dropOldGraveItems(GraveData grave) {
        ServerLevel world = findWorld(grave.getDimension());
        if (world == null) {
            DagMod.LOGGER.warn("Could not find world {} to drop old grave items", grave.getDimension());
            return;
        }

        BlockPos pos = grave.getPosition();

        // Drop items as entities at the old grave location
        for (ItemStack stack : grave.getItems().values()) {
            ItemEntity itemEntity = new ItemEntity(
                    world,
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                    stack.copy()
            );
            world.addFreshEntity(itemEntity);
        }

        // Remove the old lodestone
        world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);

        deleteGraveFile(grave.getOwnerId());
        DagMod.LOGGER.info("Dropped old grave items at {} in {}", pos, grave.getDimension());
    }

    private ServerLevel findWorld(Identifier dimensionId) {
        for (ServerLevel world : server.getAllLevels()) {
            if (world.dimension().identifier().equals(dimensionId)) {
                return world;
            }
        }
        return null;
    }

    private BlockPos findGravePosition(ServerLevel world, BlockPos deathPos) {
        // Try the death position first
        if (canPlaceGrave(world, deathPos)) {
            return deathPos;
        }

        // Search nearby for a valid position (within 3 blocks)
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                for (int dy = -3; dy <= 3; dy++) {
                    BlockPos candidate = deathPos.offset(dx, dy, dz);
                    if (canPlaceGrave(world, candidate)) {
                        return candidate;
                    }
                }
            }
        }

        // Fallback: just use the death position
        return deathPos;
    }

    private boolean canPlaceGrave(ServerLevel world, BlockPos pos) {
        return world.getBlockState(pos).canBeReplaced() || world.getBlockState(pos).isAir();
    }

    // --- Position Lookup Helpers ---

    private String positionKey(Identifier dimension, BlockPos pos) {
        return dimension.toString() + ":" + pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }

    private void addPositionLookup(GraveData grave) {
        positionLookup.put(positionKey(grave.getDimension(), grave.getPosition()), grave.getOwnerId());
    }

    private void removePositionLookup(GraveData grave) {
        positionLookup.remove(positionKey(grave.getDimension(), grave.getPosition()));
    }

    // --- Persistence ---

    private File getGravesDir() {
        File dir = server.getWorldPath(LevelResource.ROOT)
                .resolve("data").resolve("dagmod").resolve("graves").toFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private File getGraveFile(UUID playerId) {
        return new File(getGravesDir(), playerId.toString() + ".dat");
    }

    private void saveGrave(UUID playerId, GraveData grave) {
        File dataFile = getGraveFile(playerId);
        File tempFile = new File(dataFile.getPath() + ".tmp");
        File backupFile = new File(dataFile.getPath() + ".bak");

        try {
            CompoundTag nbt = grave.toNbt(server);

            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                NbtIo.writeCompressed(nbt, fos);
            }

            if (dataFile.exists()) {
                Files.copy(dataFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            Files.move(tempFile.toPath(), dataFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            DagMod.LOGGER.error("Failed to save grave for {}", playerId, e);
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    private void deleteGraveFile(UUID playerId) {
        File dataFile = getGraveFile(playerId);
        File backupFile = new File(dataFile.getPath() + ".bak");
        if (dataFile.exists()) dataFile.delete();
        if (backupFile.exists()) backupFile.delete();
    }

    private void loadAllGraves() {
        File gravesDir = getGravesDir();
        File[] files = gravesDir.listFiles((dir, name) -> name.endsWith(".dat"));

        if (files == null) return;

        for (File file : files) {
            try {
                String fileName = file.getName().replace(".dat", "");
                UUID playerId = UUID.fromString(fileName);

                CompoundTag nbt;
                try (FileInputStream fis = new FileInputStream(file)) {
                    nbt = NbtIo.readCompressed(fis, NbtAccounter.unlimitedHeap());
                }

                GraveData grave = GraveData.fromNbt(nbt, server);
                graves.put(playerId, grave);
                addPositionLookup(grave);

                DagMod.LOGGER.debug("Loaded grave for {} at {}", playerId, grave.getPosition());
            } catch (Exception e) {
                DagMod.LOGGER.error("Failed to load grave file: {}", file.getName(), e);
            }
        }
    }
}
