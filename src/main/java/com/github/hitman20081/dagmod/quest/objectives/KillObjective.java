package com.github.hitman20081.dagmod.quest.objectives;

import com.github.hitman20081.dagmod.quest.QuestObjective;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;

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
        String entityName = entityType.getDescription().getString();
        return "Kill " + amount + " " + entityName + (amount > 1 ? "s" : "");
    }

    @Override
    public ObjectiveType getType() {
        return ObjectiveType.KILL;
    }

    @Override
    public boolean updateProgress(Player player, Object... params) {
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
        EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.parse(entityId));
        return new KillObjective(entityType, amount);
    }

    // Helper methods for common entities
    public static KillObjective zombies(int amount) {
        return fromIdentifier("minecraft:zombie", amount);
    }

    public static KillObjective skeletons(int amount) {
        return fromIdentifier("minecraft:skeleton", amount);
    }

    public static KillObjective spiders(int amount) {
        return fromIdentifier("minecraft:spider", amount);
    }

    @Override
    public QuestObjective copy() {
        return new KillObjective(this.targetEntityType, this.requiredKills);
    }
}