package com.github.hitman20081.dagmod.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlocksAttacksComponent;
import net.minecraft.item.ShieldItem;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.sound.SoundEvents;

import java.util.List;
import java.util.Optional;

/**
 * Custom shield item for DAGMod shields.
 * Extends vanilla ShieldItem to work with the builtin/entity model parent.
 * The shieldType field can be used later for custom texture rendering via mixin.
 */
public class DagModShieldItem extends ShieldItem {
    public final String shieldType;

    public DagModShieldItem(String shieldType, Settings settings) {
        super(applyShieldComponent(settings));
        this.shieldType = shieldType;
    }

    /**
     * Applies the BLOCKS_ATTACKS component to the item settings.
     * This is required in 1.21+ for shields to actually block damage.
     */
    private static Settings applyShieldComponent(Settings settings) {
        // Create a BlocksAttacksComponent similar to vanilla shield
        // Constructor: (blockDelaySeconds, disableCooldownScale, damageReductions, itemDamage, bypassesTag, blockSound, disableSound)
        BlocksAttacksComponent blocksAttacks = new BlocksAttacksComponent(
                0.25f,  // block_delay_seconds (5 ticks)
                1.0f,   // disable_cooldown_scale
                List.of(
                        // Block all damage (90 degree angle, no type filter, 0 base, 100% factor)
                        new BlocksAttacksComponent.DamageReduction(
                                90.0f,          // horizontal_blocking_angle
                                Optional.empty(), // all damage types
                                0.0f,           // base
                                1.0f            // factor (100% blocked)
                        )
                ),
                new BlocksAttacksComponent.ItemDamage(
                        1.0f,   // threshold
                        0.0f,   // base
                        1.0f    // factor
                ),
                Optional.of(DamageTypeTags.BYPASSES_SHIELD), // damage types that bypass
                Optional.of(SoundEvents.ITEM_SHIELD_BLOCK),
                Optional.of(SoundEvents.ITEM_SHIELD_BREAK)
        );

        return settings.component(DataComponentTypes.BLOCKS_ATTACKS, blocksAttacks);
    }
}
