package com.github.hitman20081.dagmod.pale_garden.entity;

import com.github.hitman20081.dagmod.bone_realm.BossRoomSpawnHandler;
import net.minecraft.world.BossEvent;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;

/**
 * The Spider Queen — Pale Garden dimension boss, living in the Spider Queen Lair
 * (worldgen/structure/spider_queen_lair.json). Replaces the earlier mcfunction/vanilla-spider
 * prototype (scale 3.0, spawned in the Overworld) with a proper boss entity, matching
 * SkeletonKingEntity's pattern (boss bar, seal/unseal on defeat).
 *
 * SCALE is pushed well beyond anything else in this mod (previous max was 3.0) — treat
 * rendering/hitbox/pathfinding behavior at this scale as unverified until tested live
 * (see known_issues.md).
 */
public class SpiderQueenEntity extends Spider {

    public static final double SCALE = 6.0;

    private final ServerBossEvent bossBar;

    public SpiderQueenEntity(EntityType<? extends Spider> entityType, Level world) {
        super(entityType, world);
        this.xpReward = 150;

        this.bossBar = new ServerBossEvent(
                this.getUUID(),
                Component.literal("Spider Queen").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD),
                BossEvent.BossBarColor.PURPLE,
                BossEvent.BossBarOverlay.NOTCHED_10
        );
        this.bossBar.setDarkenScreen(true);
    }

    public static AttributeSupplier.Builder createSpiderQueenAttributes() {
        return Spider.createAttributes()
                .add(Attributes.MAX_HEALTH, 275.0)
                .add(Attributes.MOVEMENT_SPEED, 0.45)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.ATTACK_SPEED, 2.0)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.ARMOR_TOUGHNESS, 2.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.9)
                .add(Attributes.JUMP_STRENGTH, 0.58)
                .add(Attributes.FOLLOW_RANGE, 48.0)
                .add(Attributes.SCALE, SCALE);
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
            BossRoomSpawnHandler.unsealRoom(serverWorld, this.blockPosition(), 45, 25, 45);
        }
        // Death drops come from loot_table/entities/spider_queen.json — reward design (unique
        // items handed directly to nearby players, mirroring SkeletonKingEntity) is TBD.
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SPIDER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SPIDER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SPIDER_DEATH;
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
