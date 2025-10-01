package com.github.hitman20081.dagmod.potion;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.effect.ModEffects;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ModPotions {
    public static final Potion XP_POTION =
            Registry.register(
                    Registries.POTION,
                    Identifier.of(DagMod.MOD_ID, "xp"),
                    new Potion("xp",
                            new StatusEffectInstance(
                                    ModEffects.XP,
                                    1200,
                                    0)));

    private static RegistryEntry<Potion> registerPotion(String name, Potion potion){
        return Registry.registerReference(Registries.POTION, Identifier.of(DagMod.MOD_ID, name),potion);

    }

    public static void registerPotion() {
        DagMod.LOGGER.info("Registering Mod Potions for " + com.github.hitman20081.dagmod.DagMod.MOD_ID);
    }
}


