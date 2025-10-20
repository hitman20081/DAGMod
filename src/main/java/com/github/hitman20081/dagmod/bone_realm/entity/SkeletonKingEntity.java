package com.github.hitman20081.dagmod.bone_realm.entity;

import com.github.hitman20081.dagmod.bone_realm.chest.BossChestSpawner;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

/**
 * Skeleton King - Epic Boss of the Bone Realm
 * High health, powerful attacks, boss bar
 */
public class SkeletonKingEntity extends HostileEntity {

    private final ServerBossBar bossBar;

    public SkeletonKingEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 100;

        // Create boss bar
        this.bossBar = new ServerBossBar(
                Text.literal("Skeleton King").formatted(Formatting.DARK_PURPLE, Formatting.BOLD),
                BossBar.Color.PURPLE,
                BossBar.Style.NOTCHED_10
        );
        this.bossBar.setDarkenSky(true);

        // Initialize equipment immediately if on server
        if (!world.isClient()) {
            this.initializeEquipment();
            System.out.println("DEBUG: Equipment initialized in constructor");
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
        System.out.println("DEBUG: Skeleton King initialized in world");
        return entityData;
    }

    private void initializeEquipment() {
        System.out.println("DEBUG: initializeEquipment called");

        // Netherite Helmet - Crown of the Bone Sovereign
        ItemStack helmet = new ItemStack(net.minecraft.item.Items.NETHERITE_HELMET);
        helmet.set(net.minecraft.component.DataComponentTypes.CUSTOM_NAME,
                Text.literal("Crown of the Bone Sovereign").formatted(Formatting.DARK_PURPLE));
        this.equipStack(net.minecraft.entity.EquipmentSlot.HEAD, helmet);

        // Netherite Chestplate - Regalia of the Death Lord
        ItemStack chestplate = new ItemStack(net.minecraft.item.Items.NETHERITE_CHESTPLATE);
        chestplate.set(net.minecraft.component.DataComponentTypes.CUSTOM_NAME,
                Text.literal("Regalia of the Death Lord").formatted(Formatting.DARK_PURPLE));
        this.equipStack(net.minecraft.entity.EquipmentSlot.CHEST, chestplate);

        // Netherite Leggings - Royal Bone Greaves
        ItemStack leggings = new ItemStack(net.minecraft.item.Items.NETHERITE_LEGGINGS);
        leggings.set(net.minecraft.component.DataComponentTypes.CUSTOM_NAME,
                Text.literal("Royal Bone Greaves").formatted(Formatting.DARK_PURPLE));
        this.equipStack(net.minecraft.entity.EquipmentSlot.LEGS, leggings);

        // Netherite Boots - Sovereign's Marrow Treads
        ItemStack boots = new ItemStack(net.minecraft.item.Items.NETHERITE_BOOTS);
        boots.set(net.minecraft.component.DataComponentTypes.CUSTOM_NAME,
                Text.literal("Sovereign's Marrow Treads").formatted(Formatting.DARK_PURPLE));
        this.equipStack(net.minecraft.entity.EquipmentSlot.FEET, boots);

        // Netherite Sword
        ItemStack sword = new ItemStack(net.minecraft.item.Items.NETHERITE_SWORD);
        this.equipStack(net.minecraft.entity.EquipmentSlot.MAINHAND, sword);

        // Set equipment to not drop (boss loot table handles drops)
        this.setEquipmentDropChance(net.minecraft.entity.EquipmentSlot.HEAD, 0.0f);
        this.setEquipmentDropChance(net.minecraft.entity.EquipmentSlot.CHEST, 0.0f);
        this.setEquipmentDropChance(net.minecraft.entity.EquipmentSlot.LEGS, 0.0f);
        this.setEquipmentDropChance(net.minecraft.entity.EquipmentSlot.FEET, 0.0f);
        this.setEquipmentDropChance(net.minecraft.entity.EquipmentSlot.MAINHAND, 0.0f);

        System.out.println("DEBUG: Equipment set on server, forcing sync...");
        // Force equipment sync to client
        this.getEntityWorld().sendEntityStatus(this, (byte) 3); // Generic update
    }

    public static DefaultAttributeContainer.Builder createSkeletonKingAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.MAX_HEALTH, 60.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.ATTACK_DAMAGE, 8.0)
                .add(EntityAttributes.ARMOR, 20.0)
                .add(EntityAttributes.ARMOR_TOUGHNESS, 5.0)
                .add(EntityAttributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(EntityAttributes.FOLLOW_RANGE, 48.0)
                .add(EntityAttributes.ATTACK_KNOCKBACK, 0.45)
                .add(EntityAttributes.SCALE, 2.0);
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
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        this.bossBar.addPlayer(player);

        // Make sure equipment is synced when player starts tracking
        if (!this.getEquippedStack(net.minecraft.entity.EquipmentSlot.HEAD).isEmpty()) {
            System.out.println("DEBUG: Equipment already set, syncing to player");
        } else {
            System.out.println("DEBUG: No equipment found, initializing for player tracking");
            this.initializeEquipment();
        }
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        this.bossBar.removePlayer(player);
    }

    @Override
    public void tick() {
        super.tick();

        // Update boss bar health
        if (!this.getEntityWorld().isClient()) {
            this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);

        // Clear boss bar
        this.bossBar.clearPlayers();

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
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_WITHER_SKELETON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_WITHER_SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_WITHER_SKELETON_DEATH;
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return false; // Boss never despawns
    }

    @Override
    public boolean cannotDespawn() {
        return true;
    }
}