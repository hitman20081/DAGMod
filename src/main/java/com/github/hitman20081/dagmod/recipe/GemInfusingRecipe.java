package com.github.hitman20081.dagmod.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookCategories;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public record GemInfusingRecipe(Ingredient ingredient, ItemStack result) implements Recipe<SingleStackRecipeInput> {
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> list = DefaultedList.of();
        list.add(this.ingredient);
        return list;
    }

    @Override
    public boolean matches(SingleStackRecipeInput input, World world) {
        if(world.isClient()) {
            return false;
        }
        return this.ingredient.test(input.item());
    }

    @Override
    public ItemStack craft(SingleStackRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return this.result.copy();
    }

    @Override
    public RecipeSerializer<? extends Recipe<SingleStackRecipeInput>> getSerializer() {
        return ModRecipes.GEM_INFUSING_SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<SingleStackRecipeInput>> getType() {
        return ModRecipes.GEM_INFUSING_TYPE;
    }

    @Override
    public IngredientPlacement getIngredientPlacement() {
        return IngredientPlacement.forSingleSlot(ingredient);
    }

    @Override
    public RecipeBookCategory getRecipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    public Ingredient getIngredient() {
        return this.ingredient;
    }

    public static class Serializer implements RecipeSerializer<GemInfusingRecipe> {
        public static final MapCodec<GemInfusingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(GemInfusingRecipe::ingredient),
                ItemStack.CODEC.fieldOf("result").forGetter(GemInfusingRecipe::result)
        ).apply(inst, GemInfusingRecipe::new));

        public static final PacketCodec<RegistryByteBuf, GemInfusingRecipe> STREAM_CODEC =
                PacketCodec.tuple(
                        Ingredient.PACKET_CODEC, GemInfusingRecipe::ingredient,
                        ItemStack.PACKET_CODEC, GemInfusingRecipe::result,
                        GemInfusingRecipe::new);

        @Override
        public MapCodec<GemInfusingRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, GemInfusingRecipe> packetCodec() {
            return STREAM_CODEC;
        }
    }
}
