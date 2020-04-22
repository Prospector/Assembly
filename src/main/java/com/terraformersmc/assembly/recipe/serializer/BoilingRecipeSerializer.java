package com.terraformersmc.assembly.recipe.serializer;

import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.google.gson.JsonObject;
import com.terraformersmc.assembly.recipe.BoilingRecipe;
import com.terraformersmc.assembly.recipe.ingredient.FluidIngredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import reborncore.common.crafting.ConditionManager;

public class BoilingRecipeSerializer implements RecipeSerializer<BoilingRecipe> {
	private final BoilingRecipeSerializer.RecipeFactory factory;

	public BoilingRecipeSerializer(BoilingRecipeSerializer.RecipeFactory factory) {
		this.factory = factory;
	}

	@Override
	public BoilingRecipe read(Identifier id, JsonObject json) {
		if (ConditionManager.shouldLoadRecipe(json)) {
			FluidIngredient input = FluidIngredient.fromJson(json.getAsJsonObject("input"));
			FluidVolume output = FluidVolume.fromJson(json.getAsJsonObject("output"));
			return this.factory.create(id, input, output);
		} else {
			return new BoilingRecipe(id);
		}
	}

	@Override
	public BoilingRecipe read(Identifier id, PacketByteBuf buf) {
		FluidIngredient input = FluidIngredient.fromPacket(buf);
		CompoundTag output = buf.readCompoundTag();
		if (output == null) {
			throw new RuntimeException("Boiling recipe deserialization failed: output NBT from the buffer is null");
		}
		return this.factory.create(id, input, FluidVolume.fromTag(output));
	}

	@Override
	public void write(PacketByteBuf buf, BoilingRecipe recipe) {
		recipe.getInputIngredient().toPacket(buf);
		buf.writeCompoundTag(recipe.getOutputVolume().getFluidKey().toTag());
	}

	interface RecipeFactory {
		BoilingRecipe create(Identifier id, FluidIngredient input, FluidVolume output);
	}
}
