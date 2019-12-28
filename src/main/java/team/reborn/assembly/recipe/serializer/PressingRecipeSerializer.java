package team.reborn.assembly.recipe.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;
import team.reborn.assembly.recipe.PressingRecipe;

public class PressingRecipeSerializer implements RecipeSerializer<PressingRecipe> {
	private final int defaultPresses;
	private final PressingRecipeSerializer.RecipeFactory recipeFactory;

	public PressingRecipeSerializer(PressingRecipeSerializer.RecipeFactory recipeFactory, int defaultPresses) {
		this.recipeFactory = recipeFactory;
		this.defaultPresses = defaultPresses;
	}

	@Override
	public PressingRecipe read(Identifier identifier, JsonObject json) {
		JsonElement ingredientJson = JsonHelper.hasArray(json, "ingredient") ? JsonHelper.getArray(json, "ingredient") : JsonHelper.getObject(json, "ingredient");
		Ingredient ingredient = Ingredient.fromJson(ingredientJson);
		String resultId = JsonHelper.getString(json, "result");
		ItemStack result = new ItemStack(Registry.ITEM.getOrEmpty(new Identifier(resultId)).orElseThrow(() -> new IllegalStateException("Result Item: " + resultId + " does not exist")));
		int presses = JsonHelper.getInt(json, "presses", this.defaultPresses);
		return this.recipeFactory.create(identifier, ingredient, result, presses);
	}

	@Override
	public PressingRecipe read(Identifier identifier, PacketByteBuf buf) {
		Ingredient ingredient = Ingredient.fromPacket(buf);
		int presses = buf.readVarInt();
		ItemStack result = buf.readItemStack();
		return this.recipeFactory.create(identifier, ingredient, result, presses);
	}

	@Override
	public void write(PacketByteBuf buf, PressingRecipe recipe) {
		recipe.input.write(buf);
		buf.writeVarInt(recipe.presses);
		buf.writeItemStack(recipe.output);
	}

	interface RecipeFactory {
		PressingRecipe create(Identifier id, Ingredient ingredient, ItemStack result, int presses);
	}
}
