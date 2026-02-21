
package com.github.hitman20081.dagmod.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class ClassResetCrystalItem extends Item {

    public ClassResetCrystalItem(Settings settings) {
        super(settings);
    }

    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal("A mysterious crystal that can reset")
                .formatted(Formatting.GRAY));
        tooltip.add(Text.literal("your class selection.")
                .formatted(Formatting.GRAY));
        tooltip.add(Text.empty());
        tooltip.add(Text.literal("Right-click the Class Selection Altar")
                .formatted(Formatting.YELLOW));
        tooltip.add(Text.literal("while holding this to reset your class.")
                .formatted(Formatting.YELLOW));
        tooltip.add(Text.empty());
        tooltip.add(Text.literal("Single Use - Choose Wisely!")
                .formatted(Formatting.RED).formatted(Formatting.BOLD));
    }
}

