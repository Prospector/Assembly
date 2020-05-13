package com.terraformersmc.assembly.recipe.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.terraformersmc.assembly.recipe.InjectingRecipe;
import com.terraformersmc.assembly.recipe.ingredient.FluidIngredient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import reborncore.common.crafting.ConditionManager;

public class InjectingRecipeSerializer implements RecipeSerializer<InjectingRecipe> {
	private final InjectingRecipeSerializer.RecipeFactory factory;

	public InjectingRecipeSerializer(InjectingRecipeSerializer.RecipeFactory factory) {
		this.factory = factory;
	}

	@Override
	public InjectingRecipe read(Identifier id, JsonObject json) {
		if (ConditionManager.shouldLoadRecipe(json)) {
			JsonElement inputJson = JsonHelper.hasArray(json, "input") ? JsonHelper.getArray(json, "input") : JsonHelper.getObject(json, "input");
			Ingredient input = Ingredient.fromJson(inputJson);
			JsonElement fluidJson = JsonHelper.hasArray(json, "fluid") ? JsonHelper.getArray(json, "fluid") : JsonHelper.getObject(json, "fluid");
			FluidIngredient fluid = FluidIngredient.fromJson(fluidJson);
			String outputId = JsonHelper.getString(json, "output");
			ItemStack output = new ItemStack(Registry.ITEM.getOrEmpty(new Identifier(outputId)).orElseThrow(() -> new IllegalStateException("Result Item: " + outputId + " does not exist")));
			return this.factory.create(id, input, fluid, output);
		} else {
			return new InjectingRecipe(id);
		}
	}

	@Override
	public InjectingRecipe read(Identifier id, PacketByteBuf buf) {
		Ingredient itemIngredient = Ingredient.fromPacket(buf);
		FluidIngredient fluidIngredient = FluidIngredient.fromPacket(buf);
		ItemStack result = buf.readItemStack();
		return this.factory.create(id, itemIngredient, fluidIngredient, result);
	}

	@Override
	public void write(PacketByteBuf buf, InjectingRecipe recipe) {
		recipe.itemInput.write(buf);
		recipe.fluidInput.toPacket(buf);
		buf.writeItemStack(recipe.output);
	}

	interface RecipeFactory {
		InjectingRecipe create(Identifier id, Ingredient itemIngredient, FluidIngredient fluidIngredient, ItemStack result);
	}
}
