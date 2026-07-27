package com.github.hitman20081.dagmod.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

/**
 * Quest-exclusive Red Dragon — spawns only when the "red_dragon_fury" quest is accepted.
 * Never spawns naturally (SpawnRestriction blocks it).
 * Always drops Dragon Heart on death (guaranteed).
 * Cannot be tamed.
 */
public class RedDragonEntity extends WildDragonEntity {

    public RedDragonEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public Component getName() {
        return Component.literal("Red Dragon").withStyle(ChatFormatting.RED);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.level().isClientSide()) {
            return InteractionResult.FAIL;
        }
        player.sendOverlayMessage(Component.literal("This dragon cannot be tamed!").withStyle(ChatFormatting.RED));
        return InteractionResult.FAIL;
    }

    @Override
    protected boolean alwaysDropHeart() {
        return true;
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    @Override
    public boolean removeWhenFarAway(double distanceSquared) {
        return false;
    }
}
