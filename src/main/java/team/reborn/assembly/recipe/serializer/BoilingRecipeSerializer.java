package team.reborn.assembly.recipe.serializer;

import com.google.gson.JsonObject;
import net.minecraft.fluid.Fluid;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;
import team.reborn.assembly.recipe.BoilingRecipe;

public class BoilingRecipeSerializer implements RecipeSerializer<BoilingRecipe> {
	private final BoilingRecipeSerializer.RecipeFactory recipeFactory;

	public BoilingRecipeSerializer(BoilingRecipeSerializer.RecipeFactory recipeFactory) {
		this.recipeFactory = recipeFactory;
	}

	@Override
	public BoilingRecipe read(Identifier identifier, JsonObject jsonObject) {
		JsonObject inputObject = jsonObject.getAsJsonObject("input");
		String inputId = JsonHelper.getString(inputObject, "fluid");
		Fluid input = Registry.FLUID.getOrEmpty(new Identifier(inputId)).orElseThrow(() -> new IllegalStateException("Input Fluid: " + inputId + " does not exist"));
		float ratio = JsonHelper.getFloat(inputObject, "ratio", 1);
		String outputId = JsonHelper.getString(jsonObject, "output");
		Fluid output = Registry.FLUID.getOrEmpty(new Identifier(outputId)).orElseThrow(() -> new IllegalStateException("Output Fluid: " + outputId + " does not exist"));
		return this.recipeFactory.create(identifier, input, ratio, output);
	}

	@Override
	public BoilingRecipe read(Identifier identifier, PacketByteBuf buf) {
		Identifier inputId = buf.readIdentifier();
		Fluid input = Registry.FLUID.getOrEmpty(inputId).orElseThrow(() -> new IllegalStateException("Input Fluid: " + inputId + " does not exist"));
		float ratio = buf.readFloat();
		Identifier outputId = buf.readIdentifier();
		Fluid output = Registry.FLUID.getOrEmpty(inputId).orElseThrow(() -> new IllegalStateException("Output Fluid: " + outputId + " does not exist"));
		return this.recipeFactory.create(identifier, input, ratio, output);
	}

	@Override
	public void write(PacketByteBuf buf, BoilingRecipe recipe) {
		buf.writeIdentifier(Registry.FLUID.getId(recipe.input));
		buf.writeFloat(recipe.ratio);
		buf.writeIdentifier(Registry.FLUID.getId(recipe.output));
	}

	interface RecipeFactory {
		BoilingRecipe create(Identifier id, Fluid input, float ratio, Fluid output);
	}
}
