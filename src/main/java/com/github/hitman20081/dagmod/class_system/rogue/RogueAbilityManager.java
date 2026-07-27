package com.github.hitman20081.dagmod.class_system.rogue;

import com.github.hitman20081.dagmod.class_system.rogue.RogueCooldownData;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.ClipContext;

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
    public static boolean useSmokeBomb(ServerPlayer player) {
        ServerLevel world = (ServerLevel) player.level();
        long worldTime = world.getGameTime();

        // Check cooldown
        if (RogueCooldownData.isOnCooldown(player.getUUID(), SMOKE_BOMB_KEY, worldTime)) {
            long remaining = RogueCooldownData.getRemainingCooldown(player.getUUID(), SMOKE_BOMB_KEY, worldTime);
            player.sendOverlayMessage(Component.literal("Smoke Bomb on cooldown: " + (remaining / 20) + "s")
                    .withStyle(ChatFormatting.RED));
            return false;
        }

        // Check energy
        if (!EnergyManager.hasEnergy(player, SMOKE_BOMB_COST)) {
            player.sendOverlayMessage(Component.literal("Not enough energy! Need " + SMOKE_BOMB_COST)
                    .withStyle(ChatFormatting.RED));
            return false;
        }

        // Consume energy
        EnergyManager.consumeEnergy(player, SMOKE_BOMB_COST);

        // Start cooldown
        RogueCooldownData.startCooldown(player.getUUID(), SMOKE_BOMB_KEY, worldTime, SMOKE_BOMB_COOLDOWN);

        // Apply effects
        player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 120, 0));
        player.addEffect(new MobEffectInstance(MobEffects.SPEED, 120, 1));

        // Create smoke cloud
        createSmokeCloud(player);

        // Sound
        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 1.0f, 0.5f);

        player.sendOverlayMessage(Component.literal("Smoke Bomb activated!")
                .withStyle(ChatFormatting.DARK_GRAY));

        return true;
    }

    private static void createSmokeCloud(ServerPlayer player) {
        ServerLevel world = (ServerLevel) player.level();
        Vec3 pos = player.position();

        AreaEffectCloud cloud = new AreaEffectCloud(world, pos.x, pos.y, pos.z);
        cloud.setRadius(3.0f);
        cloud.setDuration(100);
        cloud.setRadiusPerTick(0.0f);
        cloud.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 1));

        world.addFreshEntity(cloud);

        // Extra particles
        for (int i = 0; i < 50; i++) {
            double offsetX = (world.getRandom().nextDouble() - 0.5) * 4;
            double offsetY = world.getRandom().nextDouble() * 2;
            double offsetZ = (world.getRandom().nextDouble() - 0.5) * 4;

            world.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ,
                    1, 0.0, 0.0, 0.0, 0.01);
        }
    }

    /**
     * Activate Poison Dagger
     */
    public static boolean usePoisonDagger(ServerPlayer player) {
        if (!EnergyManager.hasEnergy(player, POISON_DAGGER_COST)) {
            player.sendOverlayMessage(Component.literal("Not enough energy! Need " + POISON_DAGGER_COST)
                    .withStyle(ChatFormatting.RED));
            return false;
        }

        EnergyManager.consumeEnergy(player, POISON_DAGGER_COST);
        poisonDaggerActive.put(player.getUUID(), System.currentTimeMillis());

        player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 0));

        ServerLevel world = (ServerLevel) player.level();
        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 1.0f, 0.7f);

        Vec3 pos = player.position();
        for (int i = 0; i < 20; i++) {
            double offsetX = (world.getRandom().nextDouble() - 0.5) * 1.5;
            double offsetY = world.getRandom().nextDouble() * 2;
            double offsetZ = (world.getRandom().nextDouble() - 0.5) * 1.5;

            // Changed to CAMPFIRE_COSY_SMOKE
            world.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ,
                    1, 0.0, 0.0, 0.0, 0.01);
        }

        player.sendOverlayMessage(Component.literal("Poison Dagger ready!")
                .withStyle(ChatFormatting.DARK_GREEN));

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
    public static boolean useShadowStep(ServerPlayer player) {
        ServerLevel world = (ServerLevel) player.level();
        long worldTime = world.getGameTime();

        if (RogueCooldownData.isOnCooldown(player.getUUID(), SHADOW_STEP_KEY, worldTime)) {
            long remaining = RogueCooldownData.getRemainingCooldown(player.getUUID(), SHADOW_STEP_KEY, worldTime);
            player.sendOverlayMessage(Component.literal("Shadow Step on cooldown: " + (remaining / 20) + "s")
                    .withStyle(ChatFormatting.RED));
            return false;
        }

        if (!EnergyManager.hasEnergy(player, SHADOW_STEP_COST)) {
            player.sendOverlayMessage(Component.literal("Not enough energy! Need " + SHADOW_STEP_COST)
                    .withStyle(ChatFormatting.RED));
            return false;
        }

        // Raycast
        Vec3 start = player.getEyePosition();
        Vec3 direction = player.getViewVector(1.0f);
        Vec3 end = start.add(direction.scale(25));

        BlockHitResult hitResult = world.clip(new ClipContext(
                start, end, ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE, player
        ));

        if (hitResult.getType() == HitResult.Type.MISS) {
            player.sendOverlayMessage(Component.literal("No valid target location!")
                    .withStyle(ChatFormatting.RED));
            return false;
        }

        BlockPos targetPos = hitResult.getBlockPos().relative(hitResult.getDirection());
        Vec3 oldPos = player.position();

        EnergyManager.consumeEnergy(player, SHADOW_STEP_COST);
        RogueCooldownData.startCooldown(player.getUUID(), SHADOW_STEP_KEY, worldTime, SHADOW_STEP_COOLDOWN);

        createShadowDecoy(world, oldPos);

        // Teleport
        player.connection.teleport(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, player.getYRot(), player.getXRot());

        player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 60, 0));
        player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 10, 4));

        spawnShadowStepParticles(world, oldPos, player.position());
        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 0.5f);

        player.sendOverlayMessage(Component.literal("Shadow Step!")
                .withStyle(ChatFormatting.DARK_PURPLE));

        return true;
    }

    private static void createShadowDecoy(ServerLevel world, Vec3 pos) {
        AreaEffectCloud decoy = new AreaEffectCloud(world, pos.x, pos.y, pos.z);
        decoy.setRadius(1.0f);
        decoy.setDuration(60);
        decoy.setRadiusPerTick(0.0f);

        world.addFreshEntity(decoy);

        for (int i = 0; i < 30; i++) {
            double offsetX = (world.getRandom().nextDouble() - 0.5) * 1.5;
            double offsetY = world.getRandom().nextDouble() * 2;
            double offsetZ = (world.getRandom().nextDouble() - 0.5) * 1.5;

            world.sendParticles(ParticleTypes.PORTAL,
                    pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ,
                    1, 0.0, 0.0, 0.0, 0.1);
        }
    }

    private static void spawnShadowStepParticles(ServerLevel world, Vec3 from, Vec3 to) {
        for (int i = 0; i < 30; i++) {
            world.sendParticles(ParticleTypes.PORTAL,
                    from.x, from.y + 1, from.z,
                    1, 0.5, 1.0, 0.5, 0.1);
        }

        for (int i = 0; i < 30; i++) {
            world.sendParticles(ParticleTypes.REVERSE_PORTAL,
                    to.x, to.y + 1, to.z,
                    1, 0.5, 1.0, 0.5, 0.1);
        }

        Vec3 direction = to.subtract(from).normalize();
        double distance = from.distanceTo(to);
        for (double d = 0; d < distance; d += 0.5) {
            Vec3 point = from.add(direction.scale(d));
            // Changed to WITCH particle
            world.sendParticles(ParticleTypes.WITCH,
                    point.x, point.y + 1, point.z,
                    1, 0.1, 0.1, 0.1, 0.01);
        }
    }
}