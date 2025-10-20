package com.github.hitman20081.dagmod.bone_realm.entity;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

/**
 * Skeleton Summoner - Elite mob that summons Bonelings
 * Summoned by Skeleton Lords to overwhelm players
 */
public class SkeletonSummonerEntity extends SkeletonEntity {

    private static final int MAX_BONELINGS = 4;
    private static final int MIN_SUMMON_COOLDOWN = 120; // 6 seconds
    private static final int MAX_SUMMON_COOLDOWN = 240; // 12 seconds

    private int summonCooldown;
    private int bonelingCount = 0;

    public SkeletonSummonerEntity(EntityType<? extends SkeletonEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 25;
        this.summonCooldown = MIN_SUMMON_COOLDOWN;

        if (!world.isClient()) {
            this.initializeEquipment();
        }
    }

    @Override
    protected void initEquipment(net.minecraft.util.math.random.Random random, LocalDifficulty localDifficulty) {
        super.initEquipment(random, localDifficulty);
        this.initializeEquipment();
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData) {
        entityData = super.initialize(world, difficulty, spawnReason, entityData);
        return entityData;
    }

    private void initializeEquipment() {
        // Iron Helmet
        ItemStack helmet = new ItemStack(net.minecraft.item.Items.IRON_HELMET);
        helmet.set(net.minecraft.component.DataComponentTypes.CUSTOM_NAME,
                Text.literal("Summoner's Hood").formatted(Formatting.GRAY));
        this.equipStack(net.minecraft.entity.EquipmentSlot.HEAD, helmet);

        // Leather chestplate (robes)
        ItemStack chestplate = new ItemStack(net.minecraft.item.Items.LEATHER_CHESTPLATE);
        chestplate.set(net.minecraft.component.DataComponentTypes.CUSTOM_NAME,
                Text.literal("Summoner's Robes").formatted(Formatting.GRAY));
        this.equipStack(net.minecraft.entity.EquipmentSlot.CHEST, chestplate);

        // Iron sword
        ItemStack sword = new ItemStack(net.minecraft.item.Items.IRON_SWORD);
        this.equipStack(net.minecraft.entity.EquipmentSlot.MAINHAND, sword);

        // Set equipment to not drop (summoned mobs don't drop special loot)
        this.setEquipmentDropChance(net.minecraft.entity.EquipmentSlot.HEAD, 0.0f);
        this.setEquipmentDropChance(net.minecraft.entity.EquipmentSlot.CHEST, 0.0f);
        this.setEquipmentDropChance(net.minecraft.entity.EquipmentSlot.MAINHAND, 0.0f);
    }

    public static DefaultAttributeContainer.Builder createSkeletonSummonerAttributes() {
        return SkeletonEntity.createAbstractSkeletonAttributes()
                .add(EntityAttributes.MAX_HEALTH, 30.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.26)
                .add(EntityAttributes.ATTACK_DAMAGE, 4.0)
                .add(EntityAttributes.ARMOR, 8.0)
                .add(EntityAttributes.ARMOR_TOUGHNESS, 2.0)
                .add(EntityAttributes.KNOCKBACK_RESISTANCE, 0.3)
                .add(EntityAttributes.FOLLOW_RANGE, 32.0)
                .add(EntityAttributes.SCALE, 1.1);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getEntityWorld().isClient() && this.isAlive()) {
            // Count nearby bonelings
            this.bonelingCount = this.getEntityWorld().getEntitiesByClass(
                    BonelingEntity.class,
                    this.getBoundingBox().expand(24),
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
        if (!(this.getEntityWorld() instanceof ServerWorld serverWorld)) {
            return;
        }

        BlockPos spawnPos = this.getBlockPos().add(
                this.random.nextInt(4) - 2,
                0,
                this.random.nextInt(4) - 2
        );

        BonelingEntity boneling = new BonelingEntity(BoneRealmEntityRegistry.BONELING, serverWorld);
        boneling.refreshPositionAndAngles(
                spawnPos.getX() + 0.5,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5,
                this.random.nextFloat() * 360.0f,
                0.0f
        );

        if (this.getTarget() != null) {
            boneling.setTarget(this.getTarget());
        }

        serverWorld.spawnEntity(boneling);

        // Spawn effects - purple/dark magic theme
        for (int i = 0; i < 15; i++) {
            double offsetX = (this.random.nextDouble() - 0.5) * 0.8;
            double offsetY = this.random.nextDouble() * 0.8;
            double offsetZ = (this.random.nextDouble() - 0.5) * 0.8;

            serverWorld.spawnParticles(
                    ParticleTypes.WITCH,
                    spawnPos.getX() + 0.5,
                    spawnPos.getY() + 0.5,
                    spawnPos.getZ() + 0.5,
                    1,
                    offsetX, offsetY, offsetZ,
                    0.05
            );
        }

        this.playSound(SoundEvents.ENTITY_EVOKER_CAST_SPELL, 0.8f, 1.2f);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SKELETON_DEATH;
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return false; // Don't despawn while summoned
    }

    @Override
    public boolean cannotDespawn() {
        return true;
    }
}