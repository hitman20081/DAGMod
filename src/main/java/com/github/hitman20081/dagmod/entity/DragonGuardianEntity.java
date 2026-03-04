package com.github.hitman20081.dagmod.entity;

import java.util.EnumSet;

import com.github.hitman20081.dagmod.DagMod;
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
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
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
 * Dragon Guardian - Flying dragon that protects mountain peaks
 * Drops Dragon Scales used for crafting Dragon Armor
 * Features: Flying AI, aerial combat, boss bar, fire breath, landing behavior
 */
public class DragonGuardianEntity extends HostileEntity {

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
    public enum DragonVariant implements net.minecraft.util.StringIdentifiable {
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
        public String asString() {
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

    private static final TrackedData<Integer> ANIMATION_STATE = DataTracker.registerData(DragonGuardianEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> IS_PERCHED = DataTracker.registerData(DragonGuardianEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(DragonGuardianEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> IS_TAMED = DataTracker.registerData(DragonGuardianEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<String> OWNER_UUID = DataTracker.registerData(DragonGuardianEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Boolean> IS_SITTING = DataTracker.registerData(DragonGuardianEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> GROWTH_STAGE = DataTracker.registerData(DragonGuardianEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> MEAT_FED = DataTracker.registerData(DragonGuardianEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> IS_BOSS = DataTracker.registerData(DragonGuardianEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private final ServerBossBar bossBar;
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

    public DragonGuardianEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 50;

        // Enable flying
        this.setNoGravity(true);
        this.moveControl = new FlightMoveControl(this, 20, true);

        // Boss bar for dramatic effect
        this.bossBar = new ServerBossBar(
                Text.literal("Dragon").formatted(Formatting.DARK_RED, Formatting.BOLD),
                BossBar.Color.RED,
                BossBar.Style.NOTCHED_10
        );
        this.bossBar.setDarkenSky(false);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(ANIMATION_STATE, AnimationState.IDLE.ordinal());
        builder.add(IS_PERCHED, false);
        builder.add(VARIANT, DragonVariant.RED.ordinal());

        // Taming system data trackers
        builder.add(IS_TAMED, false);
        builder.add(OWNER_UUID, ""); // Empty string = no owner
        builder.add(IS_SITTING, false);
        builder.add(GROWTH_STAGE, GrowthStage.ADULT.ordinal()); // Wild dragons spawn as adults
        builder.add(MEAT_FED, 0); // Track meat feeding progress for taming
        builder.add(IS_BOSS, false); // Boss flag (set to true for Dragon Realm boss)
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

    public DragonVariant getVariant() {
        return DragonVariant.values()[this.dataTracker.get(VARIANT)];
    }

    public void setVariant(DragonVariant variant) {
        this.dataTracker.set(VARIANT, variant.ordinal());
        updateBossBarName();
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

    private void updateBossBarName() {
        if (this.bossBar != null) {
            this.bossBar.setName(getName());

            // Update boss bar color - purple for boss, variant color for regular dragons
            BossBar.Color barColor;
            if (isBoss()) {
                barColor = BossBar.Color.PURPLE; // Dragon Guardian boss
            } else {
                barColor = switch (getVariant()) {
                    case RED -> BossBar.Color.RED;
                    case ICE -> BossBar.Color.BLUE;
                    case LAVA -> BossBar.Color.YELLOW; // Closest to orange/lava
                    case EARTH -> BossBar.Color.GREEN; // Earth/nature theme
                    case WIND -> BossBar.Color.WHITE; // Sky/wind theme
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
        this.goalSelector.add(6, new PerchGoal(this));
        this.goalSelector.add(7, new FlyGoal(this, 1.0D));
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
        private final DragonGuardianEntity dragon;
        private LivingEntity target;
        private int chargingTicks = 0;
        private static final int CHARGE_TIME = 40; // 2 seconds

        public FireBreathGoal(DragonGuardianEntity dragon) {
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
            dragon.getEntityWorld().playSound(null, dragon.getBlockPos(), SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.HOSTILE, 2.0F, 1.0F);
        }

        @Override
        public void tick() {
            chargingTicks++;
            dragon.getLookControl().lookAt(target, 30.0F, 30.0F);

            // Spawn fire particles during charging
            if (dragon.getEntityWorld() instanceof ServerWorld serverWorld && chargingTicks % 2 == 0) {
                Vec3d mouthPos = new Vec3d(dragon.getX(), dragon.getY(), dragon.getZ()).add(dragon.getRotationVec(1.0F).multiply(2.0));
                serverWorld.spawnParticles(ParticleTypes.FLAME, mouthPos.x, mouthPos.y + 1.0, mouthPos.z, 3, 0.2, 0.2, 0.2, 0.02);
            }

            // Fire the fireball
            if (chargingTicks == CHARGE_TIME) {
                Vec3d lookVec = dragon.getRotationVec(1.0F);

                // Spawn fireball from head position (use eye height as head approximation)
                double spawnX = dragon.getX() + lookVec.x * 2.0;
                double spawnY = dragon.getEyeY() + 0.5; // Eye height + small offset for mouth
                double spawnZ = dragon.getZ() + lookVec.z * 2.0;

                // Calculate direction vector toward current target position
                Vec3d targetPos = new Vec3d(target.getX(), target.getY() + target.getHeight() / 2.0, target.getZ()); // Aim at center mass
                Vec3d spawnPos = new Vec3d(spawnX, spawnY, spawnZ);
                Vec3d direction = targetPos.subtract(spawnPos).normalize();

                // Create fireball with proper parameters (world, x, y, z, velocity)
                SmallFireballEntity fireball = new SmallFireballEntity(
                    dragon.getEntityWorld(),
                    spawnX, spawnY, spawnZ,
                    direction
                );
                fireball.setOwner(dragon);
                dragon.getEntityWorld().spawnEntity(fireball);

                dragon.getEntityWorld().playSound(null, dragon.getBlockPos(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.HOSTILE, 1.5F, 1.0F);
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
        private Vec3d swoopStart;
        private boolean isSwooping = false;

        public SwoopAttackGoal(DragonGuardianEntity dragon) {
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
            dragon.setVelocity(dragon.getVelocity().multiply(1.5, 0.5, 1.5)); // Speed up horizontally, slow down vertically
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

            dragon.setVelocity(direction.multiply(1.5)); // Fast swoop

            // Wing dust particles during swoop
            if (dragon.getEntityWorld() instanceof ServerWorld serverWorld && dragon.age % 2 == 0) {
                serverWorld.spawnParticles(ParticleTypes.CLOUD, dragonPos.x, dragonPos.y, dragonPos.z, 2, 0.5, 0.2, 0.5, 0);
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
        private final DragonGuardianEntity dragon;
        private int roarTicks = 0;
        private static final int ROAR_DURATION = 30; // 1.5 seconds

        public RoarGoal(DragonGuardianEntity dragon) {
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
            dragon.getEntityWorld().playSound(null, dragon.getBlockPos(), SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.HOSTILE, 3.0F, 0.8F);
        }

        @Override
        public void tick() {
            roarTicks++;
            dragon.setVelocity(Vec3d.ZERO); // Stationary while roaring

            // Intimidation effect - apply slowness to nearby players
            if (roarTicks == 15) {
                dragon.getEntityWorld().getEntitiesByClass(PlayerEntity.class, dragon.getBoundingBox().expand(10), p -> true)
                        .forEach(player -> {
                            player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                                    net.minecraft.entity.effect.StatusEffects.SLOWNESS, 60, 1));
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
        private final DragonGuardianEntity dragon;

        public PerchGoal(DragonGuardianEntity dragon) {
            this.dragon = dragon;
        }

        @Override
        public boolean canStart() {
            // Only perch if no target and not already perched
            return dragon.getTarget() == null && !dragon.isPerched() && dragon.random.nextInt(200) == 0;
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
                Vec3d targetPos = new Vec3d(target.getX(), target.getY() + 2, target.getZ());
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

    public static DefaultAttributeContainer.Builder createDragonGuardianAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.MAX_HEALTH, 300.0)          // 300 HP (up from 200)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.18)       // Base speed
                .add(EntityAttributes.FLYING_SPEED, 0.4)          // Flying speed
                .add(EntityAttributes.ATTACK_DAMAGE, 12.0)        // Strong attacks
                .add(EntityAttributes.ARMOR, 12.0)                // Moderate armor
                .add(EntityAttributes.ARMOR_TOUGHNESS, 10.0)      // High toughness counters heavy hits (up from 4)
                .add(EntityAttributes.KNOCKBACK_RESISTANCE, 0.8)  // Hard to knock back
                .add(EntityAttributes.FOLLOW_RANGE, 48.0)         // Larger range for flying
                .add(EntityAttributes.ATTACK_KNOCKBACK, 1.0)      // Strong knockback
                .add(EntityAttributes.SCALE, 0.6);                // 60% size
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
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        updateBossBarName(); // Ensure boss bar shows correct variant name
        this.bossBar.addPlayer(player);
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        this.bossBar.removePlayer(player);
    }

    @Override
    public void tick() {
        super.tick();

        // Server-side logic
        if (!this.getEntityWorld().isClient()) {
            this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());

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
                    if (this.getEntityWorld() instanceof ServerWorld serverWorld) {
                        serverWorld.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                            this.getX(), this.getY() + this.getHeight() / 2, this.getZ(),
                            20, 0.5, 0.5, 0.5, 0.1);

                        // Play level up sound
                        serverWorld.playSound(null, this.getBlockPos(),
                            SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.NEUTRAL, 1.0F, 1.5F);
                    }

                    DagMod.LOGGER.info("Dragon grew from {} to {}", currentStage.name(), nextStage.name());
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

        // Wing flap sounds (both client and server hear it)
        if (this.age % 15 == 0 && getAnimationState() != AnimationState.PERCHED) {
            this.getEntityWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_ENDER_DRAGON_FLAP, SoundCategory.HOSTILE, 0.5F, 1.0F);
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
                    true // Action bar
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
                this.setTarget(null); // Stop attacking
                this.dataTracker.set(MEAT_FED, 0); // Reset counter

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
                // Adult wild dragons cannot be tamed
                player.sendMessage(Text.literal("This dragon is too old to be tamed!").formatted(Formatting.RED), false);
                return ActionResult.FAIL;
            }

            // Random chance to tame (33% per heart)
            if (this.random.nextInt(100) < TAMING_CHANCE) {
                // Successfully tamed!
                this.setTamed(true);
                this.setOwnerUuid(player.getUuid());
                this.setTarget(null); // Stop attacking

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
                    float healAmount = food.nutrition() * 2.0F; // 2 HP per hunger point
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
                // Wing dust trails
                if (this.age % 3 == 0) {
                    double yawRad = Math.toRadians(this.getYaw());
                    Vec3d wingLeft = new Vec3d(
                        this.getX() + (-1.5 * Math.cos(yawRad)),
                        this.getY() + 0.5,
                        this.getZ() + (-1.5 * Math.sin(yawRad))
                    );
                    Vec3d wingRight = new Vec3d(
                        this.getX() + (1.5 * Math.cos(yawRad)),
                        this.getY() + 0.5,
                        this.getZ() + (1.5 * Math.sin(yawRad))
                    );

                    world.spawnParticles(ParticleTypes.CLOUD, wingLeft.x, wingLeft.y, wingLeft.z, 1, 0.0, -0.05, 0.0, 0.0);
                    world.spawnParticles(ParticleTypes.CLOUD, wingRight.x, wingRight.y, wingRight.z, 1, 0.0, -0.05, 0.0, 0.0);
                }
            }
            case ROARING -> {
                // Intimidation particles
                if (this.age % 5 == 0) {
                    double radius = 2.0;
                    for (int i = 0; i < 3; i++) {
                        double angle = this.random.nextDouble() * 2 * Math.PI;
                        double x = this.getX() + Math.cos(angle) * radius;
                        double z = this.getZ() + Math.sin(angle) * radius;
                        world.spawnParticles(ParticleTypes.ANGRY_VILLAGER, x, this.getY() + 1.5, z, 1, 0.0, 0.1, 0.0, 0.0);
                    }
                }
            }
            case LANDING -> {
                // Dust particles during landing
                if (this.age % 2 == 0 && this.getVelocity().y < -0.1) {
                    world.spawnParticles(ParticleTypes.CLOUD, this.getX(), this.getY(), this.getZ(),
                            3, 0.3, 0.1, 0.3, 0.02);
                }
            }
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        this.bossBar.clearPlayers();

        // Death effects and drops
        if (!this.getEntityWorld().isClient()) {
            ServerWorld serverWorld = (ServerWorld) this.getEntityWorld();

            // Dramatic death particle explosion
            for (int i = 0; i < 50; i++) {
                double offsetX = this.random.nextGaussian() * 2.0;
                double offsetY = this.random.nextGaussian() * 2.0;
                double offsetZ = this.random.nextGaussian() * 2.0;
                serverWorld.spawnParticles(ParticleTypes.POOF,
                        this.getX() + offsetX, this.getY() + 1.0 + offsetY, this.getZ() + offsetZ,
                        5, 0.5, 0.5, 0.5, 0.1);
            }

            // Explosion effect (visual only, no damage)
            serverWorld.spawnParticles(ParticleTypes.EXPLOSION,
                    this.getX(), this.getY() + 1.0, this.getZ(),
                    1, 0.0, 0.0, 0.0, 0.0);

            // Play dramatic death sound (Wither death sound for distinction)
            serverWorld.playSound(null, this.getBlockPos(), SoundEvents.ENTITY_WITHER_DEATH, SoundCategory.HOSTILE, 3.0F, 0.8F);

            // Drop Dragon Scales (3-7)
            int scaleCount = DRAGON_SCALE_DROP_MIN + this.random.nextInt(DRAGON_SCALE_DROP_MAX - DRAGON_SCALE_DROP_MIN + 1);
            ItemStack scaleStack = new ItemStack(ModItems.DRAGON_SCALE, scaleCount);
            ItemEntity scaleEntity = new ItemEntity(serverWorld, this.getX(), this.getY(), this.getZ(), scaleStack);
            serverWorld.spawnEntity(scaleEntity);

            // Drop Dragon Bones (2-4)
            int boneCount = 2 + this.random.nextInt(3);
            ItemStack boneStack = new ItemStack(ModItems.DRAGON_BONE, boneCount);
            ItemEntity boneEntity = new ItemEntity(serverWorld, this.getX(), this.getY(), this.getZ(), boneStack);
            serverWorld.spawnEntity(boneEntity);

            // Drop Dragon Skin (1-2)
            int skinCount = 1 + this.random.nextInt(2);
            ItemStack skinStack = new ItemStack(ModItems.DRAGON_SKIN, skinCount);
            ItemEntity skinEntity = new ItemEntity(serverWorld, this.getX(), this.getY(), this.getZ(), skinStack);
            serverWorld.spawnEntity(skinEntity);

            // Drop Dragon Heart (always 1 - rare crafting material)
            ItemStack heartStack = new ItemStack(ModItems.DRAGON_HEART, 1);
            ItemEntity heartEntity = new ItemEntity(serverWorld, this.getX(), this.getY(), this.getZ(), heartStack);
            serverWorld.spawnEntity(heartEntity);

            // Drop King's Scale (always 1 - boss-exclusive rare material)
            ItemStack kingsScaleStack = new ItemStack(ModItems.KINGS_SCALE, 1);
            ItemEntity kingsScaleEntity = new ItemEntity(serverWorld, this.getX(), this.getY(), this.getZ(), kingsScaleStack);
            serverWorld.spawnEntity(kingsScaleEntity);

            // If this is the boss dragon, trigger respawn timer
            if (isBoss()) {
                com.github.hitman20081.dagmod.dragon_realm.boss.DragonGuardianSpawner.onBossDeath(this, serverWorld);
            }

            // Remove this dragon's location from spawn tracking
            DragonSpawner.removeDragonLocation(this.getBlockPos());
        }
    }


    @Override
    protected SoundEvent getAmbientSound() {
        // Use dragon sounds for immersion (Ender Dragon sounds)
        return SoundEvents.ENTITY_ENDER_DRAGON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_ENDER_DRAGON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        // Use Wither death sound for a dramatic, distinct death (not Ender Dragon)
        return SoundEvents.ENTITY_WITHER_DEATH;
    }

    public boolean isBoss() {
        return this.dataTracker.get(IS_BOSS);
    }

    public void setBoss(boolean isBoss) {
        this.dataTracker.set(IS_BOSS, isBoss);
        // Update boss bar name when boss status changes
        if (isBoss) {
            updateBossBarName();
        }
    }

    @Override
    public Text getName() {
        // Boss dragon always uses "Dragon Guardian" name
        if (isBoss()) {
            return Text.translatable("entity.dagmod.dragon_guardian").formatted(Formatting.DARK_PURPLE, Formatting.BOLD);
        }

        // Return variant-specific name with matching color
        return switch (getVariant()) {
            case RED -> Text.translatable("entity.dagmod.red_dragon").formatted(Formatting.DARK_RED, Formatting.BOLD);
            case ICE -> Text.translatable("entity.dagmod.ice_dragon").formatted(Formatting.AQUA, Formatting.BOLD);
            case LAVA -> Text.translatable("entity.dagmod.lava_dragon").formatted(Formatting.GOLD, Formatting.BOLD);
            case EARTH -> Text.translatable("entity.dagmod.earth_dragon").formatted(Formatting.DARK_GREEN, Formatting.BOLD);
            case WIND -> Text.translatable("entity.dagmod.wind_dragon").formatted(Formatting.WHITE, Formatting.BOLD);
        };
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        // Dragon Guardians should not despawn (they guard nests permanently)
        return false;
    }

    @Override
    public boolean isPersistent() {
        // Always persistent
        return true;
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);

        // When variant data changes, update boss bar to match
        if (VARIANT.equals(data)) {
            updateBossBarName();
        }
    }

    /**
     * Write custom dragon data for persistence (Minecraft 1.21 API)
     */
    @Override
    protected void writeCustomData(net.minecraft.storage.WriteView writeView) {
        super.writeCustomData(writeView);

        // Save dragon variant
        writeView.putInt("DragonVariant", this.getVariant().ordinal());

        // Save taming data
        writeView.putBoolean("IsTamed", this.isTamed());
        writeView.putBoolean("IsSitting", this.isSitting());
        writeView.putInt("GrowthStage", this.getGrowthStage().ordinal());
        writeView.putInt("GrowthProgress", this.growthProgress);
        writeView.putInt("MeatFed", this.dataTracker.get(MEAT_FED));

        // Save owner UUID if present
        if (this.getOwnerUuid().isPresent()) {
            writeView.putString("OwnerUUID", this.getOwnerUuid().get().toString());
        }

        DagMod.LOGGER.debug("[SAVE] Saving {} Dragon (variant ordinal: {}, tamed: {}, growth: {})",
            this.getVariant().name(), this.getVariant().ordinal(), this.isTamed(), this.getGrowthStage().name());
    }

    /**
     * Read custom dragon data when loading (Minecraft 1.21 API)
     */
    @Override
    protected void readCustomData(net.minecraft.storage.ReadView readView) {
        super.readCustomData(readView);

        // Load dragon variant with fallback to RED (ordinal 0) if not found
        int variantOrdinal = readView.getInt("DragonVariant", 0);

        if (variantOrdinal >= 0 && variantOrdinal < DragonVariant.values().length) {
            DragonVariant loadedVariant = DragonVariant.values()[variantOrdinal];
            this.setVariant(loadedVariant);
            DagMod.LOGGER.debug("[LOAD] Loaded {} Dragon (variant ordinal: {})",
                loadedVariant.name(), variantOrdinal);
        } else {
            DagMod.LOGGER.warn("[LOAD] Invalid variant ordinal {}, defaulting to RED", variantOrdinal);
            this.setVariant(DragonVariant.RED);
        }

        // Load taming data
        this.setTamed(readView.getBoolean("IsTamed", false));
        this.setSitting(readView.getBoolean("IsSitting", false));
        this.growthProgress = readView.getInt("GrowthProgress", 0);
        this.dataTracker.set(MEAT_FED, readView.getInt("MeatFed", 0));

        // Load growth stage
        int growthStageOrdinal = readView.getInt("GrowthStage", GrowthStage.ADULT.ordinal());
        if (growthStageOrdinal >= 0 && growthStageOrdinal < GrowthStage.values().length) {
            this.setGrowthStage(GrowthStage.values()[growthStageOrdinal]);
        }

        // Load owner UUID if present
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
        private final DragonGuardianEntity dragon;
        private PlayerEntity owner;
        private final double speed;
        private final float maxDistance;
        private final float minDistance;
        private int updateCountdownTicks;

        public FollowOwnerGoal(DragonGuardianEntity dragon, double speed, float maxDistance, float minDistance) {
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
        private final DragonGuardianEntity dragon;
        private LivingEntity attacker;
        private int lastAttackedTime;

        public TrackOwnerAttackerGoal(DragonGuardianEntity dragon) {
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
        private final DragonGuardianEntity dragon;
        private LivingEntity target;
        private int lastAttackTime;

        public AttackWithOwnerGoal(DragonGuardianEntity dragon) {
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
}
