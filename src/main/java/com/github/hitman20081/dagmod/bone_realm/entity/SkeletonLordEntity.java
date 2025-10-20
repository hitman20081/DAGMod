package com.github.hitman20081.dagmod.bone_realm.entity;

import com.github.hitman20081.dagmod.bone_realm.chest.BossChestSpawner;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

/**
 * Skeleton Lord - Mid-tier boss that summons Bonelings
 * Periodically spawns skeleton minions to fight for it
 */
public class SkeletonLordEntity extends HostileEntity {

    private static final int MAX_BONELINGS = 8;
    private static final int MIN_SUMMON_COOLDOWN = 100; // 5 seconds
    private static final int MAX_SUMMON_COOLDOWN = 200; // 10 seconds

    private int summonCooldown;
    private int bonelingCount = 0;

    public SkeletonLordEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 50;
        this.summonCooldown = MIN_SUMMON_COOLDOWN;

        // Initialize equipment immediately if on server
        if (!world.isClient()) {
            this.initializeEquipment();
            System.out.println("DEBUG: Equipment initialized in constructor (Skeleton Lord)");
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
        System.out.println("DEBUG: Skeleton Lord initialized in world");
        return entityData;
    }

    private void initializeEquipment() {
        // Diamond Helmet - Helm of the Bone Lord
        ItemStack helmet = new ItemStack(net.minecraft.item.Items.DIAMOND_HELMET);
        helmet.set(net.minecraft.component.DataComponentTypes.CUSTOM_NAME,
                Text.literal("Helm of the Bone Lord").formatted(Formatting.DARK_RED));
        this.equipStack(net.minecraft.entity.EquipmentSlot.HEAD, helmet);

        // Diamond Chestplate - Chest of the Bone Lord
        ItemStack chestplate = new ItemStack(net.minecraft.item.Items.DIAMOND_CHESTPLATE);
        chestplate.set(net.minecraft.component.DataComponentTypes.CUSTOM_NAME,
                Text.literal("Chest of the Bone Lord").formatted(Formatting.DARK_RED));
        this.equipStack(net.minecraft.entity.EquipmentSlot.CHEST, chestplate);

        // Diamond Leggings - Leggings of the Bone Lord
        ItemStack leggings = new ItemStack(net.minecraft.item.Items.DIAMOND_LEGGINGS);
        leggings.set(net.minecraft.component.DataComponentTypes.CUSTOM_NAME,
                Text.literal("Leggings of the Bone Lord").formatted(Formatting.DARK_RED));
        this.equipStack(net.minecraft.entity.EquipmentSlot.LEGS, leggings);

        // Diamond Boots - Sabatons of the Bone Lord
        ItemStack boots = new ItemStack(net.minecraft.item.Items.DIAMOND_BOOTS);
        boots.set(net.minecraft.component.DataComponentTypes.CUSTOM_NAME,
                Text.literal("Sabatons of the Bone Lord").formatted(Formatting.DARK_RED));
        this.equipStack(net.minecraft.entity.EquipmentSlot.FEET, boots);

        // Diamond Sword
        ItemStack sword = new ItemStack(net.minecraft.item.Items.DIAMOND_SWORD);
        this.equipStack(net.minecraft.entity.EquipmentSlot.MAINHAND, sword);

        // Set equipment to not drop (boss loot table handles drops)
        this.setEquipmentDropChance(net.minecraft.entity.EquipmentSlot.HEAD, 0.0f);
        this.setEquipmentDropChance(net.minecraft.entity.EquipmentSlot.CHEST, 0.0f);
        this.setEquipmentDropChance(net.minecraft.entity.EquipmentSlot.LEGS, 0.0f);
        this.setEquipmentDropChance(net.minecraft.entity.EquipmentSlot.FEET, 0.0f);
        this.setEquipmentDropChance(net.minecraft.entity.EquipmentSlot.MAINHAND, 0.0f);
    }

    public static DefaultAttributeContainer.Builder createSkeletonLordAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.MAX_HEALTH, 45.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.28)
                .add(EntityAttributes.ATTACK_DAMAGE, 6.0)
                .add(EntityAttributes.ARMOR, 15.0)
                .add(EntityAttributes.ARMOR_TOUGHNESS, 4.0)
                .add(EntityAttributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(EntityAttributes.FOLLOW_RANGE, 40.0)
                .add(EntityAttributes.ATTACK_KNOCKBACK, 0.25)
                .add(EntityAttributes.SCALE, 1.5);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(5, new LookAroundGoal(this));

        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getEntityWorld().isClient() && this.isAlive()) {
            // Count nearby bonelings
            this.bonelingCount = this.getEntityWorld().getEntitiesByClass(
                    BonelingEntity.class,
                    this.getBoundingBox().expand(32),
                    boneling -> boneling.isAlive()
            ).size();

            // Summon bonelings
            this.summonCooldown--;
            if (this.summonCooldown <= 0 && this.bonelingCount < MAX_BONELINGS) {
                this.summonBoneling();
                // Random cooldown between summons
                this.summonCooldown = MIN_SUMMON_COOLDOWN +
                        this.random.nextInt(MAX_SUMMON_COOLDOWN - MIN_SUMMON_COOLDOWN);
            }
        }
    }

    private void summonBoneling() {
        if (!(this.getEntityWorld() instanceof ServerWorld serverWorld)) {
            return;
        }

        // Find spawn position near the lord
        BlockPos spawnPos = this.getBlockPos().add(
                this.random.nextInt(5) - 2,
                0,
                this.random.nextInt(5) - 2
        );

        // Spawn boneling
        BonelingEntity boneling = new BonelingEntity(BoneRealmEntityRegistry.BONELING, serverWorld);
        if (boneling != null) {
            boneling.refreshPositionAndAngles(
                    spawnPos.getX() + 0.5,
                    spawnPos.getY(),
                    spawnPos.getZ() + 0.5,
                    this.random.nextFloat() * 360.0f,
                    0.0f
            );

            // Set the boneling's target to the lord's target
            if (this.getTarget() != null) {
                boneling.setTarget(this.getTarget());
            }

            serverWorld.spawnEntity(boneling);

            // Spawn effects
            for (int i = 0; i < 20; i++) {
                double offsetX = (this.random.nextDouble() - 0.5) * 1.0;
                double offsetY = this.random.nextDouble() * 1.0;
                double offsetZ = (this.random.nextDouble() - 0.5) * 1.0;

                serverWorld.spawnParticles(
                        ParticleTypes.SOUL,
                        spawnPos.getX() + 0.5,
                        spawnPos.getY() + 0.5,
                        spawnPos.getZ() + 0.5,
                        1,
                        offsetX, offsetY, offsetZ,
                        0.05
                );
            }

            // Play sound
            this.playSound(SoundEvents.ENTITY_SKELETON_AMBIENT, 1.0f, 0.8f);
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);

        // Spawn treasure chest
        if (!this.getEntityWorld().isClient()) {
            PlayerEntity killer = null;
            if (damageSource.getAttacker() instanceof PlayerEntity) {
                killer = (PlayerEntity) damageSource.getAttacker();
            }

            BossChestSpawner.onBossDeath(this, killer, this.getEntityWorld());
        }
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return false;
    }

    @Override
    public boolean cannotDespawn() {
        return true;
    }
}