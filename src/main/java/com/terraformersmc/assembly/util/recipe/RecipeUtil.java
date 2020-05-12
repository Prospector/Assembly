package com.terraformersmc.assembly.util.recipe;

import com.google.common.collect.Lists;
import com.terraformersmc.assembly.mixin.common.recipe.RecipeManagerInvoker;
import com.terraformersmc.assembly.recipe.AssemblyRecipe;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.UnlockRecipesS2CPacket;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.List;

public class RecipeUtil {
	public static <I extends Inventory, R extends Recipe<I> & InputValidator, T extends RecipeType<R>> boolean isValidInput(World world, T recipeType, ItemStack input) {
		if (world != null) {
			RecipeManagerInvoker recipeManager = (RecipeManagerInvoker) world.getRecipeManager();
			return recipeManager.callGetAllOfType(recipeType).values().stream().anyMatch(recipe -> recipe != null && ((InputValidator) recipe).isValidInput(input));
		}
		return false;
	}

	public static String getSmeltingRecipeType(RecipeSerializer<? extends AbstractCookingRecipe> serializer) {
		return serializer == RecipeSerializer.SMELTING ? "smelting/" : serializer == RecipeSerializer.BLASTING ? "blasting/" : serializer == RecipeSerializer.SMOKING ? "smoking/" : serializer == RecipeSerializer.CAMPFIRE_COOKING ? "campfire_cooking/" : "";
	}

	public static void sendUnlockedAssemblyRecipesPacket(Collection<Recipe<?>> recipes, ServerPlayerEntity player, UnlockRecipesPacketSender sender) {
		List<Identifier> sending = Lists.newArrayList();
		for (Recipe<?> recipe : recipes) {
			Identifier identifier = recipe.getId();
			if (recipe instanceof AssemblyRecipe && !((AssemblyRecipe<?>) recipe).isIgnoredByPacketSender()) {
				sending.add(identifier);
				Criteria.RECIPE_UNLOCKED.trigger(player, recipe);
			}
		}
		sender.sendUnlockRecipesPacket(UnlockRecipesS2CPacket.Action.ADD, player, sending);
	}
}
