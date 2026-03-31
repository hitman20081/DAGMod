package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.bone_realm.portal.BoneRealmTeleporter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.Heightmap;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

/**
 * Dropped by the Skeleton King. Right-click to teleport back to the Overworld.
 * Only works inside the Bone Realm. Consumed on use.
 */
public class KingsRecallStoneItem extends Item {

    public KingsRecallStoneItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
        ServerWorld serverWorld = (ServerWorld) world;

        if (serverWorld.getRegistryKey() != BoneRealmTeleporter.BONE_REALM) {
            serverPlayer.sendMessage(
                Text.literal("This stone only resonates within the Bone Realm.")
                    .formatted(Formatting.GRAY, Formatting.ITALIC),
                true
            );
            return ActionResult.FAIL;
        }

        serverWorld.playSound(
            null,
            player.getBlockPos(),
            SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT,
            SoundCategory.PLAYERS,
            1.0f, 1.0f
        );

        serverPlayer.sendMessage(
            Text.literal("The stone shatters, pulling you back to the living world...")
                .formatted(Formatting.DARK_PURPLE, Formatting.ITALIC),
            false
        );

        // Consume the item
        ItemStack stack = player.getStackInHand(hand);
        if (!serverPlayer.isCreative()) {
            stack.decrement(1);
        }

        // Teleport directly to overworld at equivalent X/Z, above ground
        ServerWorld overworld = serverWorld.getServer().getWorld(BoneRealmTeleporter.OVERWORLD);
        int x = serverPlayer.getBlockX();
        int z = serverPlayer.getBlockZ();

        // Force the destination chunk to generate before querying the heightmap.
        // Without this, ungenerated chunks return the world bottom Y (~-64).
        overworld.getChunk(x >> 4, z >> 4);
        int y = overworld.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);

        // Fallback to sea level if chunk still returned an invalid height
        if (y <= overworld.getBottomY() + 5) {
            y = 64;
        }

        TeleportTarget target = new TeleportTarget(
            overworld,
            new net.minecraft.util.math.Vec3d(x + 0.5, y, z + 0.5),
            net.minecraft.util.math.Vec3d.ZERO,
            serverPlayer.getYaw(),
            serverPlayer.getPitch(),
            TeleportTarget.NO_OP
        );
        serverPlayer.teleportTo(target);

        return ActionResult.SUCCESS;
    }
}
