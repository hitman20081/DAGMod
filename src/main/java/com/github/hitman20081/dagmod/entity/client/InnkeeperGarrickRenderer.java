package com.github.hitman20081.dagmod.entity.client;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.entity.InnkeeperGarrickNPC;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.client.render.entity.state.VillagerEntityRenderState;
import net.minecraft.util.Identifier;

/**
 * Renderer for Innkeeper Garrick
 * Uses the villager model with a barkeep texture
 */
public class InnkeeperGarrickRenderer extends MobEntityRenderer<InnkeeperGarrickNPC, VillagerEntityRenderState, VillagerResemblingModel> {

    // You can use the same texture as SimpleNPC or create a new one
    private static final Identifier TEXTURE = Identifier.of(DagMod.MOD_ID, "textures/entity/villager/innkeeper_garrick.png");

    public InnkeeperGarrickRenderer(EntityRendererFactory.Context context) {
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