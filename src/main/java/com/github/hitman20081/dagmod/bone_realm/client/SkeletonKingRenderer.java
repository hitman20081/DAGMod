package com.github.hitman20081.dagmod.bone_realm.client;

import com.github.hitman20081.dagmod.bone_realm.entity.SkeletonKingEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.client.render.entity.state.SkeletonEntityRenderState;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.util.Identifier;

public class SkeletonKingRenderer extends MobEntityRenderer<SkeletonKingEntity, SkeletonEntityRenderState, SkeletonEntityModel<SkeletonEntityRenderState>> {

    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/skeleton/skeleton.png");

    public SkeletonKingRenderer(EntityRendererFactory.Context context) {
        super(context, new SkeletonEntityModel<>(context.getPart(EntityModelLayers.SKELETON)), 0.5f);

        System.out.println("DEBUG: SkeletonKingRenderer created");
    }

    @Override
    public SkeletonEntityRenderState createRenderState() {
        return new SkeletonEntityRenderState();
    }

    @Override
    public Identifier getTexture(SkeletonEntityRenderState state) {
        return TEXTURE;
    }

    @Override
    public void updateRenderState(SkeletonKingEntity entity, SkeletonEntityRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);

        // Skeleton-specific: ensure the arms are set to attacking pose if holding items
        state.attacking = entity.isAttacking();
    }
}