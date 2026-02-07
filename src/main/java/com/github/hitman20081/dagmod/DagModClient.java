package com.github.hitman20081.dagmod;

import com.github.hitman20081.dagmod.bone_realm.chest.BoneRealmChestRegistry;
import com.github.hitman20081.dagmod.bone_realm.client.BonelingRenderer;
import com.github.hitman20081.dagmod.bone_realm.client.SkeletonKingRenderer;
import com.github.hitman20081.dagmod.bone_realm.client.SkeletonLordRenderer;
import com.github.hitman20081.dagmod.bone_realm.client.SkeletonSummonerRenderer;
import com.github.hitman20081.dagmod.bone_realm.entity.BoneRealmEntityRegistry;
import com.github.hitman20081.dagmod.entity.ModEntities;
import com.github.hitman20081.dagmod.entity.client.DragonGuardianModel;
import com.github.hitman20081.dagmod.entity.client.SimpleNPCRenderer;
import com.github.hitman20081.dagmod.entity.client.InnkeeperGarrickRenderer;
import com.github.hitman20081.dagmod.entity.client.WildDragonRenderer;
import com.github.hitman20081.dagmod.entity.client.MysteryMerchantRenderer;
import com.github.hitman20081.dagmod.entity.client.MinerNPCRenderer;
import com.github.hitman20081.dagmod.entity.client.LumberjackNPCRenderer;
import com.github.hitman20081.dagmod.entity.client.EnchantsmithNPCRenderer;
import com.github.hitman20081.dagmod.entity.client.LuxuryMerchantNPCRenderer;
import com.github.hitman20081.dagmod.entity.client.VillageMerchantNPCRenderer;
import com.github.hitman20081.dagmod.entity.client.HunterNPCRenderer;
import com.github.hitman20081.dagmod.entity.client.VoodooIllusionerNPCRenderer;
import com.github.hitman20081.dagmod.entity.client.ArmorerNPCRenderer;
import com.github.hitman20081.dagmod.entity.client.CuteVillagerNPCRenderer;
import com.github.hitman20081.dagmod.class_system.mana.ManaNetworking;
import com.github.hitman20081.dagmod.class_system.mana.client.ClientManaData;
import com.github.hitman20081.dagmod.class_system.mana.client.ManaHudRenderer;
import com.github.hitman20081.dagmod.networking.QuestSyncPacket;
import com.github.hitman20081.dagmod.progression.client.ClientProgressionData;
import com.github.hitman20081.dagmod.progression.client.ProgressionHUD;
import com.github.hitman20081.dagmod.progression.client.ToggleProgressionHUDCommand;
import com.github.hitman20081.dagmod.quest.ClientQuestData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.render.entity.EntityRendererFactories;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.util.Identifier;

public class DagModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        System.out.println("DAGMod client initializing...");

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            ToggleProgressionHUDCommand.register(dispatcher);
        });

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

        // Register entity model layers
        EntityModelLayerRegistry.registerModelLayer(DragonGuardianModel.LAYER_LOCATION, DragonGuardianModel::getTexturedModelData);

        // Register entity renderers
        registerEntityRenderers();

        // Register block entity renderers
        registerBlockEntityRenderers();

        // Register screens
        registerScreens();

        System.out.println("DAGMod client networking initialized!");
    }

    private void registerScreens() {
        net.minecraft.client.gui.screen.ingame.HandledScreens.register(
                com.github.hitman20081.dagmod.screen.ModScreenHandlers.GEM_POLISHING_STATION_SCREEN_HANDLER,
                com.github.hitman20081.dagmod.screen.GemPolishingStationScreen::new
        );
        net.minecraft.client.gui.screen.ingame.HandledScreens.register(
                com.github.hitman20081.dagmod.screen.ModScreenHandlers.GEM_INFUSING_STATION_SCREEN_HANDLER,
                com.github.hitman20081.dagmod.screen.GemInfusingStationScreen::new
        );
        net.minecraft.client.gui.screen.ingame.HandledScreens.register(
                com.github.hitman20081.dagmod.screen.ModScreenHandlers.GEM_CUTTING_STATION_SCREEN_HANDLER,
                com.github.hitman20081.dagmod.screen.GemCuttingStationScreen::new
        );
        // Iron Chest uses vanilla chest screen (ChestBlockEntity)
        System.out.println("Screens registered!");
    }

    private void registerEntityRenderers() {
        EntityRendererFactories.register(BoneRealmEntityRegistry.SKELETON_KING, SkeletonKingRenderer::new);
        EntityRendererFactories.register(BoneRealmEntityRegistry.SKELETON_LORD, SkeletonLordRenderer::new);
        EntityRendererFactories.register(BoneRealmEntityRegistry.BONELING, BonelingRenderer::new);
        EntityRendererFactories.register(BoneRealmEntityRegistry.SKELETON_SUMMONER, SkeletonSummonerRenderer::new);

        // Register SimpleNPC renderer
        EntityRendererFactories.register(ModEntities.SIMPLE_NPC, SimpleNPCRenderer::new);

        // Register Innkeeper Garrick renderer
        EntityRendererFactories.register(ModEntities.INNKEEPER_GARRICK, InnkeeperGarrickRenderer::new);

        // Register Mystery Merchant renderer
        EntityRendererFactories.register(ModEntities.MYSTERY_MERCHANT_NPC, MysteryMerchantRenderer::new);

        // Register Miner renderer
        EntityRendererFactories.register(ModEntities.MINER_NPC, MinerNPCRenderer::new);

        // Register Lumberjack renderer
        EntityRendererFactories.register(ModEntities.LUMBERJACK_NPC, LumberjackNPCRenderer::new);

        // Register Enchantsmith renderer
        EntityRendererFactories.register(ModEntities.ENCHANTSMITH_NPC, EnchantsmithNPCRenderer::new);

        // Register Luxury Merchant renderer
        EntityRendererFactories.register(ModEntities.LUXURY_MERCHANT_NPC, LuxuryMerchantNPCRenderer::new);

        // Register Village Merchant renderer
        EntityRendererFactories.register(ModEntities.VILLAGE_MERCHANT_NPC, VillageMerchantNPCRenderer::new);

        // Register Hunter renderer
        EntityRendererFactories.register(ModEntities.HUNTER_NPC, HunterNPCRenderer::new);

        // Register Voodoo Illusioner renderer
        EntityRendererFactories.register(ModEntities.VOODOO_ILLUSIONER_NPC, VoodooIllusionerNPCRenderer::new);

        // Register Armorer renderer
        EntityRendererFactories.register(ModEntities.ARMORER_NPC, ArmorerNPCRenderer::new);

        // Register Cute Villager renderer
        EntityRendererFactories.register(ModEntities.CUTE_VILLAGER_NPC, CuteVillagerNPCRenderer::new);

        // Register Wild Dragon renderer
        EntityRendererFactories.register(ModEntities.WILD_DRAGON, WildDragonRenderer::new);

        System.out.println("Entity renderers registered!");
    }

    private void registerBlockEntityRenderers() {
        BlockEntityRendererFactories.register(
                BoneRealmChestRegistry.LOCKED_BONE_CHEST_ENTITY,
                ChestBlockEntityRenderer::new
        );

        // Iron Chest renderer
        BlockEntityRendererFactories.register(
                com.github.hitman20081.dagmod.block.entity.ModBlockEntities.IRON_CHEST,
                ChestBlockEntityRenderer::new
        );

        System.out.println("Block entity renderers registered!");
    }
}
