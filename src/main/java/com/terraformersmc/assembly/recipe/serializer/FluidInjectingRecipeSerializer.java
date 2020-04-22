package com.terraformersmc.assembly.recipe.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.terraformersmc.assembly.recipe.FluidInjectingRecipe;
import com.terraformersmc.assembly.recipe.ingredient.FluidIngredient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import reborncore.common.crafting.ConditionManager;

public class FluidInjectingRecipeSerializer implements RecipeSerializer<FluidInjectingRecipe> {
	private final FluidInjectingRecipeSerializer.RecipeFactory factory;

	public FluidInjectingRecipeSerializer(FluidInjectingRecipeSerializer.RecipeFactory factory) {
		this.factory = factory;
	}

	@Override
	public FluidInjectingRecipe read(Identifier id, JsonObject json) {
		if (ConditionManager.shouldLoadRecipe(json)) {
			JsonElement inputJson = JsonHelper.hasArray(json, "input") ? JsonHelper.getArray(json, "input") : JsonHelper.getObject(json, "input");
			Ingredient input = Ingredient.fromJson(inputJson);
			JsonElement fluidJson = JsonHelper.hasArray(json, "fluid") ? JsonHelper.getArray(json, "fluid") : JsonHelper.getObject(json, "fluid");
			FluidIngredient fluid = FluidIngredient.fromJson(fluidJson);
			String outputId = JsonHelper.getString(json, "output");
			ItemStack output = new ItemStack(Registry.ITEM.getOrEmpty(new Identifier(outputId)).orElseThrow(() -> new IllegalStateException("Result Item: " + outputId + " does not exist")));
			return this.factory.create(id, input, fluid, output);
		} else {
			return new FluidInjectingRecipe(id);
		}
	}

	@Override
	public FluidInjectingRecipe read(Identifier id, PacketByteBuf buf) {
		Ingredient itemIngredient = Ingredient.fromPacket(buf);
		FluidIngredient fluidIngredient = FluidIngredient.fromPacket(buf);
		ItemStack result = buf.readItemStack();
		return this.factory.create(id, itemIngredient, fluidIngredient, result);
	}

	@Override
	public void write(PacketByteBuf buf, FluidInjectingRecipe recipe) {
		recipe.itemInput.write(buf);
		recipe.fluidInput.toPacket(buf);
		buf.writeItemStack(recipe.output);
	}

	interface RecipeFactory {
		FluidInjectingRecipe create(Identifier id, Ingredient itemIngredient, FluidIngredient fluidIngredient, ItemStack result);
	}
}
