package com.github.hitman20081.dagmod.entity;

import java.util.EnumSet;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.block.DragonEggBlock;
import com.github.hitman20081.dagmod.block.ModBlocks;
import com.github.hitman20081.dagmod.item.ModItems;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Wild Dragon - Tameable flying dragon found in mountains and Dragon Realm
 * Smaller than Dragon Guardian boss, can be tamed from eggs
 * Features: Flying AI, egg laying, nesting, taming, growth stages
 */
public class WildDragonEntity extends HostileEntity {

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

    private static final TrackedData<Integer> ANIMATION_STATE = DataTracker.registerData(WildDragonEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> IS_PERCHED = DataTracker.registerData(WildDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(WildDragonEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> IS_TAMED = DataTracker.registerData(WildDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<String> OWNER_UUID = DataTracker.registerData(WildDragonEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Boolean> IS_SITTING = DataTracker.registerData(WildDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> GROWTH_STAGE = DataTracker.registerData(WildDragonEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> MEAT_FED = DataTracker.registerData(WildDragonEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> IS_ABANDONED = DataTracker.registerData(WildDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);


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

    public WildDragonEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 20; // Less XP than boss

        // Enable flying
        this.setNoGravity(true);
        this.moveControl = new FlightMoveControl(this, 20, true);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(ANIMATION_STATE, AnimationState.IDLE.ordinal());
        builder.add(IS_PERCHED, false);
        builder.add(VARIANT, DragonGuardianEntity.DragonVariant.RED.ordinal());

        // Taming system data trackers
        builder.add(IS_TAMED, false);
        builder.add(OWNER_UUID, ""); // Empty string = no owner
        builder.add(IS_SITTING, false);
        builder.add(GROWTH_STAGE, GrowthStage.ADULT.ordinal()); // Wild dragons spawn as adults
        builder.add(MEAT_FED, 0); // Track meat feeding progress for taming
        builder.add(IS_ABANDONED, false);
    }

    // Animation state management
    public AnimationState getAnimationState() {
        return AnimationState.values()[this.dataTracker.get(ANIMATION_STATE)];
    }

    public void setAnimationState(AnimationState state) {
        this.dataTracker.set(ANIMATION_STATE, state.ordinal());
        this.animationTimer = 0;
    }

    public boolean isPerched() {
        return this.dataTracker.get(IS_PERCHED);
    }

    public void setPerched(boolean perched) {
        this.dataTracker.set(IS_PERCHED, perched);
    }

    public DragonGuardianEntity.DragonVariant getVariant() {
        return DragonGuardianEntity.DragonVariant.values()[this.dataTracker.get(VARIANT)];
    }

    public void setVariant(DragonGuardianEntity.DragonVariant variant) {
        this.dataTracker.set(VARIANT, variant.ordinal());
    }

    // Taming system getters/setters
    public boolean isTamed() {
        return this.dataTracker.get(IS_TAMED);
    }

    public void setTamed(boolean tamed) {
        this.dataTracker.set(IS_TAMED, tamed);
    }

    public java.util.Optional<java.util.UUID> getOwnerUuid() {
        String uuidString = this.dataTracker.get(OWNER_UUID);
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
        this.dataTracker.set(OWNER_UUID, uuid == null ? "" : uuid.toString());
    }

    public boolean isSitting() {
        return this.dataTracker.get(IS_SITTING);
    }

    public void setSitting(boolean sitting) {
        this.dataTracker.set(IS_SITTING, sitting);
    }

    public GrowthStage getGrowthStage() {
        return GrowthStage.values()[this.dataTracker.get(GROWTH_STAGE)];
    }

    public void setGrowthStage(GrowthStage stage) {
        this.dataTracker.set(GROWTH_STAGE, stage.ordinal());
        // Update scale to match growth stage
        this.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.SCALE).setBaseValue(stage.getScale());
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
    protected EntityNavigation createNavigation(World world) {
        // Use bird navigation for flying
        BirdNavigation birdNavigation = new BirdNavigation(this, world);
        birdNavigation.setCanSwim(false);
        return birdNavigation;
    }

    @Override
    protected void initGoals() {
        // Tamed dragon goals (highest priority when tamed)
        this.goalSelector.add(0, new SitGoal(this));
        this.goalSelector.add(1, new FollowOwnerGoal(this, 1.2D, 8.0F, 3.0F));

        // Combat goals (only for wild/attacking dragons)
        this.goalSelector.add(2, new FireBreathGoal(this));
        this.goalSelector.add(3, new SwoopAttackGoal(this));
        this.goalSelector.add(4, new RoarGoal(this));
        this.goalSelector.add(5, new FlyingMeleeAttackGoal(this, 1.2D));

        // Movement and perching
        this.goalSelector.add(6, new ReturnToNestGoal(this));
        this.goalSelector.add(7, new PerchGoal(this));
        this.goalSelector.add(8, new FlyGoal(this, 1.0D));
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 32.0F));
        this.goalSelector.add(10, new LookAroundGoal(this));

        // Target goals
        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(3, new RevengeGoal(this));
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    /**
     * Override to prevent targeting Creative or Spectator mode players,
     * and prevent tamed dragons from attacking their owner
     */
    @Override
    public boolean canTarget(LivingEntity target) {
        // Tamed dragons should not attack their owner
        if (this.isTamed() && target instanceof PlayerEntity player) {
            if (this.getOwnerUuid().isPresent() && this.getOwnerUuid().get().equals(player.getUuid())) {
                return false; // Never attack owner
            }
        }

        // Wild dragons don't attack creative/spectator players
        if (target instanceof PlayerEntity player) {
            if (player.isCreative() || player.isSpectator()) {
                return false;
            }
        }

        return super.canTarget(target);
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
        public boolean canStart() {
            if (dragon.fireBreathCooldown > 0) return false;
            this.target = dragon.getTarget();
            if (target == null) return false;
            double distance = dragon.squaredDistanceTo(target);
            return distance >= 16.0 && distance <= 400.0; // 4-20 blocks
        }

        @Override
        public boolean shouldContinue() {
            return chargingTicks < CHARGE_TIME && target != null && target.isAlive();
        }

        @Override
        public void start() {
            chargingTicks = 0;
            dragon.setAnimationState(AnimationState.FIRE_BREATHING);
            dragon.getEntityWorld().playSound(null, dragon.getBlockPos(), SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.HOSTILE, 1.5F, 1.2F);
        }

        @Override
        public void tick() {
            chargingTicks++;
            dragon.getLookControl().lookAt(target, 30.0F, 30.0F);

            // Spawn fire particles during charging
            if (dragon.getEntityWorld() instanceof ServerWorld serverWorld && chargingTicks % 2 == 0) {
                Vec3d mouthPos = new Vec3d(dragon.getX(), dragon.getY(), dragon.getZ()).add(dragon.getRotationVec(1.0F).multiply(1.5));
                serverWorld.spawnParticles(ParticleTypes.FLAME, mouthPos.x, mouthPos.y + 0.8, mouthPos.z, 2, 0.15, 0.15, 0.15, 0.02);
            }

            // Fire the fireball
            if (chargingTicks == CHARGE_TIME) {
                Vec3d lookVec = dragon.getRotationVec(1.0F);

                // Spawn fireball from head position
                double spawnX = dragon.getX() + lookVec.x * 1.5;
                double spawnY = dragon.getEyeY() + 0.3;
                double spawnZ = dragon.getZ() + lookVec.z * 1.5;

                // Calculate direction vector toward current target position
                Vec3d targetPos = new Vec3d(target.getX(), target.getY() + target.getHeight() / 2.0, target.getZ());
                Vec3d spawnPos = new Vec3d(spawnX, spawnY, spawnZ);
                Vec3d direction = targetPos.subtract(spawnPos).normalize();

                // Create fireball
                SmallFireballEntity fireball = new SmallFireballEntity(
                    dragon.getEntityWorld(),
                    spawnX, spawnY, spawnZ,
                    direction
                );
                fireball.setOwner(dragon);
                dragon.getEntityWorld().spawnEntity(fireball);

                dragon.getEntityWorld().playSound(null, dragon.getBlockPos(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.HOSTILE, 1.0F, 1.0F);
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
        private Vec3d swoopStart;
        private boolean isSwooping = false;

        public SwoopAttackGoal(WildDragonEntity dragon) {
            this.dragon = dragon;
        }

        @Override
        public boolean canStart() {
            if (dragon.swoopCooldown > 0 || dragon.isPerched()) return false;
            this.target = dragon.getTarget();
            if (target == null) return false;
            double distance = dragon.squaredDistanceTo(target);
            // Only swoop if dragon is above target
            return distance < 400.0 && dragon.getY() > target.getY() + 5;
        }

        @Override
        public void start() {
            isSwooping = true;
            swoopStart = new Vec3d(dragon.getX(), dragon.getY(), dragon.getZ());
            dragon.setAnimationState(AnimationState.SWOOPING);
            dragon.setVelocity(dragon.getVelocity().multiply(1.5, 0.5, 1.5));
        }

        @Override
        public void tick() {
            if (target == null || !target.isAlive()) {
                stop();
                return;
            }

            // Dive towards target
            Vec3d targetPos = new Vec3d(target.getX(), target.getY(), target.getZ());
            Vec3d dragonPos = new Vec3d(dragon.getX(), dragon.getY(), dragon.getZ());
            Vec3d direction = targetPos.subtract(dragonPos).normalize();

            dragon.setVelocity(direction.multiply(1.3));

            // Wing dust particles during swoop
            if (dragon.getEntityWorld() instanceof ServerWorld serverWorld && dragon.age % 2 == 0) {
                serverWorld.spawnParticles(ParticleTypes.CLOUD, dragonPos.x, dragonPos.y, dragonPos.z, 2, 0.4, 0.2, 0.4, 0);
            }

            // Attack if close enough
            if (dragon.squaredDistanceTo(target) < 4.0) {
                if (dragon.getEntityWorld() instanceof ServerWorld serverWorld) {
                    dragon.tryAttack(serverWorld, target);
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
        public boolean shouldContinue() {
            return isSwooping && target != null && target.isAlive() && dragon.squaredDistanceTo(target) > 4.0;
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
        public boolean canStart() {
            if (dragon.roarCooldown > 0) return false;
            LivingEntity target = dragon.getTarget();
            // Roar when first acquiring a target
            return target != null && dragon.age - dragon.getLastAttackTime() > 200;
        }

        @Override
        public void start() {
            roarTicks = 0;
            dragon.setAnimationState(AnimationState.ROARING);
            dragon.getEntityWorld().playSound(null, dragon.getBlockPos(), SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.HOSTILE, 2.0F, 1.0F);
        }

        @Override
        public void tick() {
            roarTicks++;
            dragon.setVelocity(Vec3d.ZERO); // Stationary while roaring

            // Intimidation effect - apply slowness to nearby players
            if (roarTicks == 15) {
                dragon.getEntityWorld().getEntitiesByClass(PlayerEntity.class, dragon.getBoundingBox().expand(8), p -> true)
                        .forEach(player -> {
                            player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                                    net.minecraft.entity.effect.StatusEffects.SLOWNESS, 40, 0));
                        });
            }
        }

        @Override
        public void stop() {
            dragon.roarCooldown = 400; // 20 second cooldown
            dragon.setAnimationState(AnimationState.FLYING);
        }

        @Override
        public boolean shouldContinue() {
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
        public boolean canStart() {
            // Only perch if it has a nest, no target, and not already perched
            return dragon.nestPosition != null && dragon.getTarget() == null && !dragon.isPerched() && dragon.random.nextInt(200) == 0;
        }

        @Override
        public void start() {
            // Find a high point nearby to perch on
            BlockPos currentPos = dragon.getBlockPos();
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
                Vec3d targetPos = Vec3d.ofCenter(dragon.perchLocation);
                Vec3d dragonPos = new Vec3d(dragon.getX(), dragon.getY(), dragon.getZ());
                Vec3d direction = targetPos.subtract(dragonPos).normalize().multiply(0.3);
                dragon.setVelocity(direction);

                // Land when close enough
                if (dragon.squaredDistanceTo(Vec3d.ofCenter(dragon.perchLocation)) < 4.0) {
                    dragon.setPosition(Vec3d.ofCenter(dragon.perchLocation));
                    dragon.setVelocity(Vec3d.ZERO);
                    dragon.setPerched(true);
                    dragon.setAnimationState(AnimationState.PERCHED);
                    dragon.perchTime = 0;

                    // Check if landing on a nest (stone/Magma Block platform)
                    BlockPos groundPos = dragon.perchLocation.down();
                    if (dragon.getEntityWorld().getBlockState(groundPos).getBlock() == Blocks.STONE ||
                        dragon.getEntityWorld().getBlockState(groundPos).getBlock() == Blocks.MAGMA_BLOCK) {
                        dragon.nestPosition = groundPos; // Set nest position
                        DagMod.LOGGER.debug("Wild Dragon landed on nest at {}", groundPos);
                    }
                }
            } else {
                // Stay perched
                dragon.perchTime++;
                dragon.setVelocity(Vec3d.ZERO);

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
        public boolean shouldContinue() {
            return dragon.perchLocation != null && dragon.getTarget() == null;
        }

        private BlockPos findPerchLocation(BlockPos center) {
            // Look for a solid block within 20 blocks horizontally, preferring higher locations
            for (int y = 10; y >= -5; y--) {
                for (int x = -20; x <= 20; x += 5) {
                    for (int z = -20; z <= 20; z += 5) {
                        BlockPos testPos = center.add(x, y, z);
                        if (dragon.getEntityWorld().getBlockState(testPos).isSolidBlock(dragon.getEntityWorld(), testPos)
                                && dragon.getEntityWorld().isAir(testPos.up())) {
                            return testPos.up();
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
        public boolean canStart() {
            if (dragon.nestPosition == null || dragon.getTarget() != null || dragon.isPerched()) {
                return false;
            }

            ticksSinceCheck++;
            if (ticksSinceCheck < CHECK_INTERVAL) {
                return false;
            }
            ticksSinceCheck = 0;

            double distance = dragon.squaredDistanceTo(
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
            Vec3d targetPos = Vec3d.ofCenter(dragon.nestPosition).add(0, 3, 0);
            Vec3d dragonPos = new Vec3d(dragon.getX(), dragon.getY(), dragon.getZ());
            double distance = dragonPos.distanceTo(targetPos);

            if (distance < 8.0) {
                stop();
                return;
            }

            Vec3d direction = targetPos.subtract(dragonPos).normalize().multiply(0.25);
            dragon.setVelocity(direction);
            dragon.getLookControl().lookAt(targetPos.x, targetPos.y, targetPos.z);
        }

        @Override
        public void stop() {
            returning = false;
            ticksSinceCheck = 0;
            DagMod.LOGGER.debug("Wild Dragon stopped returning to nest");
        }

        @Override
        public boolean shouldContinue() {
            if (!returning || dragon.nestPosition == null || dragon.getTarget() != null) {
                return false;
            }

            double distance = dragon.squaredDistanceTo(
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
        public boolean canStart() {
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

            double distance = dragon.squaredDistanceTo(target);

            if (distance < 4.0) {
                // Attack range
                if (dragon.getEntityWorld() instanceof ServerWorld serverWorld) {
                    dragon.tryAttack(serverWorld, target);
                }
                cooldown = 20; // 1 second cooldown
            } else if (distance < 256.0) {
                // Chase range - fly towards target
                Vec3d targetPos = new Vec3d(target.getX(), target.getY() + 1.5, target.getZ());
                Vec3d dragonPos = new Vec3d(dragon.getX(), dragon.getY(), dragon.getZ());
                Vec3d direction = targetPos.subtract(dragonPos).normalize().multiply(speed);
                dragon.setVelocity(direction);
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

    public static DefaultAttributeContainer.Builder createWildDragonAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.MAX_HEALTH, 40.0)           // 40 HP (vs boss 200 HP)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.18)       // Base speed
                .add(EntityAttributes.FLYING_SPEED, 0.35)         // Flying speed (slightly slower than boss)
                .add(EntityAttributes.ATTACK_DAMAGE, 7.0)         // Moderate attacks (vs boss 12.0)
                .add(EntityAttributes.ARMOR, 2.0)                 // Light armor (vs boss 12.0)
                .add(EntityAttributes.ARMOR_TOUGHNESS, 0.0)       // No toughness (vs boss 4.0)
                .add(EntityAttributes.KNOCKBACK_RESISTANCE, 0.3)  // Lower resistance (vs boss 0.8)
                .add(EntityAttributes.FOLLOW_RANGE, 40.0)         // Slightly lower range
                .add(EntityAttributes.ATTACK_KNOCKBACK, 0.5)      // Moderate knockback (vs boss 1.0)
                .add(EntityAttributes.SCALE, 0.4);                // 40% size (vs boss 60%)
    }

    @Override
    public boolean damage(net.minecraft.server.world.ServerWorld world, DamageSource source, float amount) {
        // Immune to fall damage
        if (source.isOf(net.minecraft.entity.damage.DamageTypes.FALL)) {
            return false;
        }
        return super.damage(world, source, amount);
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, net.minecraft.block.BlockState state, net.minecraft.util.math.BlockPos pos) {
        // Don't apply fall damage logic for flying entity
    }

    @Override
    public boolean isClimbing() {
        // Never climbing, always flying
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        // Server-side logic
        if (!this.getEntityWorld().isClient()) {
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
                    tryLayEgg((ServerWorld) this.getEntityWorld());
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
                    if (this.getEntityWorld() instanceof ServerWorld serverWorld) {
                        serverWorld.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                            this.getX(), this.getY() + this.getHeight() / 2, this.getZ(),
                            20, 0.5, 0.5, 0.5, 0.1);

                        serverWorld.playSound(null, this.getBlockPos(),
                            SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.NEUTRAL, 1.0F, 1.5F);
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
                            BlockPos checkPos = this.nestPosition.add(x, 1, z);
                            if (this.getEntityWorld().getBlockState(checkPos).getBlock() == ModBlocks.DRAGON_EGG_BLOCK) {
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
                    this.dataTracker.set(IS_ABANDONED, true);
                    this.nestAbandonmentTimer = -1; // Stop timer
                }
            }

            // Update animation state based on velocity if not in special state
            if (getAnimationState() == AnimationState.FLYING || getAnimationState() == AnimationState.IDLE) {
                double velocity = this.getVelocity().length();
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
        if (!this.getEntityWorld().isClient() && this.getEntityWorld() instanceof ServerWorld serverWorld) {
            spawnAmbientParticles(serverWorld);
        }

        // Wing flap sounds
        if (this.age % 15 == 0 && getAnimationState() != AnimationState.PERCHED) {
            this.getEntityWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_ENDER_DRAGON_FLAP, SoundCategory.HOSTILE, 0.4F, 1.2F);
        }
    }

    /**
     * Handle player interactions with the dragon (taming, sitting, feeding)
     */
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        // Only handle on server
        if (this.getEntityWorld().isClient()) {
            return this.isTamed() ? ActionResult.SUCCESS : ActionResult.PASS;
        }

        // Alternative taming with raw meat (only for baby/juvenile dragons)
        if (!this.isTamed() && isRawMeat(itemStack)) {
            if (this.getGrowthStage() == GrowthStage.ADULT) {
                // Adult wild dragons cannot be tamed
                player.sendMessage(Text.literal("This dragon is too old to be tamed!").formatted(Formatting.RED), false);
                return ActionResult.FAIL;
            }

            // Increment meat fed counter
            int currentMeat = this.dataTracker.get(MEAT_FED);
            currentMeat++;
            this.dataTracker.set(MEAT_FED, currentMeat);

            // Progress feedback
            int remaining = MEAT_REQUIRED_FOR_TAMING - currentMeat;
            if (remaining > 0) {
                player.sendMessage(
                    Text.literal("Dragon trusts you more... (" + currentMeat + "/" + MEAT_REQUIRED_FOR_TAMING + " meat fed)")
                        .formatted(Formatting.YELLOW),
                    true
                );

                // Progress particles
                if (this.getEntityWorld() instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(ParticleTypes.HEART,
                        this.getX(), this.getY() + this.getHeight(), this.getZ(),
                        3, 0.3, 0.3, 0.3, 0.0);
                }
            }

            // Check if enough meat has been fed to tame
            if (currentMeat >= MEAT_REQUIRED_FOR_TAMING) {
                // Successfully tamed!
                this.setTamed(true);
                this.setOwnerUuid(player.getUuid());
                this.setTarget(null);
                this.dataTracker.set(MEAT_FED, 0);

                // Heart particles
                if (this.getEntityWorld() instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(ParticleTypes.HEART,
                        this.getX(), this.getY() + this.getHeight(), this.getZ(),
                        20, 0.5, 0.5, 0.5, 0.0);
                }

                this.getEntityWorld().playSound(null, this.getBlockPos(),
                    SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.NEUTRAL, 1.0F, 2.0F);

                player.sendMessage(Text.literal("The dragon has been tamed!").formatted(Formatting.GREEN), false);
            }

            if (!player.isCreative()) {
                itemStack.decrement(1);
            }
            return ActionResult.SUCCESS;
        }

        // Taming with Dragon Heart (only for baby/juvenile dragons)
        if (!this.isTamed() && itemStack.isOf(ModItems.DRAGON_HEART)) {
            if (this.getGrowthStage() == GrowthStage.ADULT) {
                player.sendMessage(Text.literal("This dragon is too old to be tamed!").formatted(Formatting.RED), false);
                return ActionResult.FAIL;
            }

            // Random chance to tame (33% per heart)
            if (this.random.nextInt(100) < TAMING_CHANCE) {
                // Successfully tamed!
                this.setTamed(true);
                this.setOwnerUuid(player.getUuid());
                this.setTarget(null);

                // Heart particles
                if (this.getEntityWorld() instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(ParticleTypes.HEART,
                        this.getX(), this.getY() + this.getHeight(), this.getZ(),
                        15, 0.5, 0.5, 0.5, 0.0);
                }

                this.getEntityWorld().playSound(null, this.getBlockPos(),
                    SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.NEUTRAL, 1.0F, 2.0F);

                player.sendMessage(Text.literal("The dragon has been tamed!").formatted(Formatting.GREEN), false);

                if (!player.isCreative()) {
                    itemStack.decrement(1);
                }
                return ActionResult.SUCCESS;
            } else {
                // Failed, try again
                if (this.getEntityWorld() instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(ParticleTypes.SMOKE,
                        this.getX(), this.getY() + this.getHeight(), this.getZ(),
                        5, 0.3, 0.3, 0.3, 0.0);
                }

                if (!player.isCreative()) {
                    itemStack.decrement(1);
                }
                return ActionResult.CONSUME;
            }
        }

        // Tamed dragon interactions
        if (this.isTamed() && this.getOwnerUuid().isPresent() && this.getOwnerUuid().get().equals(player.getUuid())) {
            // Shift + right-click to toggle sitting
            if (player.isSneaking()) {
                this.setSitting(!this.isSitting());
                player.sendMessage(
                    Text.literal(this.isSitting() ? "Dragon is now sitting" : "Dragon is now following")
                        .formatted(Formatting.YELLOW),
                    true
                );
                return ActionResult.SUCCESS;
            }

            // Feeding system (food items heal the dragon)
            FoodComponent food = itemStack.get(DataComponentTypes.FOOD);
            if (food != null && this.getHealth() < this.getMaxHealth()) {
                    // Heal dragon based on food value
                    float healAmount = food.nutrition() * 2.0F;
                    this.heal(healAmount);

                    // Eating particles and sound
                    if (this.getEntityWorld() instanceof ServerWorld serverWorld) {
                        serverWorld.spawnParticles(ParticleTypes.HEART,
                            this.getX(), this.getY() + this.getHeight() / 2, this.getZ(),
                            5, 0.3, 0.3, 0.3, 0.0);

                        serverWorld.playSound(null, this.getBlockPos(),
                            SoundEvents.ENTITY_GENERIC_EAT.value(), SoundCategory.NEUTRAL, 1.0F, 1.0F);
                    }

                if (!player.isCreative()) {
                    itemStack.decrement(1);
                }
                return ActionResult.SUCCESS;
            }
        }

        return super.interactMob(player, hand);
    }

    /**
     * Check if an item is raw meat that can be used for taming
     */
    private boolean isRawMeat(ItemStack stack) {
        return stack.isOf(Items.BEEF) ||
               stack.isOf(Items.PORKCHOP) ||
               stack.isOf(Items.CHICKEN) ||
               stack.isOf(Items.MUTTON) ||
               stack.isOf(Items.RABBIT) ||
               stack.isOf(Items.COD) ||
               stack.isOf(Items.SALMON);
    }

    /**
     * Spawn ambient particles on server side
     */
    private void spawnAmbientParticles(ServerWorld world) {
        switch (getAnimationState()) {
            case FLYING, ATTACKING -> {
                // Wing dust trails (smaller than boss)
                if (this.age % 4 == 0) {
                    double yawRad = Math.toRadians(this.getYaw());
                    Vec3d wingLeft = new Vec3d(
                        this.getX() + (-1.0 * Math.cos(yawRad)),
                        this.getY() + 0.3,
                        this.getZ() + (-1.0 * Math.sin(yawRad))
                    );
                    Vec3d wingRight = new Vec3d(
                        this.getX() + (1.0 * Math.cos(yawRad)),
                        this.getY() + 0.3,
                        this.getZ() + (1.0 * Math.sin(yawRad))
                    );

                    world.spawnParticles(ParticleTypes.CLOUD, wingLeft.x, wingLeft.y, wingLeft.z, 1, 0.0, -0.05, 0.0, 0.0);
                    world.spawnParticles(ParticleTypes.CLOUD, wingRight.x, wingRight.y, wingRight.z, 1, 0.0, -0.05, 0.0, 0.0);
                }
            }
            case ROARING -> {
                // Intimidation particles
                if (this.age % 6 == 0) {
                    double radius = 1.5;
                    for (int i = 0; i < 2; i++) {
                        double angle = this.random.nextDouble() * 2 * Math.PI;
                        double x = this.getX() + Math.cos(angle) * radius;
                        double z = this.getZ() + Math.sin(angle) * radius;
                        world.spawnParticles(ParticleTypes.ANGRY_VILLAGER, x, this.getY() + 1.0, z, 1, 0.0, 0.1, 0.0, 0.0);
                    }
                }
            }
            case LANDING -> {
                // Dust particles during landing
                if (this.age % 3 == 0 && this.getVelocity().y < -0.1) {
                    world.spawnParticles(ParticleTypes.CLOUD, this.getX(), this.getY(), this.getZ(),
                            2, 0.2, 0.1, 0.2, 0.02);
                }
            }
            case LAYING_EGG -> {
                // Heart and sparkle particles during egg laying
                if (this.age % 10 == 0) {
                    world.spawnParticles(ParticleTypes.HEART, this.getX(), this.getY() + 0.8, this.getZ(),
                            1, 0.3, 0.3, 0.3, 0.0);
                }
                if (this.age % 5 == 0) {
                    world.spawnParticles(ParticleTypes.ENCHANT, this.getX(), this.getY(), this.getZ(),
                            2, 0.4, 0.2, 0.4, 0.01);
                }
            }
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);

        // Death effects and drops
        if (!this.getEntityWorld().isClient()) {
            ServerWorld serverWorld = (ServerWorld) this.getEntityWorld();

            // Death particle explosion
            for (int i = 0; i < 30; i++) {
                double offsetX = this.random.nextGaussian() * 1.5;
                double offsetY = this.random.nextGaussian() * 1.5;
                double offsetZ = this.random.nextGaussian() * 1.5;
                serverWorld.spawnParticles(ParticleTypes.POOF,
                        this.getX() + offsetX, this.getY() + 0.8 + offsetY, this.getZ() + offsetZ,
                        3, 0.3, 0.3, 0.3, 0.1);
            }

            // Explosion effect
            serverWorld.spawnParticles(ParticleTypes.EXPLOSION,
                    this.getX(), this.getY() + 0.8, this.getZ(),
                    1, 0.0, 0.0, 0.0, 0.0);

            // Play death sound
            serverWorld.playSound(null, this.getBlockPos(), SoundEvents.ENTITY_WITHER_DEATH, SoundCategory.HOSTILE, 1.5F, 1.2F);

            // Drop Dragon Scales (2-4)
            int scaleCount = DRAGON_SCALE_DROP_MIN + this.random.nextInt(DRAGON_SCALE_DROP_MAX - DRAGON_SCALE_DROP_MIN + 1);
            ItemStack scaleStack = new ItemStack(ModItems.DRAGON_SCALE, scaleCount);
            ItemEntity scaleEntity = new ItemEntity(serverWorld, this.getX(), this.getY(), this.getZ(), scaleStack);
            serverWorld.spawnEntity(scaleEntity);

            // Drop Dragon Bones (1-2)
            int boneCount = 1 + this.random.nextInt(2);
            ItemStack boneStack = new ItemStack(ModItems.DRAGON_BONE, boneCount);
            ItemEntity boneEntity = new ItemEntity(serverWorld, this.getX(), this.getY(), this.getZ(), boneStack);
            serverWorld.spawnEntity(boneEntity);

            // Drop Dragon Skin (1)
            ItemStack skinStack = new ItemStack(ModItems.DRAGON_SKIN, 1);
            ItemEntity skinEntity = new ItemEntity(serverWorld, this.getX(), this.getY(), this.getZ(), skinStack);
            serverWorld.spawnEntity(skinEntity);

            // 50% chance to drop Dragon Heart
            if (this.random.nextBoolean()) {
                ItemStack heartStack = new ItemStack(ModItems.DRAGON_HEART, 1);
                ItemEntity heartEntity = new ItemEntity(serverWorld, this.getX(), this.getY(), this.getZ(), heartStack);
                serverWorld.spawnEntity(heartEntity);
            }

            // Remove this dragon's location from spawn tracking
            DragonSpawner.removeDragonLocation(this.getBlockPos());
        }
    }

    /**
     * Attempt to lay an egg on the nest
     */
    private void tryLayEgg(ServerWorld world) {
        if (this.nestPosition == null) return;

        // Count existing eggs in nest area
        int eggCount = 0;
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                BlockPos checkPos = this.nestPosition.add(x, 1, z);
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
            BlockPos eggPos = this.nestPosition.add(x, 1, z);

            if (world.getBlockState(eggPos).isAir() && world.getBlockState(eggPos.down()).isOf(Blocks.MAGMA_BLOCK)) {
                // Place the egg with the correct variant in the BlockState (for rendering)
                world.setBlockState(eggPos, ModBlocks.DRAGON_EGG_BLOCK.getDefaultState()
                    .with(DragonEggBlock.VARIANT, this.getVariant()));

                // Set the egg's variant in the BlockEntity as well (for hatching logic)
                if (world.getBlockEntity(eggPos) instanceof DragonEggBlockEntity eggEntity) {
                    eggEntity.setVariant(this.getVariant());
                    eggEntity.markDirty();
                }

                // Particle effects
                world.spawnParticles(ParticleTypes.HEART,
                        eggPos.getX() + 0.5, eggPos.getY() + 0.5, eggPos.getZ() + 0.5,
                        5, 0.3, 0.3, 0.3, 0.0);

                // Success sound
                world.playSound(null, eggPos, SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.NEUTRAL, 1.0F, 0.7F);

                DagMod.LOGGER.info("Wild {} Dragon laid egg at {} (total eggs in nest: {})",
                        this.getVariant().name(), eggPos, eggCount + 1);
                return;
            }
        }

        DagMod.LOGGER.warn("Wild Dragon couldn't find spot to lay egg in nest at {}", this.nestPosition);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ENDER_DRAGON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_ENDER_DRAGON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_WITHER_DEATH;
    }

    @Override
    public Text getName() {
        // Return variant-specific name with matching color
        return switch (getVariant()) {
            case RED -> Text.translatable("entity.dagmod.wild_red_dragon").formatted(Formatting.RED);
            case ICE -> Text.translatable("entity.dagmod.wild_ice_dragon").formatted(Formatting.AQUA);
            case LAVA -> Text.translatable("entity.dagmod.wild_lava_dragon").formatted(Formatting.GOLD);
            case EARTH -> Text.translatable("entity.dagmod.wild_earth_dragon").formatted(Formatting.DARK_GREEN);
            case WIND -> Text.translatable("entity.dagmod.wild_wind_dragon").formatted(Formatting.WHITE);
        };
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return !isTamed() && this.dataTracker.get(IS_ABANDONED);
    }

    @Override
    public boolean isPersistent() {
        return isTamed() || !this.dataTracker.get(IS_ABANDONED);
    }


    /**
     * Write custom dragon data for persistence
     */
    @Override
    protected void writeCustomData(net.minecraft.storage.WriteView writeView) {
        super.writeCustomData(writeView);

        writeView.putInt("DragonVariant", this.getVariant().ordinal());
        writeView.putBoolean("IsTamed", this.isTamed());
        writeView.putBoolean("IsSitting", this.isSitting());
        writeView.putInt("GrowthStage", this.getGrowthStage().ordinal());
        writeView.putInt("GrowthProgress", this.growthProgress);
        writeView.putInt("MeatFed", this.dataTracker.get(MEAT_FED));
        writeView.putBoolean("IsAbandoned", this.dataTracker.get(IS_ABANDONED));


        if (this.getOwnerUuid().isPresent()) {
            writeView.putString("OwnerUUID", this.getOwnerUuid().get().toString());
        }

        DagMod.LOGGER.info("[SAVE] Saving Wild {} Dragon (variant: {}, tamed: {}, growth: {}, abandoned: {})",
            this.getVariant().name(), this.getVariant().ordinal(), this.isTamed(), this.getGrowthStage().name(), this.dataTracker.get(IS_ABANDONED));
    }

    /**
     * Read custom dragon data when loading
     */
    @Override
    protected void readCustomData(net.minecraft.storage.ReadView readView) {
        super.readCustomData(readView);

        int variantOrdinal = readView.getInt("DragonVariant", 0);

        if (variantOrdinal >= 0 && variantOrdinal < DragonGuardianEntity.DragonVariant.values().length) {
            DragonGuardianEntity.DragonVariant loadedVariant = DragonGuardianEntity.DragonVariant.values()[variantOrdinal];
            this.setVariant(loadedVariant);
            DagMod.LOGGER.info("[LOAD] Loaded Wild {} Dragon (variant ordinal: {})",
                loadedVariant.name(), variantOrdinal);
        } else {
            DagMod.LOGGER.warn("[LOAD] Invalid variant ordinal {}, defaulting to RED", variantOrdinal);
            this.setVariant(DragonGuardianEntity.DragonVariant.RED);
        }

        this.setTamed(readView.getBoolean("IsTamed", false));
        this.setSitting(readView.getBoolean("IsSitting", false));
        this.growthProgress = readView.getInt("GrowthProgress", 0);
        this.dataTracker.set(MEAT_FED, readView.getInt("MeatFed", 0));
        this.dataTracker.set(IS_ABANDONED, readView.getBoolean("IsAbandoned", false));


        int growthStageOrdinal = readView.getInt("GrowthStage", GrowthStage.ADULT.ordinal());
        if (growthStageOrdinal >= 0 && growthStageOrdinal < GrowthStage.values().length) {
            this.setGrowthStage(GrowthStage.values()[growthStageOrdinal]);
        }

        if (readView.contains("OwnerUUID")) {
            try {
                String uuidString = readView.getString("OwnerUUID", "");
                if (uuidString != null && !uuidString.isEmpty()) {
                    java.util.UUID ownerUuid = java.util.UUID.fromString(uuidString);
                    this.setOwnerUuid(ownerUuid);
                }
            } catch (Exception e) {
                DagMod.LOGGER.warn("[LOAD] Failed to load owner UUID: {}", e.getMessage());
            }
        }

        DagMod.LOGGER.info("[LOAD] Loaded Wild Dragon taming state: tamed={}, growth={}, owner={}, abandoned={}",
            this.isTamed(), this.getGrowthStage().name(), this.getOwnerUuid().isPresent(), this.dataTracker.get(IS_ABANDONED));
    }

    /**
     * Sit goal - makes tamed dragons sit when commanded
     */
    private static class SitGoal extends Goal {
        private final WildDragonEntity dragon;

        public SitGoal(WildDragonEntity dragon) {
            this.dragon = dragon;
            this.setControls(EnumSet.of(Control.MOVE, Control.JUMP));
        }

        @Override
        public boolean canStart() {
            return dragon.isTamed() && dragon.isSitting();
        }

        @Override
        public boolean shouldContinue() {
            return dragon.isSitting();
        }
    }

    /**
     * Follow owner goal - makes tamed dragons follow their owner
     */
    private static class FollowOwnerGoal extends Goal {
        private final WildDragonEntity dragon;
        private PlayerEntity owner;
        private final double speed;
        private final float maxDistance;
        private final float minDistance;
        private int updateCountdownTicks;

        public FollowOwnerGoal(WildDragonEntity dragon, double speed, float maxDistance, float minDistance) {
            this.dragon = dragon;
            this.speed = speed;
            this.maxDistance = maxDistance;
            this.minDistance = minDistance;
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            if (!dragon.isTamed() || dragon.isSitting()) {
                return false;
            }

            if (dragon.getOwnerUuid().isEmpty()) {
                return false;
            }

            this.owner = dragon.getEntityWorld().getPlayerByUuid(dragon.getOwnerUuid().get());
            if (this.owner == null) {
                return false;
            }

            if (this.owner.isSpectator()) {
                return false;
            }

            double distance = dragon.squaredDistanceTo(owner);
            return distance > (this.minDistance * this.minDistance);
        }

        @Override
        public boolean shouldContinue() {
            if (this.owner == null || dragon.isSitting()) {
                return false;
            }

            double distance = dragon.squaredDistanceTo(owner);
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
            dragon.getLookControl().lookAt(owner, 10.0F, dragon.getMaxLookPitchChange());

            if (--this.updateCountdownTicks <= 0) {
                this.updateCountdownTicks = 10;

                double distance = dragon.squaredDistanceTo(owner);

                // Teleport if too far
                if (distance > (maxDistance * 2 * maxDistance * 2)) {
                    dragon.refreshPositionAndAngles(
                        owner.getX(), owner.getY(), owner.getZ(),
                        dragon.getYaw(), dragon.getPitch()
                    );
                } else {
                    // Fly towards owner
                    dragon.getNavigation().startMovingTo(owner, this.speed);
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
            this.setControls(EnumSet.of(Control.TARGET));
        }

        @Override
        public boolean canStart() {
            if (!dragon.isTamed() || dragon.getOwnerUuid().isEmpty()) {
                return false;
            }

            PlayerEntity owner = dragon.getEntityWorld().getPlayerByUuid(dragon.getOwnerUuid().get());
            if (owner == null) {
                return false;
            }

            this.attacker = owner.getAttacker();
            int timeSinceAttacked = owner.getLastAttackedTime();

            return timeSinceAttacked != this.lastAttackedTime &&
                   dragon.canTarget(attacker) &&
                   attacker != null;
        }

        @Override
        public void start() {
            dragon.setTarget(this.attacker);
            PlayerEntity owner = dragon.getEntityWorld().getPlayerByUuid(dragon.getOwnerUuid().get());
            if (owner != null) {
                this.lastAttackedTime = owner.getLastAttackedTime();
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
            this.setControls(EnumSet.of(Control.TARGET));
        }

        @Override
        public boolean canStart() {
            if (!dragon.isTamed() || dragon.isSitting() || dragon.getOwnerUuid().isEmpty()) {
                return false;
            }

            PlayerEntity owner = dragon.getEntityWorld().getPlayerByUuid(dragon.getOwnerUuid().get());
            if (owner == null) {
                return false;
            }

            this.target = owner.getAttacking();
            int timeSinceAttacked = owner.getLastAttackTime();

            return timeSinceAttacked != this.lastAttackTime &&
                   dragon.canTarget(target) &&
                   target != null;
        }

        @Override
        public void start() {
            dragon.setTarget(this.target);
            PlayerEntity owner = dragon.getEntityWorld().getPlayerByUuid(dragon.getOwnerUuid().get());
            if (owner != null) {
                this.lastAttackTime = owner.getLastAttackTime();
            }
        }
    }

    /**
     * Make dragon aggressive towards a specific player
     * Called when player mines egg without Silk Touch
     */
    public void setAngryAt(PlayerEntity player) {
        this.setTarget(player);
        this.setAttacking(true);
    }

    // Setter for nestPosition, called by DragonSpawner
    public void setNestPosition(BlockPos pos) {
        this.nestPosition = pos;
    }
}