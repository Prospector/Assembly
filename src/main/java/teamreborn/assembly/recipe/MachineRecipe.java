package teamreborn.assembly.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

import java.util.List;

public interface MachineRecipe {

	Identifier getId();

	Identifier getType();

	List<Ingredient> getInputs();

	List<ItemStack> getOutputs();

}
