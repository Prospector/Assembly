package com.terraformersmc.assembly.data.factory;

import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.google.gson.JsonObject;
import com.terraformersmc.assembly.Assembly;
import com.terraformersmc.assembly.recipe.serializer.AssemblyRecipeSerializers;
import com.terraformersmc.assembly.recipe.serializer.PressingRecipeSerializer;
import com.terraformersmc.assembly.recipe.serializer.SqueezingRecipeSerializer;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriteriaMerger;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class SqueezingRecipeJsonFactory {
	private final String recipeType;
	private final FluidVolume output;
	private final Ingredient input;
	private final int squeezes;
	private final Advancement.Task builder = Advancement.Task.create();
	private String group;
	private final SqueezingRecipeSerializer serializer;

	private SqueezingRecipeJsonFactory(FluidVolume output, Ingredient input, int squeezes, SqueezingRecipeSerializer serializer, String recipeType) {
		this.output = output;
		this.input = input;
		this.squeezes = squeezes;
		this.serializer = serializer;
		this.recipeType = recipeType;
	}

	public static SqueezingRecipeJsonFactory create(Ingredient input, FluidVolume output, int presses, SqueezingRecipeSerializer serializer, String recipeType) {
		return new SqueezingRecipeJsonFactory(output, input, presses, serializer, recipeType);
	}

	public static SqueezingRecipeJsonFactory create(Ingredient input, FluidVolume output, int presses) {
		return create(input, output, presses, AssemblyRecipeSerializers.SQUEEZING, "squeezing");
	}

	public static SqueezingRecipeJsonFactory create(Ingredient input, FluidVolume output) {
		return create(input, output, AssemblyRecipeSerializers.SQUEEZING.getDefaultSqueezes());
	}

	public SqueezingRecipeJsonFactory criterion(String criterionName, CriterionConditions conditions) {
		this.builder.criterion(criterionName, conditions);
		return this;
	}

	public void offerTo(Consumer<RecipeJsonProvider> exporter) {
		if (output.getRawFluid() != null) {
			this.offerTo(exporter, Registry.FLUID.getId(this.output.getRawFluid()));
		} else {
			throw new IllegalStateException("Squeezing recipe name cannot be inferred from a volume without a raw Fluid: " + this.output.toString());
		}
	}

	public void offerTo(Consumer<RecipeJsonProvider> exporter, String recipeIdStr) {
		Identifier outputId = null;
		if (output.getRawFluid() != null) {
			outputId = Registry.FLUID.getId(this.output.getRawFluid());
		}
		Identifier recipeId = new Identifier(recipeIdStr);
		if (recipeId.equals(outputId)) {
			throw new IllegalStateException("Recipe " + recipeId + " should remove its 'save' argument");
		} else {
			this.offerTo(exporter, recipeId);
		}
	}

	public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId) {
		this.validate(recipeId);
		recipeId = new Identifier(Assembly.MOD_ID, recipeType + "/" + recipeId.getPath());
		this.builder.parent(new Identifier("recipes/root")).criterion("has_the_recipe", new RecipeUnlockedCriterion.Conditions(EntityPredicate.Extended.EMPTY, recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(CriteriaMerger.OR);
		exporter.accept(new Provider(recipeId, this.group == null ? "" : this.group, this.input, this.output, this.squeezes, this.builder, new Identifier(Assembly.MOD_ID, "recipes/" + recipeId.getPath()), this.serializer));
	}

	private void validate(Identifier recipeId) {
		if (this.builder.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + recipeId);
		}
	}

	public static class Provider implements RecipeJsonProvider {
		private final Identifier recipeId;
		private final String group;
		private final Ingredient ingredient;
		private final FluidVolume output;
		private final int squeezes;
		private final Advancement.Task builder;
		private final Identifier advancementId;
		private final SqueezingRecipeSerializer serializer;

		public Provider(Identifier recipeId, String group, Ingredient input, FluidVolume output, int squeezes, Advancement.Task builder, Identifier advancementId, SqueezingRecipeSerializer serializer) {
			this.recipeId = recipeId;
			this.group = group;
			this.ingredient = input;
			this.output = output;
			this.squeezes = squeezes;
			this.builder = builder;
			this.advancementId = advancementId;
			this.serializer = serializer;
		}

		@Override
		public void serialize(JsonObject json) {
			if (!this.group.isEmpty()) {
				json.addProperty("group", this.group);
			}

			json.add("input", this.ingredient.toJson());
			json.add("output", this.output.toJson());
			json.addProperty("squeezes", this.squeezes);
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
