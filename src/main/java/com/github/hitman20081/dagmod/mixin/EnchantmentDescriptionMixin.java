package com.github.hitman20081.dagmod.mixin;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemEnchantments.class)
public class EnchantmentDescriptionMixin {

    @Inject(method = "addToTooltip", at = @At("RETURN"))
    private void addDescriptions(
            Item.TooltipContext context,
            Consumer<Component> consumer,
            TooltipFlag flag,
            DataComponentGetter getter,
            CallbackInfo ci) {

        if (!flag.isAdvanced()) return;
        ItemEnchantments self = (ItemEnchantments) (Object) this;
        for (Holder<Enchantment> holder : self.keySet()) {
            if (holder.unwrapKey().isEmpty()) continue;
            Identifier id = holder.unwrapKey().get().identifier();
            if (!id.getNamespace().equals("dagmod")) continue;

            String descKey = "enchantment." + id.getNamespace() + "." + id.getPath() + ".desc";
            consumer.accept(
                Component.translatable(descKey).withStyle(ChatFormatting.DARK_GRAY)
            );
        }
    }
}
