package com.github.hitman20081.dagmod.item;

import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.ConsumableComponents;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;

import java.util.List;

public class ModFoodComponents {

    // ========== Tier 1 — Simple (Early Game / Novice Rewards) ==========

    public static final FoodComponent HONEY_BREAD_FOOD = new FoodComponent.Builder()
            .nutrition(6).saturationModifier(0.6f).alwaysEdible().build();
    public static final ConsumableComponent HONEY_BREAD_CONSUMABLE = ConsumableComponents.food()
            .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 0)))
            .build();

    public static final FoodComponent CANDIED_APPLE_FOOD = new FoodComponent.Builder()
            .nutrition(5).saturationModifier(0.5f).alwaysEdible().build();
    public static final ConsumableComponent CANDIED_APPLE_CONSUMABLE = ConsumableComponents.food()
            .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.SPEED, 300, 0)))
            .build();

    public static final FoodComponent CHICKEN_STEW = new FoodComponent.Builder()
            .nutrition(7).saturationModifier(0.7f).alwaysEdible().build();

    public static final FoodComponent PUMPKIN_PARFAIT_FOOD = new FoodComponent.Builder()
            .nutrition(6).saturationModifier(0.6f).alwaysEdible().build();
    public static final ConsumableComponent PUMPKIN_PARFAIT_CONSUMABLE = ConsumableComponents.food()
            .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 300, 0)))
            .build();

    // ========== Tier 2 — Hearty (Mid Game / Apprentice-Expert Rewards) ==========

    public static final FoodComponent BEEF_STEW_FOOD = new FoodComponent.Builder()
            .nutrition(9).saturationModifier(0.8f).alwaysEdible().build();
    public static final ConsumableComponent BEEF_STEW_CONSUMABLE = ConsumableComponents.food()
            .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 600, 0)))
            .build();

    public static final FoodComponent SPICED_RABBIT_FOOD = new FoodComponent.Builder()
            .nutrition(8).saturationModifier(0.7f).alwaysEdible().build();
    public static final ConsumableComponent SPICED_RABBIT_CONSUMABLE = ConsumableComponents.food()
            .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.SPEED, 600, 0)))
            .build();

    public static final FoodComponent SAVORY_BEEF_ROAST_FOOD = new FoodComponent.Builder()
            .nutrition(10).saturationModifier(0.9f).alwaysEdible().build();
    public static final ConsumableComponent SAVORY_BEEF_ROAST_CONSUMABLE = ConsumableComponents.food()
            .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 400, 0)))
            .build();

    public static final FoodComponent ELVEN_BREAD_FOOD = new FoodComponent.Builder()
            .nutrition(6).saturationModifier(1.0f).alwaysEdible().build();
    public static final ConsumableComponent ELVEN_BREAD_CONSUMABLE = ConsumableComponents.food()
            .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 1)))
            .build();

    public static final FoodComponent GLOWBERRY_JAM_FOOD = new FoodComponent.Builder()
            .nutrition(5).saturationModifier(0.6f).alwaysEdible().build();
    public static final ConsumableComponent GLOWBERRY_JAM_CONSUMABLE = ConsumableComponents.food()
            .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 1200, 0)))
            .build();

    public static final FoodComponent GOLDEN_APPLE_STRUDEL_FOOD = new FoodComponent.Builder()
            .nutrition(7).saturationModifier(0.8f).alwaysEdible().build();
    public static final ConsumableComponent GOLDEN_APPLE_STRUDEL_CONSUMABLE = ConsumableComponents.food()
            .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 600, 1)))
            .build();

    public static final FoodComponent FROSTBERRY_PIE_FOOD = new FoodComponent.Builder()
            .nutrition(7).saturationModifier(0.7f).alwaysEdible().build();
    public static final ConsumableComponent FROSTBERRY_PIE_CONSUMABLE = ConsumableComponents.food()
            .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 600, 0)))
            .build();

    public static final FoodComponent MYSTIC_STEW_FOOD = new FoodComponent.Builder()
            .nutrition(8).saturationModifier(0.7f).alwaysEdible().build();
    public static final ConsumableComponent MYSTIC_STEW_CONSUMABLE = ConsumableComponents.food()
            .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.LUCK, 1200, 0)))
            .build();

    // ========== Tier 3 — Exotic (Late Game / Expert-Master Rewards) ==========

    public static final FoodComponent MOLTEN_CHILI_FOOD = new FoodComponent.Builder()
            .nutrition(6).saturationModifier(0.6f).alwaysEdible().build();
    public static final ConsumableComponent MOLTEN_CHILI_CONSUMABLE = ConsumableComponents.food()
            .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 400, 1)))
            .build();

    public static final FoodComponent CRIMSON_SOUP_FOOD = new FoodComponent.Builder()
            .nutrition(8).saturationModifier(0.8f).alwaysEdible().build();
    public static final ConsumableComponent CRIMSON_SOUP_CONSUMABLE = ConsumableComponents.food()
            .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 1)))
            .build();

    public static final FoodComponent NETHER_SALAD_FOOD = new FoodComponent.Builder()
            .nutrition(7).saturationModifier(0.7f).alwaysEdible().build();
    public static final ConsumableComponent NETHER_SALAD_CONSUMABLE = ConsumableComponents.food()
            .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 1200, 0)))
            .build();

    public static final FoodComponent STORMFISH_FILLET_FOOD = new FoodComponent.Builder()
            .nutrition(8).saturationModifier(0.8f).alwaysEdible().build();
    public static final ConsumableComponent STORMFISH_FILLET_CONSUMABLE = ConsumableComponents.food()
            .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.SPEED, 400, 1)))
            .build();

    public static final FoodComponent DRAGONFRUIT_TART_FOOD = new FoodComponent.Builder()
            .nutrition(9).saturationModifier(0.9f).alwaysEdible().build();
    public static final ConsumableComponent DRAGONFRUIT_TART_CONSUMABLE = ConsumableComponents.food()
            .consumeEffect(new ApplyEffectsConsumeEffect(List.of(
                    new StatusEffectInstance(StatusEffects.STRENGTH, 600, 0),
                    new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 600, 0))))
            .build();

    public static final FoodComponent PHOENIX_ROAST_FOOD = new FoodComponent.Builder()
            .nutrition(10).saturationModifier(1.0f).alwaysEdible().build();
    public static final ConsumableComponent PHOENIX_ROAST_CONSUMABLE = ConsumableComponents.food()
            .consumeEffect(new ApplyEffectsConsumeEffect(List.of(
                    new StatusEffectInstance(StatusEffects.REGENERATION, 200, 1),
                    new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 900, 0))))
            .build();

    public static final FoodComponent SHADOW_CAKE_FOOD = new FoodComponent.Builder()
            .nutrition(7).saturationModifier(0.7f).alwaysEdible().build();
    public static final ConsumableComponent SHADOW_CAKE_CONSUMABLE = ConsumableComponents.food()
            .consumeEffect(new ApplyEffectsConsumeEffect(List.of(
                    new StatusEffectInstance(StatusEffects.INVISIBILITY, 300, 0),
                    new StatusEffectInstance(StatusEffects.SPEED, 600, 0))))
            .build();

    public static final FoodComponent ETHEREAL_COOKIES_FOOD = new FoodComponent.Builder()
            .nutrition(5).saturationModifier(0.5f).alwaysEdible().build();
    public static final ConsumableComponent ETHEREAL_COOKIES_CONSUMABLE = ConsumableComponents.food()
            .consumeEffect(new ApplyEffectsConsumeEffect(List.of(
                    new StatusEffectInstance(StatusEffects.SLOW_FALLING, 600, 0),
                    new StatusEffectInstance(StatusEffects.SPEED, 400, 0))))
            .build();

    public static final FoodComponent STARFRUIT_SMOOTHIE_FOOD = new FoodComponent.Builder()
            .nutrition(8).saturationModifier(0.9f).alwaysEdible().build();
    public static final ConsumableComponent STARFRUIT_SMOOTHIE_CONSUMABLE = ConsumableComponents.food()
            .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.HASTE, 900, 1)))
            .build();

    public static final FoodComponent VOID_TRUFFLES_FOOD = new FoodComponent.Builder()
            .nutrition(6).saturationModifier(0.8f).alwaysEdible().build();
    public static final ConsumableComponent VOID_TRUFFLES_CONSUMABLE = ConsumableComponents.food()
            .consumeEffect(new ApplyEffectsConsumeEffect(List.of(
                    new StatusEffectInstance(StatusEffects.RESISTANCE, 300, 1),
                    new StatusEffectInstance(StatusEffects.STRENGTH, 400, 0))))
            .build();
}
