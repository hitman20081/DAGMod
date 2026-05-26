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
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;

import java.util.List;

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
        MANA_SHIELD("Mana Shield", 15.0f);

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
            net.minecraft.world.entity.LightningBolt lightning =
                    net.minecraft.world.entity.EntityType.LIGHTNING_BOLT.create(serverWorld, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
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