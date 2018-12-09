package teamreborn.assembly.recipe;

import com.google.gson.*;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.crafting.ShapedRecipe;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class MachineRecipeManager implements ResourceReloadListener {

    public static final MachineRecipeManager INSTANCE = new MachineRecipeManager();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger LOGGER = LogManager.getLogger();

    public final Map<Identifier, MachineRecipe> recipes = new HashMap<>();

    private MachineRecipeManager() {
    }

    @Override
    public void onResourceReload(ResourceManager resourceManager) {
        recipes.clear();

        String directory = "assembly_recipes";
        Collection<Identifier> resources = resourceManager.findResources(directory, s -> s.endsWith(".json"));
        resources.forEach(resourceIdentifier -> {
            String path = resourceIdentifier.getPath();
            Identifier identifier = new Identifier(resourceIdentifier.getNamespace(), path.substring(directory.length() + 1, path.length() - 5));
            try {
                Resource resource = resourceManager.getResource(resourceIdentifier);
                String json = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
                JsonObject jsonObject = GSON.fromJson(json, JsonObject.class);
                MachineRecipe machineRecipe = parse(jsonObject, identifier);
                recipes.put(machineRecipe.getId(), machineRecipe);
                resource.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        LOGGER.info("Loaded {} machine recipes", recipes.size());
    }

    //Not null //JsonHelper is used as it throws a nice exception if its not found.
    public static MachineRecipe parse(JsonObject jsonObject, Identifier identifier) throws JsonParseException {
        BaseMachineRecipe recipe = new BaseMachineRecipe(identifier);
        recipe.type = new Identifier(JsonHelper.getString(jsonObject, "type"));
        recipe.ingredients = readIngredients(JsonHelper.getArray(jsonObject, "ingredients"));
        if (recipe.ingredients.isEmpty()) {
            throw new JsonParseException("No ingredients for recipe");
        }
        recipe.outputs.add(ShapedRecipe.deserializeItemStack(JsonHelper.getObject(jsonObject, "result")));
        recipe.tickTime = JsonHelper.getInt(jsonObject, "tick_time");

        return recipe;
    }

    private static DefaultedList<Ingredient> readIngredients(JsonArray jsonArray) {
        DefaultedList<Ingredient> ingredients = DefaultedList.create();
        for (int i = 0; i < jsonArray.size(); ++i) {
            Ingredient ingredient = Ingredient.fromJson(jsonArray.get(i));
            if (!ingredient.method_8103()) {
                ingredients.add(ingredient);
            }
        }
        return ingredients;
    }

    public static class BaseMachineRecipe implements MachineRecipe {

        private final Identifier identifier;
        private Identifier type;
        private List<Ingredient> ingredients = new ArrayList<>();
        private List<ItemStack> outputs = new ArrayList<>();
        private int tickTime;

        public BaseMachineRecipe(Identifier identifier) {
            this.identifier = identifier;
        }

        @Override
        public Identifier getId() {
            return identifier;
        }

        @Override
        public Identifier getType() {
            return type;
        }

        @Override
        public List<Ingredient> getInputs() {
            return ingredients;
        }

        @Override
        public List<ItemStack> getOutputs() {
            return outputs;
        }

        @Override
        public int tickTime() {
            return tickTime;
        }
    }

}
