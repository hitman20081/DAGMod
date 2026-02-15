package com.github.hitman20081.dagmod.grave;

import com.github.hitman20081.dagmod.DagMod;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

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

    public NbtCompound toNbt(MinecraftServer server) {
        NbtCompound nbt = new NbtCompound();
        RegistryWrapper.WrapperLookup lookup = server.getRegistryManager();
        RegistryOps<NbtElement> ops = RegistryOps.of(NbtOps.INSTANCE, lookup);

        nbt.putString("ownerId", ownerId.toString());
        nbt.putString("ownerName", ownerName);
        nbt.putInt("posX", position.getX());
        nbt.putInt("posY", position.getY());
        nbt.putInt("posZ", position.getZ());
        nbt.putString("dimension", dimension.toString());
        nbt.putLong("createdAt", createdAt);

        NbtList itemList = new NbtList();
        for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
            NbtCompound itemNbt = new NbtCompound();
            itemNbt.putInt("slot", entry.getKey());
            ItemStack.CODEC.encodeStart(ops, entry.getValue())
                    .result()
                    .ifPresent(encoded -> itemNbt.put("item", encoded));
            itemList.add(itemNbt);
        }
        nbt.put("items", itemList);

        return nbt;
    }

    public static GraveData fromNbt(NbtCompound nbt, MinecraftServer server) {
        RegistryWrapper.WrapperLookup lookup = server.getRegistryManager();
        RegistryOps<NbtElement> ops = RegistryOps.of(NbtOps.INSTANCE, lookup);

        UUID ownerId = UUID.fromString(nbt.getString("ownerId").orElse(""));
        String ownerName = nbt.getString("ownerName").orElse("Unknown");
        int posX = nbt.getInt("posX").orElse(0);
        int posY = nbt.getInt("posY").orElse(0);
        int posZ = nbt.getInt("posZ").orElse(0);
        BlockPos position = new BlockPos(posX, posY, posZ);
        Identifier dimension = Identifier.of(nbt.getString("dimension").orElse("minecraft:overworld"));
        long createdAt = nbt.getLong("createdAt").orElse(0L);

        Map<Integer, ItemStack> items = new HashMap<>();
        NbtElement itemListElement = nbt.get("items");
        if (itemListElement instanceof NbtList itemList) {
            for (int i = 0; i < itemList.size(); i++) {
                NbtElement compound = itemList.get(i);
                if (compound instanceof NbtCompound itemNbt) {
                    int slot = itemNbt.getInt("slot").orElse(0);
                    NbtElement itemElement = itemNbt.get("item");
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
