package com.github.hitman20081.dagmod.bone_realm.chest;

import com.github.hitman20081.dagmod.bone_realm.BoneRealmRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Locked Bone Chest - Requires specific keys to open
 * Visual effects when locked, opens with particle effects when unlocked
 */
public class LockedBoneChestBlock extends BlockWithEntity {

    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;

    private final LockedChestType chestType;

    // Codec for block serialization - simplified without LockedChestType
    public static final MapCodec<LockedBoneChestBlock> CODEC = createCodec(settings ->
            new LockedBoneChestBlock(settings, LockedChestType.BONE_REALM)
    );

    public LockedBoneChestBlock(Settings settings, LockedChestType type) {
        super(settings);
        this.chestType = type;
        setDefaultState(getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof LockedBoneChestBlockEntity chestEntity)) {
            return ActionResult.FAIL;
        }

        // Check if already unlocked
        if (chestEntity.isUnlocked()) {
            player.openHandledScreen(chestEntity);
            return ActionResult.SUCCESS;
        }

        // Try to unlock with key
        ItemStack heldItem = player.getMainHandStack();
        if (canUnlockWith(heldItem)) {
            // Unlock the chest
            chestEntity.unlock();

            // Consume key (unless creative)
            if (!player.isCreative()) {
                heldItem.decrement(1);
            }

            // Visual and audio effects
            unlockEffects(world, pos);

            // Success message
            player.sendMessage(
                    Text.literal("✦ Chest Unlocked! ✦")
                            .formatted(Formatting.GOLD, Formatting.BOLD),
                    true
            );

            // Open the chest
            player.openHandledScreen(chestEntity);
            return ActionResult.SUCCESS;
        }

        // Wrong key or no key
        lockedEffects(world, pos);
        player.sendMessage(
                Text.literal("This chest is locked!")
                        .formatted(Formatting.RED)
                        .append(Text.literal("\nRequires: " + chestType.getKeyName())
                                .formatted(Formatting.GRAY)),
                true
        );

        return ActionResult.FAIL;
    }

    private boolean canUnlockWith(ItemStack stack) {
        return stack.getItem() == chestType.getRequiredKey();
    }

    private void unlockEffects(World world, BlockPos pos) {
        // Play unlock sound
        world.playSound(null, pos, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 1.0f, 1.0f);
        world.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 0.8f, 1.2f);

        // Spawn particles
        Random random = world.getRandom();
        for (int i = 0; i < 20; i++) {
            double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.8;
            double y = pos.getY() + 0.5 + random.nextDouble() * 0.5;
            double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.8;

            world.addParticleClient(ParticleTypes.ENCHANT, x, y, z, 0, 0.1, 0);
            world.addParticleClient(ParticleTypes.END_ROD, x, y, z,
                    (random.nextDouble() - 0.5) * 0.1,
                    random.nextDouble() * 0.2,
                    (random.nextDouble() - 0.5) * 0.1
            );
        }
    }

    private void lockedEffects(World world, BlockPos pos) {
        // Play locked sound
        world.playSound(null, pos, SoundEvents.BLOCK_CHEST_LOCKED, SoundCategory.BLOCKS, 1.0f, 1.0f);

        // Spawn red particles
        Random random = world.getRandom();
        for (int i = 0; i < 5; i++) {
            double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.8;
            double y = pos.getY() + 0.5 + random.nextDouble() * 0.5;
            double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.8;

            world.addParticleClient(ParticleTypes.SMOKE, x, y, z, 0, 0.05, 0);
        }
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof LockedBoneChestBlockEntity chestEntity && !chestEntity.isUnlocked()) {
            // Locked chest particle effects
            if (random.nextInt(10) == 0) {
                double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
                double y = pos.getY() + 0.5 + random.nextDouble() * 0.3;
                double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.6;

                world.addParticleClient(chestType.getParticleType(), x, y, z, 0, 0.02, 0);
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LockedBoneChestBlockEntity(pos, state, chestType);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return null; // No ticking needed for now
    }

    public LockedChestType getChestType() {
        return chestType;
    }

    /**
     * Enum for different chest types with their required keys
     */
    public enum LockedChestType {
        SKELETON_KING("Skeleton King Chest", "Skeleton King's Key", ParticleTypes.SOUL_FIRE_FLAME),
        BONE_REALM("Bone Realm Chest", "Bone Realm Chest Key", ParticleTypes.SOUL);

        private final String name;
        private final String keyName;
        private final net.minecraft.particle.ParticleEffect particleType;

        LockedChestType(String name, String keyName, net.minecraft.particle.ParticleEffect particleType) {
            this.name = name;
            this.keyName = keyName;
            this.particleType = particleType;
        }

        public String getName() {
            return name;
        }

        public String getKeyName() {
            return keyName;
        }

        public net.minecraft.particle.ParticleEffect getParticleType() {
            return particleType;
        }

        public net.minecraft.item.Item getRequiredKey() {
            return switch (this) {
                case SKELETON_KING -> BoneRealmRegistry.SKELETON_KING_KEY;
                case BONE_REALM -> BoneRealmRegistry.BONE_REALM_CHEST_KEY;
            };
        }
    }
}