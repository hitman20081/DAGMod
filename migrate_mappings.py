#!/usr/bin/env python3
"""
Migration script: Yarn 1.21.11 → Mojang 26.1.2 class/package names
Applies to all .java files under src/main/java/
"""

import os
import re
import sys

# --- IMPORT LINE REPLACEMENTS ---
# Each entry: (old_import_string, new_import_string)
# Order matters: more specific first.
IMPORT_REPLACEMENTS = [
    # === NBT ===
    ("import net.minecraft.nbt.NbtCompound;",           "import net.minecraft.nbt.CompoundTag;"),
    ("import net.minecraft.nbt.NbtList;",               "import net.minecraft.nbt.ListTag;"),
    ("import net.minecraft.nbt.NbtElement;",            "import net.minecraft.nbt.Tag;"),
    ("import net.minecraft.nbt.NbtSizeTracker;",        "import net.minecraft.nbt.NbtAccounter;"),
    # NbtIo, NbtOps stay in net.minecraft.nbt — no change needed

    # === Text / Chat ===
    ("import net.minecraft.text.Text;",                 "import net.minecraft.network.chat.Component;"),
    ("import net.minecraft.text.MutableText;",          "import net.minecraft.network.chat.MutableComponent;"),
    ("import net.minecraft.text.ClickEvent;",           "import net.minecraft.network.chat.ClickEvent;"),
    ("import net.minecraft.text.HoverEvent;",           "import net.minecraft.network.chat.HoverEvent;"),

    # === Util top-level ===
    ("import net.minecraft.util.ActionResult;",         "import net.minecraft.world.InteractionResult;"),
    ("import net.minecraft.util.Hand;",                 "import net.minecraft.world.InteractionHand;"),
    ("import net.minecraft.util.Formatting;",           "import net.minecraft.ChatFormatting;"),
    ("import net.minecraft.util.Identifier;",           "import net.minecraft.resources.Identifier;"),
    ("import net.minecraft.util.Rarity;",               "import net.minecraft.world.item.Rarity;"),
    ("import net.minecraft.util.Util;",                 "import net.minecraft.util.Util;"),  # stays same
    ("import net.minecraft.util.WorldSavePath;",        "import net.minecraft.world.level.storage.LevelStorageSource;"),
    ("import net.minecraft.util.ItemScatterer;",        "import net.minecraft.world.Containers;"),
    ("import net.minecraft.util.BlockMirror;",          "import net.minecraft.world.level.block.Mirror;"),
    ("import net.minecraft.util.BlockRotation;",        "import net.minecraft.world.level.block.Rotation;"),

    # util.math
    ("import net.minecraft.util.math.BlockPos;",        "import net.minecraft.core.BlockPos;"),
    ("import net.minecraft.util.math.Box;",             "import net.minecraft.world.phys.AABB;"),
    ("import net.minecraft.util.math.ChunkPos;",        "import net.minecraft.world.level.ChunkPos;"),
    ("import net.minecraft.util.math.Direction;",       "import net.minecraft.core.Direction;"),
    ("import net.minecraft.util.math.Vec3d;",           "import net.minecraft.world.phys.Vec3;"),
    ("import net.minecraft.util.math.intprovider.UniformIntProvider;", "import net.minecraft.util.valueproviders.UniformInt;"),
    ("import net.minecraft.util.math.random.Random;",   "import net.minecraft.util.RandomSource;"),

    # util.hit
    ("import net.minecraft.util.hit.BlockHitResult;",   "import net.minecraft.world.phys.BlockHitResult;"),
    ("import net.minecraft.util.hit.EntityHitResult;",  "import net.minecraft.world.phys.EntityHitResult;"),
    ("import net.minecraft.util.hit.HitResult;",        "import net.minecraft.world.phys.HitResult;"),

    # util.shape
    ("import net.minecraft.util.shape.VoxelShape;",     "import net.minecraft.world.phys.shapes.VoxelShape;"),
    ("import net.minecraft.util.shape.VoxelShapes;",    "import net.minecraft.world.phys.shapes.Shapes;"),

    # util.collection
    ("import net.minecraft.util.collection.DefaultedList;", "import net.minecraft.core.NonNullList;"),

    # === Block ===
    ("import net.minecraft.block.*;",                   "import net.minecraft.world.level.block.*;"),
    ("import net.minecraft.block.AbstractBlock;",       "import net.minecraft.world.level.block.state.BlockBehaviour;"),
    ("import net.minecraft.block.Block;",               "import net.minecraft.world.level.block.Block;"),
    ("import net.minecraft.block.BlockRenderType;",     "import net.minecraft.world.level.block.RenderShape;"),
    ("import net.minecraft.block.BlockState;",          "import net.minecraft.world.level.block.state.BlockState;"),
    ("import net.minecraft.block.BlockWithEntity;",     "import net.minecraft.world.level.block.BaseEntityBlock;"),
    ("import net.minecraft.block.Blocks;",              "import net.minecraft.world.level.block.Blocks;"),
    ("import net.minecraft.block.ExperienceDroppingBlock;", "import net.minecraft.world.level.block.DropExperienceBlock;"),
    ("import net.minecraft.block.HorizontalFacingBlock;","import net.minecraft.world.level.block.HorizontalDirectionalBlock;"),
    ("import net.minecraft.block.LightBlock;",          "import net.minecraft.world.level.block.LightBlock;"),
    ("import net.minecraft.block.MapColor;",            "import net.minecraft.world.level.material.MapColor;"),
    ("import net.minecraft.block.ShapeContext;",        "import net.minecraft.world.phys.shapes.CollisionContext;"),

    # block.entity
    ("import net.minecraft.block.entity.BlockEntity;",         "import net.minecraft.world.level.block.entity.BlockEntity;"),
    ("import net.minecraft.block.entity.BlockEntityTicker;",   "import net.minecraft.world.level.block.entity.BlockEntityTicker;"),
    ("import net.minecraft.block.entity.BlockEntityType;",     "import net.minecraft.world.level.block.entity.BlockEntityType;"),
    ("import net.minecraft.block.entity.ChestBlockEntity;",    "import net.minecraft.world.level.block.entity.ChestBlockEntity;"),
    ("import net.minecraft.block.entity.LidOpenable;",         "import net.minecraft.world.level.block.entity.ContainerOpenersCounter;"),

    # block.enums
    ("import net.minecraft.block.enums.ChestType;",     "import net.minecraft.world.level.block.state.properties.ChestType;"),

    # === Entity ===
    ("import net.minecraft.entity.*;",                  "import net.minecraft.world.entity.*;"),
    ("import net.minecraft.entity.AreaEffectCloudEntity;","import net.minecraft.world.entity.AreaEffectCloud;"),
    ("import net.minecraft.entity.Entity;",             "import net.minecraft.world.entity.Entity;"),
    ("import net.minecraft.entity.EntityData;",         "import net.minecraft.world.entity.SpawnGroupData;"),
    ("import net.minecraft.entity.EntityType;",         "import net.minecraft.world.entity.EntityType;"),
    ("import net.minecraft.entity.EquipmentSlot;",      "import net.minecraft.world.entity.EquipmentSlot;"),
    ("import net.minecraft.entity.ExperienceOrbEntity;","import net.minecraft.world.entity.ExperienceOrb;"),
    ("import net.minecraft.entity.ItemEntity;",         "import net.minecraft.world.entity.item.ItemEntity;"),
    ("import net.minecraft.entity.LivingEntity;",       "import net.minecraft.world.entity.LivingEntity;"),
    ("import net.minecraft.entity.SpawnGroup;",         "import net.minecraft.world.entity.MobCategory;"),
    ("import net.minecraft.entity.SpawnLocationTypes;", "import net.minecraft.world.entity.SpawnPlacements;"),
    ("import net.minecraft.entity.SpawnReason;",        "import net.minecraft.world.entity.EntitySpawnReason;"),
    ("import net.minecraft.entity.SpawnRestriction;",   "import net.minecraft.world.entity.SpawnPlacements;"),

    # entity.ai.goal
    ("import net.minecraft.entity.ai.goal.*;",          "import net.minecraft.world.entity.ai.goal.*;"),
    ("import net.minecraft.entity.ai.goal.LookAroundGoal;",     "import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;"),
    ("import net.minecraft.entity.ai.goal.LookAtEntityGoal;",   "import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;"),
    ("import net.minecraft.entity.ai.goal.WanderAroundFarGoal;","import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;"),

    # entity.ai.pathing
    ("import net.minecraft.entity.ai.pathing.BirdNavigation;",   "import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;"),
    ("import net.minecraft.entity.ai.pathing.EntityNavigation;", "import net.minecraft.world.entity.ai.navigation.PathNavigation;"),

    # entity.ai.control
    ("import net.minecraft.entity.ai.control.FlightMoveControl;","import net.minecraft.world.entity.ai.control.FlyingMoveControl;"),

    # entity.attribute
    ("import net.minecraft.entity.attribute.DefaultAttributeContainer;","import net.minecraft.world.entity.ai.attributes.AttributeSupplier;"),
    ("import net.minecraft.entity.attribute.EntityAttributeModifier;",  "import net.minecraft.world.entity.ai.attributes.AttributeModifier;"),
    ("import net.minecraft.entity.attribute.EntityAttributes;",         "import net.minecraft.world.entity.ai.attributes.Attributes;"),

    # entity.boss
    ("import net.minecraft.entity.boss.BossBar;",               "import net.minecraft.world.BossEvent;"),
    ("import net.minecraft.entity.boss.ServerBossBar;",         "import net.minecraft.server.level.ServerBossEvent;"),
    ("import net.minecraft.entity.boss.WitherEntity;",          "import net.minecraft.world.entity.boss.wither.WitherBoss;"),
    ("import net.minecraft.entity.boss.dragon.EnderDragonEntity;","import net.minecraft.world.entity.boss.enderdragon.EnderDragon;"),

    # entity.damage
    ("import net.minecraft.entity.damage.DamageSource;",        "import net.minecraft.world.damagesource.DamageSource;"),
    ("import net.minecraft.entity.damage.DamageTypes;",         "import net.minecraft.world.damagesource.DamageTypes;"),

    # entity.data
    ("import net.minecraft.entity.data.DataTracker;",               "import net.minecraft.network.syncher.SynchedEntityData;"),
    ("import net.minecraft.entity.data.TrackedData;",               "import net.minecraft.network.syncher.EntityDataAccessor;"),
    ("import net.minecraft.entity.data.TrackedDataHandlerRegistry;","import net.minecraft.network.syncher.EntityDataSerializers;"),

    # entity.effect
    ("import net.minecraft.entity.effect.StatusEffect;",        "import net.minecraft.world.effect.MobEffect;"),
    ("import net.minecraft.entity.effect.StatusEffectCategory;","import net.minecraft.world.effect.MobEffectCategory;"),
    ("import net.minecraft.entity.effect.StatusEffectInstance;","import net.minecraft.world.effect.MobEffectInstance;"),
    ("import net.minecraft.entity.effect.StatusEffects;",       "import net.minecraft.world.effect.MobEffects;"),

    # entity.mob
    ("import net.minecraft.entity.mob.*;",              "import net.minecraft.world.entity.monster.*;"),
    ("import net.minecraft.entity.mob.HostileEntity;",  "import net.minecraft.world.entity.monster.Monster;"),
    ("import net.minecraft.entity.mob.PathAwareEntity;","import net.minecraft.world.entity.PathfinderMob;"),
    ("import net.minecraft.entity.mob.SkeletonEntity;", "import net.minecraft.world.entity.monster.skeleton.Skeleton;"),

    # entity.passive
    ("import net.minecraft.entity.passive.*;",          "import net.minecraft.world.entity.animal.*;"),
    ("import net.minecraft.entity.passive.AnimalEntity;","import net.minecraft.world.entity.animal.Animal;"),

    # entity.player
    ("import net.minecraft.entity.player.PlayerEntity;",    "import net.minecraft.world.entity.player.Player;"),
    ("import net.minecraft.entity.player.PlayerInventory;", "import net.minecraft.world.entity.player.Inventory;"),

    # entity.projectile
    ("import net.minecraft.entity.projectile.FireballEntity;",    "import net.minecraft.world.entity.projectile.hurtingprojectile.Fireball;"),
    ("import net.minecraft.entity.projectile.SmallFireballEntity;","import net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball;"),
    ("import net.minecraft.entity.projectile.ShulkerBulletEntity;","import net.minecraft.world.entity.projectile.ShulkerBullet;"),

    # === Inventory ===
    ("import net.minecraft.inventory.Inventories;",     "import net.minecraft.world.ContainerHelper;"),
    ("import net.minecraft.inventory.Inventory;",       "import net.minecraft.world.Container;"),
    ("import net.minecraft.inventory.SidedInventory;",  "import net.minecraft.world.WorldlyContainer;"),

    # === Item ===
    ("import net.minecraft.item.*;",                    "import net.minecraft.world.item.*;"),
    ("import net.minecraft.item.BlockItem;",            "import net.minecraft.world.item.BlockItem;"),
    ("import net.minecraft.item.Item;",                 "import net.minecraft.world.item.Item;"),
    ("import net.minecraft.item.ItemDisplayContext;",   "import net.minecraft.world.item.ItemDisplayContext;"),
    ("import net.minecraft.item.ItemGroup;",            "import net.minecraft.world.item.CreativeModeTab;"),
    ("import net.minecraft.item.ItemGroups;",           "import net.minecraft.world.item.CreativeModeTabs;"),
    ("import net.minecraft.item.ItemPlacementContext;", "import net.minecraft.world.item.context.BlockPlaceContext;"),
    ("import net.minecraft.item.ItemStack;",            "import net.minecraft.world.item.ItemStack;"),
    ("import net.minecraft.item.ItemUsageContext;",     "import net.minecraft.world.item.context.UseOnContext;"),
    ("import net.minecraft.item.Items;",                "import net.minecraft.world.item.Items;"),
    ("import net.minecraft.item.ShieldItem;",           "import net.minecraft.world.item.ShieldItem;"),
    ("import net.minecraft.item.ToolMaterial;",         "import net.minecraft.world.item.ToolMaterial;"),
    ("import net.minecraft.item.consume.ApplyEffectsConsumeEffect;", "import net.minecraft.world.item.consume.ApplyEffectsConsumeEffect;"),
    ("import net.minecraft.item.equipment.ArmorMaterial;",  "import net.minecraft.world.item.equipment.ArmorMaterial;"),
    ("import net.minecraft.item.equipment.EquipmentAsset;", "import net.minecraft.world.item.equipment.EquipmentAsset;"),
    ("import net.minecraft.item.equipment.EquipmentType;",  "import net.minecraft.world.item.equipment.EquipmentType;"),
    ("import net.minecraft.item.tooltip.TooltipType;",      "import net.minecraft.world.item.tooltip.TooltipType;"),

    # === Loot ===
    ("import net.minecraft.loot.LootTable;",            "import net.minecraft.world.level.storage.loot.LootTable;"),

    # === Particle ===
    ("import net.minecraft.particle.ParticleTypes;",    "import net.minecraft.core.particles.ParticleTypes;"),

    # === Potion ===
    ("import net.minecraft.potion.Potion;",             "import net.minecraft.world.item.alchemy.Potion;"),
    ("import net.minecraft.potion.Potions;",            "import net.minecraft.world.item.alchemy.Potions;"),

    # === Recipe ===
    ("import net.minecraft.recipe.*;",                  "import net.minecraft.world.item.crafting.*;"),
    ("import net.minecraft.recipe.RecipeEntry;",        "import net.minecraft.world.item.crafting.RecipeHolder;"),
    ("import net.minecraft.recipe.RecipeSerializer;",   "import net.minecraft.world.item.crafting.RecipeSerializer;"),
    ("import net.minecraft.recipe.RecipeType;",         "import net.minecraft.world.item.crafting.RecipeType;"),
    ("import net.minecraft.recipe.book.RecipeBookCategories;","import net.minecraft.world.item.crafting.RecipeBookCategories;"),
    ("import net.minecraft.recipe.book.RecipeBookCategory;",  "import net.minecraft.world.item.crafting.RecipeBookCategory;"),
    ("import net.minecraft.recipe.input.SingleStackRecipeInput;","import net.minecraft.world.item.crafting.CraftingInput;"),

    # === Registry ===
    ("import net.minecraft.registry.tag.BiomeTags;",    "import net.minecraft.tags.BiomeTags;"),
    ("import net.minecraft.registry.tag.DamageTypeTags;","import net.minecraft.tags.DamageTypeTags;"),
    ("import net.minecraft.registry.tag.ItemTags;",     "import net.minecraft.tags.ItemTags;"),
    ("import net.minecraft.registry.tag.TagKey;",       "import net.minecraft.tags.TagKey;"),
    ("import net.minecraft.registry.Registries;",       "import net.minecraft.core.registries.BuiltInRegistries;"),
    ("import net.minecraft.registry.Registry;",         "import net.minecraft.core.Registry;"),
    ("import net.minecraft.registry.RegistryKey;",      "import net.minecraft.resources.ResourceKey;"),
    ("import net.minecraft.registry.RegistryKeys;",     "import net.minecraft.core.registries.Registries;"),
    ("import net.minecraft.registry.RegistryOps;",      "import net.minecraft.resources.RegistryOps;"),
    ("import net.minecraft.registry.RegistryWrapper;",  "import net.minecraft.core.HolderLookup;"),
    ("import net.minecraft.registry.entry.RegistryEntry;",     "import net.minecraft.core.Holder;"),
    ("import net.minecraft.registry.entry.RegistryEntryList;", "import net.minecraft.core.HolderSet;"),

    # === Scoreboard ===
    ("import net.minecraft.scoreboard.ScoreHolder;",        "import net.minecraft.world.scores.ScoreHolder;"),
    ("import net.minecraft.scoreboard.Scoreboard;",         "import net.minecraft.world.scores.Scoreboard;"),
    ("import net.minecraft.scoreboard.ScoreboardObjective;","import net.minecraft.world.scores.Objective;"),

    # === Screen / Inventory ===
    ("import net.minecraft.screen.slot.Slot;",                  "import net.minecraft.world.inventory.Slot;"),
    ("import net.minecraft.screen.ArrayPropertyDelegate;",      "import net.minecraft.world.inventory.SimpleContainerData;"),
    ("import net.minecraft.screen.MerchantScreenHandler;",      "import net.minecraft.world.inventory.MerchantMenu;"),
    ("import net.minecraft.screen.NamedScreenHandlerFactory;",  "import net.minecraft.world.MenuProvider;"),
    ("import net.minecraft.screen.PropertyDelegate;",           "import net.minecraft.world.inventory.ContainerData;"),
    ("import net.minecraft.screen.ScreenHandler;",              "import net.minecraft.world.inventory.AbstractContainerMenu;"),
    ("import net.minecraft.screen.ScreenHandlerType;",          "import net.minecraft.world.inventory.MenuType;"),
    ("import net.minecraft.screen.SimpleNamedScreenHandlerFactory;","import net.minecraft.world.SimpleMenuProvider;"),

    # === Server ===
    ("import net.minecraft.server.command.CommandManager;",     "import net.minecraft.commands.Commands;"),
    ("import net.minecraft.server.command.ServerCommandSource;","import net.minecraft.commands.CommandSourceStack;"),
    ("import net.minecraft.server.network.ServerPlayerEntity;", "import net.minecraft.server.level.ServerPlayer;"),
    ("import net.minecraft.server.world.ServerWorld;",          "import net.minecraft.server.level.ServerLevel;"),

    # === Sound ===
    ("import net.minecraft.sound.BlockSoundGroup;",     "import net.minecraft.world.level.block.SoundType;"),
    ("import net.minecraft.sound.SoundCategory;",       "import net.minecraft.sounds.SoundSource;"),
    ("import net.minecraft.sound.SoundEvent;",          "import net.minecraft.sounds.SoundEvent;"),
    ("import net.minecraft.sound.SoundEvents;",         "import net.minecraft.sounds.SoundEvents;"),

    # === State ===
    ("import net.minecraft.state.StateManager;",                "import net.minecraft.world.level.block.state.StateDefinition;"),
    ("import net.minecraft.state.property.EnumProperty;",       "import net.minecraft.world.level.block.state.properties.EnumProperty;"),
    ("import net.minecraft.state.property.Properties;",         "import net.minecraft.world.level.block.state.properties.BlockStateProperties;"),

    # === Storage ===
    ("import net.minecraft.storage.ReadView;",          "import net.minecraft.world.level.storage.loot.LootContext;"),  # placeholder - may need fixing
    ("import net.minecraft.storage.WriteView;",         "import net.minecraft.world.level.storage.loot.LootContext;"),  # placeholder

    # === Village ===
    ("import net.minecraft.village.Merchant;",          "import net.minecraft.world.entity.npc.Merchant;"),
    ("import net.minecraft.village.TradeOffer;",        "import net.minecraft.world.entity.npc.MerchantOffer;"),
    ("import net.minecraft.village.TradeOfferList;",    "import net.minecraft.world.entity.npc.MerchantOffers;"),
    ("import net.minecraft.village.TradedItem;",        "import net.minecraft.world.entity.npc.MerchantOffer;"),  # placeholder

    # === World ===
    ("import net.minecraft.world.*;",                   "import net.minecraft.world.level.*;"),
    ("import net.minecraft.world.BlockRenderView;",     "import net.minecraft.world.level.BlockAndLightGetter;"),
    ("import net.minecraft.world.BlockView;",           "import net.minecraft.world.level.BlockGetter;"),
    ("import net.minecraft.world.Heightmap;",           "import net.minecraft.world.level.levelgen.Heightmap;"),
    ("import net.minecraft.world.LightType;",           "import net.minecraft.world.level.LightLayer;"),
    ("import net.minecraft.world.LocalDifficulty;",     "import net.minecraft.world.DifficultyInstance;"),
    ("import net.minecraft.world.RaycastContext;",      "import net.minecraft.world.level.ClipContext;"),
    ("import net.minecraft.world.ServerWorldAccess;",   "import net.minecraft.world.level.ServerLevelAccessor;"),
    ("import net.minecraft.world.StructureWorldAccess;","import net.minecraft.world.level.WorldGenLevel;"),
    ("import net.minecraft.world.TeleportTarget;",      "import net.minecraft.world.level.portal.TeleportTransition;"),
    ("import net.minecraft.world.World;",               "import net.minecraft.world.level.Level;"),
    ("import net.minecraft.world.WorldAccess;",         "import net.minecraft.world.level.LevelAccessor;"),
    ("import net.minecraft.world.WorldProperties;",     "import net.minecraft.world.level.storage.LevelData;"),
    ("import net.minecraft.world.WorldView;",           "import net.minecraft.world.level.LevelReader;"),

    # world.biome
    ("import net.minecraft.world.biome.Biome;",         "import net.minecraft.world.level.biome.Biome;"),
    ("import net.minecraft.world.biome.BiomeKeys;",     "import net.minecraft.world.level.biome.Biomes;"),

    # world.gen
    ("import net.minecraft.world.gen.GenerationStep;",  "import net.minecraft.world.level.levelgen.GenerationStep;"),
    ("import net.minecraft.world.gen.chunk.ChunkGenerator;", "import net.minecraft.world.level.chunk.ChunkGenerator;"),
    ("import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;","import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;"),
    ("import net.minecraft.world.gen.chunk.placement.SpreadType;","import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;"),
    ("import net.minecraft.world.gen.chunk.placement.StructurePlacement;","import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;"),
    ("import net.minecraft.world.gen.chunk.placement.StructurePlacementCalculator;","import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementCalculator;"),
    ("import net.minecraft.world.gen.chunk.placement.StructurePlacementType;","import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;"),
    ("import net.minecraft.world.gen.feature.FeaturePlacementContext;","import net.minecraft.world.level.levelgen.placement.PlacementContext;"),
    ("import net.minecraft.world.gen.feature.PlacedFeature;",    "import net.minecraft.world.level.levelgen.placement.PlacedFeature;"),
    ("import net.minecraft.world.gen.placementmodifier.PlacementModifier;",     "import net.minecraft.world.level.levelgen.placement.PlacementModifier;"),
    ("import net.minecraft.world.gen.placementmodifier.PlacementModifierType;", "import net.minecraft.world.level.levelgen.placement.PlacementModifierType;"),
    ("import net.minecraft.world.gen.structure.Structure;",      "import net.minecraft.world.level.levelgen.structure.Structure;"),

    # world.rule
    ("import net.minecraft.world.rule.GameRules;",      "import net.minecraft.world.level.gamerules.GameRules;"),

    # world.tick
    ("import net.minecraft.world.tick.ScheduledTickView;","import net.minecraft.world.level.redstone.ScheduledTickAccess;"),

    # === Component ===
    ("import net.minecraft.component.ComponentMap;",        "import net.minecraft.core.component.DataComponentMap;"),
    ("import net.minecraft.component.DataComponentTypes;",  "import net.minecraft.core.component.DataComponents;"),
    ("import net.minecraft.component.type.BlocksAttacksComponent;","import net.minecraft.world.item.component.BlocksAttacksComponent;"),
    ("import net.minecraft.component.type.ConsumableComponent;",   "import net.minecraft.world.item.component.ConsumableComponent;"),
    ("import net.minecraft.component.type.ConsumableComponents;",  "import net.minecraft.world.item.component.ConsumableComponents;"),
    ("import net.minecraft.component.type.FoodComponent;",         "import net.minecraft.world.food.FoodProperties;"),
    ("import net.minecraft.component.type.ItemEnchantmentsComponent;","import net.minecraft.world.item.enchantment.ItemEnchantments;"),
    ("import net.minecraft.component.type.NbtComponent;",          "import net.minecraft.world.item.component.CustomData;"),
    ("import net.minecraft.component.type.PotionContentsComponent;","import net.minecraft.world.item.alchemy.PotionContents;"),

    # === Enchantment ===
    ("import net.minecraft.enchantment.Enchantment;",           "import net.minecraft.world.item.enchantment.Enchantment;"),
    ("import net.minecraft.enchantment.EnchantmentLevelEntry;", "import net.minecraft.world.item.enchantment.EnchantmentInstance;"),
    ("import net.minecraft.enchantment.Enchantments;",          "import net.minecraft.world.item.enchantment.Enchantments;"),

    # === Stat ===
    ("import net.minecraft.stat.Stats;",                "import net.minecraft.stats.Stats;"),

    # === Command ===
    ("import net.minecraft.command.CommandRegistryAccess;",      "import net.minecraft.commands.CommandBuildContext;"),
    ("import net.minecraft.command.argument.EntityArgumentType;","import net.minecraft.commands.arguments.EntityArgument;"),
    ("import net.minecraft.command.permission.Permission;",      "import net.minecraft.commands.Commands;"),
    ("import net.minecraft.command.permission.PermissionLevel;", "import net.minecraft.server.permissions.PermissionLevel;"),

    # === Client ===
    ("import net.minecraft.client.MinecraftClient;",            "import net.minecraft.client.Minecraft;"),
    ("import net.minecraft.client.font.TextRenderer;",          "import net.minecraft.client.gui.Font;"),
    ("import net.minecraft.client.gl.RenderPipelines;",         "import net.minecraft.client.renderer.RenderPipelines;"),
    ("import net.minecraft.client.gui.DrawContext;",             "import net.minecraft.client.gui.GuiGraphics;"),
    ("import net.minecraft.client.gui.screen.Screen;",          "import net.minecraft.client.gui.screens.Screen;"),
    ("import net.minecraft.client.gui.screen.ingame.HandledScreen;","import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;"),
    ("import net.minecraft.client.gui.widget.ButtonWidget;",    "import net.minecraft.client.gui.components.Button;"),
    ("import net.minecraft.client.model.*;",                    "import net.minecraft.client.model.*;"),
    ("import net.minecraft.client.network.ClientPlayerEntity;", "import net.minecraft.client.player.LocalPlayer;"),
    ("import net.minecraft.client.render.RenderLayer;",         "import net.minecraft.client.renderer.rendertype.RenderType;"),
    ("import net.minecraft.client.render.RenderLayers;",        "import net.minecraft.client.renderer.rendertype.RenderTypes;"),
    ("import net.minecraft.client.render.RenderTickCounter;",   "import net.minecraft.client.renderer.RenderTickCounter;"),
    ("import net.minecraft.client.render.TexturedRenderLayers;","import net.minecraft.client.renderer.Sheets;"),
    ("import net.minecraft.client.render.VertexConsumerProvider;","import net.minecraft.client.renderer.MultiBufferSource;"),
    ("import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;","import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;"),
    ("import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;","import net.minecraft.client.renderer.blockentity.ChestRenderer;"),
    ("import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;","import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;"),
    ("import net.minecraft.client.render.block.entity.state.ChestBlockEntityRenderState;","import net.minecraft.client.renderer.blockentity.ChestRenderer;"),
    ("import net.minecraft.client.render.command.ModelCommandRenderer;",       "import net.minecraft.client.renderer.debug.DebugRenderer;"),
    ("import net.minecraft.client.render.command.OrderedRenderCommandQueue;",  "import net.minecraft.client.renderer.debug.DebugRenderer;"),
    ("import net.minecraft.client.render.entity.AbstractSkeletonEntityRenderer;","import net.minecraft.client.renderer.entity.AbstractSkeletonRenderer;"),
    ("import net.minecraft.client.render.entity.EntityRendererFactories;",     "import net.minecraft.client.renderer.entity.EntityRenderers;"),
    ("import net.minecraft.client.render.entity.EntityRendererFactory;",       "import net.minecraft.client.renderer.entity.EntityRendererProvider;"),
    ("import net.minecraft.client.render.entity.MobEntityRenderer;",           "import net.minecraft.client.renderer.entity.MobRenderer;"),
    ("import net.minecraft.client.render.entity.model.EntityModel;",           "import net.minecraft.client.model.EntityModel;"),
    ("import net.minecraft.client.render.entity.model.EntityModelLayer;",      "import net.minecraft.client.model.geom.ModelLayerLocation;"),
    ("import net.minecraft.client.render.entity.model.EntityModelLayers;",     "import net.minecraft.client.model.geom.ModelLayers;"),
    ("import net.minecraft.client.render.entity.model.ShieldEntityModel;",     "import net.minecraft.client.model.object.equipment.ShieldModel;"),
    ("import net.minecraft.client.render.entity.model.SkeletonEntityModel;",   "import net.minecraft.client.model.monster.skeleton.SkeletonModel;"),
    ("import net.minecraft.client.render.entity.model.VillagerResemblingModel;","import net.minecraft.client.model.VillagerModel;"),
    ("import net.minecraft.client.render.entity.state.LivingEntityRenderState;","import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;"),
    ("import net.minecraft.client.render.entity.state.SkeletonEntityRenderState;","import net.minecraft.client.renderer.entity.state.SkeletonRenderState;"),
    ("import net.minecraft.client.render.entity.state.VillagerEntityRenderState;","import net.minecraft.client.renderer.entity.state.VillagerEntityRenderState;"),
    ("import net.minecraft.client.render.item.model.special.ShieldModelRenderer;","import net.minecraft.client.renderer.item.properties.conditional.HasComponent;"),
    ("import net.minecraft.client.render.state.CameraRenderState;",            "import net.minecraft.client.renderer.culling.Frustum;"),
    ("import net.minecraft.client.util.SpriteIdentifier;",                     "import net.minecraft.client.renderer.texture.TextureAtlasSprite;"),
    ("import net.minecraft.client.util.math.MatrixStack;",                     "import com.mojang.blaze3d.vertex.PoseStack;"),
    ("import net.minecraft.client.render.entity.EntityRendererFactory;",       "import net.minecraft.client.renderer.entity.EntityRendererProvider;"),

    # Client rendering
    ("import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;","import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;"),
    ("import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;",  "import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;"),
]

# --- CODE BODY REPLACEMENTS ---
# These are regex-based replacements for class name usages in code (not just imports).
# Order matters: longer/more specific first.
# Each: (pattern, replacement) -- pattern uses \b word boundaries where appropriate.
CODE_REPLACEMENTS = [
    # NBT class names
    (r'\bNbtCompound\b',    'CompoundTag'),
    (r'\bNbtList\b',        'ListTag'),
    (r'\bNbtElement\b',     'Tag'),
    (r'\bNbtSizeTracker\b', 'NbtAccounter'),

    # Text / Chat
    (r'\bMutableText\b',    'MutableComponent'),
    (r'\bText\.literal\b',  'Component.literal'),
    (r'\bText\.translatable\b', 'Component.translatable'),
    (r'\bText\.of\b',       'Component.literal'),
    (r'\bText\.empty\b',    'Component.empty'),

    # Util renames
    (r'\bActionResult\b',           'InteractionResult'),
    (r'\bHand\b(?=\s*[;,\)]|\s+\w)',  'InteractionHand'),  # careful - "Hand" is common word
    (r'\bFormatting\b',             'ChatFormatting'),
    (r'\bVec3d\b',                  'Vec3'),
    (r'\bBox\b(?=\s*[;,\)<>])',     'AABB'),
    (r'\bBlockMirror\b',            'Mirror'),
    (r'\bBlockRotation\b',          'Rotation'),
    (r'\bDefaultedList\b',          'NonNullList'),
    (r'\bVoxelShapes\b',            'Shapes'),
    (r'\bUniformIntProvider\b',     'UniformInt'),

    # Block
    (r'\bAbstractBlock\.Settings\b',    'BlockBehaviour.Properties'),
    (r'\bAbstractBlock\b',          'BlockBehaviour'),
    (r'\bBlockRenderType\b',        'RenderShape'),
    (r'\bBlockWithEntity\b',        'BaseEntityBlock'),
    (r'\bExperienceDroppingBlock\b', 'DropExperienceBlock'),
    (r'\bHorizontalFacingBlock\b',   'HorizontalDirectionalBlock'),
    (r'\bShapeContext\b',            'CollisionContext'),

    # Entity renames
    (r'\bPlayerEntity\b',           'Player'),
    (r'\bServerPlayerEntity\b',     'ServerPlayer'),
    (r'\bClientPlayerEntity\b',     'LocalPlayer'),
    (r'\bSkeletonEntity\b',         'Skeleton'),
    (r'\bHostileEntity\b',          'Monster'),
    (r'\bPathAwareEntity\b',        'PathfinderMob'),
    (r'\bAnimalEntity\b',           'Animal'),
    (r'\bFireballEntity\b',         'Fireball'),
    (r'\bSmallFireballEntity\b',    'SmallFireball'),
    (r'\bShulkerBulletEntity\b',    'ShulkerBullet'),
    (r'\bExperienceOrbEntity\b',    'ExperienceOrb'),
    (r'\bAreaEffectCloudEntity\b',  'AreaEffectCloud'),
    (r'\bEnderDragonEntity\b',      'EnderDragon'),
    (r'\bWitherEntity\b',           'WitherBoss'),
    (r'\bEntityData\b',             'SpawnGroupData'),
    (r'\bSpawnGroup\b',             'MobCategory'),
    (r'\bSpawnReason\b',            'EntitySpawnReason'),

    # AI
    (r'\bLookAroundGoal\b',         'RandomLookAroundGoal'),
    (r'\bLookAtEntityGoal\b',       'LookAtPlayerGoal'),
    (r'\bWanderAroundFarGoal\b',    'WaterAvoidingRandomStrollGoal'),
    (r'\bBirdNavigation\b',         'FlyingPathNavigation'),
    (r'\bEntityNavigation\b',       'PathNavigation'),
    (r'\bFlightMoveControl\b',      'FlyingMoveControl'),

    # Attributes
    (r'\bDefaultAttributeContainer\b',  'AttributeSupplier'),
    (r'\bEntityAttributeModifier\b',    'AttributeModifier'),
    (r'\bEntityAttributes\b',          'Attributes'),
    (r'\bEntityAttributes\.GENERIC_',  'Attributes.'),

    # Boss / Boss bar
    (r'\bBossBar\b',                'BossEvent'),
    (r'\bServerBossBar\b',          'ServerBossEvent'),

    # Entity data tracking
    (r'\bDataTracker\b',                    'SynchedEntityData'),
    (r'\bTrackedData\b',                    'EntityDataAccessor'),
    (r'\bTrackedDataHandlerRegistry\b',     'EntityDataSerializers'),
    (r'\bDataTracker\.registerData\b',      'SynchedEntityData.defineId'),

    # Effects
    (r'\bStatusEffect\b',           'MobEffect'),
    (r'\bStatusEffectInstance\b',   'MobEffectInstance'),
    (r'\bStatusEffects\b',          'MobEffects'),
    (r'\bStatusEffectCategory\b',   'MobEffectCategory'),

    # Inventory / Container
    (r'\bInventories\b',            'ContainerHelper'),
    (r'\bPlayerInventory\b',        'Inventory'),
    (r'\bSidedInventory\b',         'WorldlyContainer'),

    # Item
    (r'\bItemGroup\b',              'CreativeModeTab'),
    (r'\bItemGroups\b',             'CreativeModeTabs'),
    (r'\bItemPlacementContext\b',   'BlockPlaceContext'),
    (r'\bItemUsageContext\b',       'UseOnContext'),

    # Recipe
    (r'\bRecipeEntry\b',            'RecipeHolder'),
    (r'\bSingleStackRecipeInput\b', 'CraftingInput'),

    # Registry
    (r'\bRegistries\b',             'BuiltInRegistries'),
    (r'\bRegistryKey\b',            'ResourceKey'),
    (r'\bRegistryKeys\b',           'Registries'),
    (r'\bRegistryWrapper\b',        'HolderLookup'),
    (r'\bRegistryEntry\b',          'Holder'),
    (r'\bRegistryEntryList\b',      'HolderSet'),

    # Scoreboard
    (r'\bScoreboardObjective\b',    'Objective'),

    # Screen / Menu
    (r'\bArrayPropertyDelegate\b',      'SimpleContainerData'),
    (r'\bMerchantScreenHandler\b',      'MerchantMenu'),
    (r'\bNamedScreenHandlerFactory\b',  'MenuProvider'),
    (r'\bPropertyDelegate\b',           'ContainerData'),
    (r'\bScreenHandler\b',              'AbstractContainerMenu'),
    (r'\bScreenHandlerType\b',          'MenuType'),
    (r'\bSimpleNamedScreenHandlerFactory\b', 'SimpleMenuProvider'),

    # Commands
    (r'\bCommandManager\b',         'Commands'),
    (r'\bServerCommandSource\b',    'CommandSourceStack'),
    (r'\bCommandRegistryAccess\b',  'CommandBuildContext'),
    (r'\bEntityArgumentType\b',     'EntityArgument'),

    # Server world
    (r'\bServerWorld\b',            'ServerLevel'),

    # Sound
    (r'\bBlockSoundGroup\b',        'SoundType'),
    (r'\bSoundCategory\b',          'SoundSource'),

    # State
    (r'\bStateManager\b',           'StateDefinition'),
    (r'\bStateManager\.Builder\b',  'StateDefinition.Builder'),

    # Village
    (r'\bTradeOfferList\b',         'MerchantOffers'),
    (r'\bTradeOffer\b',             'MerchantOffer'),

    # World / Level
    (r'\bServerWorldAccess\b',      'ServerLevelAccessor'),
    (r'\bStructureWorldAccess\b',   'WorldGenLevel'),
    (r'\bWorldAccess\b',            'LevelAccessor'),
    (r'\bWorldView\b',              'LevelReader'),
    (r'\bBlockRenderView\b',        'BlockAndLightGetter'),
    (r'\bBlockView\b',              'BlockGetter'),
    (r'\bLightType\b',              'LightLayer'),
    (r'\bLocalDifficulty\b',        'DifficultyInstance'),
    (r'\bRaycastContext\b',         'ClipContext'),
    (r'\bTeleportTarget\b',         'TeleportTransition'),

    # Biome
    (r'\bBiomeKeys\b',              'Biomes'),

    # Component
    (r'\bDataComponentTypes\b',     'DataComponents'),
    (r'\bComponentMap\b',           'DataComponentMap'),
    (r'\bFoodComponent\b',          'FoodProperties'),
    (r'\bItemEnchantmentsComponent\b', 'ItemEnchantments'),
    (r'\bNbtComponent\b',           'CustomData'),
    (r'\bPotionContentsComponent\b', 'PotionContents'),

    # Enchantment
    (r'\bEnchantmentLevelEntry\b',  'EnchantmentInstance'),

    # Client
    (r'\bMinecraftClient\b',        'Minecraft'),
    (r'\bTextRenderer\b',           'Font'),
    (r'\bDrawContext\b',            'GuiGraphics'),
    (r'\bButtonWidget\b',           'Button'),
    (r'\bMatrixStack\b',            'PoseStack'),
    (r'\bEntityModelLayer\b',       'ModelLayerLocation'),
    (r'\bEntityModelLayers\b',      'ModelLayers'),
    (r'\bMobEntityRenderer\b',      'MobRenderer'),
    (r'\bRenderLayer\b',            'RenderType'),
    (r'\bVertexConsumerProvider\b', 'MultiBufferSource'),
    (r'\bEntityRendererFactories\b','EntityRenderers'),
    (r'\bEntityRendererFactory\b',  'EntityRendererProvider'),
    (r'\bBlockEntityRendererFactories\b', 'BlockEntityRenderers'),
    (r'\bHudRenderCallback\b',      'HudLayerRegistrationCallback'),
]

def process_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    original = content

    # 1. Import line replacements (exact string match)
    for old, new in IMPORT_REPLACEMENTS:
        content = content.replace(old, new)

    # 2. Code body replacements (regex with word boundaries)
    for pattern, replacement in CODE_REPLACEMENTS:
        content = re.sub(pattern, replacement, content)

    if content != original:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        return True
    return False


def main():
    src_dir = os.path.join(os.path.dirname(__file__), 'src', 'main', 'java')
    changed = 0
    total = 0
    for root, dirs, files in os.walk(src_dir):
        for fname in files:
            if not fname.endswith('.java'):
                continue
            path = os.path.join(root, fname)
            total += 1
            if process_file(path):
                changed += 1
                print(f"  Updated: {os.path.relpath(path, src_dir)}")
    print(f"\nDone: {changed}/{total} files updated.")


if __name__ == '__main__':
    main()
