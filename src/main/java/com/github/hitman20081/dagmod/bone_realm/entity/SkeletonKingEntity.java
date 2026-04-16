package com.github.hitman20081.dagmod.bone_realm.entity;

import com.github.hitman20081.dagmod.bone_realm.BoneRealmRegistry;
import com.github.hitman20081.dagmod.bone_realm.BossRoomSpawnHandler;
import com.github.hitman20081.dagmod.item.ModItems;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public class SkeletonKingEntity extends SkeletonEntity {

    private final ServerBossBar bossBar;

    public SkeletonKingEntity(EntityType<? extends SkeletonEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 100;

        this.bossBar = new ServerBossBar(
                Text.literal("Skeleton King").formatted(Formatting.DARK_PURPLE, Formatting.BOLD),
                BossBar.Color.PURPLE,
                BossBar.Style.NOTCHED_10
        );
        this.bossBar.setDarkenSky(true);

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
        ItemStack helmet = new ItemStack(net.minecraft.item.Items.NETHERITE_HELMET);
        helmet.set(net.minecraft.component.DataComponentTypes.CUSTOM_NAME,
                Text.literal("Crown of the Bone Sovereign").formatted(Formatting.DARK_PURPLE));
        this.equipStack(net.minecraft.entity.EquipmentSlot.HEAD, helmet);

        ItemStack chestplate = new ItemStack(net.minecraft.item.Items.NETHERITE_CHESTPLATE);
        chestplate.set(net.minecraft.component.DataComponentTypes.CUSTOM_NAME,
                Text.literal("Regalia of the Death Lord").formatted(Formatting.DARK_PURPLE));
        this.equipStack(net.minecraft.entity.EquipmentSlot.CHEST, chestplate);

        ItemStack leggings = new ItemStack(net.minecraft.item.Items.NETHERITE_LEGGINGS);
        leggings.set(net.minecraft.component.DataComponentTypes.CUSTOM_NAME,
                Text.literal("Royal Bone Greaves").formatted(Formatting.DARK_PURPLE));
        this.equipStack(net.minecraft.entity.EquipmentSlot.LEGS, leggings);

        ItemStack boots = new ItemStack(net.minecraft.item.Items.NETHERITE_BOOTS);
        boots.set(net.minecraft.component.DataComponentTypes.CUSTOM_NAME,
                Text.literal("Sovereign's Marrow Treads").formatted(Formatting.DARK_PURPLE));
        this.equipStack(net.minecraft.entity.EquipmentSlot.FEET, boots);

        ItemStack sword = new ItemStack(net.minecraft.item.Items.NETHERITE_SWORD);
        this.equipStack(net.minecraft.entity.EquipmentSlot.MAINHAND, sword);

        this.setEquipmentDropChance(net.minecraft.entity.EquipmentSlot.HEAD, 0.0f);
        this.setEquipmentDropChance(net.minecraft.entity.EquipmentSlot.CHEST, 0.0f);
        this.setEquipmentDropChance(net.minecraft.entity.EquipmentSlot.LEGS, 0.0f);
        this.setEquipmentDropChance(net.minecraft.entity.EquipmentSlot.FEET, 0.0f);
        this.setEquipmentDropChance(net.minecraft.entity.EquipmentSlot.MAINHAND, 0.0f);
    }

    public static DefaultAttributeContainer.Builder createSkeletonKingAttributes() {
        return SkeletonEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 300.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.ATTACK_DAMAGE, 13.0)
                .add(EntityAttributes.ARMOR, 22.0)
                .add(EntityAttributes.ARMOR_TOUGHNESS, 8.0)
                .add(EntityAttributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(EntityAttributes.FOLLOW_RANGE, 48.0)
                .add(EntityAttributes.ATTACK_KNOCKBACK, 0.45)
                .add(EntityAttributes.SCALE, 2.0);
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

    @Override
    public void tick() {
        super.tick();
        if (!this.getEntityWorld().isClient()) {
            this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        this.bossBar.clearPlayers();

        if (!this.getEntityWorld().isClient()) {
            ServerWorld serverWorld = (ServerWorld) this.getEntityWorld();

            // Unseal the throne room
            BossRoomSpawnHandler.unsealRoom(serverWorld, this.getBlockPos());

            // Give rewards to every player within 25 blocks
            BlockPos deathPos = this.getBlockPos();
            for (ServerPlayerEntity nearbyPlayer : serverWorld.getPlayers()) {
                if (nearbyPlayer.getBlockPos().isWithinDistance(deathPos, 25)) {
                    // Two keys — players choose which pre-placed chests to open
                    for (int i = 0; i < 2; i++) {
                        ItemStack key = new ItemStack(BoneRealmRegistry.SKELETON_KING_KEY);
                        if (!nearbyPlayer.getInventory().insertStack(key)) {
                            nearbyPlayer.dropItem(key, false);
                        }
                    }
                    nearbyPlayer.sendMessage(
                            Text.literal("✦ You received 2 Skeleton King's Keys! ✦")
                                    .formatted(Formatting.DARK_PURPLE, Formatting.BOLD),
                            false
                    );

                    // Recall Stone
                    ItemStack stone = new ItemStack(ModItems.KINGS_RECALL_STONE);
                    if (!nearbyPlayer.getInventory().insertStack(stone)) {
                        nearbyPlayer.dropItem(stone, false);
                    }
                }
            }
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
        return false;
    }

    @Override
    public boolean cannotDespawn() {
        return true;
    }
}