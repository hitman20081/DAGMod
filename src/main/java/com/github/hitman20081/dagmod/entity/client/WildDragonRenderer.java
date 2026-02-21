package com.github.hitman20081.dagmod.entity.client;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.entity.WildDragonEntity;
import com.github.hitman20081.dagmod.entity.DragonGuardianEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

/**
 * Renderer for Wild Dragon Variants
 * Uses the same dragon model AND render state as Dragon Guardian (fully reused)
 * Features: body, 5-segment neck, head with jaw, wings, 4 legs, 12-segment tail
 * Supports multiple dragon variants: Red, Ice, and Lava
 * Scale is handled by entity attributes (0.4 for adults vs 0.6 for boss)
 */
public class WildDragonRenderer extends MobEntityRenderer<WildDragonEntity, DragonGuardianRenderState, DragonGuardianModel> {

    private static final Identifier RED_DRAGON_TEXTURE = Identifier.of(DagMod.MOD_ID, "textures/entity/dragon/red_dragon.png");
    private static final Identifier ICE_DRAGON_TEXTURE = Identifier.of(DagMod.MOD_ID, "textures/entity/dragon/ice_dragon.png");
    private static final Identifier LAVA_DRAGON_TEXTURE = Identifier.of(DagMod.MOD_ID, "textures/entity/dragon/lava_dragon.png");
    private static final Identifier EARTH_DRAGON_TEXTURE = Identifier.of(DagMod.MOD_ID, "textures/entity/dragon/earth_dragon.png");
    private static final Identifier WIND_DRAGON_TEXTURE = Identifier.of(DagMod.MOD_ID, "textures/entity/dragon/wind_dragon.png");

    public WildDragonRenderer(EntityRendererFactory.Context context) {
        // Reuse DragonGuardianModel - scale is handled by entity attributes
        super(context, new DragonGuardianModel(context.getPart(DragonGuardianModel.LAYER_LOCATION)), 0.8f);
        this.shadowRadius = 1.0f; // Smaller shadow than boss (0.4 scale vs 0.6)
    }

    @Override
    public DragonGuardianRenderState createRenderState() {
        return new DragonGuardianRenderState();
    }

    @Override
    public void updateRenderState(WildDragonEntity entity, DragonGuardianRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);

        // Transfer animation data from entity to render state (compatible types)
        state.animationState = convertAnimationState(entity.getAnimationState());
        state.animationSpeed = entity.getAnimationSpeed();
        state.isPerched = entity.isPerched();
        state.animationTimer = entity.getAnimationTimer();
        state.variant = entity.getVariant();
    }

    // Convert WildDragonEntity.AnimationState to DragonGuardianEntity.AnimationState
    private DragonGuardianEntity.AnimationState convertAnimationState(WildDragonEntity.AnimationState wildState) {
        return DragonGuardianEntity.AnimationState.valueOf(wildState.name());
    }

    @Override
    public Identifier getTexture(DragonGuardianRenderState state) {
        // Return texture based on dragon variant
        return switch (state.variant) {
            case RED -> RED_DRAGON_TEXTURE;
            case ICE -> ICE_DRAGON_TEXTURE;
            case LAVA -> LAVA_DRAGON_TEXTURE;
            case EARTH -> EARTH_DRAGON_TEXTURE;
            case WIND -> WIND_DRAGON_TEXTURE;
        };
    }
}
