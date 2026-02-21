package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.class_system.mana.ManaData;
import com.github.hitman20081.dagmod.class_system.mana.ManaManager;
import com.github.hitman20081.dagmod.class_system.mana.ManaNetworking;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AdeptWandItem extends Item {
    private static final float MANA_COST = 12.0f;
    private static final int CHARGE_TIME = 40; // 2.0 seconds (40 ticks)

    public AdeptWandItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

        // Check if player is a Mage
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUuid());
        if (!"Mage".equals(playerClass)) {
            player.sendMessage(
                    Text.literal("Only Mages can use wands!")
                            .formatted(Formatting.RED),
                    true
            );
            return ActionResult.FAIL;
        }

        // Check mana
        ManaData manaData = ManaManager.getManaData(serverPlayer);
        if (!manaData.hasMana(MANA_COST)) {
            player.sendMessage(
                    Text.literal("Not enough mana! Need " + MANA_COST + " mana")
                            .formatted(Formatting.RED),
                    true
            );
            return ActionResult.FAIL;
        }

        // Start charging
        player.setCurrentHand(hand);
        return ActionResult.CONSUME;
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return false;

        int chargeTime = this.getMaxUseTime(stack, user) - remainingUseTicks;

        if (!world.isClient() && chargeTime >= CHARGE_TIME) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

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
                        SoundEvents.ITEM_FIRECHARGE_USE,
                        SoundCategory.PLAYERS,
                        1.2f,
                        1.1f
                );

                // Success message
                player.sendMessage(
                        Text.literal("✦ Fireball launched! ✦ (-" + String.format("%.0f", MANA_COST) + " mana)")
                                .formatted(Formatting.GOLD),
                        true
                );
                return true;
            }
        } else if (!world.isClient()) {
            // Not charged enough
            player.sendMessage(
                    Text.literal("Wand not fully charged!")
                            .formatted(Formatting.YELLOW),
                    true
            );
        }
        return false;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (world.isClient()) return;

        int chargeTime = this.getMaxUseTime(stack, user) - remainingUseTicks;

        ServerWorld serverWorld = (ServerWorld) world;
        Vec3d pos = user.getEyePos();

        // Spawn charging particles every 4 ticks (faster than apprentice)
        if (chargeTime % 4 == 0) {
            // Progress-based particles
            if (chargeTime < CHARGE_TIME) {
                // Charging - soul fire flame particles (blue fire)
                serverWorld.spawnParticles(
                        ParticleTypes.SOUL_FIRE_FLAME,
                        pos.x, pos.y, pos.z,
                        2, 0.2, 0.2, 0.2, 0.01
                );
            } else {
                // Fully charged - enchanted hit particles
                serverWorld.spawnParticles(
                        ParticleTypes.ENCHANTED_HIT,
                        pos.x, pos.y, pos.z,
                        3, 0.3, 0.3, 0.3, 0.02
                );
            }
        }

        // Play charging sound at certain intervals
        if (chargeTime == 20) {
            world.playSound(
                    null,
                    user.getX(),
                    user.getY(),
                    user.getZ(),
                    SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
                    SoundCategory.PLAYERS,
                    0.7f,
                    1.7f
            );
        }

        // Play fully charged sound
        if (chargeTime == CHARGE_TIME) {
            world.playSound(
                    null,
                    user.getX(),
                    user.getY(),
                    user.getZ(),
                    SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
                    SoundCategory.PLAYERS,
                    1.2f,
                    2.0f
            );

            if (user instanceof PlayerEntity player) {
                player.sendMessage(
                        Text.literal("✦ Wand fully charged! ✦")
                                .formatted(Formatting.AQUA),
                        true
                );
            }
        }
    }

    private void launchFireball(World world, PlayerEntity player) {
        Vec3d lookVec = player.getRotationVec(1.0f);
        Vec3d spawnPos = player.getEyePos().add(lookVec.multiply(0.5));

        // Create regular fireball (more damage than small fireball)
        FireballEntity fireball = new FireballEntity(
                world,
                player,
                lookVec,
                1 // explosion power
        );

        fireball.setPosition(spawnPos);
        world.spawnEntity(fireball);

        // Spawn launch particles (blue)
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(
                    ParticleTypes.SOUL_FIRE_FLAME,
                    spawnPos.x, spawnPos.y, spawnPos.z,
                    30, 0.1, 0.1, 0.1, 0.1
            );
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000; // Maximum use time (1 hour in ticks)
    }
}