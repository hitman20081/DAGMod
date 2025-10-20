package com.github.hitman20081.dagmod;

import com.github.hitman20081.dagmod.bone_realm.chest.BoneRealmChestRegistry;
import com.github.hitman20081.dagmod.bone_realm.client.BonelingRenderer;
import com.github.hitman20081.dagmod.bone_realm.client.SkeletonKingRenderer;
import com.github.hitman20081.dagmod.bone_realm.client.SkeletonLordRenderer;
import com.github.hitman20081.dagmod.bone_realm.client.SkeletonSummonerRenderer;
import com.github.hitman20081.dagmod.bone_realm.entity.BoneRealmEntityRegistry;
import com.github.hitman20081.dagmod.class_system.mana.ManaNetworking;
import com.github.hitman20081.dagmod.class_system.mana.client.ClientManaData;
import com.github.hitman20081.dagmod.class_system.mana.client.ManaHudRenderer;
import com.github.hitman20081.dagmod.networking.QuestSyncPacket;
import com.github.hitman20081.dagmod.progression.client.ClientProgressionData;
import com.github.hitman20081.dagmod.progression.client.ProgressionHUD;
import com.github.hitman20081.dagmod.quest.ClientQuestData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;

public class DagModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        System.out.println("DAGMod client initializing...");

        // Register progression system FIRST (before any packets)
        ClientProgressionData.registerClientPackets();
        ProgressionHUD.register();
        System.out.println("Progression HUD registered!");

        // Register client-side quest packet receivers
        ClientPlayNetworking.registerGlobalReceiver(QuestSyncPacket.ID, (payload, context) -> {
            context.client().execute(() -> {
                ClientQuestData.getInstance().updateFromPacket(payload);
            });
        });

        // Register mana system
        ClientPlayNetworking.registerGlobalReceiver(
                ManaNetworking.ManaUpdatePayload.ID,
                (payload, context) -> {
                    context.client().execute(() -> {
                        ClientManaData.setMana(payload.currentMana(), payload.maxMana());
                    });
                }
        );

        // Register Mana HUD renderer
        HudRenderCallback.EVENT.register(new ManaHudRenderer());
        System.out.println("Mana system registered!");

        // Register entity renderers
        registerEntityRenderers();

        // Register block entity renderers
        registerBlockEntityRenderers();

        System.out.println("DAGMod client networking initialized!");
    }

    private void registerEntityRenderers() {
        EntityRendererRegistry.register(BoneRealmEntityRegistry.SKELETON_KING, SkeletonKingRenderer::new);
        EntityRendererRegistry.register(BoneRealmEntityRegistry.SKELETON_LORD, SkeletonLordRenderer::new);
        EntityRendererRegistry.register(BoneRealmEntityRegistry.BONELING, BonelingRenderer::new);
        EntityRendererRegistry.register(BoneRealmEntityRegistry.SKELETON_SUMMONER, SkeletonSummonerRenderer::new);

        System.out.println("Entity renderers registered!");
    }

    private void registerBlockEntityRenderers() {
        BlockEntityRendererFactories.register(
                BoneRealmChestRegistry.LOCKED_BONE_CHEST_ENTITY,
                ChestBlockEntityRenderer::new
        );

        System.out.println("Block entity renderers registered!");
    }
}