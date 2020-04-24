package com.terraformersmc.assembly.data.factory;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import com.google.gson.JsonObject;
import com.terraformersmc.assembly.Assembly;
import com.terraformersmc.assembly.recipe.serializer.AssemblyRecipeSerializers;
import com.terraformersmc.assembly.recipe.serializer.BoilingRecipeSerializer;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriteriaMerger;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.fluid.Fluid;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class BoilingRecipeJsonFactory {
	private static final String RECIPE_TYPE = "boiling";
	private final Fluid input;
	private final FluidAmount ratio;
	private final Fluid output;
	private final Advancement.Task builder = Advancement.Task.create();
	private String group;
	private final BoilingRecipeSerializer serializer = AssemblyRecipeSerializers.BOILING;

	private BoilingRecipeJsonFactory(Fluid input, FluidAmount ratio, Fluid output) {
		this.input = input;
		this.ratio = ratio;
		this.output = output;
	}

	public static BoilingRecipeJsonFactory create(Fluid input, FluidAmount amount, Fluid output) {
		return new BoilingRecipeJsonFactory(input, amount, output);
	}

	public BoilingRecipeJsonFactory criterion(String criterionName, CriterionConditions conditions) {
		this.builder.criterion(criterionName, conditions);
		return this;
	}

	public void offerTo(Consumer<RecipeJsonProvider> exporter) {
		this.offerTo(exporter, Registry.FLUID.getId(output));
	}

	public void offerTo(Consumer<RecipeJsonProvider> exporter, String recipeIdStr) {
		Identifier recipeId = new Identifier(recipeIdStr);
		if (recipeId.equals(Registry.FLUID.getId(output))) {
			throw new IllegalStateException("Recipe " + recipeId + " should not manually set its recipe id because it would be the same if automatically inferred");
		} else {
			this.offerTo(exporter, recipeId);
		}
	}

	public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId) {
		this.validate(recipeId);
		recipeId = new Identifier(Assembly.MOD_ID, RECIPE_TYPE + "/" + recipeId.getPath());
		this.builder.parent(new Identifier("recipes/root")).criterion("has_the_recipe", new RecipeUnlockedCriterion.Conditions(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(CriteriaMerger.OR);
		exporter.accept(new Provider(recipeId, this.group == null ? "" : this.group, this.input, this.ratio, this.output, this.builder, new Identifier(Assembly.MOD_ID, "recipes/" + recipeId.getPath()), this.serializer));
	}

	private void validate(Identifier recipeId) {
		if (this.builder.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + recipeId);
		}
	}

	public static class Provider implements RecipeJsonProvider {
		private final Identifier recipeId;
		private final String group;
		public Fluid input;
		public FluidAmount ratio;
		public Fluid output;
		private final Advancement.Task builder;
		private final Identifier advancementId;
		private final BoilingRecipeSerializer serializer;

		public Provider(Identifier recipeId, String group, Fluid input, FluidAmount ratio, Fluid output, Advancement.Task builder, Identifier advancementId, BoilingRecipeSerializer serializer) {
			this.recipeId = recipeId;
			this.group = group;
			this.input = input;
			this.ratio = ratio;
			this.output = output;
			this.builder = builder;
			this.advancementId = advancementId;
			this.serializer = serializer;
		}

		@Override
		public void serialize(JsonObject json) {
			if (!this.group.isEmpty()) {
				json.addProperty("group", this.group);
			}
			JsonObject input = new JsonObject();
			input.addProperty(	"fluid", Registry.FLUID.getId(this.input).toString());
			json.add("input", input);
			json.addProperty("ratio", ratio.toParseableString());
			json.addProperty("output", Registry.FLUID.getId(output).toString());
		}

		@Override
		public RecipeSerializer<?> getSerializer() {
			return this.serializer;
		}

		@Override
		public Identifier getRecipeId() {
			return this.recipeId;
		}

		@Override
		@Nullable
		public JsonObject toAdvancementJson() {
			return this.builder.toJson();
		}

		@Override
		@Nullable
		public Identifier getAdvancementId() {
			return this.advancementId;
		}
	}
}
