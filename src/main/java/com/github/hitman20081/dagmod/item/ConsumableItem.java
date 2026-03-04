package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.class_system.warrior.CooldownManager;
import com.github.hitman20081.dagmod.event.DodgeHandler;
import com.github.hitman20081.dagmod.event.FortuneDustHandler;
import com.github.hitman20081.dagmod.event.LastStandHandler;
import com.github.hitman20081.dagmod.event.ShadowBlendHandler;
import com.github.hitman20081.dagmod.event.SpellModifierHandler;
import com.github.hitman20081.dagmod.event.VampireDustHandler;
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
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

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

        boolean success = applyEffect(world, serverPlayer, playerClass);

        if (success) {
            stack.decrement(1);

            world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PLAYER_LEVELUP,
                    SoundCategory.PLAYERS, 0.5f, 1.5f);

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
                CooldownManager.reduceAllCooldowns(player, 600);
                player.sendMessage(Text.literal("⏰ Cooldown reduction active! ⏰")
                        .formatted(Formatting.GOLD), true);
                return true;
            }

            case VAMPIRE_DUST -> {
                if (!(world instanceof ServerWorld serverWorld)) return false;
                VampireDustHandler.activate(player.getUuid(), serverWorld.getTime());
                player.sendMessage(Text.literal("🩸 Vampire Dust active for 20 seconds! 🩸")
                        .formatted(Formatting.RED), true);
                return true;
            }

            case PHANTOM_DUST -> {
                if (!(world instanceof ServerWorld serverWorld)) return false;
                DodgeHandler.activate(player.getUuid(), 0.5f, serverWorld.getTime(), 300);
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
                SpellModifierHandler.activateSpellEcho(player.getUuid());
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
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 300, 1));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 300, 1));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 300, 2));
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
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 6000, 0));
                ShadowBlendHandler.activateShadowBlend(player.getUuid());
                player.sendMessage(Text.literal("🌑 Shadow Blend active! 🌑")
                        .formatted(Formatting.DARK_GRAY), true);
                return true;
            }

            case FORTUNE_DUST -> {
                FortuneDustHandler.activateFortuneDust(player.getUuid(), 10);
                player.sendMessage(Text.literal("💎 Fortune Dust active for next 10 blocks! 💎")
                        .formatted(Formatting.GREEN), true);
                return true;
            }

            case FEATHERFALL_POWDER -> {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 1200, 0));
                player.sendMessage(Text.literal("🪶 Featherfall active for 60 seconds! 🪶")
                        .formatted(Formatting.WHITE), true);
                return true;
            }

            case LAST_STAND_POWDER -> {
                LastStandHandler.activate(player.getUuid());
                player.sendMessage(Text.literal("✝ Last Stand ready! You will survive one lethal hit! ✝")
                        .formatted(Formatting.YELLOW), true);
                return true;
            }

            case TIME_DISTORTION -> {
                if (!(world instanceof ServerWorld serverWorld)) return false;
                // Speed II for self
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 200, 1));
                // Slowness IV on nearby enemies within 10 blocks
                Box searchBox = Box.of(player.getEntityPos(), 20, 20, 20);
                List<LivingEntity> nearbyEnemies = serverWorld.getEntitiesByClass(
                        LivingEntity.class,
                        searchBox,
                        e -> e != player && e.isAlive() && player.squaredDistanceTo(e) <= 100
                );
                for (LivingEntity enemy : nearbyEnemies) {
                    enemy.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 200, 3));
                }
                player.sendMessage(Text.literal("⏱ Time Distortion! Slowed " + nearbyEnemies.size() + " nearby enemies!")
                        .formatted(Formatting.DARK_AQUA), true);
                return true;
            }

            case OVERCHARGE_DUST -> {
                if (!"Mage".equals(playerClass)) {
                    player.sendMessage(Text.literal("Only Mages can use Overcharge Dust!")
                            .formatted(Formatting.RED), true);
                    return false;
                }
                SpellModifierHandler.activateOvercharge(player.getUuid());
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
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 400, 4));
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
                if (!(world instanceof ServerWorld serverWorld)) return false;
                DodgeHandler.activate(player.getUuid(), 1.0f, serverWorld.getTime(), 200);
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
