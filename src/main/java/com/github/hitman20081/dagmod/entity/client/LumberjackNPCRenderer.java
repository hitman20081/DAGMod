package com.github.hitman20081.dagmod.entity.client;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.entity.LumberjackNPC;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.npc.VillagerModel;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.resources.Identifier;

public class LumberjackNPCRenderer extends MobRenderer<LumberjackNPC, VillagerRenderState, VillagerModel> {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "textures/entity/villager/lumberjack.png");

    public LumberjackNPCRenderer(EntityRendererProvider.Context context) {
        super(context, new VillagerModel(context.bakeLayer(ModelLayers.VILLAGER)), 0.5f);
    }

    @Override
    public VillagerRenderState createRenderState() {
        return new VillagerRenderState();
    }

    @Override
    public Identifier getTextureLocation(VillagerRenderState state) {
        return TEXTURE;
    }
}
