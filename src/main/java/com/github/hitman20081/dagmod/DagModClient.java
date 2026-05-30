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
import com.github.hitman20081.dagmod.entity.client.DragonGuardianRenderer;
import com.github.hitman20081.dagmod.entity.client.WildDragonRenderer;
import com.github.hitman20081.dagmod.entity.client.RedDragonRenderer;
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
import com.github.hitman20081.dagmod.entity.client.BakerNPCRenderer;
import com.github.hitman20081.dagmod.entity.client.AlchemistNPCRenderer;
import com.github.hitman20081.dagmod.entity.client.BlacksmithNPCRenderer;
import com.github.hitman20081.dagmod.entity.client.JewelerNPCRenderer;
import com.github.hitman20081.dagmod.class_system.mana.ManaNetworking;
import com.github.hitman20081.dagmod.class_system.mana.client.ClientManaData;
import com.github.hitman20081.dagmod.class_system.mana.client.ManaHudRenderer;
import com.github.hitman20081.dagmod.client.DynamicLightManager;
import com.github.hitman20081.dagmod.networking.QuestSyncPacket;
import com.github.hitman20081.dagmod.progression.client.ClientProgressionData;
import com.github.hitman20081.dagmod.progression.client.ProgressionHUD;
import com.github.hitman20081.dagmod.progression.client.ToggleProgressionHUDCommand;
import com.github.hitman20081.dagmod.quest.ClientQuestData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.resources.Identifier;

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
        HudElementRegistry.addLast(net.minecraft.resources.Identifier.fromNamespaceAndPath("dagmod", "mana_hud"), new ManaHudRenderer()::onHudRender);
        System.out.println("Mana system registered!");

        // Register dynamic held-item lighting
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            LocalPlayer player = client.player;
            if (player == null) {
                DynamicLightManager.clear();
                return;
            }

            net.minecraft.world.item.Item mainItem = player.getMainHandItem().getItem();
            net.minecraft.world.item.Item offItem  = player.getOffhandItem().getItem();
            int mainLevel = DynamicLightManager.getLightLevelForItem(mainItem);
            int offLevel  = DynamicLightManager.getLightLevelForItem(offItem);

            // Dominant hand: whichever is brighter; use its radius
            net.minecraft.world.item.Item dominant = (mainLevel >= offLevel) ? mainItem : offItem;
            int newLevel  = Math.max(mainLevel, offLevel);
            int newRadius = DynamicLightManager.getRadiusForItem(dominant);

            net.minecraft.core.BlockPos playerPos = player.blockPosition();

            // Schedule chunk rebuilds so terrain updates, not just entities
            if (DynamicLightManager.needsChunkRebuild(playerPos, newRadius) && client.levelRenderer != null) {
                net.minecraft.core.BlockPos oldPos = DynamicLightManager.getLastRebuildPos();
                int oldRadius = DynamicLightManager.getLastRebuildRadius();

                // Dirty sections within new light radius so they pick up the boost
                if (newRadius > 0) {
                    int r  = (newRadius >> 4) + 1;
                    int px = playerPos.getX() >> 4;
                    int py = playerPos.getY() >> 4;
                    int pz = playerPos.getZ() >> 4;
                    client.levelRenderer.setSectionRangeDirty(px - r, py - r, pz - r, px + r, py + r, pz + r);
                }
                // Dirty sections at old position so stale light is cleared.
                // Always do this when oldRadius > 0 — the player may have simply
                // dropped the light source without moving, so !equals check is wrong.
                if (oldRadius > 0) {
                    int r  = (oldRadius >> 4) + 1;
                    int px = oldPos.getX() >> 4;
                    int py = oldPos.getY() >> 4;
                    int pz = oldPos.getZ() >> 4;
                    client.levelRenderer.setSectionRangeDirty(px - r, py - r, pz - r, px + r, py + r, pz + r);
                }

                DynamicLightManager.setLastRebuildState(playerPos, newRadius);
            }

            DynamicLightManager.updatePlayerLight(playerPos, newLevel, newRadius);
        });
        System.out.println("Dynamic lighting registered!");

        // Register entity model layers
        ModelLayerRegistry.registerModelLayer(DragonGuardianModel.LAYER_LOCATION, DragonGuardianModel::getTexturedModelData);

        // Register entity renderers
        registerEntityRenderers();

        // Register block entity renderers
        registerBlockEntityRenderers();

        // Register screens
        registerScreens();

        System.out.println("DAGMod client networking initialized!");
    }

    private void registerScreens() {
        net.minecraft.client.gui.screens.MenuScreens.register(
                com.github.hitman20081.dagmod.screen.ModScreenHandlers.GEM_POLISHING_STATION_SCREEN_HANDLER,
                com.github.hitman20081.dagmod.screen.GemPolishingStationScreen::new
        );
        net.minecraft.client.gui.screens.MenuScreens.register(
                com.github.hitman20081.dagmod.screen.ModScreenHandlers.GEM_INFUSING_STATION_SCREEN_HANDLER,
                com.github.hitman20081.dagmod.screen.GemInfusingStationScreen::new
        );
        net.minecraft.client.gui.screens.MenuScreens.register(
                com.github.hitman20081.dagmod.screen.ModScreenHandlers.GEM_CUTTING_STATION_SCREEN_HANDLER,
                com.github.hitman20081.dagmod.screen.GemCuttingStationScreen::new
        );
        // Iron Chest uses vanilla chest screen (ChestBlockEntity)
        System.out.println("Screens registered!");
    }

    private void registerEntityRenderers() {
        EntityRenderers.register(BoneRealmEntityRegistry.SKELETON_KING, SkeletonKingRenderer::new);
        EntityRenderers.register(BoneRealmEntityRegistry.SKELETON_LORD, SkeletonLordRenderer::new);
        EntityRenderers.register(BoneRealmEntityRegistry.BONELING, BonelingRenderer::new);
        EntityRenderers.register(BoneRealmEntityRegistry.SKELETON_SUMMONER, SkeletonSummonerRenderer::new);

        // Register SimpleNPC renderer
        EntityRenderers.register(ModEntities.SIMPLE_NPC, SimpleNPCRenderer::new);

        // Register Innkeeper Garrick renderer
        EntityRenderers.register(ModEntities.INNKEEPER_GARRICK, InnkeeperGarrickRenderer::new);

        // Register Mystery Merchant renderer
        EntityRenderers.register(ModEntities.MYSTERY_MERCHANT_NPC, MysteryMerchantRenderer::new);

        // Register Miner renderer
        EntityRenderers.register(ModEntities.MINER_NPC, MinerNPCRenderer::new);

        // Register Lumberjack renderer
        EntityRenderers.register(ModEntities.LUMBERJACK_NPC, LumberjackNPCRenderer::new);

        // Register Enchantsmith renderer
        EntityRenderers.register(ModEntities.ENCHANTSMITH_NPC, EnchantsmithNPCRenderer::new);

        // Register Luxury Merchant renderer
        EntityRenderers.register(ModEntities.LUXURY_MERCHANT_NPC, LuxuryMerchantNPCRenderer::new);

        // Register Village Merchant renderer
        EntityRenderers.register(ModEntities.VILLAGE_MERCHANT_NPC, VillageMerchantNPCRenderer::new);

        // Register Hunter renderer
        EntityRenderers.register(ModEntities.HUNTER_NPC, HunterNPCRenderer::new);

        // Register Voodoo Illusioner renderer
        EntityRenderers.register(ModEntities.VOODOO_ILLUSIONER_NPC, VoodooIllusionerNPCRenderer::new);

        // Register Armorer renderer
        EntityRenderers.register(ModEntities.ARMORER_NPC, ArmorerNPCRenderer::new);

        // Register Cute Villager renderer
        EntityRenderers.register(ModEntities.CUTE_VILLAGER_NPC, CuteVillagerNPCRenderer::new);

        // Register Baker renderer
        EntityRenderers.register(ModEntities.BAKER_NPC, BakerNPCRenderer::new);

        // Register Alchemist renderer
        EntityRenderers.register(ModEntities.ALCHEMIST_NPC, AlchemistNPCRenderer::new);

        // Register Blacksmith renderer
        EntityRenderers.register(ModEntities.BLACKSMITH_NPC, BlacksmithNPCRenderer::new);

        // Register Jeweler renderer
        EntityRenderers.register(ModEntities.JEWELER_NPC, JewelerNPCRenderer::new);

        // Register Dragon Guardian renderer
        EntityRenderers.register(ModEntities.DRAGON_GUARDIAN, DragonGuardianRenderer::new);

        // Register Wild Dragon renderer
        EntityRenderers.register(ModEntities.WILD_DRAGON, WildDragonRenderer::new);

        // Register Red Dragon renderer (quest-exclusive variant)
        EntityRenderers.register(ModEntities.RED_DRAGON, RedDragonRenderer::new);

        System.out.println("Entity renderers registered!");
    }

    private void registerBlockEntityRenderers() {
        BlockEntityRenderers.register(
                BoneRealmChestRegistry.LOCKED_BONE_CHEST_ENTITY,
                ChestRenderer::new
        );

        // Iron Chest renderer
        BlockEntityRenderers.register(
                com.github.hitman20081.dagmod.block.entity.ModBlockEntities.IRON_CHEST,
                ChestRenderer::new
        );

        System.out.println("Block entity renderers registered!");
    }
}
