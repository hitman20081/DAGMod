package com.github.hitman20081.dagmod.recipe;

import com.github.hitman20081.dagmod.DagMod;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

public class ModRecipes {
    public static final RecipeSerializer<GemPolishingRecipe> GEM_POLISHING_SERIALIZER =
            Registry.register(
                    BuiltInRegistries.RECIPE_SERIALIZER,
                    Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "gem_polishing"),
                    GemPolishingRecipe.SERIALIZER
            );

    public static final RecipeType<GemPolishingRecipe> GEM_POLISHING_TYPE =
            Registry.register(
                    BuiltInRegistries.RECIPE_TYPE,
                    Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "gem_polishing"),
                    new RecipeType<GemPolishingRecipe>() {
                        @Override
                        public String toString() {
                            return "gem_polishing";
                        }
                    }
            );

    public static final RecipeSerializer<GemCrushingRecipe> GEM_CRUSHING_SERIALIZER =
            Registry.register(
                    BuiltInRegistries.RECIPE_SERIALIZER,
                    Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "gem_crushing"),
                    GemCrushingRecipe.SERIALIZER
            );

    public static final RecipeType<GemCrushingRecipe> GEM_CRUSHING_TYPE =
            Registry.register(
                    BuiltInRegistries.RECIPE_TYPE,
                    Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "gem_crushing"),
                    new RecipeType<GemCrushingRecipe>() {
                        @Override
                        public String toString() {
                            return "gem_crushing";
                        }
                    }
            );

    public static void registerRecipes() {
        DagMod.LOGGER.info("Registering Mod Recipes for " + DagMod.MOD_ID);
    }
}
