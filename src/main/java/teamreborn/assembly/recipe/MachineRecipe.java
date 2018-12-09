package teamreborn.assembly.recipe;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

import java.util.List;

public interface MachineRecipe {

    Identifier getId();

    Identifier getType();

    List<Ingredient> getInputs();

    List<ItemStack> getOutputs();

    default int tickTime() {
        return 120;
    }

    default boolean checkTags() {
        return true;
    }

    default boolean canCraft(BlockEntity blockEntity) {
        return true;
    }

    default boolean onCraft(BlockEntity blockEntity) {
        return true;
    }


}
