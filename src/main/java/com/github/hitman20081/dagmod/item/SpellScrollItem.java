package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.class_system.mana.ManaData;
import com.github.hitman20081.dagmod.class_system.mana.ManaManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.hurtingprojectile.LargeFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.level.block.Blocks;

public class SpellScrollItem extends Item {
    private final float manaCost;
    private final SpellType spellType;

    public enum SpellType {
        HEAL("Heal", 20.0f),
        FIREBALL("Fireball", 30.0f),
        ABSORPTION("Arcane Shield", 25.0f),
        LIGHTNING("Lightning Bolt", 35.0f),
        FROST_NOVA("Frost Nova", 40.0f),
        TELEPORT("Blink", 30.0f),
        MANA_SHIELD("Mana Shield", 15.0f),
        GRAVITY_WELL("Gravity Well", 45.0f),
        CHAIN_LIGHTNING("Chain Lightning", 50.0f),
        ICE_WALL("Ice Wall", 35.0f),
        METEOR_STORM("Meteor Storm", 60.0f),
        LIFE_DRAIN("Life Drain", 40.0f),
        DIMENSIONAL_RIFT("Dimensional Rift", 30.0f),
        POLYMORPH("Polymorph", 55.0f);

        private final String displayName;
        private final float baseCost;

        SpellType(String displayName, float baseCost) {
            this.displayName = displayName;
            this.baseCost = baseCost;
        }

        public String getDisplayName() { return displayName; }
        public float getBaseCost() { return baseCost; }
    }

    public SpellScrollItem(Properties settings, SpellType spellType) {
        super(settings);
        this.spellType = spellType;
        this.manaCost = spellType.getBaseCost();
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ServerPlayer serverPlayer = (ServerPlayer) player;

        // Check if player is a Mage
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUUID());
        if (!"Mage".equals(playerClass)) {
            player.sendOverlayMessage(Component.literal("Only Mages can use spell scrolls!")
                    .withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }

        // Calculate final mana cost with armor bonus
        float baseCost = manaCost;
        float costReduction = com.github.hitman20081.dagmod.class_system.armor.CustomArmorSetBonus
                .getManaCostReduction(serverPlayer);
        float finalCost = baseCost * (1.0f - costReduction);

        // Check if player has enough mana (but don't consume yet)
        ManaData manaData = ManaManager.getManaData(serverPlayer);
        if (!manaData.hasMana(finalCost)) {
            player.sendOverlayMessage(Component.literal("Not enough mana! Need " + String.format("%.0f", finalCost) + " mana.")
                    .withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }

        // Try to cast the spell
        boolean spellSuccess = castSpell(world, serverPlayer);

        // Only consume mana if spell was successful
        if (spellSuccess) {
            manaData.useMana(finalCost);

            // Show mana cost reduction if applicable
            if (costReduction > 0) {
                player.sendOverlayMessage(Component.literal("✦ Cast " + spellType.getDisplayName() + "! ✦ ")
                        .withStyle(ChatFormatting.AQUA)
                        .append(Component.literal("(" + String.format("%.0f", finalCost) + " mana)")
                                .withStyle(ChatFormatting.DARK_AQUA)));
            } else {
                player.sendOverlayMessage(Component.literal("✦ Cast " + spellType.getDisplayName() + "! ✦")
                        .withStyle(ChatFormatting.AQUA));
            }

            // Play sound
            world.playSound(null, player.blockPosition(), SoundEvents.EVOKER_CAST_SPELL,
                    SoundSource.PLAYERS, 1.0f, 1.0f);

            return InteractionResult.SUCCESS;
        } else {
            // Spell failed, no mana consumed
            return InteractionResult.FAIL;
        }
    }

    private boolean castSpell(Level world, ServerPlayer player) {
        return switch (spellType) {
            case HEAL -> castHeal(player);
            case FIREBALL -> castFireball(world, player);
            case ABSORPTION -> castAbsorption(player);
            case LIGHTNING -> castLightning(world, player);
            case FROST_NOVA -> castFrostNova(world, player);
            case TELEPORT -> castTeleport(world, player);
            case MANA_SHIELD -> castManaShield(player);
            case GRAVITY_WELL -> castGravityWell(world, player);
            case CHAIN_LIGHTNING -> castChainLightning(world, player);
            case ICE_WALL -> castIceWall(world, player);
            case METEOR_STORM -> castMeteorStorm(world, player);
            case LIFE_DRAIN -> castLifeDrain(world, player);
            case DIMENSIONAL_RIFT -> castDimensionalRift(world, player);
            case POLYMORPH -> castPolymorph(world, player);
        };
    }

    private boolean castHeal(ServerPlayer player) {
        player.heal(6.0f);
        spawnParticles((ServerLevel) player.level(), player.position(), ParticleTypes.HEART, 10);
        return true;
    }

    private boolean castFireball(Level world, ServerPlayer player) {
        Vec3 lookVec = player.getViewVector(1.0f);
        Vec3 velocity = lookVec.scale(1.0);

        LargeFireball fireball = new LargeFireball(world, player, velocity, 1);
        fireball.setPos(player.getEyePosition());
        world.addFreshEntity(fireball);
        spawnParticles((ServerLevel) world, player.getEyePosition(), ParticleTypes.FLAME, 20);
        return true;
    }

    private boolean castAbsorption(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(
                MobEffects.ABSORPTION,
                600,
                1,
                false,
                true
        ));
        spawnParticles((ServerLevel) player.level(), player.position(), ParticleTypes.ENCHANT, 30);
        return true;
    }

    private boolean castLightning(Level world, ServerPlayer player) {
        Vec3 start = player.getEyePosition();
        Vec3 end = start.add(player.getViewVector(1.0f).scale(30));

        HitResult hitResult = world.clip(new ClipContext(
                start, end,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                player
        ));

        Vec3 strikePos = hitResult.getLocation();

        if (world instanceof ServerLevel serverWorld) {
            @SuppressWarnings("unchecked")
            net.minecraft.world.entity.EntityType<net.minecraft.world.entity.LightningBolt> lightningType =
                    (net.minecraft.world.entity.EntityType<net.minecraft.world.entity.LightningBolt>)
                    net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getValue(
                            net.minecraft.resources.Identifier.parse("minecraft:lightning_bolt"));
            net.minecraft.world.entity.LightningBolt lightning =
                    lightningType.create(serverWorld, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
            if (lightning != null) {
                lightning.setPos(strikePos);
                world.addFreshEntity(lightning);
                return true;
            }
        }
        return false;
    }

    private boolean castFrostNova(Level world, ServerPlayer player) {
        AABB area = player.getBoundingBox().inflate(8.0);
        List<LivingEntity> nearbyEntities = world.getEntitiesOfClass(
                LivingEntity.class, area,
                entity -> entity != player && entity.isAlive()
        );

        for (LivingEntity entity : nearbyEntities) {
            entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 3));
            entity.addEffect(new MobEffectInstance(MobEffects.MINING_FATIGUE, 100, 2));
            entity.hurtServer((ServerLevel) world, world.damageSources().freeze(), 4.0f);
            spawnParticles((ServerLevel) world, entity.position(), ParticleTypes.SNOWFLAKE, 20);
        }

        spawnParticles((ServerLevel) world, player.position(), ParticleTypes.SNOWFLAKE, 50);
        player.sendOverlayMessage(Component.literal("Froze " + nearbyEntities.size() + " enemies!")
                .withStyle(ChatFormatting.AQUA));
        return true;
    }

    private boolean castTeleport(Level world, ServerPlayer player) {
        Vec3 start = player.getEyePosition();
        Vec3 end = start.add(player.getViewVector(1.0f).scale(20));

        HitResult hitResult = world.clip(new ClipContext(
                start, end,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                player
        ));

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            Vec3 targetPos = hitResult.getLocation().add(0, 1, 0);

            spawnParticles((ServerLevel) world, player.position(), ParticleTypes.PORTAL, 30);
            player.teleportTo(targetPos.x, targetPos.y, targetPos.z);
            spawnParticles((ServerLevel) world, player.position(), ParticleTypes.PORTAL, 30);

            return true; // Success!
        } else {
            player.sendOverlayMessage(Component.literal("No valid teleport target!")
                    .withStyle(ChatFormatting.RED));
            return false; // Failed - no mana consumed
        }
    }

    private boolean castManaShield(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(
                MobEffects.RESISTANCE,
                200,
                1,
                false,
                true
        ));
        spawnParticles((ServerLevel) player.level(), player.position(), ParticleTypes.ENCHANTED_HIT, 40);
        return true;
    }

    private boolean castGravityWell(Level world, ServerPlayer player) {
        Vec3 playerPos = player.position();
        AABB area = player.getBoundingBox().inflate(15.0);
        List<LivingEntity> pulled = world.getEntitiesOfClass(
                LivingEntity.class, area,
                e -> e != player && e.isAlive()
        );
        for (LivingEntity entity : pulled) {
            Vec3 toPlayer = playerPos.subtract(entity.position()).normalize().scale(2.0);
            entity.push(toPlayer.x, 0.5, toPlayer.z);
        }
        spawnParticles((ServerLevel) world, playerPos, ParticleTypes.REVERSE_PORTAL, 60);
        player.sendOverlayMessage(Component.literal("Pulled " + pulled.size() + " enemies!")
                .withStyle(ChatFormatting.DARK_PURPLE));
        return true;
    }

    private boolean castChainLightning(Level world, ServerPlayer player) {
        if (!(world instanceof ServerLevel serverLevel)) return false;
        List<LivingEntity> struck = new ArrayList<>();
        Vec3 searchFrom = player.getEyePosition();
        LivingEntity target = nearestEnemy(world, searchFrom, player, 20.0, struck);
        if (target == null) {
            player.sendOverlayMessage(Component.literal("No target in range!").withStyle(ChatFormatting.RED));
            return false;
        }
        for (int bounce = 0; bounce < 4 && target != null; bounce++) {
            struck.add(target);
            spawnLightningAt(serverLevel, target.position());
            target = nearestEnemy(world, target.position(), player, 8.0, struck);
        }
        return true;
    }

    private LivingEntity nearestEnemy(Level world, Vec3 origin, Player exclude, double range, List<LivingEntity> skip) {
        AABB area = AABB.ofSize(origin, range * 2, range * 2, range * 2);
        LivingEntity nearest = null;
        double nearestDist = Double.MAX_VALUE;
        for (LivingEntity e : world.getEntitiesOfClass(LivingEntity.class, area)) {
            if (e == exclude || !e.isAlive() || skip.contains(e)) continue;
            double d = e.position().distanceTo(origin);
            if (d < range && d < nearestDist) { nearest = e; nearestDist = d; }
        }
        return nearest;
    }

    private void spawnLightningAt(ServerLevel world, Vec3 pos) {
        @SuppressWarnings("unchecked")
        net.minecraft.world.entity.EntityType<net.minecraft.world.entity.LightningBolt> lightningType =
                (net.minecraft.world.entity.EntityType<net.minecraft.world.entity.LightningBolt>)
                net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getValue(
                        net.minecraft.resources.Identifier.parse("minecraft:lightning_bolt"));
        net.minecraft.world.entity.LightningBolt bolt =
                lightningType.create(world, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
        if (bolt != null) { bolt.setPos(pos); world.addFreshEntity(bolt); }
    }

    private boolean castIceWall(Level world, ServerPlayer player) {
        Vec3 look = player.getViewVector(1.0f);
        Vec3 forward = new Vec3(look.x, 0, look.z).normalize();
        Vec3 right = new Vec3(-forward.z, 0, forward.x);
        BlockPos base = player.blockPosition().offset(
                (int) Math.round(forward.x * 3), 0, (int) Math.round(forward.z * 3));
        int placed = 0;
        for (int h = 0; h < 3; h++) {
            for (int w = -2; w <= 2; w++) {
                BlockPos pos = base.offset(
                        (int) Math.round(right.x * w), h, (int) Math.round(right.z * w));
                if (world.getBlockState(pos).isAir()) {
                    world.setBlock(pos, Blocks.FROSTED_ICE.defaultBlockState(), 3);
                    placed++;
                }
            }
        }
        if (placed == 0) {
            player.sendOverlayMessage(Component.literal("No space for ice wall!").withStyle(ChatFormatting.RED));
            return false;
        }
        spawnParticles((ServerLevel) world, player.position(), ParticleTypes.SNOWFLAKE, 50);
        return true;
    }

    private boolean castMeteorStorm(Level world, ServerPlayer player) {
        if (!(world instanceof ServerLevel serverLevel)) return false;
        Vec3 target = player.getEyePosition().add(player.getViewVector(1.0f).scale(20));
        for (int i = 0; i < 5; i++) {
            double ox = (world.getRandom().nextDouble() - 0.5) * 12;
            double oz = (world.getRandom().nextDouble() - 0.5) * 12;
            Vec3 spawnPos = new Vec3(target.x + ox, target.y + 25, target.z + oz);
            Vec3 velocity = target.subtract(spawnPos).normalize().scale(1.5);
            LargeFireball fireball = new LargeFireball(world, player, velocity, 1);
            fireball.setPos(spawnPos);
            serverLevel.addFreshEntity(fireball);
        }
        spawnParticles(serverLevel, player.getEyePosition(), ParticleTypes.LAVA, 30);
        return true;
    }

    private boolean castLifeDrain(Level world, ServerPlayer player) {
        AABB area = player.getBoundingBox().inflate(12.0);
        List<LivingEntity> enemies = world.getEntitiesOfClass(
                LivingEntity.class, area,
                e -> e != player && e.isAlive()
        );
        if (enemies.isEmpty()) {
            player.sendOverlayMessage(Component.literal("No enemies nearby!").withStyle(ChatFormatting.RED));
            return false;
        }
        float totalHeal = 0;
        for (LivingEntity entity : enemies) {
            entity.hurtServer((ServerLevel) world, world.damageSources().magic(), 4.0f);
            totalHeal += 2.0f;
            spawnParticles((ServerLevel) world, entity.position(), ParticleTypes.SOUL_FIRE_FLAME, 10);
        }
        float healed = Math.min(totalHeal, 10.0f);
        player.heal(healed);
        spawnParticles((ServerLevel) world, player.position(), ParticleTypes.HEART, 15);
        player.sendOverlayMessage(Component.literal("Drained " + enemies.size() + " enemies for +"
                + String.format("%.1f", healed) + " health!").withStyle(ChatFormatting.DARK_RED));
        return true;
    }

    private boolean castDimensionalRift(Level world, ServerPlayer player) {
        Vec3 look = player.getViewVector(1.0f);
        Vec3 horizontal = new Vec3(look.x, 0, look.z).normalize();
        HitResult hit = world.clip(new ClipContext(
                player.getEyePosition(),
                player.getEyePosition().add(horizontal.scale(15)),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                player
        ));
        double destX, destZ;
        if (hit.getType() == HitResult.Type.BLOCK) {
            Vec3 safe = hit.getLocation().subtract(horizontal.scale(1.5));
            destX = safe.x; destZ = safe.z;
        } else {
            destX = player.getX() + horizontal.x * 15;
            destZ = player.getZ() + horizontal.z * 15;
        }
        spawnParticles((ServerLevel) world, player.position(), ParticleTypes.REVERSE_PORTAL, 30);
        player.teleportTo(destX, player.getY(), destZ);
        spawnParticles((ServerLevel) world, player.position(), ParticleTypes.PORTAL, 30);
        return true;
    }

    private boolean castPolymorph(Level world, ServerPlayer player) {
        AABB area = player.getBoundingBox().inflate(15.0);
        LivingEntity target = null;
        double nearest = Double.MAX_VALUE;
        for (LivingEntity e : world.getEntitiesOfClass(LivingEntity.class, area)) {
            if (e == player || !e.isAlive() || e instanceof Player) continue;
            double d = e.distanceTo(player);
            if (d < nearest) { target = e; nearest = d; }
        }
        if (target == null) {
            player.sendOverlayMessage(Component.literal("No target in range!").withStyle(ChatFormatting.RED));
            return false;
        }
        target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS,  160, 5));
        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,  160, 5));
        target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 160, 0));
        spawnParticles((ServerLevel) world, target.position(), ParticleTypes.WITCH, 40);
        player.sendOverlayMessage(Component.literal("✦ Polymorphed "
                + target.getType().getDescription().getString() + "! ✦")
                .withStyle(ChatFormatting.LIGHT_PURPLE));
        return true;
    }

    private void spawnParticles(ServerLevel world, Vec3 pos, net.minecraft.core.particles.ParticleOptions particle, int count) {
        for (int i = 0; i < count; i++) {
            double offsetX = (world.getRandom().nextDouble() - 0.5) * 2;
            double offsetY = world.getRandom().nextDouble() * 2;
            double offsetZ = (world.getRandom().nextDouble() - 0.5) * 2;

            world.sendParticles(particle,
                    pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ,
                    1, 0.0, 0.0, 0.0, 0.0);
        }
    }
}