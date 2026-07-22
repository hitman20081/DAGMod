package com.github.hitman20081.dagmod.screen;

import com.github.hitman20081.dagmod.DagMod;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class GemCrushingStationScreen extends AbstractContainerScreen<GemCrushingStationScreenHandler> {
    private static final Identifier GUI_TEXTURE =
            Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "textures/gui/gem_crushing_station_gui.png");

    public GemCrushingStationScreen(GemCrushingStationScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title, 176, 168);
        this.inventoryLabelY = 74;
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
                    x + 104, y + 41,  // screen position matching the empty outline
                    178, 0,           // UV of the filled sprite in the texture sheet
                    2, progress,      // 2px wide, height grows as crafting progresses
                    256, 256);
        }
    }
}
