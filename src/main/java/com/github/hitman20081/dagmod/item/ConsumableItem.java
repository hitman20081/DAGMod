package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.class_system.warrior.CooldownManager;
import com.github.hitman20081.dagmod.event.DodgeHandler;
import com.github.hitman20081.dagmod.event.FortuneDustHandler;
import com.github.hitman20081.dagmod.event.LastStandHandler;
import com.github.hitman20081.dagmod.event.ShadowBlendHandler;
import com.github.hitman20081.dagmod.event.SpellModifierHandler;
import com.github.hitman20081.dagmod.event.VampireDustHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;

import java.util.List;

public class ConsumableItem extends Item {
    private final ConsumableType type;

    public enum ConsumableType {
        MANA_CRYSTAL("Mana Crystal", ChatFormatting.AQUA),
        ENERGY_TONIC("Energy Tonic", ChatFormatting.DARK_PURPLE),
        COOLDOWN_ELIXIR("Cooldown Elixir", ChatFormatting.GOLD),
        VAMPIRE_DUST("Vampire Dust", ChatFormatting.RED),
        PHANTOM_DUST("Phantom Dust", ChatFormatting.GRAY),
        SPELL_ECHO("Spell Echo", ChatFormatting.LIGHT_PURPLE),
        BATTLE_FRENZY("Battle Frenzy", ChatFormatting.DARK_RED),
        SHADOW_BLEND("Shadow Blend", ChatFormatting.DARK_GRAY),
        FORTUNE_DUST("Fortune Dust", ChatFormatting.GREEN),
        FEATHERFALL_POWDER("Featherfall Powder", ChatFormatting.WHITE),
        LAST_STAND_POWDER("Last Stand Powder", ChatFormatting.YELLOW),
        TIME_DISTORTION("Time Distortion", ChatFormatting.DARK_AQUA),
        OVERCHARGE_DUST("Overcharge Dust", ChatFormatting.BLUE),
        TITAN_STRENGTH("Titan's Strength", ChatFormatting.DARK_RED),
        PERFECT_DODGE("Perfect Dodge", ChatFormatting.WHITE);

        private final String displayName;
        private final ChatFormatting color;

        ConsumableType(String displayName, ChatFormatting color) {
            this.displayName = displayName;
            this.color = color;
        }

        public String getDisplayName() { return displayName; }
        public ChatFormatting getColor() { return color; }
    }

    public ConsumableItem(Properties settings, ConsumableType type) {
        super(settings);
        this.type = type;
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ServerPlayer serverPlayer = (ServerPlayer) player;
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUUID());

        boolean success = applyEffect(world, serverPlayer, playerClass);

        if (success) {
            stack.shrink(1);

            world.playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP,
                    SoundSource.PLAYERS, 0.5f, 1.5f);

            if (world instanceof ServerLevel serverWorld) {
                spawnParticles(serverWorld, player);
            }

            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.FAIL;
        }
    }

    private boolean applyEffect(Level world, ServerPlayer player, String playerClass) {
        switch (type) {
            case MANA_CRYSTAL -> {
                if (!"Mage".equals(playerClass)) {
                    player.sendOverlayMessage(Component.literal("Only Mages can use Mana Crystals!")
                            .withStyle(ChatFormatting.RED));
                    return false;
                }
                com.github.hitman20081.dagmod.class_system.mana.ManaData manaData =
                        com.github.hitman20081.dagmod.class_system.mana.ManaManager.getManaData(player);
                manaData.addMana(50);
                player.sendOverlayMessage(Component.literal("✦ Restored 50 Mana! ✦")
                        .withStyle(ChatFormatting.AQUA));
                return true;
            }

            case ENERGY_TONIC -> {
                if (!"Rogue".equals(playerClass)) {
                    player.sendOverlayMessage(Component.literal("Only Rogues can use Energy Tonics!")
                            .withStyle(ChatFormatting.RED));
                    return false;
                }
                com.github.hitman20081.dagmod.class_system.rogue.EnergyManager.addEnergy(player, 50);
                player.sendOverlayMessage(Component.literal("⚡ Restored 50 Energy! ⚡")
                        .withStyle(ChatFormatting.DARK_PURPLE));
                return true;
            }

            case COOLDOWN_ELIXIR -> {
                if (!"Warrior".equals(playerClass)) {
                    player.sendOverlayMessage(Component.literal("Only Warriors can use Cooldown Elixirs!")
                            .withStyle(ChatFormatting.RED));
                    return false;
                }
                CooldownManager.reduceAllCooldowns(player, 600);
                player.sendOverlayMessage(Component.literal("⏰ Cooldown reduction active! ⏰")
                        .withStyle(ChatFormatting.GOLD));
                return true;
            }

            case VAMPIRE_DUST -> {
                if (!(world instanceof ServerLevel serverWorld)) return false;
                VampireDustHandler.activate(player.getUUID(), serverWorld.getGameTime());
                player.sendOverlayMessage(Component.literal("🩸 Vampire Dust active for 20 seconds! 🩸")
                        .withStyle(ChatFormatting.RED));
                return true;
            }

            case PHANTOM_DUST -> {
                if (!(world instanceof ServerLevel serverWorld)) return false;
                DodgeHandler.activate(player.getUUID(), 0.5f, serverWorld.getGameTime(), 300);
                player.sendOverlayMessage(Component.literal("👻 Phantom Dust active for 15 seconds! 👻")
                        .withStyle(ChatFormatting.GRAY));
                return true;
            }

            case SPELL_ECHO -> {
                if (!"Mage".equals(playerClass)) {
                    player.sendOverlayMessage(Component.literal("Only Mages can use Spell Echo!")
                            .withStyle(ChatFormatting.RED));
                    return false;
                }
                SpellModifierHandler.activateSpellEcho(player.getUUID());
                player.sendOverlayMessage(Component.literal("✨ Next spell will cast twice! ✨")
                        .withStyle(ChatFormatting.LIGHT_PURPLE));
                return true;
            }

            case BATTLE_FRENZY -> {
                if (!"Warrior".equals(playerClass)) {
                    player.sendOverlayMessage(Component.literal("Only Warriors can use Battle Frenzy!")
                            .withStyle(ChatFormatting.RED));
                    return false;
                }
                player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 300, 1));
                player.addEffect(new MobEffectInstance(MobEffects.SPEED, 300, 1));
                player.addEffect(new MobEffectInstance(MobEffects.HASTE, 300, 2));
                player.sendOverlayMessage(Component.literal("⚔ Battle Frenzy active for 15 seconds! ⚔")
                        .withStyle(ChatFormatting.DARK_RED));
                return true;
            }

            case SHADOW_BLEND -> {
                if (!"Rogue".equals(playerClass)) {
                    player.sendOverlayMessage(Component.literal("Only Rogues can use Shadow Blend!")
                            .withStyle(ChatFormatting.RED));
                    return false;
                }
                player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 6000, 0));
                ShadowBlendHandler.activateShadowBlend(player.getUUID());
                player.sendOverlayMessage(Component.literal("🌑 Shadow Blend active! 🌑")
                        .withStyle(ChatFormatting.DARK_GRAY));
                return true;
            }

            case FORTUNE_DUST -> {
                FortuneDustHandler.activateFortuneDust(player.getUUID(), 10);
                player.sendOverlayMessage(Component.literal("💎 Fortune Dust active for next 10 blocks! 💎")
                        .withStyle(ChatFormatting.GREEN));
                return true;
            }

            case FEATHERFALL_POWDER -> {
                player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 1200, 0));
                player.sendOverlayMessage(Component.literal("🪶 Featherfall active for 60 seconds! 🪶")
                        .withStyle(ChatFormatting.WHITE));
                return true;
            }

            case LAST_STAND_POWDER -> {
                LastStandHandler.activate(player.getUUID());
                player.sendOverlayMessage(Component.literal("✝ Last Stand ready! You will survive one lethal hit! ✝")
                        .withStyle(ChatFormatting.YELLOW));
                return true;
            }

            case TIME_DISTORTION -> {
                if (!(world instanceof ServerLevel serverWorld)) return false;
                // Speed II for self
                player.addEffect(new MobEffectInstance(MobEffects.SPEED, 200, 1));
                // Slowness IV on nearby enemies within 10 blocks
                AABB searchBox = AABB.ofSize(player.position(), 20, 20, 20);
                List<LivingEntity> nearbyEnemies = serverWorld.getEntitiesOfClass(
                        LivingEntity.class,
                        searchBox,
                        e -> e != player && e.isAlive() && player.distanceToSqr(e) <= 100
                );
                for (LivingEntity enemy : nearbyEnemies) {
                    enemy.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 200, 3));
                }
                player.sendOverlayMessage(Component.literal("⏱ Time Distortion! Slowed " + nearbyEnemies.size() + " nearby enemies!")
                        .withStyle(ChatFormatting.DARK_AQUA));
                return true;
            }

            case OVERCHARGE_DUST -> {
                if (!"Mage".equals(playerClass)) {
                    player.sendOverlayMessage(Component.literal("Only Mages can use Overcharge Dust!")
                            .withStyle(ChatFormatting.RED));
                    return false;
                }
                SpellModifierHandler.activateOvercharge(player.getUUID());
                player.sendOverlayMessage(Component.literal("⚡ Next spell has 2x power! ⚡")
                        .withStyle(ChatFormatting.BLUE));
                return true;
            }

            case TITAN_STRENGTH -> {
                if (!"Warrior".equals(playerClass)) {
                    player.sendOverlayMessage(Component.literal("Only Warriors can use Titan's Strength!")
                            .withStyle(ChatFormatting.RED));
                    return false;
                }
                player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 400, 4));
                player.sendOverlayMessage(Component.literal("💪 Titan's Strength active for 20 seconds! 💪")
                        .withStyle(ChatFormatting.DARK_RED));
                return true;
            }

            case PERFECT_DODGE -> {
                if (!"Rogue".equals(playerClass)) {
                    player.sendOverlayMessage(Component.literal("Only Rogues can use Perfect Dodge!")
                            .withStyle(ChatFormatting.RED));
                    return false;
                }
                if (!(world instanceof ServerLevel serverWorld)) return false;
                DodgeHandler.activate(player.getUUID(), 1.0f, serverWorld.getGameTime(), 200);
                player.sendOverlayMessage(Component.literal("⚡ Perfect Dodge active for 10 seconds! ⚡")
                        .withStyle(ChatFormatting.WHITE));
                return true;
            }
        }
        return false;
    }

    private void spawnParticles(ServerLevel world, Player player) {
        net.minecraft.core.particles.ParticleOptions particle = switch (type) {
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
            double offsetX = (world.getRandom().nextDouble() - 0.5) * 2;
            double offsetY = world.getRandom().nextDouble() * 2;
            double offsetZ = (world.getRandom().nextDouble() - 0.5) * 2;

            world.sendParticles(particle,
                    player.getX() + offsetX,
                    player.getY() + offsetY,
                    player.getZ() + offsetZ,
                    1, 0, 0, 0, 0);
        }
    }
}
