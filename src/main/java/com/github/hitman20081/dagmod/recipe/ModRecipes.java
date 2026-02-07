package com.github.hitman20081.dagmod.recipe;

import com.github.hitman20081.dagmod.DagMod;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipes {
    public static final RecipeSerializer<GemPolishingRecipe> GEM_POLISHING_SERIALIZER =
            Registry.register(
                    Registries.RECIPE_SERIALIZER,
                    Identifier.of(DagMod.MOD_ID, "gem_polishing"),
                    new GemPolishingRecipe.Serializer()
            );

    public static final RecipeType<GemPolishingRecipe> GEM_POLISHING_TYPE =
            Registry.register(
                    Registries.RECIPE_TYPE,
                    Identifier.of(DagMod.MOD_ID, "gem_polishing"),
                    new RecipeType<GemPolishingRecipe>() {
                        @Override
                        public String toString() {
                            return "gem_polishing";
                        }
                    }
            );

    public static final RecipeSerializer<GemInfusingRecipe> GEM_INFUSING_SERIALIZER =
            Registry.register(
                    Registries.RECIPE_SERIALIZER,
                    Identifier.of(DagMod.MOD_ID, "gem_infusing"),
                    new GemInfusingRecipe.Serializer()
            );

    public static final RecipeType<GemInfusingRecipe> GEM_INFUSING_TYPE =
            Registry.register(
                    Registries.RECIPE_TYPE,
                    Identifier.of(DagMod.MOD_ID, "gem_infusing"),
                    new RecipeType<GemInfusingRecipe>() {
                        @Override
                        public String toString() {
                            return "gem_infusing";
                        }
                    }
            );

    public static void registerRecipes() {
        DagMod.LOGGER.info("Registering Mod Recipes for " + DagMod.MOD_ID);
    }
}
