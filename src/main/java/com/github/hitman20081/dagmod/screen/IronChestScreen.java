package com.github.hitman20081.dagmod.screen;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class IronChestScreen extends AbstractContainerScreen<IronChestScreenHandler> {
    private static final Identifier GUI_TEXTURE = Identifier.withDefaultNamespace("textures/gui/container/generic_54.png");

    public IronChestScreen(IronChestScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title, 176, 222);
        this.inventoryLabelY = 222 - 94;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void extractContents(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        context.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);
    }
}
