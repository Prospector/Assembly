package team.reborn.assembly.recipe.serializer;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.fluid.Fluid;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import reborncore.common.crafting.ConditionManager;
import team.reborn.assembly.recipe.BoilingRecipe;

public class BoilingRecipeSerializer implements RecipeSerializer<BoilingRecipe> {
	private final BoilingRecipeSerializer.RecipeFactory factory;

	public BoilingRecipeSerializer(BoilingRecipeSerializer.RecipeFactory factory) {
		this.factory = factory;
	}

	@Override
	public BoilingRecipe read(Identifier id, JsonObject json) {
		if (ConditionManager.shouldLoadRecipe(json)) {
			JsonObject inputObject = json.getAsJsonObject("input");
			String inputId = JsonHelper.getString(inputObject, "fluid");
			Fluid input = Registry.FLUID.getOrEmpty(new Identifier(inputId)).orElseThrow(() -> new IllegalStateException("Input Fluid: " + inputId + " does not exist"));
			JsonElement ratioElement = inputObject.get("ratio");
			FluidAmount ratio;
			if (!inputObject.has("ratio")) {
				ratio = FluidAmount.ofWhole(1);
			} else if (ratioElement.isJsonObject()) {
				JsonObject ratioObject = inputObject.getAsJsonObject("ratio");
				int whole = JsonHelper.getInt(ratioObject, "whole", 0);
				int numerator = JsonHelper.getInt(ratioObject, "numerator");
				int denominator = JsonHelper.getInt(ratioObject, "denominator");
				ratio = FluidAmount.of(whole, numerator, denominator);
			} else {
				try {
					ratio = FluidAmount.ofWhole(ratioElement.getAsInt());
				} catch (Exception e) {
					e.printStackTrace();
					throw new JsonParseException("Boiling recipe '" + id + "' has an invalid definition of a I/O ratio.");
				}
			}
			String outputId = JsonHelper.getString(json, "output");
			Fluid output = Registry.FLUID.getOrEmpty(new Identifier(outputId)).orElseThrow(() -> new IllegalStateException("Output Fluid: " + outputId + " does not exist"));
			return this.factory.create(id, input, ratio, output);
		} else {
			return new BoilingRecipe(id);
		}
	}

	@Override
	public BoilingRecipe read(Identifier id, PacketByteBuf buf) {
		Identifier inputId = buf.readIdentifier();
		Fluid input = Registry.FLUID.getOrEmpty(inputId).orElseThrow(() -> new IllegalStateException("Input Fluid: " + inputId + " does not exist"));
		FluidAmount ratio = FluidAmount.fromMcBuffer(buf);
		Identifier outputId = buf.readIdentifier();
		Fluid output = Registry.FLUID.getOrEmpty(inputId).orElseThrow(() -> new IllegalStateException("Output Fluid: " + outputId + " does not exist"));
		return this.factory.create(id, input, ratio, output);
	}

	@Override
	public void write(PacketByteBuf buf, BoilingRecipe recipe) {
		buf.writeIdentifier(Registry.FLUID.getId(recipe.inputFluid));
		recipe.ratio.toMcBuffer(buf);
		buf.writeIdentifier(Registry.FLUID.getId(recipe.output));
	}

	interface RecipeFactory {
		BoilingRecipe create(Identifier id, Fluid input, FluidAmount ratio, Fluid output);
	}
}
