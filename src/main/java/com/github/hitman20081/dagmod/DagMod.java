package com.github.hitman20081.dagmod;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.effect.ModEffects;
import com.github.hitman20081.dagmod.item.ModItems;
import com.github.hitman20081.dagmod.networking.ModNetworking;
import com.github.hitman20081.dagmod.potion.ModPotions;
import com.github.hitman20081.dagmod.entity.ModEntities;
import com.github.hitman20081.dagmod.block.ModBlocks;
import com.github.hitman20081.dagmod.quest.QuestManager;
import com.github.hitman20081.dagmod.quest.registry.QuestRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import com.github.hitman20081.dagmod.command.ResetClassCommand;
import com.github.hitman20081.dagmod.class_system.ClassAbilityManager;
import com.github.hitman20081.dagmod.class_system.MagePotionHandler;
import com.github.hitman20081.dagmod.class_system.RogueCombatHandler;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.LivingEntity;

import static com.github.hitman20081.dagmod.potion.ModPotions.XP_POTION;

public class DagMod implements ModInitializer {
    public static final String MOD_ID = "dagmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModItems.initialize();
        ModItems.registerModItems();

        ModEffects.registerEffects();
        ModPotions.registerPotion();

        ModBlocks.initialize();

        // Initialize Networking
        ModNetworking.initialize();

        // Initialize Quest System
        LOGGER.info("Initializing Quest System for " + MOD_ID);
        QuestRegistry.registerQuests();
        QuestManager.getInstance();
        LOGGER.info("Quest System initialized successfully!");

        // Register command for class reset
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ResetClassCommand.register(dispatcher, registryAccess, environment);
        });

        // Apply class abilities when player respawns (including after death)
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            ClassAbilityManager.applyClassAbilities(newPlayer);
        });

        // Apply class abilities when player logs in
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof ServerPlayerEntity player) {
                ClassAbilityManager.applyClassAbilities(player);
            }
        });

        // Hook into entity death events for kill objectives - ADD THIS HERE
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            // Check if a player killed this entity
            if (damageSource.getAttacker() instanceof ServerPlayerEntity player) {
                // Update kill objectives for this player
                QuestManager.getInstance().updateKillProgress(player, entity.getType());
            }
        });
        // Then replace the placeholder with:
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUuid());

                if ("Mage".equals(playerClass)) {
                    // Give Mages permanent Night Vision
                    if (!player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
                        player.addStatusEffect(new StatusEffectInstance(
                                StatusEffects.NIGHT_VISION,
                                300, // 15 seconds
                                0,
                                true,  // ambient
                                false, // no particles
                                false  // no icon
                        ));
                    }
                }
            }
        });
        // Rogue critical hit and backstab system
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
                if (entity instanceof LivingEntity target) {
                    // Note: We can't directly modify damage here, but we can track it
                    // The actual damage modification happens in a mixin or damage event

                    // For now, we'll handle this differently - see the mixin approach below
                }
            }
            return ActionResult.PASS;
        });

        // Rogue fall damage reduction
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (entity instanceof ServerPlayerEntity player && source.getName().equals("fall")) {
                float modifiedDamage = RogueCombatHandler.modifyFallDamage(player, amount);
                // Unfortunately, we can't modify damage in ALLOW_DAMAGE
                // We need to use a mixin for proper damage modification
            }
            return true;
        });

        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            builder.registerPotionRecipe(
                    // Input potion.
                    Potions.LUCK,
                    // Ingredient
                    Items.EXPERIENCE_BOTTLE,
                    // Output potion.
                    Registries.POTION.getEntry(XP_POTION)
            );
        });
    } // Make sure this closing brace for onInitialize() is here

    // Helper method for updating quest progress - THIS GOES OUTSIDE onInitialize()
    public static void updatePlayerQuestProgress(ServerPlayerEntity player) {
        QuestManager.getInstance().updateQuestProgress(player);
    }
}