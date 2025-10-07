package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.class_system.mana.ManaData;
import com.github.hitman20081.dagmod.class_system.mana.ManaManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

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

    public SpellScrollItem(Settings settings, SpellType spellType) {
        super(settings);
        this.spellType = spellType;
        this.manaCost = spellType.getBaseCost();
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

        // Check if player is a Mage
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUuid());
        if (!"Mage".equals(playerClass)) {
            player.sendMessage(Text.literal("Only Mages can use spell scrolls!")
                    .formatted(Formatting.RED), true);
            return ActionResult.FAIL;
        }

        // Check if player has enough mana (but don't consume yet)
        ManaData manaData = ManaManager.getManaData(serverPlayer);
        if (!manaData.hasMana(manaCost)) {
            player.sendMessage(Text.literal("Not enough mana! Need " + manaCost + " mana.")
                    .formatted(Formatting.RED), true);
            return ActionResult.FAIL;
        }

        // Try to cast the spell
        boolean spellSuccess = castSpell(world, serverPlayer);

        // Only consume mana if spell was successful
        if (spellSuccess) {
            manaData.useMana(manaCost);

            player.sendMessage(Text.literal("✦ Cast " + spellType.getDisplayName() + "! ✦")
                    .formatted(Formatting.AQUA), true);

            // Play sound
            world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_EVOKER_CAST_SPELL,
                    SoundCategory.PLAYERS, 1.0f, 1.0f);

            return ActionResult.SUCCESS;
        } else {
            // Spell failed, no mana consumed
            return ActionResult.FAIL;
        }
    }

    private boolean castSpell(World world, ServerPlayerEntity player) {
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

    private boolean castHeal(ServerPlayerEntity player) {
        player.heal(6.0f);
        spawnParticles((ServerWorld) player.getWorld(), player.getPos(), ParticleTypes.HEART, 10);
        return true;
    }

    private boolean castFireball(World world, ServerPlayerEntity player) {
        Vec3d lookVec = player.getRotationVec(1.0f);
        Vec3d velocity = lookVec.multiply(1.0);

        FireballEntity fireball = new FireballEntity(world, player, velocity, 1);
        fireball.setPosition(player.getEyePos());
        world.spawnEntity(fireball);
        spawnParticles((ServerWorld) world, player.getEyePos(), ParticleTypes.FLAME, 20);
        return true;
    }

    private boolean castAbsorption(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.ABSORPTION,
                600,
                1,
                false,
                true
        ));
        spawnParticles((ServerWorld) player.getWorld(), player.getPos(), ParticleTypes.ENCHANT, 30);
        return true;
    }

    private boolean castLightning(World world, ServerPlayerEntity player) {
        Vec3d start = player.getEyePos();
        Vec3d end = start.add(player.getRotationVec(1.0f).multiply(30));

        HitResult hitResult = world.raycast(new RaycastContext(
                start, end,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                player
        ));

        Vec3d strikePos = hitResult.getPos();

        if (world instanceof ServerWorld serverWorld) {
            net.minecraft.entity.LightningEntity lightning =
                    net.minecraft.entity.EntityType.LIGHTNING_BOLT.create(serverWorld, net.minecraft.entity.SpawnReason.TRIGGERED);
            if (lightning != null) {
                lightning.setPosition(strikePos);
                world.spawnEntity(lightning);
                return true;
            }
        }
        return false;
    }

    private boolean castFrostNova(World world, ServerPlayerEntity player) {
        Box area = player.getBoundingBox().expand(8.0);
        List<LivingEntity> nearbyEntities = world.getEntitiesByClass(
                LivingEntity.class, area,
                entity -> entity != player && entity.isAlive()
        );

        for (LivingEntity entity : nearbyEntities) {
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 3));
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 100, 2));
            entity.damage((ServerWorld) world, world.getDamageSources().freeze(), 4.0f);
            spawnParticles((ServerWorld) world, entity.getPos(), ParticleTypes.SNOWFLAKE, 20);
        }

        spawnParticles((ServerWorld) world, player.getPos(), ParticleTypes.SNOWFLAKE, 50);
        player.sendMessage(Text.literal("Froze " + nearbyEntities.size() + " enemies!")
                .formatted(Formatting.AQUA), true);
        return true;
    }

    private boolean castTeleport(World world, ServerPlayerEntity player) {
        Vec3d start = player.getEyePos();
        Vec3d end = start.add(player.getRotationVec(1.0f).multiply(20));

        HitResult hitResult = world.raycast(new RaycastContext(
                start, end,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                player
        ));

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            Vec3d targetPos = hitResult.getPos().add(0, 1, 0);

            spawnParticles((ServerWorld) world, player.getPos(), ParticleTypes.PORTAL, 30);
            player.teleport(targetPos.x, targetPos.y, targetPos.z, true);
            spawnParticles((ServerWorld) world, player.getPos(), ParticleTypes.PORTAL, 30);

            return true; // Success!
        } else {
            player.sendMessage(Text.literal("No valid teleport target!")
                    .formatted(Formatting.RED), true);
            return false; // Failed - no mana consumed
        }
    }

    private boolean castManaShield(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.RESISTANCE,
                200,
                1,
                false,
                true
        ));
        spawnParticles((ServerWorld) player.getWorld(), player.getPos(), ParticleTypes.ENCHANTED_HIT, 40);
        return true;
    }

    private void spawnParticles(ServerWorld world, Vec3d pos, net.minecraft.particle.ParticleEffect particle, int count) {
        for (int i = 0; i < count; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 2;
            double offsetY = world.random.nextDouble() * 2;
            double offsetZ = (world.random.nextDouble() - 0.5) * 2;

            world.spawnParticles(particle,
                    pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ,
                    1, 0, 0, 0, 0);
        }
    }
}