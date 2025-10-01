
package com.github.hitman20081.dagmod.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class ClassTokenItem extends Item {
    private final String className;
    private final Formatting color;
    private final String symbol;

    public ClassTokenItem(Settings settings, String className, Formatting color, String symbol) {
        super(settings);
        this.className = className;
        this.color = color;
        this.symbol = symbol;
    }


    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal("Right-click the Class Selection Altar")
                .formatted(Formatting.GRAY));
        tooltip.add(Text.literal("while holding this to become a " + className)
                .formatted(Formatting.GRAY));
        tooltip.add(Text.empty());
        tooltip.add(Text.literal(symbol + " " + className + " Path")
                .formatted(color));
    }
}