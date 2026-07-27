package com.github.hitman20081.dagmod.entity;

import java.util.EnumSet;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.block.DragonEggBlock;
import com.github.hitman20081.dagmod.block.ModBlocks;
import com.github.hitman20081.dagmod.item.ModItems;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.util.RandomSource;

/**
 * Wild Dragon - Tameable flying dragon found in mountains and Dragon Realm
 * Smaller than Dragon Guardian boss, can be tamed from eggs
 * Features: Flying AI, egg laying, nesting, taming, growth stages
 */
public class WildDragonEntity extends Monster {

    // Animation states
    public enum AnimationState {
        IDLE,           // Slow, majestic flight
        FLYING,         // Normal flight
        ATTACKING,      // Fast aggressive flight
        SWOOPING,       // Diving attack
        ROARING,        // Intimidation display
        FIRE_BREATHING, // Fire attack
        LANDING,        // Landing on perch
        PERCHED,        // Stationary on ground
        LAYING_EGG      // Laying an egg on nest
    }

    // WildDragonEntity uses DragonGuardianEntity.DragonVariant (shared enum)
    // No need to duplicate the variant enum

    // Growth stages for tamed dragons
    public enum GrowthStage {
        BABY(0.2F, 6000),       // 5 minutes (6000 ticks) - Smaller than boss baby
        JUVENILE(0.3F, 12000),  // 10 minutes (12000 ticks)
        ADULT(0.4F, 0);         // Fully grown (smaller than boss)

        private final float scale;
        private final int growthTicks; // Ticks needed to reach next stage

        GrowthStage(float scale, int growthTicks) {
            this.scale = scale;
            this.growthTicks = growthTicks;
        }

        public float getScale() {
            return scale;
        }

        public int getGrowthTicks() {
            return growthTicks;
        }

        public GrowthStage next() {
            return values()[Math.min(ordinal() + 1, values().length - 1)];
        }
    }

    private static final EntityDataAccessor<Integer> ANIMATION_STATE = SynchedEntityData.defineId(WildDragonEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_PERCHED = SynchedEntityData.defineId(WildDragonEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(WildDragonEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_TAMED = SynchedEntityData.defineId(WildDragonEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> OWNER_UUID = SynchedEntityData.defineId(WildDragonEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> IS_SITTING = SynchedEntityData.defineId(WildDragonEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> GROWTH_STAGE = SynchedEntityData.defineId(WildDragonEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MEAT_FED = SynchedEntityData.defineId(WildDragonEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_ABANDONED = SynchedEntityData.defineId(WildDragonEntity.class, EntityDataSerializers.BOOLEAN);


    private static final int DRAGON_SCALE_DROP_MIN = 2;
    private static final int DRAGON_SCALE_DROP_MAX = 4;

    // Attack cooldowns
    private int fireBreathCooldown = 0;
    private int roarCooldown = 0;
    private int swoopCooldown = 0;

    // Egg laying system
    private BlockPos nestPosition = null;  // Track nest location
    private int eggLayingTimer = 0;        // Timer for laying eggs
    private static final int EGG_LAYING_INTERVAL = 12000; // 10 minutes (12000 ticks)
    private static final int EGG_LAYING_DURATION = 60;     // 3 seconds animation
    private static final int MAX_EGGS_PER_NEST = 3;        // Maximum eggs in one nest

    // Taming and growth system
    private int growthProgress = 0;        // Progress towards next growth stage
    private static final int TAMING_CHANCE = 33; // 33% chance per Dragon Heart
    private static final int MEAT_REQUIRED_FOR_TAMING = 96; // 1.5 stacks of raw meat

    // Perching
    private BlockPos perchLocation = null;
    private int perchTime = 0;
    private static final int MAX_PERCH_TIME = 200; // 10 seconds

    // Animation timers
    private int animationTimer = 0;

    private int nestCheckCooldown = 200; // Check every 10 seconds
    private int nestAbandonmentTimer = -1;
    private static final int NEST_ABANDON_TIME = 6000; // 5 minutes

    public WildDragonEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
        this.xpReward = 20; // Less XP than boss

        // Enable flying
        this.setNoGravity(true);
        this.moveControl = new FlyingMoveControl(this, 20, true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ANIMATION_STATE, AnimationState.IDLE.ordinal());
        builder.define(IS_PERCHED, false);
        builder.define(VARIANT, DragonGuardianEntity.DragonVariant.RED.ordinal());

        // Taming system data trackers
        builder.define(IS_TAMED, false);
        builder.define(OWNER_UUID, ""); // Empty string = no owner
        builder.define(IS_SITTING, false);
        builder.define(GROWTH_STAGE, GrowthStage.ADULT.ordinal()); // Wild dragons spawn as adults
        builder.define(MEAT_FED, 0); // Track meat feeding progress for taming
        builder.define(IS_ABANDONED, false);
    }

    // Animation state management
    public AnimationState getAnimationState() {
        return AnimationState.values()[this.entityData.get(ANIMATION_STATE)];
    }

    public void setAnimationState(AnimationState state) {
        this.entityData.set(ANIMATION_STATE, state.ordinal());
        this.animationTimer = 0;
    }

    public boolean isPerched() {
        return this.entityData.get(IS_PERCHED);
    }

    public void setPerched(boolean perched) {
        this.entityData.set(IS_PERCHED, perched);
    }

    public DragonGuardianEntity.DragonVariant getVariant() {
        return DragonGuardianEntity.DragonVariant.values()[this.entityData.get(VARIANT)];
    }

    public void setVariant(DragonGuardianEntity.DragonVariant variant) {
        this.entityData.set(VARIANT, variant.ordinal());
    }

    // Taming system getters/setters
    public boolean isTamed() {
        return this.entityData.get(IS_TAMED);
    }

    public void setTamed(boolean tamed) {
        this.entityData.set(IS_TAMED, tamed);
    }

    public java.util.Optional<java.util.UUID> getOwnerUuid() {
        String uuidString = this.entityData.get(OWNER_UUID);
        if (uuidString == null || uuidString.isEmpty()) {
            return java.util.Optional.empty();
        }
        try {
            return java.util.Optional.of(java.util.UUID.fromString(uuidString));
        } catch (IllegalArgumentException e) {
            return java.util.Optional.empty();
        }
    }

    public void setOwnerUuid(java.util.UUID uuid) {
        this.entityData.set(OWNER_UUID, uuid == null ? "" : uuid.toString());
    }

    public boolean isSitting() {
        return this.entityData.get(IS_SITTING);
    }

    public void setSitting(boolean sitting) {
        this.entityData.set(IS_SITTING, sitting);
    }

    public GrowthStage getGrowthStage() {
        return GrowthStage.values()[this.entityData.get(GROWTH_STAGE)];
    }

    public void setGrowthStage(GrowthStage stage) {
        this.entityData.set(GROWTH_STAGE, stage.ordinal());
        // Update scale to match growth stage
        this.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.SCALE).setBaseValue(stage.getScale());
    }

    public int getAnimationTimer() {
        return this.animationTimer;
    }

    public float getAnimationSpeed() {
        return switch (getAnimationState()) {
            case IDLE, PERCHED -> 0.5F;           // Slow breathing/idle
            case FLYING -> 1.2F;                  // Faster wing flaps for smoother flight
            case ATTACKING -> 1.6F;               // Fast aggressive
            case SWOOPING -> 2.2F;                // Very fast dive
            case ROARING, FIRE_BREATHING -> 0.8F; // Slightly slower for dramatic effect
            case LANDING -> 0.6F;                 // Controlled descent with steady wing beats
            case LAYING_EGG -> 0.3F;              // Very slow, calm crouched laying animation
        };
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        // Use bird navigation for flying
        FlyingPathNavigation birdNavigation = new FlyingPathNavigation(this, world);
        return birdNavigation;
    }

    @Override
    protected void registerGoals() {
        // Tamed dragon goals (highest priority when tamed)
        this.goalSelector.addGoal(0, new SitGoal(this));
        this.goalSelector.addGoal(1, new FollowOwnerGoal(this, 1.2D, 8.0F, 3.0F));

        // Combat goals (only for wild/attacking dragons)
        this.goalSelector.addGoal(2, new FireBreathGoal(this));
        this.goalSelector.addGoal(3, new SwoopAttackGoal(this));
        this.goalSelector.addGoal(4, new RoarGoal(this));
        this.goalSelector.addGoal(5, new FlyingMeleeAttackGoal(this, 1.2D));

        // Movement and perching
        this.goalSelector.addGoal(6, new ReturnToNestGoal(this));
        this.goalSelector.addGoal(7, new PerchGoal(this));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 32.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));

        // Target goals
        this.targetSelector.addGoal(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.addGoal(2, new AttackWithOwnerGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    /**
     * Override to prevent targeting Creative or Spectator mode players,
     * and prevent tamed dragons from attacking their owner
     */
    @Override
    public boolean canAttack(LivingEntity target) {
        // Tamed dragons should not attack their owner
        if (this.isTamed() && target instanceof Player player) {
            if (this.getOwnerUuid().isPresent() && this.getOwnerUuid().get().equals(player.getUUID())) {
                return false; // Never attack owner
            }
        }

        // Wild dragons don't attack creative/spectator players
        if (target instanceof Player player) {
            if (player.isCreative() || player.isSpectator()) {
                return false;
            }
        }

        return super.canAttack(target);
    }

    /**
     * Fire breath attack goal - shoots fireballs at distant targets
     */
    private static class FireBreathGoal extends Goal {
        private final WildDragonEntity dragon;
        private LivingEntity target;
        private int chargingTicks = 0;
        private static final int CHARGE_TIME = 40; // 2 seconds

        public FireBreathGoal(WildDragonEntity dragon) {
            this.dragon = dragon;
        }

        @Override
        public boolean canUse() {
            if (dragon.fireBreathCooldown > 0) return false;
            this.target = dragon.getTarget();
            if (target == null) return false;
            double distance = dragon.distanceToSqr(target);
            return distance >= 16.0 && distance <= 400.0; // 4-20 blocks
        }

        @Override
        public boolean canContinueToUse() {
            return chargingTicks < CHARGE_TIME && target != null && target.isAlive();
        }

        @Override
        public void start() {
            chargingTicks = 0;
            dragon.setAnimationState(AnimationState.FIRE_BREATHING);
            dragon.level().playSound(null, dragon.blockPosition(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 1.5F, 1.2F);
        }

        @Override
        public void tick() {
            chargingTicks++;
            dragon.getLookControl().setLookAt(target, 30.0F, 30.0F);

            // Spawn fire particles during charging
            if (dragon.level() instanceof ServerLevel serverWorld && chargingTicks % 2 == 0) {
                Vec3 mouthPos = new Vec3(dragon.getX(), dragon.getY(), dragon.getZ()).add(dragon.getViewVector(1.0F).scale(1.5));
                serverWorld.sendParticles(ParticleTypes.FLAME, mouthPos.x, mouthPos.y + 0.8, mouthPos.z, 2, 0.15, 0.15, 0.15, 0.02);
            }

            // Fire the fireball
            if (chargingTicks == CHARGE_TIME) {
                Vec3 lookVec = dragon.getViewVector(1.0F);

                // Spawn fireball from head position
                double spawnX = dragon.getX() + lookVec.x * 1.5;
                double spawnY = dragon.getEyeY() + 0.3;
                double spawnZ = dragon.getZ() + lookVec.z * 1.5;

                // Calculate direction vector toward current target position
                Vec3 targetPos = new Vec3(target.getX(), target.getY() + target.getBbHeight() / 2.0, target.getZ());
                Vec3 spawnPos = new Vec3(spawnX, spawnY, spawnZ);
                Vec3 direction = targetPos.subtract(spawnPos).normalize();

                // Create fireball
                SmallFireball fireball = new SmallFireball(
                    dragon.level(),
                    spawnX, spawnY, spawnZ,
                    direction
                );
                fireball.setOwner(dragon);
                dragon.level().addFreshEntity(fireball);

                dragon.level().playSound(null, dragon.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.HOSTILE, 1.0F, 1.0F);
                dragon.fireBreathCooldown = 100; // 5 second cooldown
            }
        }

        @Override
        public void stop() {
            chargingTicks = 0;
            dragon.setAnimationState(AnimationState.FLYING);
        }
    }

    /**
     * Swooping dive attack - fast descent to attack ground targets
     */
    private static class SwoopAttackGoal extends Goal {
        private final WildDragonEntity dragon;
        private LivingEntity target;
        private Vec3 swoopStart;
        private boolean isSwooping = false;

        public SwoopAttackGoal(WildDragonEntity dragon) {
            this.dragon = dragon;
        }

        @Override
        public boolean canUse() {
            if (dragon.swoopCooldown > 0 || dragon.isPerched()) return false;
            this.target = dragon.getTarget();
            if (target == null) return false;
            double distance = dragon.distanceToSqr(target);
            // Only swoop if dragon is above target
            return distance < 400.0 && dragon.getY() > target.getY() + 5;
        }

        @Override
        public void start() {
            isSwooping = true;
            swoopStart = new Vec3(dragon.getX(), dragon.getY(), dragon.getZ());
            dragon.setAnimationState(AnimationState.SWOOPING);
            dragon.setDeltaMovement(dragon.getDeltaMovement().multiply(1.5, 0.5, 1.5));
        }

        @Override
        public void tick() {
            if (target == null || !target.isAlive()) {
                stop();
                return;
            }

            // Dive towards target
            Vec3 targetPos = new Vec3(target.getX(), target.getY(), target.getZ());
            Vec3 dragonPos = new Vec3(dragon.getX(), dragon.getY(), dragon.getZ());
            Vec3 direction = targetPos.subtract(dragonPos).normalize();

            dragon.setDeltaMovement(direction.scale(1.3));

            // Wing dust particles during swoop
            if (dragon.level() instanceof ServerLevel serverWorld && dragon.tickCount % 2 == 0) {
                serverWorld.sendParticles(ParticleTypes.CLOUD, dragonPos.x, dragonPos.y, dragonPos.z, 2, 0.4, 0.2, 0.4, 0);
            }

            // Attack if close enough
            if (dragon.distanceToSqr(target) < 4.0) {
                if (dragon.level() instanceof ServerLevel serverWorld) {
                    dragon.doHurtTarget(serverWorld, target);
                }
                dragon.swoopCooldown = 120; // 6 second cooldown
                stop();
            }
        }

        @Override
        public void stop() {
            isSwooping = false;
            dragon.setAnimationState(AnimationState.FLYING);
        }

        @Override
        public boolean canContinueToUse() {
            return isSwooping && target != null && target.isAlive() && dragon.distanceToSqr(target) > 4.0;
        }
    }

    /**
     * Roar goal - intimidation display when entering combat
     */
    private static class RoarGoal extends Goal {
        private final WildDragonEntity dragon;
        private int roarTicks = 0;
        private static final int ROAR_DURATION = 30; // 1.5 seconds

        public RoarGoal(WildDragonEntity dragon) {
            this.dragon = dragon;
        }

        @Override
        public boolean canUse() {
            if (dragon.roarCooldown > 0) return false;
            LivingEntity target = dragon.getTarget();
            // Roar when first acquiring a target
            return target != null && dragon.tickCount - dragon.getLastHurtMobTimestamp() > 200;
        }

        @Override
        public void start() {
            roarTicks = 0;
            dragon.setAnimationState(AnimationState.ROARING);
            dragon.level().playSound(null, dragon.blockPosition(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 2.0F, 1.0F);
        }

        @Override
        public void tick() {
            roarTicks++;
            dragon.setDeltaMovement(Vec3.ZERO); // Stationary while roaring

            // Intimidation effect - apply slowness to nearby players
            if (roarTicks == 15) {
                dragon.level().getEntitiesOfClass(Player.class, dragon.getBoundingBox().inflate(8), p -> true)
                        .forEach(player -> {
                            player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                                    net.minecraft.world.effect.MobEffects.SLOWNESS, 40, 0));
                        });
            }
        }

        @Override
        public void stop() {
            dragon.roarCooldown = 400; // 20 second cooldown
            dragon.setAnimationState(AnimationState.FLYING);
        }

        @Override
        public boolean canContinueToUse() {
            return roarTicks < ROAR_DURATION;
        }
    }

    /**
     * Perching goal - land on high points when not in combat
     */
    private static class PerchGoal extends Goal {
        private final WildDragonEntity dragon;

        public PerchGoal(WildDragonEntity dragon) {
            this.dragon = dragon;
        }

        @Override
        public boolean canUse() {
            // Only perch if it has a nest, no target, and not already perched
            return dragon.nestPosition != null && dragon.getTarget() == null && !dragon.isPerched() && dragon.random.nextInt(200) == 0;
        }

        @Override
        public void start() {
            // Find a high point nearby to perch on
            BlockPos currentPos = dragon.blockPosition();
            BlockPos perchPos = findPerchLocation(currentPos);

            if (perchPos != null) {
                dragon.perchLocation = perchPos;
                dragon.setAnimationState(AnimationState.LANDING);
            }
        }

        @Override
        public void tick() {
            if (dragon.perchLocation == null) {
                stop();
                return;
            }

            if (!dragon.isPerched()) {
                // Descend to perch location
                Vec3 targetPos = Vec3.atCenterOf(dragon.perchLocation);
                Vec3 dragonPos = new Vec3(dragon.getX(), dragon.getY(), dragon.getZ());
                Vec3 direction = targetPos.subtract(dragonPos).normalize().scale(0.3);
                dragon.setDeltaMovement(direction);

                // Land when close enough
                if (dragon.distanceToSqr(Vec3.atCenterOf(dragon.perchLocation)) < 4.0) {
                    dragon.setPos(Vec3.atCenterOf(dragon.perchLocation));
                    dragon.setDeltaMovement(Vec3.ZERO);
                    dragon.setPerched(true);
                    dragon.setAnimationState(AnimationState.PERCHED);
                    dragon.perchTime = 0;

                    // Check if landing on a nest (stone/Magma Block platform)
                    BlockPos groundPos = dragon.perchLocation.below();
                    if (dragon.level().getBlockState(groundPos).getBlock() == Blocks.STONE ||
                        dragon.level().getBlockState(groundPos).getBlock() == Blocks.MAGMA_BLOCK) {
                        dragon.nestPosition = groundPos; // Set nest position
                        DagMod.LOGGER.debug("Wild Dragon landed on nest at {}", groundPos);
                    }
                }
            } else {
                // Stay perched
                dragon.perchTime++;
                dragon.setDeltaMovement(Vec3.ZERO);

                // Leave perch after timeout or if threatened
                if (dragon.perchTime > MAX_PERCH_TIME || dragon.getTarget() != null) {
                    stop();
                }
            }
        }

        @Override
        public void stop() {
            dragon.setPerched(false);
            dragon.setNoGravity(true);
            dragon.setAnimationState(AnimationState.FLYING);
            dragon.perchLocation = null;
            dragon.perchTime = 0;
        }

        @Override
        public boolean canContinueToUse() {
            return dragon.perchLocation != null && dragon.getTarget() == null;
        }

        private BlockPos findPerchLocation(BlockPos center) {
            // Look for a solid block within 20 blocks horizontally, preferring higher locations
            for (int y = 10; y >= -5; y--) {
                for (int x = -20; x <= 20; x += 5) {
                    for (int z = -20; z <= 20; z += 5) {
                        BlockPos testPos = center.offset(x, y, z);
                        if (dragon.level().getBlockState(testPos).isSolid()
                                && dragon.level().getBlockState(testPos.above()).isAir()) {
                            return testPos.above();
                        }
                    }
                }
            }
            return null;
        }
    }

    /**
     * Return to nest goal - dragon periodically returns to its nest when wandering far
     */
    private static class ReturnToNestGoal extends Goal {
        private final WildDragonEntity dragon;
        private static final int MIN_DISTANCE_FROM_NEST = 60; // Start returning if > 60 blocks away
        private static final int CHECK_INTERVAL = 600; // Check every 30 seconds
        private int ticksSinceCheck = 0;
        private boolean returning = false;

        public ReturnToNestGoal(WildDragonEntity dragon) {
            this.dragon = dragon;
        }

        @Override
        public boolean canUse() {
            if (dragon.nestPosition == null || dragon.getTarget() != null || dragon.isPerched()) {
                return false;
            }

            ticksSinceCheck++;
            if (ticksSinceCheck < CHECK_INTERVAL) {
                return false;
            }
            ticksSinceCheck = 0;

            double distance = dragon.distanceToSqr(
                dragon.nestPosition.getX() + 0.5,
                dragon.nestPosition.getY() + 1.0,
                dragon.nestPosition.getZ() + 0.5
            );

            return distance > MIN_DISTANCE_FROM_NEST * MIN_DISTANCE_FROM_NEST && dragon.random.nextInt(3) == 0;
        }

        @Override
        public void start() {
            returning = true;
            dragon.setAnimationState(AnimationState.FLYING);
            DagMod.LOGGER.debug("Wild Dragon starting return to nest at {}", dragon.nestPosition);
        }

        @Override
        public void tick() {
            if (dragon.nestPosition == null) {
                stop();
                return;
            }

            // Fly toward nest
            Vec3 targetPos = Vec3.atCenterOf(dragon.nestPosition).add(0, 3, 0);
            Vec3 dragonPos = new Vec3(dragon.getX(), dragon.getY(), dragon.getZ());
            double distance = dragonPos.distanceTo(targetPos);

            if (distance < 8.0) {
                stop();
                return;
            }

            Vec3 direction = targetPos.subtract(dragonPos).normalize().scale(0.25);
            dragon.setDeltaMovement(direction);
            dragon.getLookControl().setLookAt(targetPos.x, targetPos.y, targetPos.z);
        }

        @Override
        public void stop() {
            returning = false;
            ticksSinceCheck = 0;
            DagMod.LOGGER.debug("Wild Dragon stopped returning to nest");
        }

        @Override
        public boolean canContinueToUse() {
            if (!returning || dragon.nestPosition == null || dragon.getTarget() != null) {
                return false;
            }

            double distance = dragon.distanceToSqr(
                dragon.nestPosition.getX() + 0.5,
                dragon.nestPosition.getY() + 1.0,
                dragon.nestPosition.getZ() + 0.5
            );

            return distance > 64.0;
        }
    }

    /**
     * Custom flying melee attack goal
     */
    private static class FlyingMeleeAttackGoal extends Goal {
        private final WildDragonEntity dragon;
        private final double speed;
        private int cooldown = 0;

        public FlyingMeleeAttackGoal(WildDragonEntity dragon, double speed) {
            this.dragon = dragon;
            this.speed = speed;
        }

        @Override
        public boolean canUse() {
            return dragon.getTarget() != null && dragon.getTarget().isAlive() && !dragon.isPerched();
        }

        @Override
        public void start() {
            dragon.setAnimationState(AnimationState.ATTACKING);
        }

        @Override
        public void tick() {
            if (cooldown > 0) {
                cooldown--;
                return;
            }

            LivingEntity target = dragon.getTarget();
            if (target == null) return;

            double distance = dragon.distanceToSqr(target);

            if (distance < 4.0) {
                // Attack range
                if (dragon.level() instanceof ServerLevel serverWorld) {
                    dragon.doHurtTarget(serverWorld, target);
                }
                cooldown = 20; // 1 second cooldown
            } else if (distance < 256.0) {
                // Chase range - fly towards target
                Vec3 targetPos = new Vec3(target.getX(), target.getY() + 1.5, target.getZ());
                Vec3 dragonPos = new Vec3(dragon.getX(), dragon.getY(), dragon.getZ());
                Vec3 direction = targetPos.subtract(dragonPos).normalize().scale(speed);
                dragon.setDeltaMovement(direction);
                dragon.setAnimationState(AnimationState.ATTACKING);
            }
        }

        @Override
        public void stop() {
            if (dragon.getTarget() == null) {
                dragon.setAnimationState(AnimationState.IDLE);
            }
        }
    }

    /**
     * Subclasses can override this to guarantee a Dragon Heart drop on death.
     */
    protected boolean alwaysDropHeart() {
        return false;
    }

    public static AttributeSupplier.Builder createWildDragonAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 160.0)          // 160 HP
                .add(Attributes.MOVEMENT_SPEED, 0.18)       // Base speed
                .add(Attributes.FLYING_SPEED, 0.35)         // Flying speed (slightly slower than boss)
                .add(Attributes.ATTACK_DAMAGE, 10.0)        // Moderate attacks (vs boss 16.0)
                .add(Attributes.ARMOR, 6.0)                 // Light armor (vs boss 16.0)
                .add(Attributes.ARMOR_TOUGHNESS, 8.0)       // Toughness counters heavy hits
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)  // Lower resistance (vs boss 0.8)
                .add(Attributes.FOLLOW_RANGE, 40.0)         // Slightly lower range
                .add(Attributes.ATTACK_KNOCKBACK, 0.5)      // Moderate knockback (vs boss 1.0)
                .add(Attributes.SCALE, 0.4);                // 40% size (vs boss 60%)
    }

    @Override
    public boolean hurtServer(net.minecraft.server.level.ServerLevel world, DamageSource source, float amount) {
        // Immune to fall damage
        if (source.is(net.minecraft.world.damagesource.DamageTypes.FALL)) {
            return false;
        }
        return super.hurtServer(world, source, amount);
    }

    @Override
    protected void checkFallDamage(double heightDifference, boolean onGround, net.minecraft.world.level.block.state.BlockState state, net.minecraft.core.BlockPos pos) {
        // Don't apply fall damage logic for flying entity
    }

    @Override
    public boolean onClimbable() {
        // Never climbing, always flying
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        // Server-side logic
        if (!this.level().isClientSide()) {
            // Decrement cooldowns
            if (this.fireBreathCooldown > 0) this.fireBreathCooldown--;
            if (this.roarCooldown > 0) this.roarCooldown--;
            if (this.swoopCooldown > 0) this.swoopCooldown--;

            // Egg laying logic
            if (isPerched() && this.nestPosition != null) {
                this.eggLayingTimer++;

                // Time to lay an egg
                if (this.eggLayingTimer >= EGG_LAYING_INTERVAL) {
                    if (getAnimationState() != AnimationState.LAYING_EGG) {
                        // Start laying animation
                        setAnimationState(AnimationState.LAYING_EGG);
                        this.eggLayingTimer = -EGG_LAYING_DURATION; // Negative value acts as animation timer
                    }
                }

                // Animation complete, spawn egg
                if (this.eggLayingTimer == 0 && getAnimationState() == AnimationState.LAYING_EGG) {
                    tryLayEgg((ServerLevel) this.level());
                    setAnimationState(AnimationState.PERCHED);
                }
            } else {
                // Reset timer if not on nest
                this.eggLayingTimer = 0;
            }

            // Growth mechanics for tamed dragons
            if (this.isTamed() && this.getGrowthStage() != GrowthStage.ADULT) {
                this.growthProgress++;
                GrowthStage currentStage = this.getGrowthStage();

                // Check if ready to advance to next growth stage
                if (this.growthProgress >= currentStage.getGrowthTicks()) {
                    GrowthStage nextStage = currentStage.next();
                    this.setGrowthStage(nextStage);
                    this.growthProgress = 0; // Reset progress for next stage

                    // Growth particle effects
                    if (this.level() instanceof ServerLevel serverWorld) {
                        serverWorld.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                            this.getX(), this.getY() + this.getBbHeight() / 2, this.getZ(),
                            20, 0.5, 0.5, 0.5, 0.1);

                        serverWorld.playSound(null, this.blockPosition(),
                            SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, 1.0F, 1.5F);
                    }

                    DagMod.LOGGER.info("Wild Dragon grew from {} to {}", currentStage.name(), nextStage.name());
                }
            }

            if (this.nestPosition != null && !this.isTamed()) {
                this.nestCheckCooldown--;
                if (this.nestCheckCooldown <= 0) {
                    this.nestCheckCooldown = 200; // Reset cooldown

                    boolean hasEggs = false;
                    for (int x = -3; x <= 3; x++) {
                        for (int z = -3; z <= 3; z++) {
                            BlockPos checkPos = this.nestPosition.offset(x, 1, z);
                            if (this.level().getBlockState(checkPos).getBlock() == ModBlocks.DRAGON_EGG_BLOCK) {
                                hasEggs = true;
                                break;
                            }
                        }
                        if (hasEggs) break;
                    }

                    if (!hasEggs) {
                        if (this.nestAbandonmentTimer == -1) {
                            // Start abandonment timer
                            this.nestAbandonmentTimer = NEST_ABANDON_TIME;
                            DagMod.LOGGER.info("Wild Dragon nest at {} is empty. Starting abandonment timer.", this.nestPosition);
                        }
                    } else {
                        // Eggs are present, reset timer
                        if (this.nestAbandonmentTimer != -1) {
                            this.nestAbandonmentTimer = -1;
                            DagMod.LOGGER.info("Wild Dragon detected eggs in nest at {}. Abandonment cancelled.", this.nestPosition);
                        }
                    }
                }

                if (this.nestAbandonmentTimer > 0) {
                    this.nestAbandonmentTimer--;
                } else if (this.nestAbandonmentTimer == 0) {
                    // Abandon nest
                    DagMod.LOGGER.info("Wild Dragon is abandoning its nest at {}.", this.nestPosition);
                    this.nestPosition = null;
                    this.entityData.set(IS_ABANDONED, true);
                    this.nestAbandonmentTimer = -1; // Stop timer
                }
            }

            // Update animation state based on velocity if not in special state
            if (getAnimationState() == AnimationState.FLYING || getAnimationState() == AnimationState.IDLE) {
                double velocity = this.getDeltaMovement().length();
                if (velocity > 0.3) {
                    setAnimationState(AnimationState.FLYING);
                } else if (velocity < 0.1 && !isPerched()) {
                    setAnimationState(AnimationState.IDLE);
                }
            }
        }

        // Increment animation timer
        this.animationTimer++;

        // Server-side ambient particle effects
        if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverWorld) {
            spawnAmbientParticles(serverWorld);
        }

        // Wing flap sounds
        if (this.tickCount % 15 == 0 && getAnimationState() != AnimationState.PERCHED) {
            this.level().playSound(null, this.blockPosition(), SoundEvents.ENDER_DRAGON_FLAP, SoundSource.HOSTILE, 0.4F, 1.2F);
        }
    }

    /**
     * Handle player interactions with the dragon (taming, sitting, feeding)
     */
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        // Only handle on server
        if (this.level().isClientSide()) {
            return this.isTamed() ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }

        // Alternative taming with raw meat (only for baby/juvenile dragons)
        if (!this.isTamed() && isRawMeat(itemStack)) {
            if (this.getGrowthStage() == GrowthStage.ADULT) {
                // Adult wild dragons cannot be tamed
                player.sendOverlayMessage(Component.literal("This dragon is too old to be tamed!").withStyle(ChatFormatting.RED));
                return InteractionResult.FAIL;
            }

            // Increment meat fed counter
            int currentMeat = this.entityData.get(MEAT_FED);
            currentMeat++;
            this.entityData.set(MEAT_FED, currentMeat);

            // Progress feedback
            int remaining = MEAT_REQUIRED_FOR_TAMING - currentMeat;
            if (remaining > 0) {
                player.sendSystemMessage(
                    Component.literal("Dragon trusts you more... (" + currentMeat + "/" + MEAT_REQUIRED_FOR_TAMING + " meat fed)")
                        .withStyle(ChatFormatting.YELLOW));

                // Progress particles
                if (this.level() instanceof ServerLevel serverWorld) {
                    serverWorld.sendParticles(ParticleTypes.HEART,
                        this.getX(), this.getY() + this.getBbHeight(), this.getZ(),
                        3, 0.3, 0.3, 0.3, 0.0);
                }
            }

            // Check if enough meat has been fed to tame
            if (currentMeat >= MEAT_REQUIRED_FOR_TAMING) {
                // Successfully tamed!
                this.setTamed(true);
                this.setOwnerUuid(player.getUUID());
                this.setTarget(null);
                this.entityData.set(MEAT_FED, 0);

                // Heart particles
                if (this.level() instanceof ServerLevel serverWorld) {
                    serverWorld.sendParticles(ParticleTypes.HEART,
                        this.getX(), this.getY() + this.getBbHeight(), this.getZ(),
                        20, 0.5, 0.5, 0.5, 0.0);
                }

                this.level().playSound(null, this.blockPosition(),
                    SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, 1.0F, 2.0F);

                player.sendOverlayMessage(Component.literal("The dragon has been tamed!").withStyle(ChatFormatting.GREEN));
            }

            if (!player.isCreative()) {
                itemStack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }

        // Taming with Dragon Heart (only for baby/juvenile dragons)
        if (!this.isTamed() && itemStack.getItem() == ModItems.DRAGON_HEART) {
            if (this.getGrowthStage() == GrowthStage.ADULT) {
                player.sendSystemMessage(Component.literal("This dragon is too old to be tamed!").withStyle(ChatFormatting.RED));
                return InteractionResult.FAIL;
            }

            // RandomSource chance to tame (33% per heart)
            if (this.random.nextInt(100) < TAMING_CHANCE) {
                // Successfully tamed!
                this.setTamed(true);
                this.setOwnerUuid(player.getUUID());
                this.setTarget(null);

                // Heart particles
                if (this.level() instanceof ServerLevel serverWorld) {
                    serverWorld.sendParticles(ParticleTypes.HEART,
                        this.getX(), this.getY() + this.getBbHeight(), this.getZ(),
                        15, 0.5, 0.5, 0.5, 0.0);
                }

                this.level().playSound(null, this.blockPosition(),
                    SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, 1.0F, 2.0F);

                player.sendSystemMessage(Component.literal("The dragon has been tamed!").withStyle(ChatFormatting.GREEN));

                if (!player.isCreative()) {
                    itemStack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            } else {
                // Failed, try again
                if (this.level() instanceof ServerLevel serverWorld) {
                    serverWorld.sendParticles(ParticleTypes.SMOKE,
                        this.getX(), this.getY() + this.getBbHeight(), this.getZ(),
                        5, 0.3, 0.3, 0.3, 0.0);
                }

                if (!player.isCreative()) {
                    itemStack.shrink(1);
                }
                return InteractionResult.CONSUME;
            }
        }

        // Tamed dragon interactions
        if (this.isTamed() && this.getOwnerUuid().isPresent() && this.getOwnerUuid().get().equals(player.getUUID())) {
            // Shift + right-click to toggle sitting
            if (player.isShiftKeyDown()) {
                this.setSitting(!this.isSitting());
                player.sendSystemMessage(
                    Component.literal(this.isSitting() ? "Dragon is now sitting" : "Dragon is now following")
                        .withStyle(ChatFormatting.YELLOW));
                return InteractionResult.SUCCESS;
            }

            // Feeding system (food items heal the dragon)
            FoodProperties food = itemStack.get(DataComponents.FOOD);
            if (food != null && this.getHealth() < this.getMaxHealth()) {
                    // Heal dragon based on food value
                    float healAmount = food.nutrition() * 2.0F;
                    this.heal(healAmount);

                    // Eating particles and sound
                    if (this.level() instanceof ServerLevel serverWorld) {
                        serverWorld.sendParticles(ParticleTypes.HEART,
                            this.getX(), this.getY() + this.getBbHeight() / 2, this.getZ(),
                            5, 0.3, 0.3, 0.3, 0.0);

                        serverWorld.playSound(null, this.blockPosition(),
                            SoundEvents.GENERIC_EAT.value(), SoundSource.NEUTRAL, 1.0F, 1.0F);
                    }

                if (!player.isCreative()) {
                    itemStack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }

        return super.mobInteract(player, hand);
    }

    /**
     * Check if an item is raw meat that can be used for taming
     */
    private boolean isRawMeat(ItemStack stack) {
        return stack.getItem() == Items.BEEF ||
               stack.getItem() == Items.PORKCHOP ||
               stack.getItem() == Items.CHICKEN ||
               stack.getItem() == Items.MUTTON ||
               stack.getItem() == Items.RABBIT ||
               stack.getItem() == Items.COD ||
               stack.getItem() == Items.SALMON;
    }

    /**
     * Spawn ambient particles on server side
     */
    private void spawnAmbientParticles(ServerLevel world) {
        switch (getAnimationState()) {
            case FLYING, ATTACKING -> {
                // Wing dust trails (smaller than boss)
                if (this.tickCount % 4 == 0) {
                    double yawRad = Math.toRadians(this.getYRot());
                    Vec3 wingLeft = new Vec3(
                        this.getX() + (-1.0 * Math.cos(yawRad)),
                        this.getY() + 0.3,
                        this.getZ() + (-1.0 * Math.sin(yawRad))
                    );
                    Vec3 wingRight = new Vec3(
                        this.getX() + (1.0 * Math.cos(yawRad)),
                        this.getY() + 0.3,
                        this.getZ() + (1.0 * Math.sin(yawRad))
                    );

                    world.sendParticles(ParticleTypes.CLOUD, wingLeft.x, wingLeft.y, wingLeft.z, 1, 0.0, -0.05, 0.0, 0.0);
                    world.sendParticles(ParticleTypes.CLOUD, wingRight.x, wingRight.y, wingRight.z, 1, 0.0, -0.05, 0.0, 0.0);
                }
            }
            case ROARING -> {
                // Intimidation particles
                if (this.tickCount % 6 == 0) {
                    double radius = 1.5;
                    for (int i = 0; i < 2; i++) {
                        double angle = this.random.nextDouble() * 2 * Math.PI;
                        double x = this.getX() + Math.cos(angle) * radius;
                        double z = this.getZ() + Math.sin(angle) * radius;
                        world.sendParticles(ParticleTypes.ANGRY_VILLAGER, x, this.getY() + 1.0, z, 1, 0.0, 0.1, 0.0, 0.0);
                    }
                }
            }
            case LANDING -> {
                // Dust particles during landing
                if (this.tickCount % 3 == 0 && this.getDeltaMovement().y < -0.1) {
                    world.sendParticles(ParticleTypes.CLOUD, this.getX(), this.getY(), this.getZ(),
                            2, 0.2, 0.1, 0.2, 0.02);
                }
            }
            case LAYING_EGG -> {
                // Heart and sparkle particles during egg laying
                if (this.tickCount % 10 == 0) {
                    world.sendParticles(ParticleTypes.HEART, this.getX(), this.getY() + 0.8, this.getZ(),
                            1, 0.3, 0.3, 0.3, 0.0);
                }
                if (this.tickCount % 5 == 0) {
                    world.sendParticles(ParticleTypes.ENCHANT, this.getX(), this.getY(), this.getZ(),
                            2, 0.4, 0.2, 0.4, 0.01);
                }
            }
        }
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);

        // Death effects and drops
        if (!this.level().isClientSide()) {
            ServerLevel serverWorld = (ServerLevel) this.level();

            // Death particle explosion
            for (int i = 0; i < 30; i++) {
                double offsetX = this.random.nextGaussian() * 1.5;
                double offsetY = this.random.nextGaussian() * 1.5;
                double offsetZ = this.random.nextGaussian() * 1.5;
                serverWorld.sendParticles(ParticleTypes.POOF,
                        this.getX() + offsetX, this.getY() + 0.8 + offsetY, this.getZ() + offsetZ,
                        3, 0.3, 0.3, 0.3, 0.1);
            }

            // Explosion effect
            serverWorld.sendParticles(ParticleTypes.EXPLOSION,
                    this.getX(), this.getY() + 0.8, this.getZ(),
                    1, 0.0, 0.0, 0.0, 0.0);

            // Play death sound
            serverWorld.playSound(null, this.blockPosition(), SoundEvents.WITHER_DEATH, SoundSource.HOSTILE, 1.5F, 1.2F);

            // Drop Dragon Scales (2-4)
            int scaleCount = DRAGON_SCALE_DROP_MIN + this.random.nextInt(DRAGON_SCALE_DROP_MAX - DRAGON_SCALE_DROP_MIN + 1);
            ItemStack scaleStack = new ItemStack(ModItems.DRAGON_SCALE, scaleCount);
            ItemEntity scaleEntity = new ItemEntity(serverWorld, this.getX(), this.getY(), this.getZ(), scaleStack);
            serverWorld.addFreshEntity(scaleEntity);

            // Drop Dragon Bones (1-2)
            int boneCount = 1 + this.random.nextInt(2);
            ItemStack boneStack = new ItemStack(ModItems.DRAGON_BONE, boneCount);
            ItemEntity boneEntity = new ItemEntity(serverWorld, this.getX(), this.getY(), this.getZ(), boneStack);
            serverWorld.addFreshEntity(boneEntity);

            // Drop Dragon Skin (1)
            ItemStack skinStack = new ItemStack(ModItems.DRAGON_SKIN, 1);
            ItemEntity skinEntity = new ItemEntity(serverWorld, this.getX(), this.getY(), this.getZ(), skinStack);
            serverWorld.addFreshEntity(skinEntity);

            // Drop Dragon Heart: guaranteed for quest-specific subclasses, 50% otherwise
            if (this.alwaysDropHeart() || this.random.nextBoolean()) {
                ItemStack heartStack = new ItemStack(ModItems.DRAGON_HEART, 1);
                ItemEntity heartEntity = new ItemEntity(serverWorld, this.getX(), this.getY(), this.getZ(), heartStack);
                serverWorld.addFreshEntity(heartEntity);
            }

            // Remove this dragon's location from spawn tracking
            DragonSpawner.removeDragonLocation(this.blockPosition());

            // Start respawn cooldown — only for natural wild dragons, not quest subclasses
            if (this.getClass() == WildDragonEntity.class) {
                DragonSpawner.recordDragonDeath(serverWorld.getGameTime());
            }
        }
    }

    /**
     * Attempt to lay an egg on the nest
     */
    private void tryLayEgg(ServerLevel world) {
        if (this.nestPosition == null) return;

        // Count existing eggs in nest area
        int eggCount = 0;
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                BlockPos checkPos = this.nestPosition.offset(x, 1, z);
                if (world.getBlockState(checkPos).getBlock() == ModBlocks.DRAGON_EGG_BLOCK) {
                    eggCount++;
                }
            }
        }

        // Don't lay if nest is full
        if (eggCount >= MAX_EGGS_PER_NEST) {
            DagMod.LOGGER.info("Wild Dragon nest full ({} eggs), skipping egg laying", eggCount);
            return;
        }

        // Find a suitable spot to place the egg
        for (int attempt = 0; attempt < 10; attempt++) {
            int x = random.nextInt(3) - 1;
            int z = random.nextInt(3) - 1;
            BlockPos eggPos = this.nestPosition.offset(x, 1, z);

            if (world.getBlockState(eggPos).isAir() && world.getBlockState(eggPos.below()).getBlock() == Blocks.MAGMA_BLOCK) {
                // Place the egg with the correct variant in the BlockState (for rendering)
                world.setBlock(eggPos, ModBlocks.DRAGON_EGG_BLOCK.defaultBlockState()
                    .setValue(DragonEggBlock.VARIANT, this.getVariant()), 3);

                // Set the egg's variant in the BlockEntity as well (for hatching logic)
                if (world.getBlockEntity(eggPos) instanceof DragonEggBlockEntity eggEntity) {
                    eggEntity.setVariant(this.getVariant());
                    eggEntity.setChanged();
                }

                // Particle effects
                world.sendParticles(ParticleTypes.HEART,
                        eggPos.getX() + 0.5, eggPos.getY() + 0.5, eggPos.getZ() + 0.5,
                        5, 0.3, 0.3, 0.3, 0.0);

                // Success sound
                world.playSound(null, eggPos, SoundEvents.CHICKEN_EGG, SoundSource.NEUTRAL, 1.0F, 0.7F);

                DagMod.LOGGER.info("Wild {} Dragon laid egg at {} (total eggs in nest: {})",
                        this.getVariant().name(), eggPos, eggCount + 1);
                return;
            }
        }

        DagMod.LOGGER.warn("Wild Dragon couldn't find spot to lay egg in nest at {}", this.nestPosition);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENDER_DRAGON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENDER_DRAGON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WITHER_DEATH;
    }

    @Override
    public Component getName() {
        // Return variant-specific name with matching color
        return switch (getVariant()) {
            case RED -> Component.translatable("entity.dagmod.wild_red_dragon").withStyle(ChatFormatting.RED);
            case ICE -> Component.translatable("entity.dagmod.wild_ice_dragon").withStyle(ChatFormatting.AQUA);
            case LAVA -> Component.translatable("entity.dagmod.wild_lava_dragon").withStyle(ChatFormatting.GOLD);
            case EARTH -> Component.translatable("entity.dagmod.wild_earth_dragon").withStyle(ChatFormatting.DARK_GREEN);
            case WIND -> Component.translatable("entity.dagmod.wild_wind_dragon").withStyle(ChatFormatting.WHITE);
        };
    }

    @Override
    public boolean removeWhenFarAway(double distanceSquared) {
        return !isTamed() && this.entityData.get(IS_ABANDONED);
    }

    @Override
    public boolean isPersistenceRequired() {
        return isTamed() || !this.entityData.get(IS_ABANDONED);
    }


    /**
     * Write custom dragon data for persistence
     */
    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);

        output.putInt("DragonVariant", this.getVariant().ordinal());
        output.putBoolean("IsTamed", this.isTamed());
        output.putBoolean("IsSitting", this.isSitting());
        output.putInt("GrowthStage", this.getGrowthStage().ordinal());
        output.putInt("GrowthProgress", this.growthProgress);
        output.putInt("MeatFed", this.entityData.get(MEAT_FED));
        output.putBoolean("IsAbandoned", this.entityData.get(IS_ABANDONED));

        if (this.getOwnerUuid().isPresent()) {
            output.putString("OwnerUUID", this.getOwnerUuid().get().toString());
        }

        DagMod.LOGGER.debug("[SAVE] Saving Wild {} Dragon (variant: {}, tamed: {}, growth: {}, abandoned: {})",
            this.getVariant().name(), this.getVariant().ordinal(), this.isTamed(), this.getGrowthStage().name(), this.entityData.get(IS_ABANDONED));
    }

    /**
     * Read custom dragon data when loading
     */
    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);

        int variantOrdinal = input.getIntOr("DragonVariant", 0);

        if (variantOrdinal >= 0 && variantOrdinal < DragonGuardianEntity.DragonVariant.values().length) {
            DragonGuardianEntity.DragonVariant loadedVariant = DragonGuardianEntity.DragonVariant.values()[variantOrdinal];
            this.setVariant(loadedVariant);
            DagMod.LOGGER.debug("[LOAD] Loaded Wild {} Dragon (variant ordinal: {})",
                loadedVariant.name(), variantOrdinal);
        } else {
            DagMod.LOGGER.warn("[LOAD] Invalid variant ordinal {}, defaulting to RED", variantOrdinal);
            this.setVariant(DragonGuardianEntity.DragonVariant.RED);
        }

        this.setTamed(input.getBooleanOr("IsTamed", false));
        this.setSitting(input.getBooleanOr("IsSitting", false));
        this.growthProgress = input.getIntOr("GrowthProgress", 0);
        this.entityData.set(MEAT_FED, input.getIntOr("MeatFed", 0));
        this.entityData.set(IS_ABANDONED, input.getBooleanOr("IsAbandoned", false));

        int growthStageOrdinal = input.getIntOr("GrowthStage", GrowthStage.ADULT.ordinal());
        if (growthStageOrdinal >= 0 && growthStageOrdinal < GrowthStage.values().length) {
            this.setGrowthStage(GrowthStage.values()[growthStageOrdinal]);
        }

        input.getString("OwnerUUID").ifPresent(uuidString -> {
            if (!uuidString.isEmpty()) {
                try {
                    this.setOwnerUuid(java.util.UUID.fromString(uuidString));
                } catch (Exception e) {
                    DagMod.LOGGER.warn("[LOAD] Failed to load owner UUID: {}", e.getMessage());
                }
            }
        });

        DagMod.LOGGER.debug("[LOAD] Loaded Wild Dragon taming state: tamed={}, growth={}, owner={}, abandoned={}",
            this.isTamed(), this.getGrowthStage().name(), this.getOwnerUuid().isPresent(), this.entityData.get(IS_ABANDONED));
    }

    /**
     * Sit goal - makes tamed dragons sit when commanded
     */
    private static class SitGoal extends Goal {
        private final WildDragonEntity dragon;

        public SitGoal(WildDragonEntity dragon) {
            this.dragon = dragon;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            return dragon.isTamed() && dragon.isSitting();
        }

        @Override
        public boolean canContinueToUse() {
            return dragon.isSitting();
        }
    }

    /**
     * Follow owner goal - makes tamed dragons follow their owner
     */
    private static class FollowOwnerGoal extends Goal {
        private final WildDragonEntity dragon;
        private Player owner;
        private final double speed;
        private final float maxDistance;
        private final float minDistance;
        private int updateCountdownTicks;

        public FollowOwnerGoal(WildDragonEntity dragon, double speed, float maxDistance, float minDistance) {
            this.dragon = dragon;
            this.speed = speed;
            this.maxDistance = maxDistance;
            this.minDistance = minDistance;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (!dragon.isTamed() || dragon.isSitting()) {
                return false;
            }

            if (dragon.getOwnerUuid().isEmpty()) {
                return false;
            }

            this.owner = dragon.level().getPlayerByUUID(dragon.getOwnerUuid().get());
            if (this.owner == null) {
                return false;
            }

            if (this.owner.isSpectator()) {
                return false;
            }

            double distance = dragon.distanceToSqr(owner);
            return distance > (this.minDistance * this.minDistance);
        }

        @Override
        public boolean canContinueToUse() {
            if (this.owner == null || dragon.isSitting()) {
                return false;
            }

            double distance = dragon.distanceToSqr(owner);
            return distance > (this.minDistance * this.minDistance);
        }

        @Override
        public void start() {
            this.updateCountdownTicks = 0;
        }

        @Override
        public void stop() {
            this.owner = null;
        }

        @Override
        public void tick() {
            dragon.getLookControl().setLookAt(owner, 10.0F, dragon.getMaxHeadXRot());

            if (--this.updateCountdownTicks <= 0) {
                this.updateCountdownTicks = 10;

                double distance = dragon.distanceToSqr(owner);

                // Teleport if too far
                if (distance > (maxDistance * 2 * maxDistance * 2)) {
                    dragon.snapTo(
                        owner.getX(), owner.getY(), owner.getZ(),
                        dragon.getYRot(), dragon.getXRot()
                    );
                } else {
                    // Fly towards owner
                    dragon.getNavigation().moveTo(owner, this.speed);
                }
            }
        }
    }

    /**
     * Track owner attacker goal - makes dragon attack entities that hurt the owner
     */
    private static class TrackOwnerAttackerGoal extends Goal {
        private final WildDragonEntity dragon;
        private LivingEntity attacker;
        private int lastAttackedTime;

        public TrackOwnerAttackerGoal(WildDragonEntity dragon) {
            this.dragon = dragon;
            this.setFlags(EnumSet.of(Goal.Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            if (!dragon.isTamed() || dragon.getOwnerUuid().isEmpty()) {
                return false;
            }

            Player owner = dragon.level().getPlayerByUUID(dragon.getOwnerUuid().get());
            if (owner == null) {
                return false;
            }

            this.attacker = owner;
            int timeSinceAttacked = owner.getLastHurtByMobTimestamp();

            return timeSinceAttacked != this.lastAttackedTime &&
                   attacker != null &&
                   dragon.canAttack(attacker);
        }

        @Override
        public void start() {
            dragon.setTarget(this.attacker);
            Player owner = dragon.level().getPlayerByUUID(dragon.getOwnerUuid().get());
            if (owner != null) {
                this.lastAttackedTime = owner.getLastHurtByMobTimestamp();
            }
        }
    }

    /**
     * Attack with owner goal - makes dragon attack what the owner attacks
     */
    private static class AttackWithOwnerGoal extends Goal {
        private final WildDragonEntity dragon;
        private LivingEntity target;
        private int lastAttackTime;

        public AttackWithOwnerGoal(WildDragonEntity dragon) {
            this.dragon = dragon;
            this.setFlags(EnumSet.of(Goal.Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            if (!dragon.isTamed() || dragon.isSitting() || dragon.getOwnerUuid().isEmpty()) {
                return false;
            }

            Player owner = dragon.level().getPlayerByUUID(dragon.getOwnerUuid().get());
            if (owner == null) {
                return false;
            }

            this.target = owner.getLastHurtMob();
            int timeSinceAttacked = owner.getLastHurtMobTimestamp();

            return timeSinceAttacked != this.lastAttackTime &&
                   target != null &&
                   dragon.canAttack(target);
        }

        @Override
        public void start() {
            dragon.setTarget(this.target);
            Player owner = dragon.level().getPlayerByUUID(dragon.getOwnerUuid().get());
            if (owner != null) {
                this.lastAttackTime = owner.getLastHurtMobTimestamp();
            }
        }
    }

    /**
     * Make dragon aggressive towards a specific player
     * Called when player mines egg without Silk Touch
     */
    public void setAngryAt(Player player) {
        this.setTarget(player);
        this.setAggressive(true);
    }

    // Setter for nestPosition, called by DragonSpawner
    public void setNestPosition(BlockPos pos) {
        this.nestPosition = pos;
    }
}