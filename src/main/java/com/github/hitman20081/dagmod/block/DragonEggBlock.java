package com.github.hitman20081.dagmod.block;

import com.github.hitman20081.dagmod.entity.DragonEggBlockEntity;
import com.github.hitman20081.dagmod.entity.DragonGuardianEntity;
import com.github.hitman20081.dagmod.entity.ModEntities;
import com.github.hitman20081.dagmod.item.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Dragon Egg Block - Found in dragon nests
 * Breaking without Silk Touch angers the dragon and drops dragon scales
 * Breaking with Silk Touch drops the egg intact
 * Eggs hatch into dragons after 1 hour of game time
 */
public class DragonEggBlock extends BlockWithEntity {

    // BlockState property for dragon variant
    public static final EnumProperty<DragonGuardianEntity.DragonVariant> VARIANT =
        EnumProperty.of("variant", DragonGuardianEntity.DragonVariant.class);

    // Codec for block serialization
    public static final MapCodec<DragonEggBlock> CODEC = createCodec(DragonEggBlock::new);

    private static final int DRAGON_SCALE_DROP_COUNT = 5;

    // Define the egg's collision shape to match the visual model (prevents rendering through blocks)
    // This creates a tapered egg shape that matches the actual model
    private static final VoxelShape SHAPE = VoxelShapes.union(
        // Bottom layer (widest part)
        Block.createCuboidShape(1, 3, 1, 15, 8, 15),
        // Middle layers
        Block.createCuboidShape(2, 1, 2, 14, 11, 14),
        Block.createCuboidShape(3, 0, 3, 13, 13, 13),
        // Upper layers (narrowing toward top)
        Block.createCuboidShape(4, 13, 4, 12, 14, 12),
        Block.createCuboidShape(5, 14, 5, 11, 15, 11),
        Block.createCuboidShape(6, 15, 6, 10, 16, 10)
    );

    public DragonEggBlock(Settings settings) {
        super(settings);
        // Set default variant to RED
        setDefaultState(getStateManager().getDefaultState().with(VARIANT, DragonGuardianEntity.DragonVariant.RED));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient() && world instanceof ServerWorld serverWorld) {
            ItemStack tool = player.getMainHandStack();

            // Check for Silk Touch enchantment
            boolean hasSilkTouch = false;
            for (RegistryEntry<Enchantment> enchantment : tool.getEnchantments().getEnchantments()) {
                if (enchantment.matchesKey(Enchantments.SILK_TOUCH)) {
                    hasSilkTouch = true;
                    break;
                }
            }

            if (!hasSilkTouch) {
                // Only anger dragons if player is NOT in Creative/Spectator mode
                if (!player.isCreative() && !player.isSpectator()) {
                    // Anger nearby dragon guardians
                    world.getEntitiesByClass(DragonGuardianEntity.class,
                            player.getBoundingBox().expand(50),
                            dragon -> true)
                        .forEach(dragon -> {
                            dragon.setAngryAt(player);
                            dragon.setAnimationState(DragonGuardianEntity.AnimationState.ROARING);
                        });

                    // Play angry dragon sound
                    world.playSound(null, pos, SoundEvents.ENTITY_ENDER_DRAGON_GROWL,
                            SoundCategory.HOSTILE, 2.0F, 0.8F);
                }

                // Drop dragon scales instead of egg
                dropStack(world, pos, new ItemStack(ModItems.DRAGON_SCALE, DRAGON_SCALE_DROP_COUNT));

                player.incrementStat(Stats.MINED.getOrCreateStat(this));
            } else {
                // With Silk Touch, drop the egg itself (as BlockItem)
                dropStack(world, pos, new ItemStack(this.asItem()));

                // Play success sound
                world.playSound(null, pos, SoundEvents.BLOCK_GLASS_BREAK,
                        SoundCategory.BLOCKS, 1.0F, 1.0F);

                player.incrementStat(Stats.MINED.getOrCreateStat(this));
            }
        }

        return super.onBreak(world, pos, state, player);
    }

    // BlockEntityProvider implementation

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DragonEggBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient() ? null : validateTicker(type, ModEntities.DRAGON_EGG_BLOCK_ENTITY, DragonEggBlockEntity::tick);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(VARIANT);
    }
}
