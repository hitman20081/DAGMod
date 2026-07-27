package com.github.hitman20081.dagmod.effect;

import com.github.hitman20081.dagmod.DagMod;
import net.fabricmc.api.ModInitializer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Registry;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;

public class ModEffects implements ModInitializer {
    public static final Holder<MobEffect> XP =
            Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "xp"), new XpEffect());

    public static void registerEffects() {
    }

    @Override
    public void onInitialize() {

    }
}
