package com.terraformersmc.assembly.recipe.serializer;

import com.terraformersmc.assembly.Assembly;
import com.terraformersmc.assembly.recipe.BoilingRecipe;
import com.terraformersmc.assembly.recipe.InjectingRecipe;
import com.terraformersmc.assembly.recipe.PressingRecipe;
import com.terraformersmc.assembly.recipe.SqueezingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public class AssemblyRecipeSerializers {

	private static final Map<Identifier, RecipeSerializer<?>> RECIPE_SERIALIZERS = new LinkedHashMap<>();

	public static final BoilingRecipeSerializer BOILING = add("boiling", new BoilingRecipeSerializer(BoilingRecipe::new));
	public static final PressingRecipeSerializer PRESSING = add("pressing", new PressingRecipeSerializer(PressingRecipe::new, 1));
	public static final InjectingRecipeSerializer INJECTING = add("injecting", new InjectingRecipeSerializer(InjectingRecipe::new));
	public static final SqueezingRecipeSerializer SQUEEZING = add("squeezing", new SqueezingRecipeSerializer(SqueezingRecipe::new, 1));

	private static <S extends RecipeSerializer<T>, T extends Recipe<?>> S add(String name, S serializer) {
		RECIPE_SERIALIZERS.put(new Identifier(Assembly.MOD_ID, name), serializer);
		return serializer;
	}

	public static void register() {
		for (Identifier id : RECIPE_SERIALIZERS.keySet()) {
			Registry.register(Registry.RECIPE_SERIALIZER, id, RECIPE_SERIALIZERS.get(id));
		}
	}
}
