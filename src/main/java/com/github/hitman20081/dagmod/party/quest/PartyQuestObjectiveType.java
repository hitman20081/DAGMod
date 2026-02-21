package com.github.hitman20081.dagmod.party.quest;

/**
 * Types of objectives for party quests
 */
public enum PartyQuestObjectiveType {
    KILL_ENTITY,      // Kill specific entities
    KILL_BOSS,        // Kill a boss entity
    COLLECT_ITEM,     // Collect specific items
    MINE_BLOCK,       // Mine specific blocks
    REACH_LOCATION,   // Reach a specific location
    SURVIVE_WAVES,    // Survive waves of enemies
    DEFEAT_ALL        // Defeat all enemies in an area
}