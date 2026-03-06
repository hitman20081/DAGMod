package com.github.hitman20081.dagmod.block;

import net.minecraft.block.Block;

/**
 * Invisible trigger block placed in boss room NBTs.
 * When a player comes within range, BossRoomSpawnHandler spawns the boss and removes this block.
 * Indestructible in survival. Has no collision so it can be hidden inside walls or floors.
 */
public class BossSpawnTriggerBlock extends Block {

    public BossSpawnTriggerBlock(Settings settings) {
        super(settings);
    }
}
