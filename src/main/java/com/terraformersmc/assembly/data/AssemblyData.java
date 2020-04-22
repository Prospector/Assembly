package com.terraformersmc.assembly.data;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import com.terraformersmc.assembly.Assembly;
import com.terraformersmc.assembly.block.AssemblyBlocks;
import com.terraformersmc.assembly.data.factory.BoilingRecipeFactory;
import com.terraformersmc.assembly.data.factory.FluidInjectingRecipeFactory;
import com.terraformersmc.assembly.data.factory.PressingRecipeFactory;
import com.terraformersmc.assembly.entity.AssemblyEntities;
import com.terraformersmc.assembly.fluid.AssemblyFluids;
import com.terraformersmc.assembly.item.AssemblyItems;
import com.terraformersmc.assembly.recipe.ingredient.FluidIngredient;
import com.terraformersmc.assembly.tag.AssemblyBlockTags;
import com.terraformersmc.assembly.tag.AssemblyItemTags;
import com.terraformersmc.dossier.DossierProvider;
import com.terraformersmc.dossier.Dossiers;
import com.terraformersmc.dossier.generator.BlockTagsDossier;
import com.terraformersmc.dossier.generator.ItemTagsDossier;
import com.terraformersmc.dossier.generator.LootTablesDossier;
import com.terraformersmc.dossier.generator.RecipesDossier;
import net.minecraft.advancement.criterion.EnterBlockCriterion;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.CookingRecipeJsonFactory;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonFactory;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonFactory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.registry.Registry;

import java.util.function.Consumer;

public class AssemblyData implements DossierProvider {
	@Override
	public Dossiers createDossiers() {
		return Dossiers.builder()
				.addBlockTags(AssemblyBlockTagsGenerator::new)
				.addItemTags(AssemblyItemTagsGenerator::new)
				.addRecipes(AssemblyRecipesGenerator::new)
				.addLootTables(AssemblyLootTablesGenerator::new);
	}

	@Override
	public boolean isEnabled() {
		return Assembly.DOSSIER_ENABLED;
	}

	private static class AssemblyBlockTagsGenerator extends BlockTagsDossier {
		@Override
		protected void addBlockTags() {
			this.add(AssemblyBlockTags.HEVEA_LOGS, AssemblyBlocks.HEVEA_LOG, AssemblyBlocks.STRIPPED_HEVEA_LOG, AssemblyBlocks.HEVEA_WOOD, AssemblyBlocks.STRIPPED_HEVEA_WOOD);
			this.add(BlockTags.LOGS_THAT_BURN, AssemblyBlockTags.HEVEA_LOGS);
		}
	}

	private static class AssemblyItemTagsGenerator extends ItemTagsDossier {
		@Override
		protected void addItemTags() {
			this.copyFromBlock(AssemblyItemTags.HEVEA_LOGS, AssemblyBlockTags.HEVEA_LOGS);
			this.add(ItemTags.LOGS_THAT_BURN, AssemblyItemTags.HEVEA_LOGS);
		}
	}

	private static class AssemblyRecipesGenerator extends RecipesDossier {
		@Override
		protected void addRecipes(Consumer<RecipeJsonProvider> exporter) {
			BoilingRecipeFactory.create(FluidIngredient.of(FluidTags.WATER, FluidAmount.BUCKET), FluidKeys.get(AssemblyFluids.STEAM).withAmount(FluidAmount.BUCKET)).criterion("has_water", conditionsFrom(Items.WATER_BUCKET)).offerTo(exporter);

			PressingRecipeFactory.createSteamPressing(Ingredient.fromTag(AssemblyItemTags.BRASS_INGOTS), AssemblyItems.BRASS_PLATE).criterion("has_brass_ingot", conditionsFrom(AssemblyItemTags.BRASS_INGOTS)).offerTo(exporter);

			FluidInjectingRecipeFactory.create(Ingredient.ofItems(Items.SPONGE), FluidIngredient.of(FluidTags.WATER, FluidAmount.BUCKET), Blocks.WET_SPONGE).criterion("has_sponge", conditionsFrom(Items.SPONGE)).offerTo(exporter);

			ShapelessRecipeJsonFactory.create(AssemblyItems.FORMIC_ACID).input(Items.GLASS_BOTTLE).input(AssemblyItems.VENOMOUS_FANG).criterion("has_fang", conditionsFrom(AssemblyItems.VENOMOUS_FANG)).offerTo(exporter);

			CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(AssemblyBlocks.COPPER_ORE.asItem()), AssemblyItems.COPPER_INGOT, 0.7F, 200).criterion("has_copper_ore", conditionsFrom(AssemblyBlocks.COPPER_ORE.asItem())).offerTo(exporter);
			CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(AssemblyBlocks.COPPER_ORE.asItem()), AssemblyItems.COPPER_INGOT, 0.7F, 100).criterion("has_copper_ore", conditionsFrom(AssemblyBlocks.COPPER_ORE.asItem())).offerTo(exporter);

			addPlanks(exporter, AssemblyBlocks.HEVEA_PLANKS, AssemblyItemTags.HEVEA_LOGS);
			addWood(exporter, AssemblyBlocks.HEVEA_WOOD, AssemblyBlocks.HEVEA_LOG);
			addBoat(exporter, AssemblyEntities.getBoatItem(AssemblyEntities.HEVEA_BOAT), AssemblyBlocks.HEVEA_PLANKS);
			addWoodenButton(exporter, AssemblyBlocks.HEVEA_BUTTON, AssemblyBlocks.HEVEA_PLANKS);
			addWoodenDoor(exporter, AssemblyBlocks.HEVEA_DOOR, AssemblyBlocks.HEVEA_PLANKS);
			addWoodenFence(exporter, AssemblyBlocks.HEVEA_FENCE, AssemblyBlocks.HEVEA_PLANKS);
			addWoodenFenceGate(exporter, AssemblyBlocks.HEVEA_FENCE_GATE, AssemblyBlocks.HEVEA_PLANKS);
			addWoodenPressurePlate(exporter, AssemblyBlocks.HEVEA_PRESSURE_PLATE, AssemblyBlocks.HEVEA_PLANKS);
			addWoodenSlab(exporter, AssemblyBlocks.HEVEA_SLAB, AssemblyBlocks.HEVEA_PLANKS);
			addWoodenStairs(exporter, AssemblyBlocks.HEVEA_STAIRS, AssemblyBlocks.HEVEA_PLANKS);
			addWoodenTrapdoor(exporter, AssemblyBlocks.HEVEA_TRAPDOOR, AssemblyBlocks.HEVEA_PLANKS);
			addWoodenSign(exporter, AssemblyBlocks.HEVEA_SIGN, AssemblyBlocks.HEVEA_PLANKS);
		}

		private static EnterBlockCriterion.Conditions requireEnteringFluid(Block block) {
			return new EnterBlockCriterion.Conditions(block, StatePredicate.ANY);
		}

		private void addPlanks(Consumer<RecipeJsonProvider> consumer, ItemConvertible itemConvertible, Tag<Item> tag) {
			ShapelessRecipeJsonFactory.create(itemConvertible, 4).input(tag).group("planks").criterion("has_logs", conditionsFrom(tag)).offerTo(consumer);
		}

		private void addWood(Consumer<RecipeJsonProvider> consumer, ItemConvertible itemConvertible, ItemConvertible log) {
			ShapedRecipeJsonFactory.create(itemConvertible, 3).input('#', log).pattern("##").pattern("##").group("bark").criterion("has_log", conditionsFrom(log)).offerTo(consumer);
		}

		private void addBoat(Consumer<RecipeJsonProvider> consumer, ItemConvertible itemConvertible, ItemConvertible planks) {
			ShapedRecipeJsonFactory.create(itemConvertible).input('#', planks).pattern("# #").pattern("###").group("boat").criterion("in_water", requireEnteringFluid(Blocks.WATER)).offerTo(consumer);
		}

		private void addWoodenButton(Consumer<RecipeJsonProvider> consumer, ItemConvertible itemConvertible, ItemConvertible planks) {
			ShapelessRecipeJsonFactory.create(itemConvertible).input(planks).group("wooden_button").criterion("has_planks", conditionsFrom(planks)).offerTo(consumer);
		}

		private void addWoodenDoor(Consumer<RecipeJsonProvider> consumer, ItemConvertible itemConvertible, ItemConvertible planks) {
			ShapedRecipeJsonFactory.create(itemConvertible, 3).input('#', planks).pattern("##").pattern("##").pattern("##").group("wooden_door").criterion("has_planks", conditionsFrom(planks)).offerTo(consumer);
		}

		private void addWoodenFence(Consumer<RecipeJsonProvider> consumer, ItemConvertible itemConvertible, ItemConvertible planks) {
			ShapedRecipeJsonFactory.create(itemConvertible, 3).input('#', (ItemConvertible) Items.STICK).input('W', planks).pattern("W#W").pattern("W#W").group("wooden_fence").criterion("has_planks", conditionsFrom(planks)).offerTo(consumer);
		}

		private void addWoodenFenceGate(Consumer<RecipeJsonProvider> consumer, ItemConvertible itemConvertible, ItemConvertible planks) {
			ShapedRecipeJsonFactory.create(itemConvertible).input('#', (ItemConvertible) Items.STICK).input('W', planks).pattern("#W#").pattern("#W#").group("wooden_fence_gate").criterion("has_planks", conditionsFrom(planks)).offerTo(consumer);
		}

		private void addWoodenPressurePlate(Consumer<RecipeJsonProvider> consumer, ItemConvertible itemConvertible, ItemConvertible planks) {
			ShapedRecipeJsonFactory.create(itemConvertible).input('#', planks).pattern("##").group("wooden_pressure_plate").criterion("has_planks", conditionsFrom(planks)).offerTo(consumer);
		}

		private void addWoodenSlab(Consumer<RecipeJsonProvider> consumer, ItemConvertible itemConvertible, ItemConvertible planks) {
			ShapedRecipeJsonFactory.create(itemConvertible, 6).input('#', planks).pattern("###").group("wooden_slab").criterion("has_planks", conditionsFrom(planks)).offerTo(consumer);
		}

		private void addWoodenStairs(Consumer<RecipeJsonProvider> consumer, ItemConvertible itemConvertible, ItemConvertible planks) {
			ShapedRecipeJsonFactory.create(itemConvertible, 4).input('#', planks).pattern("#  ").pattern("## ").pattern("###").group("wooden_stairs").criterion("has_planks", conditionsFrom(planks)).offerTo(consumer);
		}

		private void addWoodenTrapdoor(Consumer<RecipeJsonProvider> consumer, ItemConvertible itemConvertible, ItemConvertible planks) {
			ShapedRecipeJsonFactory.create(itemConvertible, 2).input('#', planks).pattern("###").pattern("###").group("wooden_trapdoor").criterion("has_planks", conditionsFrom(planks)).offerTo(consumer);
		}

		private void addWoodenSign(Consumer<RecipeJsonProvider> consumer, ItemConvertible itemConvertible, ItemConvertible planks) {
			String string = Registry.ITEM.getId(planks.asItem()).getPath();
			ShapedRecipeJsonFactory.create(itemConvertible, 3).group("sign").input('#', planks).input('X', Items.STICK).pattern("###").pattern("###").pattern(" X ").criterion("has_" + string, conditionsFrom(planks)).offerTo(consumer);
		}
	}


	private static class AssemblyLootTablesGenerator extends LootTablesDossier {
		@Override
		protected void addLootTables() {
			this.drops(AssemblyBlocks.HEVEA_PLANKS);
			this.drops(AssemblyBlocks.HEVEA_SAPLING);
			this.pottedPlantDrops(AssemblyBlocks.POTTED_HEVEA_SAPLING);
			this.drops(AssemblyBlocks.STRIPPED_HEVEA_LOG);
			this.drops(AssemblyBlocks.STRIPPED_HEVEA_WOOD);
			this.drops(AssemblyBlocks.HEVEA_LOG);
			this.drops(AssemblyBlocks.HEVEA_WOOD);
			this.drops(AssemblyBlocks.HEVEA_LEAVES);
			this.drops(AssemblyBlocks.HEVEA_SLAB);
			this.drops(AssemblyBlocks.HEVEA_PRESSURE_PLATE);
			this.drops(AssemblyBlocks.HEVEA_FENCE);
			this.drops(AssemblyBlocks.HEVEA_TRAPDOOR);
			this.drops(AssemblyBlocks.HEVEA_FENCE_GATE);
			this.drops(AssemblyBlocks.HEVEA_STAIRS);
			this.drops(AssemblyBlocks.HEVEA_BUTTON);
			this.drops(AssemblyBlocks.HEVEA_DOOR);
			this.drops(AssemblyBlocks.HEVEA_SIGN);
			this.drops(AssemblyBlocks.HEVEA_WALL_SIGN);

			this.drops(AssemblyBlocks.FLUID_BARREL);

			this.drops(AssemblyBlocks.BOILER, Blocks.FURNACE);
			this.drops(AssemblyBlocks.BOILER_CHAMBER);
			this.drops(AssemblyBlocks.STEAM_PRESS);

			this.drops(AssemblyBlocks.COPPER_ORE);

			this.drops(AssemblyBlocks.CRUSHED_COAL_ORE);
			this.drops(AssemblyBlocks.CRUSHED_COPPER_ORE);
			this.drops(AssemblyBlocks.CRUSHED_IRON_ORE);
			this.drops(AssemblyBlocks.CRUSHED_GOLD_ORE);
			this.drops(AssemblyBlocks.CRUSHED_REDSTONE_ORE);
			this.drops(AssemblyBlocks.CRUSHED_LAPIS_ORE);
			this.drops(AssemblyBlocks.CRUSHED_DIAMOND_ORE);
			this.drops(AssemblyBlocks.CRUSHED_EMERALD_ORE);
			this.drops(AssemblyBlocks.CRUSHED_NETHER_GOLD_ORE);

			this.drops(AssemblyBlocks.COPPER_BLOCK);
			this.drops(AssemblyBlocks.ZINC_BLOCK);
			this.drops(AssemblyBlocks.BRASS_BLOCK);

			this.drops(AssemblyBlocks.CAPROCK);
			this.drops(AssemblyBlocks.HALITE);

			this.drops(AssemblyBlocks.CONVEYOR_BELT);
			this.drops(AssemblyBlocks.FLUID_HOPPER);
			this.drops(AssemblyBlocks.SPIGOT);

		}
	}
}
