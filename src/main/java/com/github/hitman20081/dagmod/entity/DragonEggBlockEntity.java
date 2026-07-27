package com.github.hitman20081.dagmod.entity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import java.util.Optional;
import java.util.UUID;

/**
 * Block Entity for Dragon Eggs
 * Handles hatching timer and spawning baby dragons
 * REQUIRES Magma Block underneath to hatch
 */
public class DragonEggBlockEntity extends BlockEntity {

    private int hatchingTicks = 0;
    private DragonGuardianEntity.DragonVariant variant = DragonGuardianEntity.DragonVariant.RED; // Default variant
    private UUID ownerUuid = null; // Player who placed the egg
    private static final int HATCHING_TIME = 72000; // 1 hour (60 minutes * 60 seconds * 20 ticks)
    private static final int PARTICLE_INTERVAL = 100; // Particle effect every 5 seconds

    public DragonEggBlockEntity(BlockPos pos, BlockState state) {
        super(ModEntities.DRAGON_EGG_BLOCK_ENTITY, pos, state);
    }

    public void setVariant(DragonGuardianEntity.DragonVariant variant) {
        this.variant = variant;
    }

    public DragonGuardianEntity.DragonVariant getVariant() {
        return this.variant;
    }

    public void setOwnerUuid(UUID uuid) {
        this.ownerUuid = uuid;
    }

    public UUID getOwnerUuid() {
        return this.ownerUuid;
    }

    /**
     * Tick method called every game tick
     * Only increments hatching timer when placed on a Magma Block
     */
    public static void tick(Level world, BlockPos pos, BlockState state, DragonEggBlockEntity blockEntity) {
        if (world.isClientSide() || !(world instanceof ServerLevel serverWorld)) {
            return; // Only tick on server
        }

        // Check if the block below is a Magma block
        BlockPos belowPos = pos.below();
        BlockState belowState = world.getBlockState(belowPos);
        boolean onMagmaBlock = belowState.getBlock() == Blocks.MAGMA_BLOCK;

        // Only increment hatching timer when on Magma block
        if (onMagmaBlock) {
            blockEntity.hatchingTicks++;

            // Periodic particle effects to show egg is "alive" and hatching
            if (blockEntity.hatchingTicks % PARTICLE_INTERVAL == 0) {
                serverWorld.sendParticles(ParticleTypes.FLAME,
                    pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5,
                    3, 0.2, 0.1, 0.2, 0.01);
            }

            // Hatching logic
            if (blockEntity.hatchingTicks >= HATCHING_TIME) {
                blockEntity.hatchEgg(serverWorld, pos);
            }
        } else {
            // Show different particle effect when NOT on Magma block (egg is dormant)
            if (world.getGameTime() % 200 == 0) { // Every 10 seconds
                serverWorld.sendParticles(ParticleTypes.ENCHANT,
                    pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5,
                    2, 0.2, 0.2, 0.2, 0.01);
            }
        }

        blockEntity.setChanged();
    }

    /**
     * Hatch the egg and spawn a dragon
     * Spawns a Wild Dragon (tameable) of the same variant as stored in the egg
     * Baby dragons spawn at 20% scale (0.2) and will grow to adult size (0.4) over time
     * Dragons hatched from player-placed eggs are automatically tamed to that player
     */
    private void hatchEgg(ServerLevel world, BlockPos pos) {
        // Remove the egg block
        world.removeBlock(pos, false);

        // Spawn Wild Dragon from egg with the stored variant
        WildDragonEntity dragon = new WildDragonEntity(ModEntities.WILD_DRAGON, world);
        dragon.snapTo(
            pos.getX() + 0.5,
            pos.getY(),
            pos.getZ() + 0.5,
            world.getRandom().nextFloat() * 360.0F,
            0.0F
        );
        dragon.setVariant(this.variant); // Set variant before initializing
        dragon.finalizeSpawn(world, world.getCurrentDifficultyAt(pos), EntitySpawnReason.BREEDING, null);

        // Make hatched dragons spawn as babies
        // Adult Wild Dragons are 0.4 scale, babies are 0.2 scale (half size)
        dragon.setGrowthStage(WildDragonEntity.GrowthStage.BABY);
        dragon.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.SCALE).setBaseValue(
            WildDragonEntity.GrowthStage.BABY.getScale()
        );

        // Auto-tame dragon to the player who placed the egg (if UUID is set)
        if (this.ownerUuid != null) {
            dragon.setTamed(true);
            dragon.setOwnerUuid(this.ownerUuid);
            dragon.setSitting(false); // Start following immediately
        }

        world.addFreshEntity(dragon);

        // Hatching effects
        world.playSound(null, pos, SoundEvents.ENDER_DRAGON_AMBIENT,
            SoundSource.NEUTRAL, 1.0F, 1.5F);

        world.sendParticles(ParticleTypes.EXPLOSION,
            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
            20, 0.5, 0.5, 0.5, 0.1);
    }

    @Override
    protected void saveAdditional(ValueOutput data) {
        super.saveAdditional(data);
        data.putInt("HatchingTicks", hatchingTicks);
        data.putInt("Variant", variant.ordinal());
        if (ownerUuid != null) {
            data.putString("OwnerUUID", ownerUuid.toString());
        }
    }

    @Override
    protected void loadAdditional(ValueInput data) {
        super.loadAdditional(data);
        this.hatchingTicks = data.getIntOr("HatchingTicks", 0);
        int variantOrdinal = data.getIntOr("Variant", 0);
        DragonGuardianEntity.DragonVariant[] variants = DragonGuardianEntity.DragonVariant.values();
        if (variantOrdinal >= 0 && variantOrdinal < variants.length) {
            this.variant = variants[variantOrdinal];
        }
        Optional<String> ownerOpt = data.getString("OwnerUUID");
        if (ownerOpt.isPresent()) {
            try {
                this.ownerUuid = UUID.fromString(ownerOpt.get());
            } catch (IllegalArgumentException e) {
                this.ownerUuid = null;
            }
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        CompoundTag nbt = super.getUpdateTag(registryLookup);
        nbt.putInt("HatchingTicks", hatchingTicks);
        nbt.putInt("Variant", variant.ordinal());
        if (ownerUuid != null) {
            nbt.putString("OwnerUUID", ownerUuid.toString());
        }
        return nbt;
    }

    /**
     * Provide render data to the client for dynamic model/texture selection
     * This is THE KEY METHOD that enables dynamic texture selection in Minecraft 1.21+
     */
    @Override
    public Object getRenderData() {
        return this.variant; // Return the variant so the renderer can select the correct texture
    }

    public int getHatchingProgress() {
        return (int) ((float) hatchingTicks / HATCHING_TIME * 100);
    }
}
