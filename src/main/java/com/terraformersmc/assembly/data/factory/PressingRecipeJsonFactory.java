package com.terraformersmc.assembly.data.factory;

import com.google.gson.JsonObject;
import com.terraformersmc.assembly.Assembly;
import com.terraformersmc.assembly.recipe.serializer.AssemblyRecipeSerializers;
import com.terraformersmc.assembly.recipe.serializer.PressingRecipeSerializer;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriteriaMerger;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class PressingRecipeJsonFactory {
	private final String recipeType;
	private final Item output;
	private final Ingredient input;
	private final int presses;
	private final Advancement.Task builder = Advancement.Task.create();
	private String group;
	private final PressingRecipeSerializer serializer;

	private PressingRecipeJsonFactory(ItemConvertible output, Ingredient input, int presses, PressingRecipeSerializer serializer, String recipeType) {
		this.output = output.asItem();
		this.input = input;
		this.presses = presses;
		this.serializer = serializer;
		this.recipeType = recipeType;
	}

	public static PressingRecipeJsonFactory create(Ingredient input, ItemConvertible output, int presses, PressingRecipeSerializer serializer, String recipeType) {
		return new PressingRecipeJsonFactory(output, input, presses, serializer, recipeType);
	}

	public static PressingRecipeJsonFactory createSteamPressing(Ingredient input, ItemConvertible output, int presses) {
		return create(input, output, presses, AssemblyRecipeSerializers.STEAM_PRESSING, "steam_pressing");
	}

	public static PressingRecipeJsonFactory createSteamPressing(Ingredient input, ItemConvertible output) {
		return createSteamPressing(input, output, AssemblyRecipeSerializers.STEAM_PRESSING.getDefaultPresses());
	}

	public PressingRecipeJsonFactory criterion(String criterionName, CriterionConditions conditions) {
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

	public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId) {
		this.validate(recipeId);
		recipeId = new Identifier(Assembly.MOD_ID, recipeType + "/" + recipeId.getPath());
		this.builder.parent(new Identifier("recipes/root")).criterion("has_the_recipe", new RecipeUnlockedCriterion.Conditions(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(CriteriaMerger.OR);
		exporter.accept(new Provider(recipeId, this.group == null ? "" : this.group, this.input, this.output, this.presses, this.builder, new Identifier(Assembly.MOD_ID, "recipes/" + recipeId.getPath()), this.serializer));
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
		private final Item result;
		private final int presses;
		private final Advancement.Task builder;
		private final Identifier advancementId;
		private final PressingRecipeSerializer serializer;

		public Provider(Identifier recipeId, String group, Ingredient input, Item output, int presses, Advancement.Task builder, Identifier advancementId, PressingRecipeSerializer serializer) {
			this.recipeId = recipeId;
			this.group = group;
			this.ingredient = input;
			this.result = output;
			this.presses = presses;
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
			json.addProperty("output", Registry.ITEM.getId(this.result).toString());
			json.addProperty("presses", this.presses);
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
