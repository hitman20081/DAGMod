package com.github.hitman20081.dagmod.entity.client;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.entity.JewelerNPC;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.client.render.entity.state.VillagerEntityRenderState;
import net.minecraft.util.Identifier;

public class JewelerNPCRenderer extends MobEntityRenderer<JewelerNPC, VillagerEntityRenderState, VillagerResemblingModel> {

    private static final Identifier TEXTURE = Identifier.of(DagMod.MOD_ID, "textures/entity/villager/jeweler.png");

    public JewelerNPCRenderer(EntityRendererFactory.Context context) {
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
