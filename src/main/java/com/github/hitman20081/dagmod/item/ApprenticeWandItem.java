package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.class_system.mana.ManaData;
import com.github.hitman20081.dagmod.class_system.mana.ManaManager;
import com.github.hitman20081.dagmod.class_system.mana.ManaNetworking;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball;
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
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class ApprenticeWandItem extends Item {
    private static final float MANA_COST = 15.0f;
    private static final int CHARGE_TIME = 50; // 2.5 seconds (50 ticks)

    public ApprenticeWandItem(Properties settings) {
        super(settings);
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
            player.sendOverlayMessage(
                    Component.literal("Only Mages can use wands!")
                            .withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }

        // Check mana
        ManaData manaData = ManaManager.getManaData(serverPlayer);
        if (!manaData.hasMana(MANA_COST)) {
            player.sendOverlayMessage(
                    Component.literal("Not enough mana! Need " + MANA_COST + " mana")
                            .withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }

        // Start charging
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof Player player)) return false;

        int chargeTime = this.getUseDuration(stack, user) - remainingUseTicks;

        if (!world.isClientSide() && chargeTime >= CHARGE_TIME) {
            ServerPlayer serverPlayer = (ServerPlayer) player;

            // Consume mana
            ManaData manaData = ManaManager.getManaData(serverPlayer);
            if (manaData.useMana(MANA_COST)) {
                // Sync mana to client
                ManaNetworking.sendManaUpdate(serverPlayer, manaData.getCurrentMana(), manaData.getMaxMana());

                // Launch fireball
                launchFireball(world, player);

                // Play sound
                world.playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.FIRECHARGE_USE,
                        SoundSource.PLAYERS,
                        1.0f,
                        1.0f
                );

                // Success message
                player.sendOverlayMessage(
                        Component.literal("✦ Fireball launched! ✦ (-" + String.format("%.0f", MANA_COST) + " mana)")
                                .withStyle(ChatFormatting.GOLD));
                return true;
            }
        } else if (!world.isClientSide()) {
            // Not charged enough
            player.sendOverlayMessage(
                    Component.literal("Wand not fully charged!")
                            .withStyle(ChatFormatting.YELLOW));
        }
        return false;
    }

    @Override
    public void onUseTick(Level world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (world.isClientSide()) return;

        int chargeTime = this.getUseDuration(stack, user) - remainingUseTicks;

        ServerLevel serverWorld = (ServerLevel) world;
        Vec3 pos = user.getEyePosition();

        // Spawn charging particles every 5 ticks
        if (chargeTime % 5 == 0) {
            // Progress-based particles
            if (chargeTime < CHARGE_TIME) {
                // Charging - flame particles
                serverWorld.sendParticles(
                        ParticleTypes.FLAME,
                        pos.x, pos.y, pos.z,
                        1, 0.2, 0.2, 0.2, 0.01
                );
            } else {
                // Fully charged - soul fire flame particles
                serverWorld.sendParticles(
                        ParticleTypes.SOUL_FIRE_FLAME,
                        pos.x, pos.y, pos.z,
                        2, 0.3, 0.3, 0.3, 0.02
                );
            }
        }

        // Play charging sound at certain intervals
        if (chargeTime == 25) {
            world.playSound(
                    null,
                    user.getX(),
                    user.getY(),
                    user.getZ(),
                    SoundEvents.ENCHANTMENT_TABLE_USE,
                    SoundSource.PLAYERS,
                    0.5f,
                    1.5f
            );
        }

        // Play fully charged sound
        if (chargeTime == CHARGE_TIME) {
            world.playSound(
                    null,
                    user.getX(),
                    user.getY(),
                    user.getZ(),
                    SoundEvents.ENCHANTMENT_TABLE_USE,
                    SoundSource.PLAYERS,
                    1.0f,
                    2.0f
            );

            if (user instanceof Player player) {
                player.sendOverlayMessage(
                        Component.literal("✦ Wand fully charged! ✦")
                                .withStyle(ChatFormatting.GREEN));
            }
        }
    }

    private void launchFireball(Level world, Player player) {
        Vec3 lookVec = player.getViewVector(1.0f);
        Vec3 spawnPos = player.getEyePosition().add(lookVec.scale(0.5));

        // Create fireball with owner set via constructor
        SmallFireball fireball = new SmallFireball(
                world,
                player,
                lookVec
        );
        fireball.setPos(spawnPos);
        world.addFreshEntity(fireball);

        // Spawn launch particles
        if (world instanceof ServerLevel serverWorld) {
            serverWorld.sendParticles(
                    ParticleTypes.FLAME,
                    spawnPos.x, spawnPos.y, spawnPos.z,
                    20, 0.1, 0.1, 0.1, 0.1
            );
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return 72000; // Maximum use time (1 hour in ticks)
    }
}