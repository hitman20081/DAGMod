package com.github.hitman20081.dagmod.screen;

import com.github.hitman20081.dagmod.DagMod;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Iron Chest Screen - 54 slot chest GUI
 * Uses generic chest texture for 6 rows
 */
public class IronChestScreen extends HandledScreen<IronChestScreenHandler> {
    // Use vanilla generic 54 slot container texture (like large chest)
    private static final Identifier GUI_TEXTURE = Identifier.ofVanilla("textures/gui/container/generic_54.png");

    public IronChestScreen(IronChestScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        // Standard large chest dimensions
        this.backgroundHeight = 222;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        // Title uses default left alignment (titleX = 8)
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
