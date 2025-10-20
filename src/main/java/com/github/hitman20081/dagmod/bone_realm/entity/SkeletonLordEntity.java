package com.github.hitman20081.dagmod.bone_realm.entity;

import com.github.hitman20081.dagmod.bone_realm.chest.BossChestSpawner;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.server.network.ServerPlayerEntity;

public class SkeletonLordEntity extends SkeletonEntity {

    private static final int MAX_SUMMONERS = 3; // Max Summoners Spawned
    private static final int MIN_SUMMON_COOLDOWN = 200; // 10 seconds - slower
    private static final int MAX_SUMMON_COOLDOWN = 300; // 15 seconds

    private int summonCooldown;
    private int summonerCount = 0;

    private final ServerBossBar bossBar;

    public SkeletonLordEntity(EntityType<? extends SkeletonEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 50;
        this.summonCooldown = MIN_SUMMON_COOLDOWN;

        // ADD THIS:
        this.bossBar = new ServerBossBar(
                Text.literal("Skeleton Lord").formatted(Formatting.DARK_RED, Formatting.BOLD),
                BossBar.Color.RED,
                BossBar.Style.NOTCHED_10
        );

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
        ItemStack helmet = new ItemStack(net.minecraft.item.Items.DIAMOND_HELMET);
        helmet.set(net.minecraft.component.DataComponentTypes.CUSTOM_NAME,
                Text.literal("Helm of the Bone Lord").formatted(Formatting.DARK_RED));
        this.equipStack(net.minecraft.entity.EquipmentSlot.HEAD, helmet);

        ItemStack chestplate = new ItemStack(net.minecraft.item.Items.DIAMOND_CHESTPLATE);
        chestplate.set(net.minecraft.component.DataComponentTypes.CUSTOM_NAME,
                Text.literal("Chest of the Bone Lord").formatted(Formatting.DARK_RED));
        this.equipStack(net.minecraft.entity.EquipmentSlot.CHEST, chestplate);

        ItemStack leggings = new ItemStack(net.minecraft.item.Items.DIAMOND_LEGGINGS);
        leggings.set(net.minecraft.component.DataComponentTypes.CUSTOM_NAME,
                Text.literal("Leggings of the Bone Lord").formatted(Formatting.DARK_RED));
        this.equipStack(net.minecraft.entity.EquipmentSlot.LEGS, leggings);

        ItemStack boots = new ItemStack(net.minecraft.item.Items.DIAMOND_BOOTS);
        boots.set(net.minecraft.component.DataComponentTypes.CUSTOM_NAME,
                Text.literal("Sabatons of the Bone Lord").formatted(Formatting.DARK_RED));
        this.equipStack(net.minecraft.entity.EquipmentSlot.FEET, boots);

        ItemStack sword = new ItemStack(net.minecraft.item.Items.DIAMOND_SWORD);
        this.equipStack(net.minecraft.entity.EquipmentSlot.MAINHAND, sword);

        this.setEquipmentDropChance(net.minecraft.entity.EquipmentSlot.HEAD, 0.0f);
        this.setEquipmentDropChance(net.minecraft.entity.EquipmentSlot.CHEST, 0.0f);
        this.setEquipmentDropChance(net.minecraft.entity.EquipmentSlot.LEGS, 0.0f);
        this.setEquipmentDropChance(net.minecraft.entity.EquipmentSlot.FEET, 0.0f);
        this.setEquipmentDropChance(net.minecraft.entity.EquipmentSlot.MAINHAND, 0.0f);
    }

    public static DefaultAttributeContainer.Builder createSkeletonLordAttributes() {
        return SkeletonEntity.createAbstractSkeletonAttributes()
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
    public void tick() {
        super.tick();

        if (!this.getEntityWorld().isClient()) {
            this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());
        }

        if (!this.getEntityWorld().isClient() && this.isAlive()) {
            // Count nearby summoners
            this.summonerCount = this.getEntityWorld().getEntitiesByClass(
                    SkeletonSummonerEntity.class,
                    this.getBoundingBox().expand(40),
                    summoner -> summoner.isAlive()
            ).size();

            // Summon skeleton summoners
            this.summonCooldown--;
            if (this.summonCooldown <= 0 && this.summonerCount < MAX_SUMMONERS) {
                this.summonSkeletonSummoner();
                this.summonCooldown = MIN_SUMMON_COOLDOWN +
                        this.random.nextInt(MAX_SUMMON_COOLDOWN - MIN_SUMMON_COOLDOWN);
            }
        }
    }

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        this.bossBar.addPlayer(player);
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        this.bossBar.removePlayer(player);
    }

    private void summonSkeletonSummoner() {
        if (!(this.getEntityWorld() instanceof ServerWorld serverWorld)) {
            return;
        }

        BlockPos spawnPos = this.getBlockPos().add(
                this.random.nextInt(6) - 3,
                0,
                this.random.nextInt(6) - 3
        );

        SkeletonSummonerEntity summoner = new SkeletonSummonerEntity(BoneRealmEntityRegistry.SKELETON_SUMMONER, serverWorld);
        summoner.refreshPositionAndAngles(
                spawnPos.getX() + 0.5,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5,
                this.random.nextFloat() * 360.0f,
                0.0f
        );

        if (this.getTarget() != null) {
            summoner.setTarget(this.getTarget());
        }

        serverWorld.spawnEntity(summoner);

        // Epic summoning effects
        for (int i = 0; i < 30; i++) {
            double offsetX = (this.random.nextDouble() - 0.5) * 1.5;
            double offsetY = this.random.nextDouble() * 2.0;
            double offsetZ = (this.random.nextDouble() - 0.5) * 1.5;

            serverWorld.spawnParticles(
                    ParticleTypes.SOUL_FIRE_FLAME,
                    spawnPos.getX() + 0.5,
                    spawnPos.getY() + 0.5,
                    spawnPos.getZ() + 0.5,
                    1,
                    offsetX, offsetY, offsetZ,
                    0.1
            );
        }

        this.playSound(SoundEvents.ENTITY_WITHER_SPAWN, 0.5f, 1.5f);
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);

        // ADD THIS:
        this.bossBar.clearPlayers();

        if (!this.getEntityWorld().isClient()) {
            PlayerEntity killer = null;
            if (damageSource.getAttacker() instanceof PlayerEntity) {
                killer = (PlayerEntity) damageSource.getAttacker();
            }
            BossChestSpawner.onBossDeath(this, killer, this.getEntityWorld());
        }
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_WITHER_SKELETON_DEATH;
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