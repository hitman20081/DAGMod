package com.github.hitman20081.dagmod.potion;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.effect.ModEffects;
import net.fabricmc.fabric.api.registry.FabricPotionBrewingBuilder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Registry;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;

public class ModPotions {
    public static final Potion XP_POTION =
            Registry.register(
                    BuiltInRegistries.POTION,
                    Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "xp"),
                    new Potion("xp",
                            new MobEffectInstance(
                                    ModEffects.XP,
                                    1200,
                                    0)));

    // Amethyst Powder → Regeneration II (45s) + Absorption (2min)
    public static final Holder<Potion> AMETHYST_POTION = registerPotion("amethyst",
            new Potion("amethyst",
                    new MobEffectInstance(MobEffects.REGENERATION, 900, 1),
                    new MobEffectInstance(MobEffects.ABSORPTION, 2400, 0)));

    // Citrine Powder → Night Vision (5min) + Haste (3min)
    public static final Holder<Potion> CITRINE_POTION = registerPotion("citrine",
            new Potion("citrine",
                    new MobEffectInstance(MobEffects.NIGHT_VISION, 6000, 0),
                    new MobEffectInstance(MobEffects.HASTE, 3600, 0)));

    // Diamond Powder → Resistance (5min) + Health Boost (5min)
    public static final Holder<Potion> DIAMOND_POTION = registerPotion("diamond",
            new Potion("diamond",
                    new MobEffectInstance(MobEffects.RESISTANCE, 6000, 0),
                    new MobEffectInstance(MobEffects.HEALTH_BOOST, 6000, 0)));

    // Emerald Powder → Strength (3min) + Regeneration (3min)
    public static final Holder<Potion> EMERALD_POTION = registerPotion("emerald",
            new Potion("emerald",
                    new MobEffectInstance(MobEffects.STRENGTH, 3600, 0),
                    new MobEffectInstance(MobEffects.REGENERATION, 3600, 0)));

    // Quartz Powder → Haste II (3min) + Jump Boost (3min)
    public static final Holder<Potion> QUARTZ_POTION = registerPotion("quartz",
            new Potion("quartz",
                    new MobEffectInstance(MobEffects.HASTE, 3600, 1),
                    new MobEffectInstance(MobEffects.JUMP_BOOST, 3600, 0)));

    // Ruby Powder → Strength II (30s) + Fire Resistance (3min)
    public static final Holder<Potion> RUBY_POTION = registerPotion("ruby",
            new Potion("ruby",
                    new MobEffectInstance(MobEffects.STRENGTH, 600, 1),
                    new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 3600, 0)));

    // Sapphire Powder → Speed II (3min) + Water Breathing (3min)
    public static final Holder<Potion> SAPPHIRE_POTION = registerPotion("sapphire",
            new Potion("sapphire",
                    new MobEffectInstance(MobEffects.SPEED, 3600, 1),
                    new MobEffectInstance(MobEffects.WATER_BREATHING, 3600, 0)));

    // Topaz Powder → Luck (5min) + Haste II (2min)
    public static final Holder<Potion> TOPAZ_POTION = registerPotion("topaz",
            new Potion("topaz",
                    new MobEffectInstance(MobEffects.LUCK, 6000, 0),
                    new MobEffectInstance(MobEffects.HASTE, 2400, 1)));

    private static Holder<Potion> registerPotion(String name, Potion potion){
        return Registry.registerForHolder(BuiltInRegistries.POTION, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, name),potion);

    }

    public static void registerPotion() {
        DagMod.LOGGER.info("Registering Mod Potions for " + com.github.hitman20081.dagmod.DagMod.MOD_ID);
    }
}


