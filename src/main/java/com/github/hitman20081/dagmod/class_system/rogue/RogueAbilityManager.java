package com.github.hitman20081.dagmod.class_system.rogue;

import com.github.hitman20081.dagmod.class_system.rogue.RogueCooldownData;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages Rogue abilities: Smoke Bomb, Poison Dagger, Shadow Step
 */
public class RogueAbilityManager {

    // Energy costs
    public static final int SMOKE_BOMB_COST = 40;
    public static final int POISON_DAGGER_COST = 25;
    public static final int SHADOW_STEP_COST = 50;

    // Cooldown durations (in ticks, 20 ticks = 1 second)
    public static final int SMOKE_BOMB_COOLDOWN = 400; // 20 seconds
    public static final int SHADOW_STEP_COOLDOWN = 600; // 30 seconds

    // Cooldown keys
    private static final String SMOKE_BOMB_KEY = "rogue_smoke_bomb";
    private static final String SHADOW_STEP_KEY = "rogue_shadow_step";

    // Track players with active poison dagger buff
    private static final Map<UUID, Long> poisonDaggerActive = new HashMap<>();
    private static final long POISON_DAGGER_DURATION = 5000; // 5 seconds in milliseconds

    /**
     * Activate Smoke Bomb ability
     */
    public static boolean useSmokeBomb(ServerPlayerEntity player) {
        long worldTime = player.getWorld().getTime();

        // Check cooldown
        if (RogueCooldownData.isOnCooldown(player.getUuid(), SMOKE_BOMB_KEY, worldTime)) {
            long remaining = RogueCooldownData.getRemainingCooldown(player.getUuid(), SMOKE_BOMB_KEY, worldTime);
            player.sendMessage(Text.literal("Smoke Bomb on cooldown: " + (remaining / 20) + "s")
                    .formatted(Formatting.RED), true);
            return false;
        }

        // Check energy
        if (!EnergyManager.hasEnergy(player, SMOKE_BOMB_COST)) {
            player.sendMessage(Text.literal("Not enough energy! Need " + SMOKE_BOMB_COST)
                    .formatted(Formatting.RED), true);
            return false;
        }

        // Consume energy
        EnergyManager.consumeEnergy(player, SMOKE_BOMB_COST);

        // Start cooldown
        RogueCooldownData.startCooldown(player.getUuid(), SMOKE_BOMB_KEY, worldTime, SMOKE_BOMB_COOLDOWN);

        // Apply effects
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 120, 0));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 120, 1));

        // Create smoke cloud
        createSmokeCloud(player);

        // Sound
        player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 1.0f, 0.5f);

        player.sendMessage(Text.literal("Smoke Bomb activated!")
                .formatted(Formatting.DARK_GRAY), true);

        return true;
    }

    private static void createSmokeCloud(ServerPlayerEntity player) {
        ServerWorld world = (ServerWorld) player.getWorld();
        Vec3d pos = player.getPos();

        AreaEffectCloudEntity cloud = new AreaEffectCloudEntity(world, pos.x, pos.y, pos.z);
        cloud.setRadius(3.0f);
        cloud.setDuration(100);
        cloud.setRadiusGrowth(0.0f);
        cloud.addEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 40, 1));
        cloud.setParticleType(ParticleTypes.LARGE_SMOKE);
        // Note: setColor() doesn't exist in 1.21.8, color is automatic

        world.spawnEntity(cloud);

        // Extra particles
        for (int i = 0; i < 50; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 4;
            double offsetY = world.random.nextDouble() * 2;
            double offsetZ = (world.random.nextDouble() - 0.5) * 4;

            world.spawnParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ,
                    1, 0, 0, 0, 0.01);
        }
    }

    /**
     * Activate Poison Dagger
     */
    public static boolean usePoisonDagger(ServerPlayerEntity player) {
        if (!EnergyManager.hasEnergy(player, POISON_DAGGER_COST)) {
            player.sendMessage(Text.literal("Not enough energy! Need " + POISON_DAGGER_COST)
                    .formatted(Formatting.RED), true);
            return false;
        }

        EnergyManager.consumeEnergy(player, POISON_DAGGER_COST);
        poisonDaggerActive.put(player.getUuid(), System.currentTimeMillis());

        player.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 100, 0, false, false));

        player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.PLAYERS, 1.0f, 0.7f);

        ServerWorld world = (ServerWorld) player.getWorld();
        Vec3d pos = player.getPos();
        for (int i = 0; i < 20; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 1.5;
            double offsetY = world.random.nextDouble() * 2;
            double offsetZ = (world.random.nextDouble() - 0.5) * 1.5;

            world.spawnParticles(ParticleTypes.DRAGON_BREATH,
                    pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ,
                    1, 0, 0, 0, 0.01);
        }

        player.sendMessage(Text.literal("Poison Dagger ready!")
                .formatted(Formatting.DARK_GREEN), true);

        return true;
    }

    public static boolean hasPoisonDaggerActive(UUID playerUuid) {
        Long activatedTime = poisonDaggerActive.get(playerUuid);
        if (activatedTime == null) {
            return false;
        }

        if (System.currentTimeMillis() - activatedTime > POISON_DAGGER_DURATION) {
            poisonDaggerActive.remove(playerUuid);
            return false;
        }

        return true;
    }

    public static void consumePoisonDagger(UUID playerUuid) {
        poisonDaggerActive.remove(playerUuid);
    }

    /**
     * Activate Shadow Step
     */
    public static boolean useShadowStep(ServerPlayerEntity player) {
        long worldTime = player.getWorld().getTime();

        if (RogueCooldownData.isOnCooldown(player.getUuid(), SHADOW_STEP_KEY, worldTime)) {
            long remaining = RogueCooldownData.getRemainingCooldown(player.getUuid(), SHADOW_STEP_KEY, worldTime);
            player.sendMessage(Text.literal("Shadow Step on cooldown: " + (remaining / 20) + "s")
                    .formatted(Formatting.RED), true);
            return false;
        }

        if (!EnergyManager.hasEnergy(player, SHADOW_STEP_COST)) {
            player.sendMessage(Text.literal("Not enough energy! Need " + SHADOW_STEP_COST)
                    .formatted(Formatting.RED), true);
            return false;
        }

        // Raycast
        Vec3d start = player.getEyePos();
        Vec3d direction = player.getRotationVec(1.0f);
        Vec3d end = start.add(direction.multiply(25));

        BlockHitResult hitResult = player.getWorld().raycast(new RaycastContext(
                start, end, RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE, player
        ));

        if (hitResult.getType() == HitResult.Type.MISS) {
            player.sendMessage(Text.literal("No valid target location!")
                    .formatted(Formatting.RED), true);
            return false;
        }

        BlockPos targetPos = hitResult.getBlockPos().offset(hitResult.getSide());
        Vec3d oldPos = player.getPos();

        EnergyManager.consumeEnergy(player, SHADOW_STEP_COST);
        RogueCooldownData.startCooldown(player.getUuid(), SHADOW_STEP_KEY, worldTime, SHADOW_STEP_COOLDOWN);

        createShadowDecoy((ServerWorld) player.getWorld(), oldPos);

        // Fixed teleport for 1.21.8
        player.teleport(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, true);

        player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 60, 0));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 10, 4));

        spawnShadowStepParticles((ServerWorld) player.getWorld(), oldPos, player.getPos());
        player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0f, 0.5f);

        player.sendMessage(Text.literal("Shadow Step!")
                .formatted(Formatting.DARK_PURPLE), true);

        return true;
    }

    private static void createShadowDecoy(ServerWorld world, Vec3d pos) {
        AreaEffectCloudEntity decoy = new AreaEffectCloudEntity(world, pos.x, pos.y, pos.z);
        decoy.setRadius(1.0f);
        decoy.setDuration(60);
        decoy.setRadiusGrowth(0.0f);
        decoy.setParticleType(ParticleTypes.SMOKE);
        // Note: setColor() removed

        world.spawnEntity(decoy);

        for (int i = 0; i < 30; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 1.5;
            double offsetY = world.random.nextDouble() * 2;
            double offsetZ = (world.random.nextDouble() - 0.5) * 1.5;

            world.spawnParticles(ParticleTypes.PORTAL,
                    pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ,
                    1, 0, 0, 0, 0.1);
        }
    }

    private static void spawnShadowStepParticles(ServerWorld world, Vec3d from, Vec3d to) {
        for (int i = 0; i < 30; i++) {
            world.spawnParticles(ParticleTypes.PORTAL,
                    from.x, from.y + 1, from.z,
                    1, 0.5, 1, 0.5, 0.1);
        }

        for (int i = 0; i < 30; i++) {
            world.spawnParticles(ParticleTypes.REVERSE_PORTAL,
                    to.x, to.y + 1, to.z,
                    1, 0.5, 1, 0.5, 0.1);
        }

        Vec3d direction = to.subtract(from).normalize();
        double distance = from.distanceTo(to);
        for (double d = 0; d < distance; d += 0.5) {
            Vec3d point = from.add(direction.multiply(d));
            world.spawnParticles(ParticleTypes.DRAGON_BREATH,
                    point.x, point.y + 1, point.z,
                    1, 0.1, 0.1, 0.1, 0.01);
        }
    }
}