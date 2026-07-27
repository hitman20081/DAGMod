package com.github.hitman20081.dagmod.entity.client;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.entity.InnkeeperGarrickNPC;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.npc.VillagerModel;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.resources.Identifier;

/**
 * Renderer for Innkeeper Garrick
 * Uses the villager model with a barkeep texture
 */
public class InnkeeperGarrickRenderer extends MobRenderer<InnkeeperGarrickNPC, VillagerRenderState, VillagerModel> {

    // You can use the same texture as SimpleNPC or create a new one
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "textures/entity/villager/innkeeper_garrick.png");

    public InnkeeperGarrickRenderer(EntityRendererProvider.Context context) {
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