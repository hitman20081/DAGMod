package com.github.hitman20081.dagmod.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class SimpleNPC extends PathAwareEntity {

    public SimpleNPC(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    // This sets up the NPC's basic stats (health, speed, etc.)
    public static DefaultAttributeContainer.Builder createMobAttributes() {
        return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 20.0) // Same as player
                .add(EntityAttributes.MOVEMENT_SPEED, 0.25); // Slightly slower than player
    }

    // This sets up the NPC's AI behaviors
    @Override
    protected void initGoals() {
        // Look at nearby players
        this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        // Wander around occasionally
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 0.8));
        // Look around randomly
        this.goalSelector.add(3, new LookAroundGoal(this));
    }

    // This handles what happens when a player right-clicks the NPC
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (!this.getEntityWorld().isClient()) { // Only run on server side
            player.sendMessage(Text.literal("Hello! I'm a simple NPC from DAGmod!"), false);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public boolean cannotDespawn() {
        return true; // NPC never despawns
    }


    public boolean damage(DamageSource source, float amount) {
        return false; // Ignore all damage - this is the main invulnerability method
    }

    public void pushAwayFrom(net.minecraft.entity.Entity entity) {
        // Don't get pushed by other entities
    }
}