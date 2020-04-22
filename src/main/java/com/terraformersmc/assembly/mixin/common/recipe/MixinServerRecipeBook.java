package com.terraformersmc.assembly.mixin.common.recipe;

import com.terraformersmc.assembly.util.recipe.RecipeUtil;
import net.minecraft.network.packet.s2c.play.UnlockRecipesS2CPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerRecipeBook;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;

@Mixin(ServerRecipeBook.class)
public abstract class MixinServerRecipeBook {
	@Shadow
	protected abstract void sendUnlockRecipesPacket(UnlockRecipesS2CPacket.Action action, ServerPlayerEntity player, List<Identifier> recipeIds);

	@Inject(method = "unlockRecipes(Ljava/util/Collection;Lnet/minecraft/server/network/ServerPlayerEntity;)I", at = @At("RETURN"))
	private void onUnlockRecipes(Collection<Recipe<?>> recipes, ServerPlayerEntity player, CallbackInfoReturnable<Integer> info) {
		RecipeUtil.sendUnlockedAssemblyRecipesPacket(recipes, player, this::sendUnlockRecipesPacket);
	}
}
