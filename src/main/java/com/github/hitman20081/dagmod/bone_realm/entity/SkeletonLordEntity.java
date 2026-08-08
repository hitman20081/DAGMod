package com.github.hitman20081.dagmod.bone_realm.entity;

import com.github.hitman20081.dagmod.bone_realm.chest.BossChestSpawner;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.BossEvent;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;

public class SkeletonLordEntity extends Skeleton {

    private static final int MAX_SUMMONERS = 3; // Max Summoners Spawned
    private static final int MIN_SUMMON_COOLDOWN = 200; // 10 seconds - slower
    private static final int MAX_SUMMON_COOLDOWN = 300; // 15 seconds

    private int summonCooldown;
    private int summonerCount = 0;

    private final ServerBossEvent bossBar;

    public SkeletonLordEntity(EntityType<? extends Skeleton> entityType, Level world) {
        super(entityType, world);
        this.xpReward = 50;
        this.summonCooldown = MIN_SUMMON_COOLDOWN;

        this.bossBar = new ServerBossEvent(
                this.getUUID(),
                Component.literal("Skeleton Lord").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD),
                BossEvent.BossBarColor.RED,
                BossEvent.BossBarOverlay.NOTCHED_10
        );

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
        ItemStack helmet = new ItemStack(net.minecraft.world.item.Items.DIAMOND_HELMET);
        helmet.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,
                Component.literal("Helm of the Bone Lord").withStyle(ChatFormatting.DARK_RED));
        this.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD, helmet);

        ItemStack chestplate = new ItemStack(net.minecraft.world.item.Items.DIAMOND_CHESTPLATE);
        chestplate.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,
                Component.literal("Chest of the Bone Lord").withStyle(ChatFormatting.DARK_RED));
        this.setItemSlot(net.minecraft.world.entity.EquipmentSlot.CHEST, chestplate);

        ItemStack leggings = new ItemStack(net.minecraft.world.item.Items.DIAMOND_LEGGINGS);
        leggings.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,
                Component.literal("Leggings of the Bone Lord").withStyle(ChatFormatting.DARK_RED));
        this.setItemSlot(net.minecraft.world.entity.EquipmentSlot.LEGS, leggings);

        ItemStack boots = new ItemStack(net.minecraft.world.item.Items.DIAMOND_BOOTS);
        boots.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,
                Component.literal("Sabatons of the Bone Lord").withStyle(ChatFormatting.DARK_RED));
        this.setItemSlot(net.minecraft.world.entity.EquipmentSlot.FEET, boots);

        ItemStack sword = new ItemStack(net.minecraft.world.item.Items.DIAMOND_SWORD);
        this.setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND, sword);

        this.setDropChance(net.minecraft.world.entity.EquipmentSlot.HEAD, 0.0f);
        this.setDropChance(net.minecraft.world.entity.EquipmentSlot.CHEST, 0.0f);
        this.setDropChance(net.minecraft.world.entity.EquipmentSlot.LEGS, 0.0f);
        this.setDropChance(net.minecraft.world.entity.EquipmentSlot.FEET, 0.0f);
        this.setDropChance(net.minecraft.world.entity.EquipmentSlot.MAINHAND, 0.0f);
    }

    public static AttributeSupplier.Builder createSkeletonLordAttributes() {
        return Skeleton.createAttributes()
                .add(Attributes.MAX_HEALTH, 200.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.ATTACK_DAMAGE, 10.0)
                .add(Attributes.ARMOR, 18.0)
                .add(Attributes.ARMOR_TOUGHNESS, 6.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.ATTACK_KNOCKBACK, 0.25)
                .add(Attributes.SCALE, 1.5);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide()) {
            this.bossBar.setProgress(this.getHealth() / this.getMaxHealth());
        }

        if (!this.level().isClientSide() && this.isAlive()) {
            this.summonCooldown--;
            if (this.summonCooldown <= 0) {
                // Only count nearby summoners when we're actually about to decide
                // whether to summon — this scan doesn't need to run every tick.
                this.summonerCount = this.level().getEntitiesOfClass(
                        SkeletonSummonerEntity.class,
                        this.getBoundingBox().inflate(40),
                        summoner -> summoner.isAlive()
                ).size();

                if (this.summonerCount < MAX_SUMMONERS) {
                    this.summonSkeletonSummoner();
                    this.summonCooldown = MIN_SUMMON_COOLDOWN +
                            this.random.nextInt(MAX_SUMMON_COOLDOWN - MIN_SUMMON_COOLDOWN);
                } else {
                    // Still at the summoner cap — retry soon instead of scanning every tick.
                    this.summonCooldown = 20;
                }
            }
        }
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossBar.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossBar.removePlayer(player);
    }

    private void summonSkeletonSummoner() {
        if (!(this.level() instanceof ServerLevel serverWorld)) {
            return;
        }

        BlockPos spawnPos = this.blockPosition().offset(
                this.random.nextInt(6) - 3,
                0,
                this.random.nextInt(6) - 3
        );

        SkeletonSummonerEntity summoner = new SkeletonSummonerEntity(BoneRealmEntityRegistry.SKELETON_SUMMONER, serverWorld);
        summoner.snapTo(
                spawnPos.getX() + 0.5,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5,
                this.random.nextFloat() * 360.0f,
                0.0f
        );

        if (this.getTarget() != null) {
            summoner.setTarget(this.getTarget());
        }

        serverWorld.addFreshEntity(summoner);

        // Epic summoning effects
        for (int i = 0; i < 30; i++) {
            double offsetX = (this.random.nextDouble() - 0.5) * 1.5;
            double offsetY = this.random.nextDouble() * 2.0;
            double offsetZ = (this.random.nextDouble() - 0.5) * 1.5;

            serverWorld.sendParticles(
                    ParticleTypes.SOUL_FIRE_FLAME,
                    spawnPos.getX() + 0.5,
                    spawnPos.getY() + 0.5,
                    spawnPos.getZ() + 0.5,
                    1,
                    offsetX, offsetY, offsetZ,
                    0.1
            );
        }

        this.playSound(SoundEvents.WITHER_SPAWN, 0.5f, 1.5f);
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);

        // ADD THIS:
        this.bossBar.removeAllPlayers();

        if (!this.level().isClientSide()) {
            Player killer = null;
            if (damageSource.getEntity() instanceof Player) {
                killer = (Player) damageSource.getEntity();
            }
            BossChestSpawner.onBossDeath(this, killer, this.level());
        }
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WITHER_SKELETON_DEATH;
    }

    @Override
    public boolean removeWhenFarAway(double distanceSquared) {
        return false;
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

}