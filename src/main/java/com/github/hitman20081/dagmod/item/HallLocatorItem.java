package com.github.hitman20081.dagmod.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class HallLocatorItem extends Item {

    public HallLocatorItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        if (!world.isClient() && player instanceof ServerPlayerEntity) {
            player.sendMessage(Text.literal("═══════════════════════════════")
                    .formatted(Formatting.GOLD), false);
            player.sendMessage(Text.literal("HALL OF CHAMPIONS LOCATOR")
                    .formatted(Formatting.LIGHT_PURPLE).formatted(Formatting.BOLD), false);
            player.sendMessage(Text.literal("═══════════════════════════════")
                    .formatted(Formatting.GOLD), false);
            player.sendMessage(Text.literal("To find the Hall of Champions, use:")
                    .formatted(Formatting.YELLOW), false);
            player.sendMessage(Text.literal("/locate structure dag011:hall_of_champions")
                    .formatted(Formatting.AQUA), false);
            player.sendMessage(Text.literal("Then follow the coordinates!")
                    .formatted(Formatting.GRAY), false);
            player.sendMessage(Text.literal("═══════════════════════════════")
                    .formatted(Formatting.GOLD), false);
        }

        return ActionResult.SUCCESS;
    }
}