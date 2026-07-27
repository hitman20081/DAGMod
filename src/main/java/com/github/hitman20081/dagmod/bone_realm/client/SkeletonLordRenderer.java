package com.github.hitman20081.dagmod.bone_realm.client;

import com.github.hitman20081.dagmod.bone_realm.entity.SkeletonLordEntity;
import net.minecraft.client.renderer.entity.AbstractSkeletonRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.SkeletonRenderState;
import net.minecraft.resources.Identifier;

public class SkeletonLordRenderer extends AbstractSkeletonRenderer<SkeletonLordEntity, SkeletonRenderState> {

    private static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/entity/skeleton/skeleton.png");

    public SkeletonLordRenderer(EntityRendererProvider.Context context) {
        super(context, ModelLayers.SKELETON, ModelLayers.SKELETON_ARMOR);
    }

    @Override
    public Identifier getTextureLocation(SkeletonRenderState state) {
        return TEXTURE;
    }

    @Override
    public SkeletonRenderState createRenderState() {
        return new SkeletonRenderState();
    }
}