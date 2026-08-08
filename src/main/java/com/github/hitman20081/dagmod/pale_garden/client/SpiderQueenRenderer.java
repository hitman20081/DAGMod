package com.github.hitman20081.dagmod.pale_garden.client;

import com.github.hitman20081.dagmod.pale_garden.entity.SpiderQueenEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SpiderRenderer;
import net.minecraft.client.model.geom.ModelLayers;

/**
 * Reuses the vanilla spider model/texture as a placeholder (same approach SkeletonKingRenderer
 * takes with the wither skeleton texture) — swap for custom art later.
 */
public class SpiderQueenRenderer extends SpiderRenderer<SpiderQueenEntity> {

    public SpiderQueenRenderer(EntityRendererProvider.Context context) {
        super(context, ModelLayers.SPIDER);
    }
}
