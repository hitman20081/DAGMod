package com.github.hitman20081.dagmod.bone_realm.chest;

import com.github.hitman20081.dagmod.bone_realm.BoneRealmRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.ChatFormatting;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Locked Bone Chest - Requires specific keys to open
 * Visual effects when locked, opens with particle effects when unlocked
 */
public class LockedBoneChestBlock extends BaseEntityBlock {

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    private final LockedChestType chestType;

    // Codec for block serialization - simplified without LockedChestType
    public static final MapCodec<LockedBoneChestBlock> CODEC = simpleCodec(settings ->
            new LockedBoneChestBlock(settings, LockedChestType.BONE_REALM)
    );

    public LockedBoneChestBlock(Properties settings, LockedChestType type) {
        super(settings);
        this.chestType = type;
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof LockedBoneChestBlockEntity chestEntity)) {
            return InteractionResult.FAIL;
        }

        // Check if already unlocked
        if (chestEntity.isUnlocked()) {
            player.openMenu(chestEntity);
            return InteractionResult.SUCCESS;
        }

        // Try to unlock with key
        ItemStack heldItem = player.getMainHandItem();
        if (canUnlockWith(heldItem)) {
            // Unlock the chest
            chestEntity.unlock();

            // Consume key (unless creative)
            if (!player.isCreative()) {
                heldItem.shrink(1);
            }

            // Visual and audio effects
            unlockEffects(world, pos);

            // Success message
            player.sendOverlayMessage(
                    Component.literal("✦ Chest Unlocked! ✦")
                            .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));

            // Open the chest
            player.openMenu(chestEntity);
            return InteractionResult.SUCCESS;
        }

        // Wrong key or no key
        lockedEffects(world, pos);
        player.sendOverlayMessage(
                Component.literal("This chest is locked!")
                        .withStyle(ChatFormatting.RED)
                        .append(Component.literal("\nRequires: " + chestType.getKeyName())
                                .withStyle(ChatFormatting.GRAY)));

        return InteractionResult.FAIL;
    }

    private boolean canUnlockWith(ItemStack stack) {
        return stack.getItem() == chestType.getRequiredKey();
    }

    private void unlockEffects(Level world, BlockPos pos) {
        // Play unlock sound
        world.playSound(null, pos, SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 1.0f, 1.0f);
        world.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 0.8f, 1.2f);

        // Spawn particles
        RandomSource random = world.getRandom();
        for (int i = 0; i < 20; i++) {
            double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.8;
            double y = pos.getY() + 0.5 + random.nextDouble() * 0.5;
            double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.8;

            world.addParticle(ParticleTypes.ENCHANT, x, y, z, 0, 0.1, 0);
            world.addParticle(ParticleTypes.END_ROD, x, y, z,
                    (random.nextDouble() - 0.5) * 0.1,
                    random.nextDouble() * 0.2,
                    (random.nextDouble() - 0.5) * 0.1
            );
        }
    }

    private void lockedEffects(Level world, BlockPos pos) {
        // Play locked sound
        world.playSound(null, pos, SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1.0f, 1.0f);

        // Spawn red particles
        RandomSource random = world.getRandom();
        for (int i = 0; i < 5; i++) {
            double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.8;
            double y = pos.getY() + 0.5 + random.nextDouble() * 0.5;
            double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.8;

            world.addParticle(ParticleTypes.SMOKE, x, y, z, 0, 0.05, 0);
        }
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof LockedBoneChestBlockEntity chestEntity && !chestEntity.isUnlocked()) {
            // Locked chest particle effects
            if (random.nextInt(10) == 0) {
                double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
                double y = pos.getY() + 0.5 + random.nextDouble() * 0.3;
                double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.6;

                world.addParticle(chestType.getParticleType(), x, y, z, 0, 0.02, 0);
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LockedBoneChestBlockEntity(pos, state, chestType);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return null; // No ticking needed for now
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.box(0.0625, 0.0, 0.0625, 0.9375, 0.875, 0.9375);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.box(0.0625, 0.0, 0.0625, 0.9375, 0.875, 0.9375);
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
        private final net.minecraft.core.particles.ParticleOptions particleType;

        LockedChestType(String name, String keyName, net.minecraft.core.particles.ParticleOptions particleType) {
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

        public net.minecraft.core.particles.ParticleOptions getParticleType() {
            return particleType;
        }

        public net.minecraft.world.item.Item getRequiredKey() {
            return switch (this) {
                case SKELETON_KING -> BoneRealmRegistry.SKELETON_KING_KEY;
                case BONE_REALM -> BoneRealmRegistry.BONE_REALM_CHEST_KEY;
            };
        }
    }
}