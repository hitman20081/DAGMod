package com.github.hitman20081.dagmod.bone_realm.entity;

import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.util.RandomSource;

/**
 * Skeleton Summoner - Elite mob that summons Bonelings
 * Summoned by Skeleton Lords to overwhelm players
 */
public class SkeletonSummonerEntity extends Skeleton {

    private static final int MAX_BONELINGS = 4;
    private static final int MIN_SUMMON_COOLDOWN = 120; // 6 seconds
    private static final int MAX_SUMMON_COOLDOWN = 240; // 12 seconds

    private int summonCooldown;
    private int bonelingCount = 0;

    public SkeletonSummonerEntity(EntityType<? extends Skeleton> entityType, Level world) {
        super(entityType, world);
        this.xpReward = 25;
        this.summonCooldown = MIN_SUMMON_COOLDOWN;

        if (!world.isClientSide()) {
            this.initializeEquipment();
        }
    }

    @Override
    protected void populateDefaultEquipmentSlots(net.minecraft.util.RandomSource random, DifficultyInstance localDifficulty) {
        super.populateDefaultEquipmentSlots(random, localDifficulty);
        this.initializeEquipment();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, EntitySpawnReason spawnReason, SpawnGroupData entityData) {
        entityData = super.finalizeSpawn(world, difficulty, spawnReason, entityData);
        return entityData;
    }

    private void initializeEquipment() {
        // Iron Helmet
        ItemStack helmet = new ItemStack(net.minecraft.world.item.Items.IRON_HELMET);
        helmet.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,
                Component.literal("Summoner's Hood").withStyle(ChatFormatting.GRAY));
        this.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD, helmet);

        // Leather chestplate (robes)
        ItemStack chestplate = new ItemStack(net.minecraft.world.item.Items.LEATHER_CHESTPLATE);
        chestplate.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,
                Component.literal("Summoner's Robes").withStyle(ChatFormatting.GRAY));
        this.setItemSlot(net.minecraft.world.entity.EquipmentSlot.CHEST, chestplate);

        // Iron sword
        ItemStack sword = new ItemStack(net.minecraft.world.item.Items.IRON_SWORD);
        this.setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND, sword);

        // Set equipment to not drop (summoned mobs don't drop special loot)
        this.setDropChance(net.minecraft.world.entity.EquipmentSlot.HEAD, 0.0f);
        this.setDropChance(net.minecraft.world.entity.EquipmentSlot.CHEST, 0.0f);
        this.setDropChance(net.minecraft.world.entity.EquipmentSlot.MAINHAND, 0.0f);
    }

    public static AttributeSupplier.Builder createSkeletonSummonerAttributes() {
        return Skeleton.createAttributes()
                .add(Attributes.MAX_HEALTH, 120.0)
                .add(Attributes.MOVEMENT_SPEED, 0.26)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.ARMOR_TOUGHNESS, 3.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.SCALE, 1.1);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide() && this.isAlive()) {
            // Count nearby bonelings
            this.bonelingCount = this.level().getEntitiesOfClass(
                    BonelingEntity.class,
                    this.getBoundingBox().inflate(24),
                    boneling -> boneling.isAlive()
            ).size();

            // Summon bonelings
            this.summonCooldown--;
            if (this.summonCooldown <= 0 && this.bonelingCount < MAX_BONELINGS) {
                this.summonBoneling();
                this.summonCooldown = MIN_SUMMON_COOLDOWN +
                        this.random.nextInt(MAX_SUMMON_COOLDOWN - MIN_SUMMON_COOLDOWN);
            }
        }
    }

    private void summonBoneling() {
        if (!(this.level() instanceof ServerLevel serverWorld)) {
            return;
        }

        BlockPos spawnPos = this.blockPosition().offset(
                this.random.nextInt(4) - 2,
                0,
                this.random.nextInt(4) - 2
        );

        BonelingEntity boneling = new BonelingEntity(BoneRealmEntityRegistry.BONELING, serverWorld);
        boneling.snapTo(
                spawnPos.getX() + 0.5,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5,
                this.random.nextFloat() * 360.0f,
                0.0f
        );

        if (this.getTarget() != null) {
            boneling.setTarget(this.getTarget());
        }

        serverWorld.addFreshEntity(boneling);

        // Spawn effects - purple/dark magic theme
        for (int i = 0; i < 15; i++) {
            double offsetX = (this.random.nextDouble() - 0.5) * 0.8;
            double offsetY = this.random.nextDouble() * 0.8;
            double offsetZ = (this.random.nextDouble() - 0.5) * 0.8;

            serverWorld.sendParticles(
                    ParticleTypes.WITCH,
                    spawnPos.getX() + 0.5,
                    spawnPos.getY() + 0.5,
                    spawnPos.getZ() + 0.5,
                    1,
                    offsetX, offsetY, offsetZ,
                    0.05
            );
        }

        this.playSound(SoundEvents.EVOKER_CAST_SPELL, 0.8f, 1.2f);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SKELETON_DEATH;
    }

    @Override
    public boolean removeWhenFarAway(double distanceSquared) {
        return false; // Don't despawn while summoned
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }
}