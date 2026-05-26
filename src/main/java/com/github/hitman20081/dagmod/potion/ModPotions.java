package com.github.hitman20081.dagmod.potion;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.effect.ModEffects;
import net.fabricmc.fabric.api.registry.FabricPotionBrewingBuilder;
import net.minecraft.world.effect.MobEffectInstance;
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

    private static Holder<Potion> registerPotion(String name, Potion potion){
        return Registry.registerForHolder(BuiltInRegistries.POTION, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, name),potion);

    }

    public static void registerPotion() {
        DagMod.LOGGER.info("Registering Mod Potions for " + com.github.hitman20081.dagmod.DagMod.MOD_ID);
    }
}


