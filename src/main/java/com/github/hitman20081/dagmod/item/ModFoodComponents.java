package com.github.hitman20081.dagmod.item;

import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;

import java.util.List;

public class ModFoodComponents {

    // ========== Tier 1 — Simple (Early Game / Novice Rewards) ==========

    public static final FoodProperties HONEY_BREAD_FOOD = new FoodProperties.Builder()
            .nutrition(6).saturationModifier(0.6f).alwaysEdible().build();
    public static final Consumable HONEY_BREAD_CONSUMABLE = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0)))
            .build();

    public static final FoodProperties CANDIED_APPLE_FOOD = new FoodProperties.Builder()
            .nutrition(5).saturationModifier(0.5f).alwaysEdible().build();
    public static final Consumable CANDIED_APPLE_CONSUMABLE = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.SPEED, 300, 0)))
            .build();

    public static final FoodProperties CHICKEN_STEW = new FoodProperties.Builder()
            .nutrition(7).saturationModifier(0.7f).alwaysEdible().build();

    public static final FoodProperties PUMPKIN_PARFAIT_FOOD = new FoodProperties.Builder()
            .nutrition(6).saturationModifier(0.6f).alwaysEdible().build();
    public static final Consumable PUMPKIN_PARFAIT_CONSUMABLE = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.ABSORPTION, 300, 0)))
            .build();

    // ========== Tier 2 — Hearty (Mid Game / Apprentice-Expert Rewards) ==========

    public static final FoodProperties BEEF_STEW_FOOD = new FoodProperties.Builder()
            .nutrition(9).saturationModifier(0.8f).alwaysEdible().build();
    public static final Consumable BEEF_STEW_CONSUMABLE = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.STRENGTH, 600, 0)))
            .build();

    public static final FoodProperties SPICED_RABBIT_FOOD = new FoodProperties.Builder()
            .nutrition(8).saturationModifier(0.7f).alwaysEdible().build();
    public static final Consumable SPICED_RABBIT_CONSUMABLE = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.SPEED, 600, 0)))
            .build();

    public static final FoodProperties SAVORY_BEEF_ROAST_FOOD = new FoodProperties.Builder()
            .nutrition(10).saturationModifier(0.9f).alwaysEdible().build();
    public static final Consumable SAVORY_BEEF_ROAST_CONSUMABLE = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.RESISTANCE, 400, 0)))
            .build();

    public static final FoodProperties ELVEN_BREAD_FOOD = new FoodProperties.Builder()
            .nutrition(6).saturationModifier(1.0f).alwaysEdible().build();
    public static final Consumable ELVEN_BREAD_CONSUMABLE = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1)))
            .build();

    public static final FoodProperties GLOWBERRY_JAM_FOOD = new FoodProperties.Builder()
            .nutrition(5).saturationModifier(0.6f).alwaysEdible().build();
    public static final Consumable GLOWBERRY_JAM_CONSUMABLE = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 1200, 0)))
            .build();

    public static final FoodProperties GOLDEN_APPLE_STRUDEL_FOOD = new FoodProperties.Builder()
            .nutrition(7).saturationModifier(0.8f).alwaysEdible().build();
    public static final Consumable GOLDEN_APPLE_STRUDEL_CONSUMABLE = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.ABSORPTION, 600, 1)))
            .build();

    public static final FoodProperties FROSTBERRY_PIE_FOOD = new FoodProperties.Builder()
            .nutrition(7).saturationModifier(0.7f).alwaysEdible().build();
    public static final Consumable FROSTBERRY_PIE_CONSUMABLE = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 600, 0)))
            .build();

    public static final FoodProperties MYSTIC_STEW_FOOD = new FoodProperties.Builder()
            .nutrition(8).saturationModifier(0.7f).alwaysEdible().build();
    public static final Consumable MYSTIC_STEW_CONSUMABLE = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.LUCK, 1200, 0)))
            .build();

    // ========== Tier 3 — Exotic (Late Game / Expert-Master Rewards) ==========

    public static final FoodProperties MOLTEN_CHILI_FOOD = new FoodProperties.Builder()
            .nutrition(6).saturationModifier(0.6f).alwaysEdible().build();
    public static final Consumable MOLTEN_CHILI_CONSUMABLE = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.STRENGTH, 400, 1)))
            .build();

    public static final FoodProperties CRIMSON_SOUP_FOOD = new FoodProperties.Builder()
            .nutrition(8).saturationModifier(0.8f).alwaysEdible().build();
    public static final Consumable CRIMSON_SOUP_CONSUMABLE = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1)))
            .build();

    public static final FoodProperties NETHER_SALAD_FOOD = new FoodProperties.Builder()
            .nutrition(7).saturationModifier(0.7f).alwaysEdible().build();
    public static final Consumable NETHER_SALAD_CONSUMABLE = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 1200, 0)))
            .build();

    public static final FoodProperties STORMFISH_FILLET_FOOD = new FoodProperties.Builder()
            .nutrition(8).saturationModifier(0.8f).alwaysEdible().build();
    public static final Consumable STORMFISH_FILLET_CONSUMABLE = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.SPEED, 400, 1)))
            .build();

    public static final FoodProperties DRAGONFRUIT_TART_FOOD = new FoodProperties.Builder()
            .nutrition(9).saturationModifier(0.9f).alwaysEdible().build();
    public static final Consumable DRAGONFRUIT_TART_CONSUMABLE = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(List.of(
                    new MobEffectInstance(MobEffects.STRENGTH, 600, 0),
                    new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 600, 0))))
            .build();

    public static final FoodProperties PHOENIX_ROAST_FOOD = new FoodProperties.Builder()
            .nutrition(10).saturationModifier(1.0f).alwaysEdible().build();
    public static final Consumable PHOENIX_ROAST_CONSUMABLE = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(List.of(
                    new MobEffectInstance(MobEffects.REGENERATION, 200, 1),
                    new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 900, 0))))
            .build();

    public static final FoodProperties SHADOW_CAKE_FOOD = new FoodProperties.Builder()
            .nutrition(7).saturationModifier(0.7f).alwaysEdible().build();
    public static final Consumable SHADOW_CAKE_CONSUMABLE = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(List.of(
                    new MobEffectInstance(MobEffects.INVISIBILITY, 300, 0),
                    new MobEffectInstance(MobEffects.SPEED, 600, 0))))
            .build();

    public static final FoodProperties ETHEREAL_COOKIES_FOOD = new FoodProperties.Builder()
            .nutrition(5).saturationModifier(0.5f).alwaysEdible().build();
    public static final Consumable ETHEREAL_COOKIES_CONSUMABLE = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(List.of(
                    new MobEffectInstance(MobEffects.SLOW_FALLING, 600, 0),
                    new MobEffectInstance(MobEffects.SPEED, 400, 0))))
            .build();

    public static final FoodProperties STARFRUIT_SMOOTHIE_FOOD = new FoodProperties.Builder()
            .nutrition(8).saturationModifier(0.9f).alwaysEdible().build();
    public static final Consumable STARFRUIT_SMOOTHIE_CONSUMABLE = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.HASTE, 900, 1)))
            .build();

    public static final FoodProperties VOID_TRUFFLES_FOOD = new FoodProperties.Builder()
            .nutrition(6).saturationModifier(0.8f).alwaysEdible().build();
    public static final Consumable VOID_TRUFFLES_CONSUMABLE = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(List.of(
                    new MobEffectInstance(MobEffects.RESISTANCE, 300, 1),
                    new MobEffectInstance(MobEffects.STRENGTH, 400, 0))))
            .build();
}
