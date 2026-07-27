package com.github.hitman20081.dagmod.bone_realm.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;

/**
 * Boneling - Weak skeleton minion summoned by Skeleton Summoners
 * Fast, fragile, swarm creatures
 */
public class BonelingEntity extends Monster {

    private int lifeTicks = 0;
    private static final int MAX_LIFETIME = 3600; // 3 minutes (60 seconds * 60 ticks)

    public BonelingEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
        this.xpReward = 3; // Very low XP - they're just minions
    }

    public static AttributeSupplier.Builder createBonelingAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 12.0) // Fragile
                .add(Attributes.MOVEMENT_SPEED, 0.35) // Fast
                .add(Attributes.ATTACK_DAMAGE, 2.5) // Weak
                .add(Attributes.ARMOR, 0.0) // No armor
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.0) // Easy to knockback
                .add(Attributes.SCALE, 0.7); // Smaller - 70% size
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.3, false)); // Aggressive
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        // Count lifetime and despawn after max time
        if (!this.level().isClientSide() && this.isAlive()) {
            this.lifeTicks++;

            // Despawn after 3 minutes
            if (this.lifeTicks >= MAX_LIFETIME) {
                this.discard();

                // Spawn poof particles on despawn
                if (this.level() instanceof ServerLevel serverWorld) {
                    for (int i = 0; i < 10; i++) {
                        double offsetX = (this.random.nextDouble() - 0.5) * 0.5;
                        double offsetY = this.random.nextDouble() * 1.0;
                        double offsetZ = (this.random.nextDouble() - 0.5) * 0.5;

                        serverWorld.sendParticles(
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
            if (this.lifeTicks % 20 == 0 && this.level() instanceof ServerLevel serverWorld) {
                serverWorld.sendParticles(
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
    public void die(DamageSource damageSource) {
        super.die(damageSource);

        // Epic bone shatter effect on death
        if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverWorld) {
            // Bone particles explosion
            for (int i = 0; i < 20; i++) {
                double offsetX = (this.random.nextDouble() - 0.5) * 1.0;
                double offsetY = this.random.nextDouble() * 1.0;
                double offsetZ = (this.random.nextDouble() - 0.5) * 1.0;

                serverWorld.sendParticles(
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

                serverWorld.sendParticles(
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
        return SoundEvents.SKELETON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SKELETON_DEATH;
    }

    @Override
    public float getVoicePitch() {
        // Higher-pitched sounds for smaller creatures
        return super.getVoicePitch() * 1.4f;
    }

    @Override
    public boolean removeWhenFarAway(double distanceSquared) {
        return true; // Allow natural despawn
    }
}