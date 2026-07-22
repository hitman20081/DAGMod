package com.github.hitman20081.dagmod.screen;

import com.github.hitman20081.dagmod.DagMod;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class GemPolishingStationScreen extends AbstractContainerScreen<GemPolishingStationScreenHandler> {
    private static final Identifier GUI_TEXTURE =
            Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "textures/gui/gem_polishing_station_gui.png");

    public GemPolishingStationScreen(GemPolishingStationScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title, 176, 166);
        this.inventoryLabelY = 166 - 94;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractBackground(context, mouseX, mouseY, delta);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        context.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);
        if (menu.isCrafting()) {
            int progress = menu.getScaledProgress();
            context.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE,
                    x + 88, y + 31,  // screen position matching the empty outline
                    178, 0,          // UV of the filled sprite in the texture sheet
                    2, progress,     // 2px wide, height grows as crafting progresses
                    256, 256);
        }
        // Label under diamond powder catalyst slot (slot at 39,36; center at x+48)
        context.text(this.font, Component.literal("Diamond"), x + 26, y + 56, 0x404040, false);
        context.text(this.font, Component.literal("Powder"),  x + 30, y + 65, 0x404040, false);
    }
}
