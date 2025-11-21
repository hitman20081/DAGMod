package com.github.hitman20081.dagmod.entity.client;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.entity.SimpleNPC;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.client.render.entity.state.VillagerEntityRenderState;
import net.minecraft.util.Identifier;

/**
 * Renderer for SimpleNPC
 * Uses the villager model
 */
public class SimpleNPCRenderer extends MobEntityRenderer<SimpleNPC, VillagerEntityRenderState, VillagerResemblingModel> {

    private static final Identifier TEXTURE = Identifier.of(DagMod.MOD_ID, "textures/entity/villager/simple_npc.png");

    public SimpleNPCRenderer(EntityRendererFactory.Context context) {
        super(context, new VillagerResemblingModel(context.getPart(EntityModelLayers.VILLAGER)), 0.5f);
    }

    @Override
    public VillagerEntityRenderState createRenderState() {
        return new VillagerEntityRenderState();
    }

    @Override
    public Identifier getTexture(VillagerEntityRenderState state) {
        return TEXTURE;
    }
}