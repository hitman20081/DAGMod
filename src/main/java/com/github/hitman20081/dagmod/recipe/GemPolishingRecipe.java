package com.github.hitman20081.dagmod.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.Level;

public record GemPolishingRecipe(Ingredient ingredient, ItemStack result) implements Recipe<CraftingInput> {

    public static final MapCodec<GemPolishingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC.fieldOf("ingredient").forGetter(GemPolishingRecipe::ingredient),
            ItemStack.CODEC.fieldOf("result").forGetter(GemPolishingRecipe::result)
    ).apply(inst, GemPolishingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, GemPolishingRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC, GemPolishingRecipe::ingredient,
                    ItemStack.STREAM_CODEC, GemPolishingRecipe::result,
                    GemPolishingRecipe::new);

    public static final RecipeSerializer<GemPolishingRecipe> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);

    @Override
    public boolean matches(CraftingInput input, Level world) {
        if (world.isClientSide()) {
            return false;
        }
        return input.items().stream().anyMatch(this.ingredient);
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        return this.result.copy();
    }

    @Override
    public boolean showNotification() {
        return true;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public RecipeSerializer<GemPolishingRecipe> getSerializer() {
        return ModRecipes.GEM_POLISHING_SERIALIZER;
    }

    @Override
    public RecipeType<GemPolishingRecipe> getType() {
        return ModRecipes.GEM_POLISHING_TYPE;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(ingredient);
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    public Ingredient getIngredient() {
        return this.ingredient;
    }
}
