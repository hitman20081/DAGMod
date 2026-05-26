
package com.github.hitman20081.dagmod.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.List;

public class ClassResetCrystalItem extends Item {

    public ClassResetCrystalItem(Properties settings) {
        super(settings);
    }

    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        tooltip.add(Component.literal("A mysterious crystal that can reset")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("your class selection.")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("Right-click the Class Selection Altar")
                .withStyle(ChatFormatting.YELLOW));
        tooltip.add(Component.literal("while holding this to reset your class.")
                .withStyle(ChatFormatting.YELLOW));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("Single Use - Choose Wisely!")
                .withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
    }
}

