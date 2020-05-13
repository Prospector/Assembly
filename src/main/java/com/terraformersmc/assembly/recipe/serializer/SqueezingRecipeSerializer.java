package com.terraformersmc.assembly.recipe.serializer;

import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.terraformersmc.assembly.recipe.SqueezingRecipe;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import reborncore.common.crafting.ConditionManager;

public class SqueezingRecipeSerializer implements RecipeSerializer<SqueezingRecipe> {
	private final int defaultSqueezes;
	private final SqueezingRecipeSerializer.RecipeFactory factory;

	public SqueezingRecipeSerializer(SqueezingRecipeSerializer.RecipeFactory factory, int defaultSqueezes) {
		this.factory = factory;
		this.defaultSqueezes = defaultSqueezes;
	}

	public int getDefaultSqueezes() {
		return defaultSqueezes;
	}

	@Override
	public SqueezingRecipe read(Identifier id, JsonObject json) {
		if (ConditionManager.shouldLoadRecipe(json)) {
			JsonElement inputJson = JsonHelper.hasArray(json, "input") ? JsonHelper.getArray(json, "input") : JsonHelper.getObject(json, "input");
			Ingredient input = Ingredient.fromJson(inputJson);
			FluidVolume output = FluidVolume.fromJson(json.getAsJsonObject("output"));
			int squeezes = JsonHelper.getInt(json, "squeezes", this.defaultSqueezes);
			return this.factory.create(id, input, output, squeezes);
		} else {
			return new SqueezingRecipe(id);
		}
	}

	@Override
	public SqueezingRecipe read(Identifier id, PacketByteBuf buf) {
		Ingredient input = Ingredient.fromPacket(buf);
		int squeezes = buf.readVarInt();
		FluidVolume output = FluidVolume.fromTag(buf.readCompoundTag());
		return this.factory.create(id, input, output, squeezes);
	}

	@Override
	public void write(PacketByteBuf buf, SqueezingRecipe recipe) {
		recipe.input.write(buf);
		buf.writeVarInt(recipe.squeezes);
		buf.writeCompoundTag(recipe.output.toTag());
	}

	interface RecipeFactory {
		SqueezingRecipe create(Identifier id, Ingredient input, FluidVolume output, int squeezes);
	}
}
