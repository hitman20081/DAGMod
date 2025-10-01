package com.github.hitman20081.dagmod.effect;

import com.github.hitman20081.dagmod.DagMod;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ModEffects implements ModInitializer {
    public static final RegistryEntry<StatusEffect> XP =
            Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(DagMod.MOD_ID, "xp"), new XpEffect());

    public static void registerEffects() {
    }

    @Override
    public void onInitialize() {

    }
}
