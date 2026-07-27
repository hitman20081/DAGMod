package com.github.hitman20081.dagmod.bone_realm.entity;

import com.github.hitman20081.dagmod.bone_realm.BoneRealmRegistry;
import com.github.hitman20081.dagmod.bone_realm.BossRoomSpawnHandler;
import com.github.hitman20081.dagmod.item.ModItems;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.BossEvent;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.util.RandomSource;

public class SkeletonKingEntity extends Skeleton {

    private final ServerBossEvent bossBar;

    public SkeletonKingEntity(EntityType<? extends Skeleton> entityType, Level world) {
        super(entityType, world);
        this.xpReward = 100;

        this.bossBar = new ServerBossEvent(
                this.getUUID(),
                Component.literal("Skeleton King").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD),
                BossEvent.BossBarColor.PURPLE,
                BossEvent.BossBarOverlay.NOTCHED_10
        );
        this.bossBar.setDarkenScreen(true);

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
        ItemStack helmet = new ItemStack(net.minecraft.world.item.Items.NETHERITE_HELMET);
        helmet.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,
                Component.literal("Crown of the Bone Sovereign").withStyle(ChatFormatting.DARK_PURPLE));
        this.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD, helmet);

        ItemStack chestplate = new ItemStack(net.minecraft.world.item.Items.NETHERITE_CHESTPLATE);
        chestplate.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,
                Component.literal("Regalia of the Death Lord").withStyle(ChatFormatting.DARK_PURPLE));
        this.setItemSlot(net.minecraft.world.entity.EquipmentSlot.CHEST, chestplate);

        ItemStack leggings = new ItemStack(net.minecraft.world.item.Items.NETHERITE_LEGGINGS);
        leggings.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,
                Component.literal("Royal Bone Greaves").withStyle(ChatFormatting.DARK_PURPLE));
        this.setItemSlot(net.minecraft.world.entity.EquipmentSlot.LEGS, leggings);

        ItemStack boots = new ItemStack(net.minecraft.world.item.Items.NETHERITE_BOOTS);
        boots.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,
                Component.literal("Sovereign's Marrow Treads").withStyle(ChatFormatting.DARK_PURPLE));
        this.setItemSlot(net.minecraft.world.entity.EquipmentSlot.FEET, boots);

        ItemStack sword = new ItemStack(net.minecraft.world.item.Items.NETHERITE_SWORD);
        this.setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND, sword);

        this.setDropChance(net.minecraft.world.entity.EquipmentSlot.HEAD, 0.0f);
        this.setDropChance(net.minecraft.world.entity.EquipmentSlot.CHEST, 0.0f);
        this.setDropChance(net.minecraft.world.entity.EquipmentSlot.LEGS, 0.0f);
        this.setDropChance(net.minecraft.world.entity.EquipmentSlot.FEET, 0.0f);
        this.setDropChance(net.minecraft.world.entity.EquipmentSlot.MAINHAND, 0.0f);
    }

    public static AttributeSupplier.Builder createSkeletonKingAttributes() {
        return Skeleton.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 300.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_DAMAGE, 13.0)
                .add(Attributes.ARMOR, 22.0)
                .add(Attributes.ARMOR_TOUGHNESS, 8.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 48.0)
                .add(Attributes.ATTACK_KNOCKBACK, 0.45)
                .add(Attributes.SCALE, 2.0);
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

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            this.bossBar.setProgress(this.getHealth() / this.getMaxHealth());
        }
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
        this.bossBar.removeAllPlayers();

        if (!this.level().isClientSide()) {
            ServerLevel serverWorld = (ServerLevel) this.level();

            // Unseal the throne room
            BossRoomSpawnHandler.unsealRoom(serverWorld, this.blockPosition());

            // Give rewards to every player within 25 blocks
            BlockPos deathPos = this.blockPosition();
            for (ServerPlayer nearbyPlayer : serverWorld.players()) {
                if (nearbyPlayer.blockPosition().closerThan(deathPos, 25)) {
                    // Two keys — players choose which pre-placed chests to open
                    for (int i = 0; i < 2; i++) {
                        ItemStack key = new ItemStack(BoneRealmRegistry.SKELETON_KING_KEY);
                        if (!nearbyPlayer.getInventory().add(key)) {
                            nearbyPlayer.drop(key, false);
                        }
                    }
                    nearbyPlayer.sendSystemMessage(
                            Component.literal("✦ You received 2 Skeleton King's Keys! ✦")
                                    .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD));

                    // Recall Stone
                    ItemStack stone = new ItemStack(ModItems.KINGS_RECALL_STONE);
                    if (!nearbyPlayer.getInventory().add(stone)) {
                        nearbyPlayer.drop(stone, false);
                    }
                }
            }
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WITHER_SKELETON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.WITHER_SKELETON_HURT;
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