package com.github.hitman20081.dagmod.bone_realm.client;

import com.github.hitman20081.dagmod.bone_realm.entity.BonelingEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.skeleton.SkeletonModel;
import net.minecraft.client.renderer.entity.state.SkeletonRenderState;
import net.minecraft.resources.Identifier;

/**
 * Renderer for Boneling
 * Uses the regular skeleton model
 */
public class BonelingRenderer extends MobRenderer<BonelingEntity, SkeletonRenderState, SkeletonModel<SkeletonRenderState>> {

    private static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/entity/skeleton/skeleton.png");

    public BonelingRenderer(EntityRendererProvider.Context context) {
        super(context, new SkeletonModel<>(context.bakeLayer(ModelLayers.SKELETON)), 0.5f);
    }

    @Override
    public SkeletonRenderState createRenderState() {
        return new SkeletonRenderState();
    }

    @Override
    public Identifier getTextureLocation(SkeletonRenderState state) {
        return TEXTURE;
    }
}