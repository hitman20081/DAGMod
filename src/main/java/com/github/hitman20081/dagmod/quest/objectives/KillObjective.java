package com.github.hitman20081.dagmod.quest.objectives;

import com.github.hitman20081.dagmod.quest.QuestObjective;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class KillObjective extends QuestObjective {
    private final EntityType<?> targetEntityType;
    private final int requiredKills;

    public KillObjective(EntityType<?> targetEntityType, int requiredKills) {
        super(createDescription(targetEntityType, requiredKills), requiredKills);
        this.targetEntityType = targetEntityType;
        this.requiredKills = requiredKills;
    }

    // Create description text for the objective
    private static String createDescription(EntityType<?> entityType, int amount) {
        String entityName = entityType.getName().getString();
        return "Kill " + amount + " " + entityName + (amount > 1 ? "s" : "");
    }

    @Override
    public ObjectiveType getType() {
        return ObjectiveType.KILL;
    }

    @Override
    public boolean updateProgress(PlayerEntity player, Object... params) {
        // This will be called when an entity is killed
        // params[0] should be the EntityType of the killed entity
        if (params.length > 0 && params[0] instanceof EntityType<?> killedType) {
            if (killedType == targetEntityType) {
                int oldProgress = currentProgress;
                addProgress(1);
                return currentProgress > oldProgress;
            }
        }
        return false;
    }

    // Getters
    public EntityType<?> getTargetEntityType() {
        return targetEntityType;
    }

    public int getRequiredKills() {
        return requiredKills;
    }

    // Helper method to create KillObjective from entity identifier
    public static KillObjective fromIdentifier(String entityId, int amount) {
        EntityType<?> entityType = Registries.ENTITY_TYPE.get(Identifier.of(entityId));
        return new KillObjective(entityType, amount);
    }

    // Helper methods for common entities
    public static KillObjective zombies(int amount) {
        return new KillObjective(EntityType.ZOMBIE, amount);
    }

    public static KillObjective skeletons(int amount) {
        return new KillObjective(EntityType.SKELETON, amount);
    }

    public static KillObjective spiders(int amount) {
        return new KillObjective(EntityType.SPIDER, amount);
    }
}