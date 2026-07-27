package com.github.hitman20081.dagmod.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.sounds.SoundEvents;

import java.util.List;
import java.util.Optional;

/**
 * Custom shield item for DAGMod shields.
 * Extends vanilla ShieldItem to work with the builtin/entity model parent.
 * The shieldType field can be used later for custom texture rendering via mixin.
 */
public class DagModShieldItem extends ShieldItem {
    public final String shieldType;

    public DagModShieldItem(String shieldType, Properties settings) {
        super(applyShieldComponent(settings));
        this.shieldType = shieldType;
    }

    /**
     * Applies the BLOCKS_ATTACKS component to the item settings.
     * This is required in 1.21+ for shields to actually block damage.
     */
    private static Properties applyShieldComponent(Properties settings) {
        // Create a BlocksAttacks similar to vanilla shield
        // Constructor: (blockDelaySeconds, disableCooldownScale, damageReductions, itemDamage, bypassesTag, blockSound, disableSound)
        BlocksAttacks blocksAttacks = new BlocksAttacks(
                0.25f,  // block_delay_seconds (5 ticks)
                1.0f,   // disable_cooldown_scale
                List.of(
                        // Block all damage (90 degree angle, no type filter, 0 base, 100% factor)
                        new BlocksAttacks.DamageReduction(
                                90.0f,          // horizontal_blocking_angle
                                Optional.empty(), // all damage types
                                0.0f,           // base
                                1.0f            // factor (100% blocked)
                        )
                ),
                new BlocksAttacks.ItemDamageFunction(
                        1.0f,   // threshold
                        0.0f,   // base
                        1.0f    // factor
                ),
                Optional.empty(), // damage types that bypass
                Optional.of(SoundEvents.SHIELD_BLOCK),
                Optional.of(SoundEvents.SHIELD_BREAK)
        );

        return settings.component(DataComponents.BLOCKS_ATTACKS, blocksAttacks);
    }
}
