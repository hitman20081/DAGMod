package com.github.hitman20081.dagmod.entity;

import java.util.EnumSet;

import com.github.hitman20081.dagmod.DagMod;
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
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.BossEvent;
import net.minecraft.server.level.ServerBossEvent;
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
import net.minecraft.nbt.CompoundTag;
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
 * Dragon Guardian - Flying dragon that protects mountain peaks
 * Drops Dragon Scales used for crafting Dragon Armor
 * Features: Flying AI, aerial combat, boss bar, fire breath, landing behavior
 */
public class DragonGuardianEntity extends Monster {

    // Animation states
    public enum AnimationState {
        IDLE,           // Slow, majestic flight
        FLYING,         // Normal flight
        ATTACKING,      // Fast aggressive flight
        SWOOPING,       // Diving attack
        ROARING,        // Intimidation display
        FIRE_BREATHING, // Fire attack
        LANDING,        // Landing on perch
        PERCHED         // Stationary on ground (boss only - no egg laying)
    }

    // Dragon variants based on biome/dimension
    public enum DragonVariant implements net.minecraft.util.StringRepresentable {
        RED("red"),      // Red Dragon - Dragon Realm guardian (Dragon Realm dimension)
        ICE("ice"),      // Ice Dragon - Frozen peaks (Overworld - cold biomes)
        LAVA("lava"),    // Lava Dragon - Volcanic regions (Overworld - hot biomes)
        EARTH("earth"),  // Earth Dragon - Mountain ranges (Overworld - mountain biomes)
        WIND("wind");    // Wind Dragon - High altitude (Overworld - mountain peaks)

        private final String name;

        DragonVariant(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    // Growth stages for tamed dragons
    public enum GrowthStage {
        BABY(0.3F, 6000),      // 5 minutes (6000 ticks)
        JUVENILE(0.45F, 12000), // 10 minutes (12000 ticks)
        ADULT(0.6F, 0);         // Fully grown (no more growth)

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

    private static final EntityDataAccessor<Integer> ANIMATION_STATE = SynchedEntityData.defineId(DragonGuardianEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_PERCHED = SynchedEntityData.defineId(DragonGuardianEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(DragonGuardianEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_TAMED = SynchedEntityData.defineId(DragonGuardianEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> OWNER_UUID = SynchedEntityData.defineId(DragonGuardianEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> IS_SITTING = SynchedEntityData.defineId(DragonGuardianEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> GROWTH_STAGE = SynchedEntityData.defineId(DragonGuardianEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MEAT_FED = SynchedEntityData.defineId(DragonGuardianEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_BOSS = SynchedEntityData.defineId(DragonGuardianEntity.class, EntityDataSerializers.BOOLEAN);

    private final ServerBossEvent bossBar;
    private static final int DRAGON_SCALE_DROP_MIN = 3;
    private static final int DRAGON_SCALE_DROP_MAX = 7;

    // Attack cooldowns
    private int fireBreathCooldown = 0;
    private int roarCooldown = 0;
    private int swoopCooldown = 0;

    // Taming and growth system (boss cannot be tamed)
    private int growthProgress = 0;        // Progress towards next growth stage
    private static final int TAMING_CHANCE = 33; // 33% chance per Dragon Heart
    private static final int MEAT_REQUIRED_FOR_TAMING = 96; // 1.5 stacks of raw meat

    // Perching
    private BlockPos perchLocation = null;
    private int perchTime = 0;
    private static final int MAX_PERCH_TIME = 200; // 10 seconds

    // Animation timers
    private int animationTimer = 0;

    public DragonGuardianEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
        this.xpReward = 50;

        // Enable flying
        this.setNoGravity(true);
        this.moveControl = new FlyingMoveControl(this, 20, true);

        // Boss bar for dramatic effect
        this.bossBar = new ServerBossEvent(
                this.getUUID(),
                Component.literal("Dragon").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD),
                BossEvent.BossBarColor.RED,
                BossEvent.BossBarOverlay.NOTCHED_10
        );
        this.bossBar.setDarkenScreen(false);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ANIMATION_STATE, AnimationState.IDLE.ordinal());
        builder.define(IS_PERCHED, false);
        builder.define(VARIANT, DragonVariant.RED.ordinal());

        // Taming system data trackers
        builder.define(IS_TAMED, false);
        builder.define(OWNER_UUID, ""); // Empty string = no owner
        builder.define(IS_SITTING, false);
        builder.define(GROWTH_STAGE, GrowthStage.ADULT.ordinal()); // Wild dragons spawn as adults
        builder.define(MEAT_FED, 0); // Track meat feeding progress for taming
        builder.define(IS_BOSS, false); // Boss flag (set to true for Dragon Realm boss)
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

    public DragonVariant getVariant() {
        return DragonVariant.values()[this.entityData.get(VARIANT)];
    }

    public void setVariant(DragonVariant variant) {
        this.entityData.set(VARIANT, variant.ordinal());
        updateBossBarName();
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
        this.getAttribute(Attributes.SCALE).setBaseValue(stage.getScale());
    }

    private void updateBossBarName() {
        if (this.bossBar != null) {
            this.bossBar.setName(getName());

            // Update boss bar color - purple for boss, variant color for regular dragons
            BossEvent.BossBarColor barColor;
            if (isBoss()) {
                barColor = BossEvent.BossBarColor.PURPLE; // Dragon Guardian boss
            } else {
                barColor = switch (getVariant()) {
                    case RED -> BossEvent.BossBarColor.RED;
                    case ICE -> BossEvent.BossBarColor.BLUE;
                    case LAVA -> BossEvent.BossBarColor.YELLOW; // Closest to orange/lava
                    case EARTH -> BossEvent.BossBarColor.GREEN; // Earth/nature theme
                    case WIND -> BossEvent.BossBarColor.WHITE; // Sky/wind theme
                };
            }
            this.bossBar.setColor(barColor);
        }
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
        this.goalSelector.addGoal(6, new PerchGoal(this));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
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
        private final DragonGuardianEntity dragon;
        private LivingEntity target;
        private int chargingTicks = 0;
        private static final int CHARGE_TIME = 40; // 2 seconds

        public FireBreathGoal(DragonGuardianEntity dragon) {
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
            dragon.level().playSound(null, dragon.blockPosition(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 2.0F, 1.0F);
        }

        @Override
        public void tick() {
            chargingTicks++;
            dragon.getLookControl().setLookAt(target, 30.0F, 30.0F);

            // Spawn fire particles during charging
            if (dragon.level() instanceof ServerLevel serverWorld && chargingTicks % 2 == 0) {
                Vec3 mouthPos = new Vec3(dragon.getX(), dragon.getY(), dragon.getZ()).add(dragon.getViewVector(1.0F).scale(2.0));
                serverWorld.sendParticles(ParticleTypes.FLAME, mouthPos.x, mouthPos.y + 1.0, mouthPos.z, 3, 0.2, 0.2, 0.2, 0.02);
            }

            // Fire the fireball
            if (chargingTicks == CHARGE_TIME) {
                Vec3 lookVec = dragon.getViewVector(1.0F);

                // Spawn fireball from head position (use eye height as head approximation)
                double spawnX = dragon.getX() + lookVec.x * 2.0;
                double spawnY = dragon.getEyeY() + 0.5; // Eye height + small offset for mouth
                double spawnZ = dragon.getZ() + lookVec.z * 2.0;

                // Calculate direction vector toward current target position
                Vec3 targetPos = new Vec3(target.getX(), target.getY() + target.getBbHeight() / 2.0, target.getZ()); // Aim at center mass
                Vec3 spawnPos = new Vec3(spawnX, spawnY, spawnZ);
                Vec3 direction = targetPos.subtract(spawnPos).normalize();

                // Create fireball with proper parameters (world, x, y, z, velocity)
                SmallFireball fireball = new SmallFireball(
                    dragon.level(),
                    spawnX, spawnY, spawnZ,
                    direction
                );
                fireball.setOwner(dragon);
                dragon.level().addFreshEntity(fireball);

                dragon.level().playSound(null, dragon.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.HOSTILE, 1.5F, 1.0F);
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
        private final DragonGuardianEntity dragon;
        private LivingEntity target;
        private Vec3 swoopStart;
        private boolean isSwooping = false;

        public SwoopAttackGoal(DragonGuardianEntity dragon) {
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
            dragon.setDeltaMovement(dragon.getDeltaMovement().multiply(1.5, 0.5, 1.5)); // Speed up horizontally, slow down vertically
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

            dragon.setDeltaMovement(direction.scale(1.5)); // Fast swoop

            // Wing dust particles during swoop
            if (dragon.level() instanceof ServerLevel serverWorld && dragon.tickCount % 2 == 0) {
                serverWorld.sendParticles(ParticleTypes.CLOUD, dragonPos.x, dragonPos.y, dragonPos.z, 2, 0.5, 0.2, 0.5, 0);
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
        private final DragonGuardianEntity dragon;
        private int roarTicks = 0;
        private static final int ROAR_DURATION = 30; // 1.5 seconds

        public RoarGoal(DragonGuardianEntity dragon) {
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
            dragon.level().playSound(null, dragon.blockPosition(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 3.0F, 0.8F);
        }

        @Override
        public void tick() {
            roarTicks++;
            dragon.setDeltaMovement(Vec3.ZERO); // Stationary while roaring

            // Intimidation effect - apply slowness to nearby players
            if (roarTicks == 15) {
                dragon.level().getEntitiesOfClass(Player.class, dragon.getBoundingBox().inflate(10), p -> true)
                        .forEach(player -> {
                            player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                                    net.minecraft.world.effect.MobEffects.SLOWNESS, 60, 1));
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
        private final DragonGuardianEntity dragon;

        public PerchGoal(DragonGuardianEntity dragon) {
            this.dragon = dragon;
        }

        @Override
        public boolean canUse() {
            // Only perch if no target and not already perched
            return dragon.getTarget() == null && !dragon.isPerched() && dragon.random.nextInt(200) == 0;
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
                    Vec3 perchCenter = Vec3.atCenterOf(dragon.perchLocation);
                    dragon.setPos(perchCenter.x, perchCenter.y, perchCenter.z);
                    dragon.setDeltaMovement(Vec3.ZERO);
                    dragon.setPerched(true);
                    dragon.setAnimationState(AnimationState.PERCHED);
                    dragon.perchTime = 0;
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
     * Custom flying melee attack goal
     */
    private static class FlyingMeleeAttackGoal extends Goal {
        private final DragonGuardianEntity dragon;
        private final double speed;
        private int cooldown = 0;

        public FlyingMeleeAttackGoal(DragonGuardianEntity dragon, double speed) {
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
                Vec3 targetPos = new Vec3(target.getX(), target.getY() + 2, target.getZ());
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

    public static AttributeSupplier.Builder createDragonGuardianAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 400.0)          // 400 HP
                .add(Attributes.MOVEMENT_SPEED, 0.18)       // Base speed
                .add(Attributes.FLYING_SPEED, 0.4)          // Flying speed
                .add(Attributes.ATTACK_DAMAGE, 16.0)        // Strong attacks
                .add(Attributes.ARMOR, 16.0)                // Moderate armor
                .add(Attributes.ARMOR_TOUGHNESS, 12.0)      // High toughness counters heavy hits
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)  // Hard to knock back
                .add(Attributes.FOLLOW_RANGE, 48.0)         // Larger range for flying
                .add(Attributes.ATTACK_KNOCKBACK, 1.0)      // Strong knockback
                .add(Attributes.SCALE, 0.6);                // 60% size
    }

    @Override
    public boolean hurtServer(net.minecraft.server.level.ServerLevel world, DamageSource source, float amount) {
        // Immune to fall damage
        if (source.is(DamageTypes.FALL)) {
            return false;
        }
        return super.hurtServer(world, source, amount);
    }

    @Override
    protected void checkFallDamage(double heightDifference, boolean onGround, net.minecraft.world.level.block.state.BlockState state, net.minecraft.core.BlockPos pos) {
        // Don't apply fall damage logic for flying entity
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        updateBossBarName(); // Ensure boss bar shows correct variant name
        this.bossBar.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossBar.removePlayer(player);
    }

    @Override
    public void tick() {
        super.tick();

        // Server-side logic
        if (!this.level().isClientSide()) {
            this.bossBar.setProgress(this.getHealth() / this.getMaxHealth());

            // Decrement cooldowns
            if (this.fireBreathCooldown > 0) this.fireBreathCooldown--;
            if (this.roarCooldown > 0) this.roarCooldown--;
            if (this.swoopCooldown > 0) this.swoopCooldown--;

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

                        // Play level up sound
                        serverWorld.playSound(null, this.blockPosition(),
                            SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, 1.0F, 1.5F);
                    }

                    DagMod.LOGGER.info("Dragon grew from {} to {}", currentStage.name(), nextStage.name());
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

        // Wing flap sounds (both client and server hear it)
        if (this.tickCount % 15 == 0 && getAnimationState() != AnimationState.PERCHED) {
            this.level().playSound(null, this.blockPosition(), SoundEvents.ENDER_DRAGON_FLAP, SoundSource.HOSTILE, 0.5F, 1.0F);
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
                player.sendSystemMessage(Component.literal("This dragon is too old to be tamed!").withStyle(ChatFormatting.RED));
                return InteractionResult.FAIL;
            }

            // Increment meat fed counter
            int currentMeat = this.entityData.get(MEAT_FED);
            currentMeat++;
            this.entityData.set(MEAT_FED, currentMeat);

            // Progress feedback
            int remaining = MEAT_REQUIRED_FOR_TAMING - currentMeat;
            if (remaining > 0) {
                player.sendOverlayMessage(
                    Component.literal("Dragon trusts you more... (" + currentMeat + "/" + MEAT_REQUIRED_FOR_TAMING + " meat fed)")
                        .withStyle(ChatFormatting.YELLOW)
                );

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
                this.setTarget(null); // Stop attacking
                this.entityData.set(MEAT_FED, 0); // Reset counter

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
                // Adult wild dragons cannot be tamed
                player.sendSystemMessage(Component.literal("This dragon is too old to be tamed!").withStyle(ChatFormatting.RED));
                return InteractionResult.FAIL;
            }

            // RandomSource chance to tame (33% per heart)
            if (this.random.nextInt(100) < TAMING_CHANCE) {
                // Successfully tamed!
                this.setTamed(true);
                this.setOwnerUuid(player.getUUID());
                this.setTarget(null); // Stop attacking

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
                    float healAmount = food.nutrition() * 2.0F; // 2 HP per hunger point
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
                // Wing dust trails
                if (this.tickCount % 3 == 0) {
                    double yawRad = Math.toRadians(this.getYRot());
                    Vec3 wingLeft = new Vec3(
                        this.getX() + (-1.5 * Math.cos(yawRad)),
                        this.getY() + 0.5,
                        this.getZ() + (-1.5 * Math.sin(yawRad))
                    );
                    Vec3 wingRight = new Vec3(
                        this.getX() + (1.5 * Math.cos(yawRad)),
                        this.getY() + 0.5,
                        this.getZ() + (1.5 * Math.sin(yawRad))
                    );

                    world.sendParticles(ParticleTypes.CLOUD, wingLeft.x, wingLeft.y, wingLeft.z, 1, 0.0, -0.05, 0.0, 0.0);
                    world.sendParticles(ParticleTypes.CLOUD, wingRight.x, wingRight.y, wingRight.z, 1, 0.0, -0.05, 0.0, 0.0);
                }
            }
            case ROARING -> {
                // Intimidation particles
                if (this.tickCount % 5 == 0) {
                    double radius = 2.0;
                    for (int i = 0; i < 3; i++) {
                        double angle = this.random.nextDouble() * 2 * Math.PI;
                        double x = this.getX() + Math.cos(angle) * radius;
                        double z = this.getZ() + Math.sin(angle) * radius;
                        world.sendParticles(ParticleTypes.ANGRY_VILLAGER, x, this.getY() + 1.5, z, 1, 0.0, 0.1, 0.0, 0.0);
                    }
                }
            }
            case LANDING -> {
                // Dust particles during landing
                if (this.tickCount % 2 == 0 && this.getDeltaMovement().y < -0.1) {
                    world.sendParticles(ParticleTypes.CLOUD, this.getX(), this.getY(), this.getZ(),
                            3, 0.3, 0.1, 0.3, 0.02);
                }
            }
        }
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
        this.bossBar.removeAllPlayers();


        // Death effects and drops
        if (!this.level().isClientSide()) {
            ServerLevel serverWorld = (ServerLevel) this.level();

            // Dramatic death particle explosion
            for (int i = 0; i < 50; i++) {
                double offsetX = this.random.nextGaussian() * 2.0;
                double offsetY = this.random.nextGaussian() * 2.0;
                double offsetZ = this.random.nextGaussian() * 2.0;
                serverWorld.sendParticles(ParticleTypes.POOF,
                        this.getX() + offsetX, this.getY() + 1.0 + offsetY, this.getZ() + offsetZ,
                        5, 0.5, 0.5, 0.5, 0.1);
            }

            // Explosion effect (visual only, no damage)
            serverWorld.sendParticles(ParticleTypes.EXPLOSION,
                    this.getX(), this.getY() + 1.0, this.getZ(),
                    1, 0.0, 0.0, 0.0, 0.0);

            // Play dramatic death sound (Wither death sound for distinction)
            serverWorld.playSound(null, this.blockPosition(), SoundEvents.WITHER_DEATH, SoundSource.HOSTILE, 3.0F, 0.8F);

            // Drop Dragon Scales (3-7)
            int scaleCount = DRAGON_SCALE_DROP_MIN + this.random.nextInt(DRAGON_SCALE_DROP_MAX - DRAGON_SCALE_DROP_MIN + 1);
            ItemStack scaleStack = new ItemStack(ModItems.DRAGON_SCALE, scaleCount);
            ItemEntity scaleEntity = new ItemEntity(serverWorld, this.getX(), this.getY(), this.getZ(), scaleStack);
            serverWorld.addFreshEntity(scaleEntity);

            // Drop Dragon Bones (2-4)
            int boneCount = 2 + this.random.nextInt(3);
            ItemStack boneStack = new ItemStack(ModItems.DRAGON_BONE, boneCount);
            ItemEntity boneEntity = new ItemEntity(serverWorld, this.getX(), this.getY(), this.getZ(), boneStack);
            serverWorld.addFreshEntity(boneEntity);

            // Drop Dragon Skin (1-2)
            int skinCount = 1 + this.random.nextInt(2);
            ItemStack skinStack = new ItemStack(ModItems.DRAGON_SKIN, skinCount);
            ItemEntity skinEntity = new ItemEntity(serverWorld, this.getX(), this.getY(), this.getZ(), skinStack);
            serverWorld.addFreshEntity(skinEntity);

            // Drop Dragon Heart (always 1 - rare crafting material)
            ItemStack heartStack = new ItemStack(ModItems.DRAGON_HEART, 1);
            ItemEntity heartEntity = new ItemEntity(serverWorld, this.getX(), this.getY(), this.getZ(), heartStack);
            serverWorld.addFreshEntity(heartEntity);

            // Drop King's Scale (always 1 - boss-exclusive rare material)
            ItemStack kingsScaleStack = new ItemStack(ModItems.KINGS_SCALE, 1);
            ItemEntity kingsScaleEntity = new ItemEntity(serverWorld, this.getX(), this.getY(), this.getZ(), kingsScaleStack);
            serverWorld.addFreshEntity(kingsScaleEntity);

            // If this is the boss dragon, trigger respawn timer
            if (isBoss()) {
                com.github.hitman20081.dagmod.dragon_realm.boss.DragonGuardianSpawner.onBossDeath(this, serverWorld);
            }

            // Remove this dragon's location from spawn tracking
            DragonSpawner.removeDragonLocation(this.blockPosition());
        }
    }


    @Override
    protected SoundEvent getAmbientSound() {
        // Use dragon sounds for immersion (Ender Dragon sounds)
        return SoundEvents.ENDER_DRAGON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENDER_DRAGON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        // Use Wither death sound for a dramatic, distinct death (not Ender Dragon)
        return SoundEvents.WITHER_DEATH;
    }

    public boolean isBoss() {
        return this.entityData.get(IS_BOSS);
    }

    public void setBoss(boolean isBoss) {
        this.entityData.set(IS_BOSS, isBoss);
        // Update boss bar name when boss status changes
        if (isBoss) {
            updateBossBarName();
        }
    }

    @Override
    public Component getName() {
        // Boss dragon always uses "Dragon Guardian" name
        if (isBoss()) {
            return Component.translatable("entity.dagmod.dragon_guardian").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD);
        }

        // Return variant-specific name with matching color
        return switch (getVariant()) {
            case RED -> Component.translatable("entity.dagmod.red_dragon").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD);
            case ICE -> Component.translatable("entity.dagmod.ice_dragon").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD);
            case LAVA -> Component.translatable("entity.dagmod.lava_dragon").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
            case EARTH -> Component.translatable("entity.dagmod.earth_dragon").withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.BOLD);
            case WIND -> Component.translatable("entity.dagmod.wind_dragon").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD);
        };
    }

    @Override
    public boolean removeWhenFarAway(double distanceSquared) {
        // Dragon Guardians should not despawn (they guard nests permanently)
        return false;
    }

    public boolean isPersistent() {
        // Always persistent
        return true;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
        super.onSyncedDataUpdated(data);

        // When variant data changes, update boss bar to match
        if (VARIANT.equals(data)) {
            updateBossBarName();
        }
    }

    /**
     * Write custom dragon data for persistence.
     */
    @Override
    protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        super.addAdditionalSaveData(output);

        output.putInt("DragonVariant", this.getVariant().ordinal());
        output.putBoolean("IsTamed", this.isTamed());
        output.putBoolean("IsSitting", this.isSitting());
        output.putInt("GrowthStage", this.getGrowthStage().ordinal());
        output.putInt("GrowthProgress", this.growthProgress);
        output.putInt("MeatFed", this.entityData.get(MEAT_FED));

        if (this.getOwnerUuid().isPresent()) {
            output.putString("OwnerUUID", this.getOwnerUuid().get().toString());
        }

        DagMod.LOGGER.debug("[SAVE] Saving {} Dragon (variant ordinal: {}, tamed: {}, growth: {})",
            this.getVariant().name(), this.getVariant().ordinal(), this.isTamed(), this.getGrowthStage().name());
    }

    /**
     * Read custom dragon data when loading.
     */
    @Override
    protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        super.readAdditionalSaveData(input);

        int variantOrdinal = input.getIntOr("DragonVariant", 0);
        if (variantOrdinal >= 0 && variantOrdinal < DragonVariant.values().length) {
            DragonVariant loadedVariant = DragonVariant.values()[variantOrdinal];
            this.setVariant(loadedVariant);
            DagMod.LOGGER.debug("[LOAD] Loaded {} Dragon (variant ordinal: {})",
                loadedVariant.name(), variantOrdinal);
        } else {
            DagMod.LOGGER.warn("[LOAD] Invalid variant ordinal {}, defaulting to RED", variantOrdinal);
            this.setVariant(DragonVariant.RED);
        }

        this.setTamed(input.getBooleanOr("IsTamed", false));
        this.setSitting(input.getBooleanOr("IsSitting", false));
        this.growthProgress = input.getIntOr("GrowthProgress", 0);
        this.entityData.set(MEAT_FED, input.getIntOr("MeatFed", 0));

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

        DagMod.LOGGER.debug("[LOAD] Loaded dragon taming state: tamed={}, growth={}, owner={}",
            this.isTamed(), this.getGrowthStage().name(), this.getOwnerUuid().isPresent());
    }

    /**
     * Sit goal - makes tamed dragons sit when commanded
     */
    private static class SitGoal extends Goal {
        private final DragonGuardianEntity dragon;

        public SitGoal(DragonGuardianEntity dragon) {
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
        private final DragonGuardianEntity dragon;
        private Player owner;
        private final double speed;
        private final float maxDistance;
        private final float minDistance;
        private int updateCountdownTicks;

        public FollowOwnerGoal(DragonGuardianEntity dragon, double speed, float maxDistance, float minDistance) {
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
        private final DragonGuardianEntity dragon;
        private LivingEntity attacker;
        private int lastAttackedTime;

        public TrackOwnerAttackerGoal(DragonGuardianEntity dragon) {
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

            this.attacker = owner.getLastHurtByMob();
            int timeSinceAttacked = owner.getLastHurtByMobTimestamp();

            return timeSinceAttacked != this.lastAttackedTime &&
                   dragon.canAttack(attacker) &&
                   attacker != null;
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
        private final DragonGuardianEntity dragon;
        private LivingEntity target;
        private int lastAttackTime;

        public AttackWithOwnerGoal(DragonGuardianEntity dragon) {
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
                   dragon.canAttack(target) &&
                   target != null;
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
}
