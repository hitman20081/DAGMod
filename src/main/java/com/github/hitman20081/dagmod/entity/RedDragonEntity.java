package com.github.hitman20081.dagmod.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * Quest-exclusive Red Dragon — spawns only when the "red_dragon_fury" quest is accepted.
 * Never spawns naturally (SpawnRestriction blocks it).
 * Always drops Dragon Heart on death (guaranteed).
 * Cannot be tamed.
 */
public class RedDragonEntity extends WildDragonEntity {

    public RedDragonEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Text getName() {
        return Text.literal("Red Dragon").formatted(Formatting.RED);
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (this.getEntityWorld().isClient()) {
            return ActionResult.FAIL;
        }
        player.sendMessage(Text.literal("This dragon cannot be tamed!").formatted(Formatting.RED), true);
        return ActionResult.FAIL;
    }

    @Override
    protected boolean alwaysDropHeart() {
        return true;
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return false;
    }
}
