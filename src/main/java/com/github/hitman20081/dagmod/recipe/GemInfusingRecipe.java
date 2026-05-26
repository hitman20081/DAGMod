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

public record GemInfusingRecipe(Ingredient ingredient, ItemStack result) implements Recipe<CraftingInput> {

    public static final MapCodec<GemInfusingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC.fieldOf("ingredient").forGetter(GemInfusingRecipe::ingredient),
            ItemStack.CODEC.fieldOf("result").forGetter(GemInfusingRecipe::result)
    ).apply(inst, GemInfusingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, GemInfusingRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC, GemInfusingRecipe::ingredient,
                    ItemStack.STREAM_CODEC, GemInfusingRecipe::result,
                    GemInfusingRecipe::new);

    public static final RecipeSerializer<GemInfusingRecipe> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);

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
    public RecipeSerializer<GemInfusingRecipe> getSerializer() {
        return ModRecipes.GEM_INFUSING_SERIALIZER;
    }

    @Override
    public RecipeType<GemInfusingRecipe> getType() {
        return ModRecipes.GEM_INFUSING_TYPE;
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
