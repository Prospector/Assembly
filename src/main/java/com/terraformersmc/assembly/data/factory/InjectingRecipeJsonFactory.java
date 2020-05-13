package com.terraformersmc.assembly.data.factory;

import com.google.gson.JsonObject;
import com.terraformersmc.assembly.Assembly;
import com.terraformersmc.assembly.recipe.ingredient.FluidIngredient;
import com.terraformersmc.assembly.recipe.serializer.AssemblyRecipeSerializers;
import com.terraformersmc.assembly.recipe.serializer.InjectingRecipeSerializer;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriteriaMerger;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class InjectingRecipeJsonFactory {
	private static final String RECIPE_TYPE = "injecting";
	private final Ingredient input;
	private final FluidIngredient fluid;
	private final Item output;
	private final Advancement.Task builder = Advancement.Task.create();
	private String group;
	private final InjectingRecipeSerializer serializer = AssemblyRecipeSerializers.INJECTING;

	public InjectingRecipeJsonFactory(Ingredient input, FluidIngredient fluid, Item output) {
		this.input = input;
		this.fluid = fluid;
		this.output = output;
	}

	public static InjectingRecipeJsonFactory create(Ingredient itemInput, FluidIngredient fluidInput, ItemConvertible output) {
		return new InjectingRecipeJsonFactory(itemInput, fluidInput, output.asItem());
	}

	public InjectingRecipeJsonFactory criterion(String criterionName, CriterionConditions conditions) {
		this.builder.criterion(criterionName, conditions);
		return this;
	}

	public void offerTo(Consumer<RecipeJsonProvider> exporter) {
		this.offerTo(exporter, Registry.ITEM.getId(this.output));
	}

	public void offerTo(Consumer<RecipeJsonProvider> exporter, String recipeIdStr) {
		Identifier outputId = Registry.ITEM.getId(this.output);
		Identifier recipeId = new Identifier(recipeIdStr);
		if (recipeId.equals(outputId)) {
			throw new IllegalStateException("Recipe " + recipeId + " should remove its 'save' argument");
		} else {
			this.offerTo(exporter, recipeId);
		}
	}

	public InjectingRecipeJsonFactory group(String group) {
		this.group = group;
		return this;
	}

	public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId) {
		this.validate(recipeId);
		recipeId = new Identifier(Assembly.MOD_ID, RECIPE_TYPE + "/" + recipeId.getPath());
		this.builder.parent(new Identifier("recipes/root")).criterion("has_the_recipe", new RecipeUnlockedCriterion.Conditions(EntityPredicate.Extended.EMPTY, recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(CriteriaMerger.OR);
		exporter.accept(new Provider(recipeId, this.group == null ? "" : this.group, this.input, this.fluid, this.output, this.builder, new Identifier(Assembly.MOD_ID, "recipes/" + recipeId.getPath()), this.serializer));
	}

	private void validate(Identifier recipeId) {
		if (this.builder.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + recipeId);
		}
	}

	public static class Provider implements RecipeJsonProvider {
		private final Identifier recipeId;
		private final String group;
		private final Ingredient input;
		private final FluidIngredient fluid;
		private final Item output;
		private final Advancement.Task builder;
		private final Identifier advancementId;
		private final InjectingRecipeSerializer serializer;

		public Provider(Identifier recipeId, String group, Ingredient input, FluidIngredient fluid, Item output, Advancement.Task builder, Identifier advancementId, InjectingRecipeSerializer serializer) {
			this.recipeId = recipeId;
			this.group = group;
			this.input = input;
			this.fluid = fluid;
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
			json.add("input", this.input.toJson());
			json.add("fluid", this.fluid.toJson());
			json.addProperty("output", Registry.ITEM.getId(output).toString());
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
