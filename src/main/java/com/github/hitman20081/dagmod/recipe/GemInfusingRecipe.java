package com.github.hitman20081.dagmod.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.Level;

public record GemInfusingRecipe(Ingredient ingredient, Identifier resultId, int resultCount) implements Recipe<CraftingInput> {

    // Nested codec for the "result": {"id": "...", "count": N} JSON object.
    // Avoids constructing ItemStack during JSON parsing (before DataComponents are bound).
    private record ResultSpec(Identifier id, int count) {
        static final Codec<ResultSpec> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Identifier.CODEC.fieldOf("id").forGetter(ResultSpec::id),
                Codec.INT.optionalFieldOf("count", 1).forGetter(ResultSpec::count)
        ).apply(inst, ResultSpec::new));
    }

    public static final MapCodec<GemInfusingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC.fieldOf("ingredient").forGetter(GemInfusingRecipe::ingredient),
            ResultSpec.CODEC.fieldOf("result").forGetter(r -> new ResultSpec(r.resultId, r.resultCount))
    ).apply(inst, (ingredient, spec) -> new GemInfusingRecipe(ingredient, spec.id(), spec.count())));

    public static final StreamCodec<RegistryFriendlyByteBuf, GemInfusingRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC, GemInfusingRecipe::ingredient,
                    ItemStack.STREAM_CODEC, GemInfusingRecipe::result,
                    (ingredient, stack) -> new GemInfusingRecipe(ingredient,
                            BuiltInRegistries.ITEM.getKey(stack.getItem()), stack.getCount()));

    public static final RecipeSerializer<GemInfusingRecipe> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);

    /** Creates the result ItemStack lazily at runtime (safe — DataComponents are bound by then). */
    public ItemStack result() {
        Item item = BuiltInRegistries.ITEM.getValue(resultId);
        return new ItemStack(item, resultCount);
    }

    @Override
    public boolean matches(CraftingInput input, Level world) {
        if (world.isClientSide()) {
            return false;
        }
        return input.items().stream().anyMatch(this.ingredient);
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        return result();
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
