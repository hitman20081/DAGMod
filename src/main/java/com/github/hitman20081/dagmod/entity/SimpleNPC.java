package com.github.hitman20081.dagmod.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

public class SimpleNPC extends PathfinderMob {

    public SimpleNPC(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
    }

    // This sets up the NPC's basic stats (health, speed, etc.)
    public static AttributeSupplier.Builder createMobAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0) // Same as player
                .add(Attributes.MOVEMENT_SPEED, 0.25); // Slightly slower than player
    }

    // This sets up the NPC's AI behaviors
    @Override
    protected void registerGoals() {
        // Look at nearby players
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0f));
        // Wander around occasionally
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.8));
        // Look around randomly
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }

    // This handles what happens when a player right-clicks the NPC
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide()) { // Only run on server side
            player.sendSystemMessage(Component.literal("Hello! I'm a simple NPC from DAGmod!"));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean isPersistenceRequired() {
        return true; // NPC never despawns
    }


    public boolean damage(DamageSource source, float amount) {
        return false; // Ignore all damage - this is the main invulnerability method
    }

    public void pushAwayFrom(net.minecraft.world.entity.Entity entity) {
        // Don't get pushed by other entities
    }
}