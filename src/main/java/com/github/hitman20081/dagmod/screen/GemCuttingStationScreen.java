package com.github.hitman20081.dagmod.screen;

import com.github.hitman20081.dagmod.DagMod;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class GemCuttingStationScreen extends HandledScreen<GemCuttingStationScreenHandler> {
    private static final Identifier GUI_TEXTURE =
            Identifier.of(DagMod.MOD_ID, "textures/gui/gem_cutting_station_gui.png");

    public GemCuttingStationScreen(GemCuttingStationScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundHeight = 168;
        this.playerInventoryTitleY = 74;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);

        // Draw simple progress indicator when crafting
        if (handler.isCrafting()) {
            int progress = handler.getScaledProgress();
            // Draw a simple green progress bar below the arrow area
            context.fill(x + 63, y + 52, x + 63 + progress, y + 55, 0xFF00AA00);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
