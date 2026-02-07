package com.github.hitman20081.dagmod.bone_realm.client;

import com.github.hitman20081.dagmod.bone_realm.entity.BonelingEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.client.render.entity.state.SkeletonEntityRenderState;
import net.minecraft.util.Identifier;

/**
 * Renderer for Boneling
 * Uses the regular skeleton model
 */
public class BonelingRenderer extends MobEntityRenderer<BonelingEntity, SkeletonEntityRenderState, SkeletonEntityModel<SkeletonEntityRenderState>> {

    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/skeleton/skeleton.png");

    public BonelingRenderer(EntityRendererFactory.Context context) {
        super(context, new SkeletonEntityModel<>(context.getPart(EntityModelLayers.SKELETON)), 0.5f);
    }

    @Override
    public SkeletonEntityRenderState createRenderState() {
        return new SkeletonEntityRenderState();
    }

    @Override
    public Identifier getTexture(SkeletonEntityRenderState state) {
        return TEXTURE;
    }
}