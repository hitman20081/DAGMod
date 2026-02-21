package com.github.hitman20081.dagmod.entity.client;

import com.github.hitman20081.dagmod.entity.DragonGuardianEntity;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;

/**
 * Custom render state for Dragon Guardian
 * Stores animation state, speed, and variant for model animations and textures
 */
public class DragonGuardianRenderState extends LivingEntityRenderState {
    public DragonGuardianEntity.AnimationState animationState = DragonGuardianEntity.AnimationState.IDLE;
    public float animationSpeed = 1.0F;
    public boolean isPerched = false;
    public int animationTimer = 0;
    public DragonGuardianEntity.DragonVariant variant = DragonGuardianEntity.DragonVariant.RED;
}
