package com.github.hitman20081.dagmod.bone_realm.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

/**
 * Boneling - Weak skeleton minion summoned by Skeleton Summoners
 * Fast, fragile, swarm creatures
 */
public class BonelingEntity extends HostileEntity {

    private int lifeTicks = 0;
    private static final int MAX_LIFETIME = 3600; // 3 minutes (60 seconds * 60 ticks)

    public BonelingEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 3; // Very low XP - they're just minions
    }

    public static DefaultAttributeContainer.Builder createBonelingAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.MAX_HEALTH, 12.0) // Fragile
                .add(EntityAttributes.MOVEMENT_SPEED, 0.35) // Fast
                .add(EntityAttributes.ATTACK_DAMAGE, 2.5) // Weak
                .add(EntityAttributes.ARMOR, 0.0) // No armor
                .add(EntityAttributes.FOLLOW_RANGE, 32.0)
                .add(EntityAttributes.KNOCKBACK_RESISTANCE, 0.0) // Easy to knockback
                .add(EntityAttributes.SCALE, 0.7); // Smaller - 70% size
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.3, false)); // Aggressive
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(5, new LookAroundGoal(this));

        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        // Count lifetime and despawn after max time
        if (!this.getEntityWorld().isClient() && this.isAlive()) {
            this.lifeTicks++;

            // Despawn after 3 minutes
            if (this.lifeTicks >= MAX_LIFETIME) {
                this.discard();

                // Spawn poof particles on despawn
                if (this.getEntityWorld() instanceof ServerWorld serverWorld) {
                    for (int i = 0; i < 10; i++) {
                        double offsetX = (this.random.nextDouble() - 0.5) * 0.5;
                        double offsetY = this.random.nextDouble() * 1.0;
                        double offsetZ = (this.random.nextDouble() - 0.5) * 0.5;

                        serverWorld.spawnParticles(
                                ParticleTypes.POOF,
                                this.getX(),
                                this.getY() + 0.5,
                                this.getZ(),
                                1,
                                offsetX, offsetY, offsetZ,
                                0.02
                        );
                    }
                }
            }

            // Spawn ambient particles occasionally
            if (this.lifeTicks % 20 == 0 && this.getEntityWorld() instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(
                        ParticleTypes.ASH,
                        this.getX(),
                        this.getY() + 0.5,
                        this.getZ(),
                        2,
                        0.2, 0.3, 0.2,
                        0.01
                );
            }
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);

        // Epic bone shatter effect on death
        if (!this.getEntityWorld().isClient() && this.getEntityWorld() instanceof ServerWorld serverWorld) {
            // Bone particles explosion
            for (int i = 0; i < 20; i++) {
                double offsetX = (this.random.nextDouble() - 0.5) * 1.0;
                double offsetY = this.random.nextDouble() * 1.0;
                double offsetZ = (this.random.nextDouble() - 0.5) * 1.0;

                serverWorld.spawnParticles(
                        ParticleTypes.POOF,
                        this.getX(),
                        this.getY() + 0.5,
                        this.getZ(),
                        1,
                        offsetX, offsetY, offsetZ,
                        0.1
                );
            }

            // Some soul particles
            for (int i = 0; i < 5; i++) {
                double offsetX = (this.random.nextDouble() - 0.5) * 0.3;
                double offsetY = this.random.nextDouble() * 0.5;
                double offsetZ = (this.random.nextDouble() - 0.5) * 0.3;

                serverWorld.spawnParticles(
                        ParticleTypes.SOUL,
                        this.getX(),
                        this.getY() + 0.5,
                        this.getZ(),
                        1,
                        offsetX, offsetY, offsetZ,
                        0.05
                );
            }
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_SKELETON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SKELETON_DEATH;
    }

    @Override
    public float getSoundPitch() {
        // Higher-pitched sounds for smaller creatures
        return super.getSoundPitch() * 1.4f;
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return true; // Allow natural despawn
    }
}