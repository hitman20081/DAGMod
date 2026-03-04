package com.github.hitman20081.dagmod.entity.client;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.entity.DragonGuardianEntity;
import com.github.hitman20081.dagmod.entity.RedDragonEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

/**
 * Renderer for the quest-exclusive Red Dragon.
 * Reuses DragonGuardianModel and always applies the red dragon texture.
 */
public class RedDragonRenderer extends MobEntityRenderer<RedDragonEntity, DragonGuardianRenderState, DragonGuardianModel> {

    private static final Identifier RED_DRAGON_TEXTURE =
            Identifier.of(DagMod.MOD_ID, "textures/entity/dragon/red_dragon.png");

    public RedDragonRenderer(EntityRendererFactory.Context context) {
        super(context, new DragonGuardianModel(context.getPart(DragonGuardianModel.LAYER_LOCATION)), 0.8f);
        this.shadowRadius = 1.0f;
    }

    @Override
    public DragonGuardianRenderState createRenderState() {
        return new DragonGuardianRenderState();
    }

    @Override
    public void updateRenderState(RedDragonEntity entity, DragonGuardianRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        state.animationState = DragonGuardianEntity.AnimationState.valueOf(entity.getAnimationState().name());
        state.animationSpeed = entity.getAnimationSpeed();
        state.isPerched = entity.isPerched();
        state.animationTimer = entity.getAnimationTimer();
        state.variant = DragonGuardianEntity.DragonVariant.RED;
    }

    @Override
    public Identifier getTexture(DragonGuardianRenderState state) {
        return RED_DRAGON_TEXTURE;
    }
}
