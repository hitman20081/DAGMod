package com.github.hitman20081.dagmod.grave;

import com.github.hitman20081.dagmod.DagMod;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.RegistryOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GraveData {
    private final UUID ownerId;
    private final String ownerName;
    private final Map<Integer, ItemStack> items;
    private final BlockPos position;
    private final Identifier dimension;
    private final long createdAt;

    public GraveData(UUID ownerId, String ownerName, Map<Integer, ItemStack> items, BlockPos position, Identifier dimension, long createdAt) {
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.items = items;
        this.position = position;
        this.dimension = dimension;
        this.createdAt = createdAt;
    }

    public UUID getOwnerId() { return ownerId; }
    public String getOwnerName() { return ownerName; }
    public Map<Integer, ItemStack> getItems() { return items; }
    public BlockPos getPosition() { return position; }
    public Identifier getDimension() { return dimension; }
    public long getCreatedAt() { return createdAt; }

    public CompoundTag toNbt(MinecraftServer server) {
        CompoundTag nbt = new CompoundTag();
        HolderLookup.Provider lookup = server.registryAccess();
        RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, lookup);

        nbt.putString("ownerId", ownerId.toString());
        nbt.putString("ownerName", ownerName);
        nbt.putInt("posX", position.getX());
        nbt.putInt("posY", position.getY());
        nbt.putInt("posZ", position.getZ());
        nbt.putString("dimension", dimension.toString());
        nbt.putLong("createdAt", createdAt);

        ListTag itemList = new ListTag();
        for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
            CompoundTag itemNbt = new CompoundTag();
            itemNbt.putInt("slot", entry.getKey());
            ItemStack.CODEC.encodeStart(ops, entry.getValue())
                    .result()
                    .ifPresent(encoded -> itemNbt.put("item", encoded));
            itemList.add(itemNbt);
        }
        nbt.put("items", itemList);

        return nbt;
    }

    public static GraveData fromNbt(CompoundTag nbt, MinecraftServer server) {
        HolderLookup.Provider lookup = server.registryAccess();
        RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, lookup);

        UUID ownerId = UUID.fromString(nbt.getString("ownerId").orElse(""));
        String ownerName = nbt.getString("ownerName").orElse("Unknown");
        int posX = nbt.getInt("posX").orElse(0);
        int posY = nbt.getInt("posY").orElse(0);
        int posZ = nbt.getInt("posZ").orElse(0);
        BlockPos position = new BlockPos(posX, posY, posZ);
        Identifier dimension = Identifier.parse(nbt.getString("dimension").orElse("minecraft:overworld"));
        long createdAt = nbt.getLong("createdAt").orElse(0L);

        Map<Integer, ItemStack> items = new HashMap<>();
        Tag itemListElement = nbt.get("items");
        if (itemListElement instanceof ListTag itemList) {
            for (int i = 0; i < itemList.size(); i++) {
                Tag compound = itemList.get(i);
                if (compound instanceof CompoundTag itemNbt) {
                    int slot = itemNbt.getInt("slot").orElse(0);
                    Tag itemElement = itemNbt.get("item");
                    if (itemElement != null) {
                        ItemStack stack = ItemStack.CODEC.parse(ops, itemElement)
                                .result()
                                .orElse(ItemStack.EMPTY);
                        if (!stack.isEmpty()) {
                            items.put(slot, stack);
                        }
                    }
                }
            }
        }

        return new GraveData(ownerId, ownerName, items, position, dimension, createdAt);
    }
}
