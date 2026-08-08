package com.github.hitman20081.dagmod.class_system;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.util.ModTags;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Off-hand weapon strike: right-click directly on a valid combat target while a dagger/sword
 * (ModTags.Items.OFFHAND_DAGGERS/OFFHAND_SWORDS) sits in the off-hand -- see OffhandEquipGuard
 * for who's allowed to have one equipped in the first place. Deliberately built on
 * UseEntityCallback (right-click-on-entity) rather than a new keybind: it's the same event
 * ShieldBashListener/RogueCombatHandler already use, and it fires separately per hand, so this
 * only acts on the MAIN_HAND pass (which vanilla always evaluates first) to avoid handling the
 * same click twice while still reading the off-hand item directly rather than whatever's in
 * `hand`.
 *
 * Damage rules (per class + weapon):
 * - Warrior: full weapon damage with either a dagger or sword off-hand.
 * - Rogue: full weapon damage with a dagger off-hand (their specialty); halved with a sword
 *   off-hand (not their specialty, but not forbidden either).
 *
 * Base weapon damage is read directly off the off-hand ItemStack's own baked attack-damage
 * modifiers (as if it were the main-hand item -- see weaponBaseDamage) rather than the player's
 * live ATTACK_DAMAGE attribute, since that attribute is main-hand-scoped and would read 0 for
 * anything actually sitting in the off-hand.
 */
public class OffhandStrikeHandler {

    private static final int COOLDOWN_TICKS = 20; // 1 second, own cooldown independent of main-hand attack speed
    private static final float ROGUE_SWORD_PENALTY = 0.5f;

    private static final Map<UUID, Long> cooldownExpiry = new HashMap<>();

    public static void register() {
        UseEntityCallback.EVENT.register(OffhandStrikeHandler::onUseEntity);
    }

    private static InteractionResult onUseEntity(Player player, Level level, InteractionHand hand,
                                                  Entity entity, EntityHitResult hitResult) {
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        if (!isValidTarget(entity)) return InteractionResult.PASS;

        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUUID());
        boolean isWarrior = "Warrior".equals(playerClass);
        boolean isRogue = "Rogue".equals(playerClass);
        if (!isWarrior && !isRogue) return InteractionResult.PASS;

        ItemStack offhand = player.getOffhandItem();
        boolean isDagger = offhand.is(ModTags.Items.OFFHAND_DAGGERS);
        boolean isSword = offhand.is(ModTags.Items.OFFHAND_SWORDS);
        if (!isDagger && !isSword) return InteractionResult.PASS;

        // This callback fires on BOTH sides for the interacting player: once client-side as
        // vanilla's own local prediction pass (before the packet reaches the server), once more
        // server-side when it's actually received. Vanilla's own attack swing works the same way
        // -- the attacker's client predicts its own swing immediately rather than waiting on a
        // server round-trip, and the server's later swing() broadcast only reaches OTHER nearby
        // players, not back to the attacker. So: predict the swing here on the client (skipping
        // the cooldown check, which is server-only state the client doesn't track -- worst case
        // is a swing with no hit while on cooldown, purely cosmetic), then let the server side
        // below handle the actual damage and its own swing() broadcast for bystanders.
        if (level.isClientSide()) {
            player.swing(InteractionHand.OFF_HAND);
            return InteractionResult.PASS;
        }
        if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.PASS;

        LivingEntity target = (LivingEntity) entity;
        UUID uuid = player.getUUID();
        long gameTime = level.getGameTime();
        if (isOnCooldown(uuid, gameTime)) return InteractionResult.PASS;

        float damage = weaponBaseDamage(offhand);
        if (isRogue && isSword && !isDagger) {
            damage *= ROGUE_SWORD_PENALTY;
        }

        ServerLevel serverLevel = (ServerLevel) level;
        serverPlayer.swing(InteractionHand.OFF_HAND);
        target.hurt(serverLevel.damageSources().playerAttack(serverPlayer), damage);
        cooldownExpiry.put(uuid, gameTime + COOLDOWN_TICKS);

        serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK,
                target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(),
                3, 0.3, 0.3, 0.3, 0.0);
        serverLevel.playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.PLAYER_ATTACK_WEAK, SoundSource.PLAYERS, 1.0f, 1.3f);
        serverPlayer.sendOverlayMessage(Component.literal("Off-hand strike!")
                .withStyle(ChatFormatting.GRAY));

        return InteractionResult.SUCCESS;
    }

    /** Only ever true for something actually fightable -- never hijacks villager trading, taming/breeding, boat/minecart mounting, item frames, etc. */
    private static boolean isValidTarget(Entity entity) {
        if (!(entity instanceof LivingEntity target) || !target.isAlive()) return false;
        return entity instanceof Enemy || entity instanceof Player;
    }

    private static boolean isOnCooldown(UUID uuid, long gameTime) {
        Long expiry = cooldownExpiry.get(uuid);
        if (expiry == null) return false;
        if (gameTime >= expiry) {
            cooldownExpiry.remove(uuid);
            return false;
        }
        return true;
    }

    /** The weapon's own bonus attack damage (as if main-hand) plus base unarmed damage -- see class doc for why not the live attribute. */
    private static float weaponBaseDamage(ItemStack stack) {
        float[] bonus = {0f};
        stack.forEachModifier(EquipmentSlot.MAINHAND, (attribute, modifier) -> {
            if (attribute.is(Attributes.ATTACK_DAMAGE) && modifier.operation() == AttributeModifier.Operation.ADD_VALUE) {
                bonus[0] += (float) modifier.amount();
            }
        });
        return 1.0f + bonus[0];
    }
}
