
package com.github.hitman20081.dagmod.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.List;

public class ClassTokenItem extends Item {
    private final String className;
    private final ChatFormatting color;
    private final String symbol;

    public ClassTokenItem(Properties settings, String className, ChatFormatting color, String symbol) {
        super(settings);
        this.className = className;
        this.color = color;
        this.symbol = symbol;
    }


    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        tooltip.add(Component.literal("Right-click the Class Selection Altar")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("while holding this to become a " + className)
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal(symbol + " " + className + " Path")
                .withStyle(color));
    }
}