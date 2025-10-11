package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraft.world.World;

public class ConsumableItem extends Item {
    private final ConsumableType type;

    public enum ConsumableType {
        MANA_CRYSTAL("Mana Crystal", Formatting.AQUA),
        ENERGY_TONIC("Energy Tonic", Formatting.DARK_PURPLE),
        COOLDOWN_ELIXIR("Cooldown Elixir", Formatting.GOLD),
        VAMPIRE_DUST("Vampire Dust", Formatting.RED),
        PHANTOM_DUST("Phantom Dust", Formatting.GRAY),
        SPELL_ECHO("Spell Echo", Formatting.LIGHT_PURPLE),
        BATTLE_FRENZY("Battle Frenzy", Formatting.DARK_RED),
        SHADOW_BLEND("Shadow Blend", Formatting.DARK_GRAY),
        FORTUNE_DUST("Fortune Dust", Formatting.GREEN),
        FEATHERFALL_POWDER("Featherfall Powder", Formatting.WHITE),
        LAST_STAND_POWDER("Last Stand Powder", Formatting.YELLOW),
        TIME_DISTORTION("Time Distortion", Formatting.DARK_AQUA),
        OVERCHARGE_DUST("Overcharge Dust", Formatting.BLUE),
        TITAN_STRENGTH("Titan's Strength", Formatting.DARK_RED),
        PERFECT_DODGE("Perfect Dodge", Formatting.WHITE);

        private final String displayName;
        private final Formatting color;

        ConsumableType(String displayName, Formatting color) {
            this.displayName = displayName;
            this.color = color;
        }

        public String getDisplayName() { return displayName; }
        public Formatting getColor() { return color; }
    }

    public ConsumableItem(Settings settings, ConsumableType type) {
        super(settings);
        this.type = type;
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUuid());

        // Apply consumable effect
        boolean success = applyEffect(world, serverPlayer, playerClass);

        if (success) {
            // Consume the item
            stack.decrement(1);

            // Play sound
            world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PLAYER_LEVELUP,
                    SoundCategory.PLAYERS, 0.5f, 1.5f);

            // Spawn particles
            if (world instanceof ServerWorld serverWorld) {
                spawnParticles(serverWorld, player);
            }

            return ActionResult.SUCCESS;
        } else {
            return ActionResult.FAIL;
        }
    }

    private boolean applyEffect(World world, ServerPlayerEntity player, String playerClass) {
        switch (type) {
            case MANA_CRYSTAL -> {
                if (!"Mage".equals(playerClass)) {
                    player.sendMessage(Text.literal("Only Mages can use Mana Crystals!")
                            .formatted(Formatting.RED), true);
                    return false;
                }
                // Restore 50 mana
                com.github.hitman20081.dagmod.class_system.mana.ManaData manaData =
                        com.github.hitman20081.dagmod.class_system.mana.ManaManager.getManaData(player);
                manaData.addMana(50);
                player.sendMessage(Text.literal("✦ Restored 50 Mana! ✦")
                        .formatted(Formatting.AQUA), true);
                return true;
            }

            case ENERGY_TONIC -> {
                if (!"Rogue".equals(playerClass)) {
                    player.sendMessage(Text.literal("Only Rogues can use Energy Tonics!")
                            .formatted(Formatting.RED), true);
                    return false;
                }
                // Restore 50 energy using the public method
                com.github.hitman20081.dagmod.class_system.rogue.EnergyManager.addEnergy(player, 50);
                player.sendMessage(Text.literal("⚡ Restored 50 Energy! ⚡")
                        .formatted(Formatting.DARK_PURPLE), true);
                return true;
            }

            case COOLDOWN_ELIXIR -> {
                if (!"Warrior".equals(playerClass)) {
                    player.sendMessage(Text.literal("Only Warriors can use Cooldown Elixirs!")
                            .formatted(Formatting.RED), true);
                    return false;
                }
                // Reduce all cooldowns by 30 seconds - SIMPLIFIED
                // TODO: Implement actual cooldown reduction via CooldownManager
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 600, 2)); // Temporary effect
                player.sendMessage(Text.literal("⏰ Cooldown reduction active! ⏰")
                        .formatted(Formatting.GOLD), true);
                return true;
            }

            case VAMPIRE_DUST -> {
                // Give lifesteal effect (custom effect or use absorption + regeneration)
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 400, 1)); // 20s Regen II
                // TODO: Implement actual lifesteal mechanic in future
                player.sendMessage(Text.literal("🩸 Vampire Dust active for 20 seconds! 🩸")
                        .formatted(Formatting.RED), true);
                return true;
            }

            case PHANTOM_DUST -> {
                // 50% dodge chance - implement via custom effect or resistance
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 300, 2)); // 15s Resistance III
                // TODO: Implement actual dodge mechanic in future
                player.sendMessage(Text.literal("👻 Phantom Dust active for 15 seconds! 👻")
                        .formatted(Formatting.GRAY), true);
                return true;
            }

            case SPELL_ECHO -> {
                if (!"Mage".equals(playerClass)) {
                    player.sendMessage(Text.literal("Only Mages can use Spell Echo!")
                            .formatted(Formatting.RED), true);
                    return false;
                }
                // TODO: Implement spell doubling mechanic
                player.sendMessage(Text.literal("✨ Next spell will cast twice! ✨")
                        .formatted(Formatting.LIGHT_PURPLE), true);
                return true;
            }

            case BATTLE_FRENZY -> {
                if (!"Warrior".equals(playerClass)) {
                    player.sendMessage(Text.literal("Only Warriors can use Battle Frenzy!")
                            .formatted(Formatting.RED), true);
                    return false;
                }
                // Battle frenzy effects - SIMPLIFIED
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 300, 1)); // 15s Strength II
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 300, 1)); // 15s Speed II
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 300, 2)); // 15s Haste III
                player.sendMessage(Text.literal("⚔ Battle Frenzy active for 15 seconds! ⚔")
                        .formatted(Formatting.DARK_RED), true);
                return true;
            }

            case SHADOW_BLEND -> {
                if (!"Rogue".equals(playerClass)) {
                    player.sendMessage(Text.literal("Only Rogues can use Shadow Blend!")
                            .formatted(Formatting.RED), true);
                    return false;
                }
                // Invisibility until attack
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 6000, 0)); // 5 minutes
                // TODO: Remove invisibility on attack
                player.sendMessage(Text.literal("🌑 Shadow Blend active! 🌑")
                        .formatted(Formatting.DARK_GRAY), true);
                return true;
            }

            case FORTUNE_DUST -> {
                // Fortune III on next 10 blocks
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.LUCK, 600, 2)); // 30s Luck III (approximation)
                // TODO: Track block counter for exact 10 blocks
                player.sendMessage(Text.literal("💎 Fortune Dust active! 💎")
                        .formatted(Formatting.GREEN), true);
                return true;
            }

            case FEATHERFALL_POWDER -> {
                // No fall damage for 60 seconds
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 1200, 0)); // 60s
                player.sendMessage(Text.literal("🪶 Featherfall active for 60 seconds! 🪶")
                        .formatted(Formatting.WHITE), true);
                return true;
            }

            case LAST_STAND_POWDER -> {
                // Auto-revive - requires custom implementation
                // TODO: Implement totem-like revive mechanic
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 1200, 3)); // 60s Absorption IV
                player.sendMessage(Text.literal("✝ Last Stand active for 60 seconds! ✝")
                        .formatted(Formatting.YELLOW), true);
                return true;
            }

            case TIME_DISTORTION -> {
                // Slow motion effect
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 200, 4)); // 10s Speed V
                // TODO: Add slow motion effect to nearby entities
                player.sendMessage(Text.literal("⏱ Time Distortion active for 10 seconds! ⏱")
                        .formatted(Formatting.DARK_AQUA), true);
                return true;
            }

            case OVERCHARGE_DUST -> {
                if (!"Mage".equals(playerClass)) {
                    player.sendMessage(Text.literal("Only Mages can use Overcharge Dust!")
                            .formatted(Formatting.RED), true);
                    return false;
                }
                // TODO: Implement 2x spell power for next spell
                player.sendMessage(Text.literal("⚡ Next spell has 2x power! ⚡")
                        .formatted(Formatting.BLUE), true);
                return true;
            }

            case TITAN_STRENGTH -> {
                if (!"Warrior".equals(playerClass)) {
                    player.sendMessage(Text.literal("Only Warriors can use Titan's Strength!")
                            .formatted(Formatting.RED), true);
                    return false;
                }
                // +100% melee damage
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 400, 4)); // 20s Strength V
                player.sendMessage(Text.literal("💪 Titan's Strength active for 20 seconds! 💪")
                        .formatted(Formatting.DARK_RED), true);
                return true;
            }

            case PERFECT_DODGE -> {
                if (!"Rogue".equals(playerClass)) {
                    player.sendMessage(Text.literal("Only Rogues can use Perfect Dodge!")
                            .formatted(Formatting.RED), true);
                    return false;
                }
                // 100% dodge
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 200, 4)); // 10s Resistance V
                // TODO: Implement actual 100% dodge mechanic
                player.sendMessage(Text.literal("⚡ Perfect Dodge active for 10 seconds! ⚡")
                        .formatted(Formatting.WHITE), true);
                return true;
            }
        }
        return false;
    }

    private void spawnParticles(ServerWorld world, PlayerEntity player) {
        net.minecraft.particle.ParticleEffect particle = switch (type) {
            case MANA_CRYSTAL, SPELL_ECHO, OVERCHARGE_DUST -> ParticleTypes.ENCHANT;
            case ENERGY_TONIC, PERFECT_DODGE -> ParticleTypes.ELECTRIC_SPARK;
            case VAMPIRE_DUST, TITAN_STRENGTH -> ParticleTypes.ANGRY_VILLAGER;
            case PHANTOM_DUST, SHADOW_BLEND -> ParticleTypes.SMOKE;
            case FORTUNE_DUST -> ParticleTypes.HAPPY_VILLAGER;
            case FEATHERFALL_POWDER -> ParticleTypes.CLOUD;
            case LAST_STAND_POWDER -> ParticleTypes.TOTEM_OF_UNDYING;
            case TIME_DISTORTION -> ParticleTypes.PORTAL;
            case BATTLE_FRENZY, COOLDOWN_ELIXIR -> ParticleTypes.FLAME;
        };

        for (int i = 0; i < 20; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 2;
            double offsetY = world.random.nextDouble() * 2;
            double offsetZ = (world.random.nextDouble() - 0.5) * 2;

            world.spawnParticles(particle,
                    player.getX() + offsetX,
                    player.getY() + offsetY,
                    player.getZ() + offsetZ,
                    1, 0, 0, 0, 0);
        }
    }
}