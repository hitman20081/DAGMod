package com.github.hitman20081.dagmod.block;

import com.github.hitman20081.dagmod.entity.DragonEggBlockEntity;
import com.github.hitman20081.dagmod.entity.DragonGuardianEntity;
import com.github.hitman20081.dagmod.entity.ModEntities;
import com.github.hitman20081.dagmod.item.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Dragon Egg Block - Found in dragon nests
 * Breaking without Silk Touch angers the dragon and drops dragon scales
 * Breaking with Silk Touch drops the egg intact
 * Eggs hatch into dragons after 1 hour of game time
 */
public class DragonEggBlock extends BaseEntityBlock {

    // BlockState property for dragon variant
    public static final EnumProperty<DragonGuardianEntity.DragonVariant> VARIANT =
        EnumProperty.create("variant", DragonGuardianEntity.DragonVariant.class);

    // Codec for block serialization
    public static final MapCodec<DragonEggBlock> CODEC = simpleCodec(DragonEggBlock::new);

    private static final int DRAGON_SCALE_DROP_COUNT = 5;

    // Define the egg's collision shape to match the visual model (prevents rendering through blocks)
    // This creates a tapered egg shape that matches the actual model
    private static final VoxelShape SHAPE = Shapes.or(
        // Bottom layer (widest part)
        Block.box(1, 3, 1, 15, 8, 15),
        // Middle layers
        Block.box(2, 1, 2, 14, 11, 14),
        Block.box(3, 0, 3, 13, 13, 13),
        // Upper layers (narrowing toward top)
        Block.box(4, 13, 4, 12, 14, 12),
        Block.box(5, 14, 5, 11, 15, 11),
        Block.box(6, 15, 6, 10, 16, 10)
    );

    public DragonEggBlock(Properties settings) {
        super(settings);
        // Set default variant to RED
        registerDefaultState(this.stateDefinition.any().setValue(VARIANT, DragonGuardianEntity.DragonVariant.RED));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (!world.isClientSide() && world instanceof ServerLevel serverWorld) {
            ItemStack tool = player.getMainHandItem();

            // Check for Silk Touch enchantment
            boolean hasSilkTouch = false;
            for (Holder<Enchantment> enchantment : tool.getEnchantments().keySet()) {
                if (enchantment.is(Enchantments.SILK_TOUCH)) {
                    hasSilkTouch = true;
                    break;
                }
            }

            if (!hasSilkTouch) {
                // Only anger dragons if player is NOT in Creative/Spectator mode
                if (!player.isCreative() && !player.isSpectator()) {
                    // Anger nearby dragon guardians
                    world.getEntitiesOfClass(DragonGuardianEntity.class,
                            player.getBoundingBox().inflate(50),
                            dragon -> true)
                        .forEach(dragon -> {
                            dragon.setAngryAt(player);
                            dragon.setAnimationState(DragonGuardianEntity.AnimationState.ROARING);
                        });

                    // Play angry dragon sound
                    world.playSound(null, pos, SoundEvents.ENDER_DRAGON_GROWL,
                            SoundSource.HOSTILE, 2.0F, 0.8F);
                }

                // Drop dragon scales instead of egg
                popResource(world, pos, new ItemStack(ModItems.DRAGON_SCALE, DRAGON_SCALE_DROP_COUNT));

                player.awardStat(Stats.BLOCK_MINED.get(this));
            } else {
                // With Silk Touch, drop the egg itself (as BlockItem)
                popResource(world, pos, new ItemStack(this.asItem()));

                // Play success sound
                world.playSound(null, pos, SoundEvents.GLASS_BREAK,
                        SoundSource.BLOCKS, 1.0F, 1.0F);

                player.awardStat(Stats.BLOCK_MINED.get(this));
            }
        }

        return super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(world, pos, state, placer, itemStack);
        if (!world.isClientSide() && placer instanceof Player player) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof DragonEggBlockEntity eggEntity) {
                eggEntity.setOwnerUuid(player.getUUID());
                eggEntity.setChanged();
            }
        }
    }

    // BlockEntityProvider implementation

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DragonEggBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return world.isClientSide() ? null : createTickerHelper(type, ModEntities.DRAGON_EGG_BLOCK_ENTITY, DragonEggBlockEntity::tick);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(VARIANT);
    }
}
