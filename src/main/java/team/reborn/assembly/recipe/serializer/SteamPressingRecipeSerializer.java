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
import reborncore.common.crafting.ConditionManager;
import team.reborn.assembly.recipe.SteamPressingRecipe;

public class SteamPressingRecipeSerializer implements RecipeSerializer<SteamPressingRecipe> {
	private final int defaultPresses;
	private final SteamPressingRecipeSerializer.RecipeFactory factory;

	public SteamPressingRecipeSerializer(SteamPressingRecipeSerializer.RecipeFactory factory, int defaultPresses) {
		this.factory = factory;
		this.defaultPresses = defaultPresses;
	}

	@Override
	public SteamPressingRecipe read(Identifier id, JsonObject json) {
		if (ConditionManager.shouldLoadRecipe(json)) {
			JsonElement ingredientJson = JsonHelper.hasArray(json, "ingredient") ? JsonHelper.getArray(json, "ingredient") : JsonHelper.getObject(json, "ingredient");
			Ingredient ingredient = Ingredient.fromJson(ingredientJson);
			String resultId = JsonHelper.getString(json, "result");
			ItemStack result = new ItemStack(Registry.ITEM.getOrEmpty(new Identifier(resultId)).orElseThrow(() -> new IllegalStateException("Result Item: " + resultId + " does not exist")));
			int presses = JsonHelper.getInt(json, "presses", this.defaultPresses);
			return this.factory.create(id, ingredient, result, presses);
		} else {
			return new SteamPressingRecipe(id);
		}
	}

	@Override
	public SteamPressingRecipe read(Identifier id, PacketByteBuf buf) {
		Ingredient ingredient = Ingredient.fromPacket(buf);
		int presses = buf.readVarInt();
		ItemStack result = buf.readItemStack();
		return this.factory.create(id, ingredient, result, presses);
	}

	@Override
	public void write(PacketByteBuf buf, SteamPressingRecipe recipe) {
		recipe.input.write(buf);
		buf.writeVarInt(recipe.presses);
		buf.writeItemStack(recipe.output);
	}

	interface RecipeFactory {
		SteamPressingRecipe create(Identifier id, Ingredient ingredient, ItemStack result, int presses);
	}
}
