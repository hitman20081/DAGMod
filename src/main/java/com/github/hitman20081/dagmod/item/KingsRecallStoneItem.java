package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.bone_realm.portal.BoneRealmTeleporter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.Level;

/**
 * Dropped by the Skeleton King. Right-click to teleport back to the Overworld.
 * Only works inside the Bone Realm. Consumed on use.
 */
public class KingsRecallStoneItem extends Item {

    public KingsRecallStoneItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ServerPlayer serverPlayer = (ServerPlayer) player;
        ServerLevel serverWorld = (ServerLevel) world;

        if (serverWorld.dimension() != BoneRealmTeleporter.BONE_REALM) {
            serverPlayer.sendOverlayMessage(
                Component.literal("This stone only resonates within the Bone Realm.")
                    .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            return InteractionResult.FAIL;
        }

        serverWorld.playSound(
            null,
            player.blockPosition(),
            SoundEvents.CHORUS_FRUIT_TELEPORT,
            SoundSource.PLAYERS,
            1.0f, 1.0f
        );

        serverPlayer.sendSystemMessage(
            Component.literal("The stone shatters, pulling you back to the living world...")
                .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC));

        // Consume the item
        ItemStack stack = player.getItemInHand(hand);
        if (!serverPlayer.isCreative()) {
            stack.shrink(1);
        }

        // Teleport directly to overworld at equivalent X/Z, above ground
        ServerLevel overworld = serverWorld.getServer().getLevel(BoneRealmTeleporter.OVERWORLD);
        int x = serverPlayer.getBlockX();
        int z = serverPlayer.getBlockZ();

        // Force the destination chunk to generate before querying the heightmap.
        // Without this, ungenerated chunks return the world bottom Y (~-64).
        overworld.getChunk(x >> 4, z >> 4);
        int y = overworld.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);

        // Fallback to sea level if chunk still returned an invalid height
        if (y <= overworld.getMinY() + 5) {
            y = 64;
        }

        TeleportTransition target = new TeleportTransition(
            overworld,
            new net.minecraft.world.phys.Vec3(x + 0.5, y, z + 0.5),
            net.minecraft.world.phys.Vec3.ZERO,
            serverPlayer.getYRot(),
            serverPlayer.getXRot(),
            TeleportTransition.DO_NOTHING
        );
        serverPlayer.teleport(target);

        return InteractionResult.SUCCESS;
    }
}
