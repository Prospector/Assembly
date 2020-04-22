package com.terraformersmc.assembly.recipe.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.terraformersmc.assembly.recipe.PressingRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import reborncore.common.crafting.ConditionManager;

public class PressingRecipeSerializer implements RecipeSerializer<PressingRecipe> {
	private final int defaultPresses;
	private final PressingRecipeSerializer.RecipeFactory factory;

	public PressingRecipeSerializer(PressingRecipeSerializer.RecipeFactory factory, int defaultPresses) {
		this.factory = factory;
		this.defaultPresses = defaultPresses;
	}

	public int getDefaultPresses() {
		return defaultPresses;
	}

	@Override
	public PressingRecipe read(Identifier id, JsonObject json) {
		if (ConditionManager.shouldLoadRecipe(json)) {
			JsonElement inputJson = JsonHelper.hasArray(json, "input") ? JsonHelper.getArray(json, "input") : JsonHelper.getObject(json, "input");
			Ingredient input = Ingredient.fromJson(inputJson);
			String outputId = JsonHelper.getString(json, "output");
			ItemStack result = new ItemStack(Registry.ITEM.getOrEmpty(new Identifier(outputId)).orElseThrow(() -> new IllegalStateException("Result Item: " + outputId + " does not exist")));
			int presses = JsonHelper.getInt(json, "presses", this.defaultPresses);
			return this.factory.create(id, input, result, presses);
		} else {
			return new PressingRecipe(id);
		}
	}

	@Override
	public PressingRecipe read(Identifier id, PacketByteBuf buf) {
		Ingredient ingredient = Ingredient.fromPacket(buf);
		int presses = buf.readVarInt();
		ItemStack result = buf.readItemStack();
		return this.factory.create(id, ingredient, result, presses);
	}

	@Override
	public void write(PacketByteBuf buf, PressingRecipe recipe) {
		recipe.input.write(buf);
		buf.writeVarInt(recipe.presses);
		buf.writeItemStack(recipe.output);
	}

	interface RecipeFactory {
		PressingRecipe create(Identifier id, Ingredient input, ItemStack output, int presses);
	}
}
