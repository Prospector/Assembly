package com.terraformersmc.assembly.data;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import com.terraformersmc.assembly.Assembly;
import com.terraformersmc.assembly.block.AssemblyBlocks;
import com.terraformersmc.assembly.block.SteamPressBlock;
import com.terraformersmc.assembly.data.factory.BoilingRecipeJsonFactory;
import com.terraformersmc.assembly.data.factory.FluidInjectingRecipeJsonFactory;
import com.terraformersmc.assembly.data.factory.PressingRecipeJsonFactory;
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
import com.terraformersmc.dossier.util.BlockLootTableCreator;
import net.minecraft.advancement.criterion.EnterBlockCriterion;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.data.server.BlockLootTableGenerator;
import net.minecraft.data.server.recipe.CookingRecipeJsonFactory;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonFactory;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonFactory;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
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
			this.add(AssemblyBlockTags.COPPER_ORES, AssemblyBlocks.COPPER_ORE);
			this.add(AssemblyBlockTags.COPPER_BLOCKS, AssemblyBlocks.COPPER_BLOCK);
			this.add(AssemblyBlockTags.ZINC_BLOCKS, AssemblyBlocks.ZINC_BLOCK);
			this.add(AssemblyBlockTags.BRASS_BLOCKS, AssemblyBlocks.BRASS_BLOCK);
			this.add(AssemblyBlockTags.HEVEA_LOGS, AssemblyBlocks.HEVEA_LOG, AssemblyBlocks.STRIPPED_HEVEA_LOG, AssemblyBlocks.HEVEA_WOOD, AssemblyBlocks.STRIPPED_HEVEA_WOOD);
			this.add(BlockTags.LOGS_THAT_BURN, AssemblyBlockTags.HEVEA_LOGS);
		}
	}

	private static class AssemblyItemTagsGenerator extends ItemTagsDossier {
		@Override
		protected void addItemTags() {
			this.copyFromBlock(AssemblyItemTags.COPPER_ORES, AssemblyBlockTags.COPPER_ORES);
			this.copyFromBlock(AssemblyItemTags.COPPER_BLOCKS, AssemblyBlockTags.COPPER_BLOCKS);
			this.copyFromBlock(AssemblyItemTags.ZINC_BLOCKS, AssemblyBlockTags.ZINC_BLOCKS);
			this.copyFromBlock(AssemblyItemTags.BRASS_BLOCKS, AssemblyBlockTags.BRASS_BLOCKS);
			this.copyFromBlock(AssemblyItemTags.HEVEA_LOGS, AssemblyBlockTags.HEVEA_LOGS);
			this.add(ItemTags.LOGS_THAT_BURN, AssemblyItemTags.HEVEA_LOGS);
			this.add(AssemblyItemTags.COPPER_INGOTS, AssemblyItems.COPPER_INGOT);
			this.add(AssemblyItemTags.ZINC_INGOTS, AssemblyItems.ZINC_INGOT);
			this.add(AssemblyItemTags.BRASS_INGOTS, AssemblyItems.BRASS_INGOT);
			this.add(AssemblyItemTags.BRASS_PLATES, AssemblyItems.BRASS_PLATE);
			this.add(AssemblyItemTags.HAMMER, AssemblyItems.WOODEN_HAMMER, AssemblyItems.STONE_HAMMER, AssemblyItems.IRON_HAMMER, AssemblyItems.GOLDEN_HAMMER, AssemblyItems.DIAMOND_HAMMER);
		}
	}

	private static class AssemblyRecipesGenerator extends RecipesDossier {
		@Override
		protected void addRecipes(Consumer<RecipeJsonProvider> exporter) {
			BoilingRecipeJsonFactory.create(Fluids.WATER, FluidAmount.of(1, 1), AssemblyFluids.STEAM).criterion("has_water", conditionsFrom(Items.WATER_BUCKET)).offerTo(exporter);

			PressingRecipeJsonFactory.createSteamPressing(Ingredient.fromTag(AssemblyItemTags.BRASS_INGOTS), AssemblyItems.BRASS_PLATE).criterion("has_brass_ingot", conditionsFrom(AssemblyItemTags.BRASS_INGOTS)).offerTo(exporter);

			FluidInjectingRecipeJsonFactory.create(Ingredient.ofItems(Items.SPONGE), FluidIngredient.of(FluidTags.WATER, FluidAmount.BUCKET), Blocks.WET_SPONGE).criterion("has_sponge", conditionsFrom(Items.SPONGE)).offerTo(exporter);

			ShapelessRecipeJsonFactory.create(AssemblyItems.FORMIC_ACID).input(Items.GLASS_BOTTLE).input(AssemblyItems.VENOMOUS_FANG).criterion("has_fang", conditionsFrom(AssemblyItems.VENOMOUS_FANG)).offerTo(exporter);
			ShapelessRecipeJsonFactory.create(AssemblyItems.BRASS_PLATE).input(AssemblyItemTags.BRASS_INGOTS).input(AssemblyItemTags.HAMMER).criterion("has_brass", conditionsFrom(AssemblyItemTags.BRASS_INGOTS)).offerTo(exporter);

			ShapedRecipeJsonFactory.create(AssemblyBlocks.BOILER_CHAMBER).pattern("XXX").pattern("IBI").pattern("XXX").input('X', Blocks.IRON_BLOCK).input('I', Items.IRON_INGOT).input('B', Items.BUCKET).criterion("has_bucket", conditionsFrom(Items.BUCKET)).offerTo(exporter);
			ShapedRecipeJsonFactory.create(AssemblyBlocks.STEAM_PRESS).pattern(" X ").pattern("BIB").pattern("PPP").input('X', Blocks.IRON_BLOCK).input('I', Items.IRON_INGOT).input('B', AssemblyItemTags.BRASS_INGOTS).input('P', AssemblyItemTags.BRASS_PLATES).criterion("has_brass", conditionsFrom(AssemblyItemTags.BRASS_INGOTS)).offerTo(exporter);
			ShapedRecipeJsonFactory.create(AssemblyBlocks.FLUID_INJECTOR).pattern("BIB").pattern("PPP").input('I', Items.IRON_INGOT).input('B', Items.BUCKET).input('P', AssemblyItemTags.BRASS_PLATES).criterion("has_brass", conditionsFrom(AssemblyItemTags.BRASS_INGOTS)).offerTo(exporter);

			ShapedRecipeJsonFactory.create(AssemblyBlocks.COPPER_BLOCK).pattern("XXX").pattern("XXX").pattern("XXX").input('X', AssemblyItemTags.COPPER_INGOTS).criterion("has_copper", conditionsFrom(AssemblyItemTags.COPPER_INGOTS)).offerTo(exporter);
			ShapedRecipeJsonFactory.create(AssemblyBlocks.ZINC_BLOCK).pattern("XXX").pattern("XXX").pattern("XXX").input('X', AssemblyItemTags.ZINC_INGOTS).criterion("has_zinc", conditionsFrom(AssemblyItemTags.ZINC_INGOTS)).offerTo(exporter);
			ShapedRecipeJsonFactory.create(AssemblyBlocks.BRASS_BLOCK).pattern("XXX").pattern("XXX").pattern("XXX").input('X', AssemblyItemTags.BRASS_INGOTS).criterion("has_brass", conditionsFrom(AssemblyItemTags.BRASS_INGOTS)).offerTo(exporter);

			ShapedRecipeJsonFactory.create(AssemblyItems.WOODEN_HAMMER).pattern("XSX").pattern(" S ").pattern(" S ").input('X', ItemTags.PLANKS).input('S', Items.STICK).criterion("has_planks", conditionsFrom(ItemTags.PLANKS)).offerTo(exporter);
			ShapedRecipeJsonFactory.create(AssemblyItems.STONE_HAMMER).pattern("XSX").pattern(" S ").pattern(" S ").input('X', ItemTags.STONE_TOOL_MATERIALS).input('S', Items.STICK).criterion("has_stone", conditionsFrom(ItemTags.STONE_TOOL_MATERIALS)).offerTo(exporter);
			ShapedRecipeJsonFactory.create(AssemblyItems.IRON_HAMMER).pattern("XSX").pattern(" S ").pattern(" S ").input('X', Items.IRON_INGOT).input('S', Items.STICK).criterion("has_iron", conditionsFrom(Items.IRON_INGOT)).offerTo(exporter);
			ShapedRecipeJsonFactory.create(AssemblyItems.GOLDEN_HAMMER).pattern("XSX").pattern(" S ").pattern(" S ").input('X', Items.GOLD_INGOT).input('S', Items.STICK).criterion("has_gold", conditionsFrom(Items.GOLD_INGOT)).offerTo(exporter);
			ShapedRecipeJsonFactory.create(AssemblyItems.DIAMOND_HAMMER).pattern("XSX").pattern(" S ").pattern(" S ").input('X', Items.DIAMOND).input('S', Items.STICK).criterion("has_diamond", conditionsFrom(Items.DIAMOND)).offerTo(exporter);

			ShapedRecipeJsonFactory.create(AssemblyBlocks.FLUID_HOPPER).pattern("I I").pattern("IBI").pattern(" I ").input('I', AssemblyItemTags.BRASS_INGOTS).input('B', Items.BUCKET).criterion("has_brass", conditionsFrom(AssemblyItemTags.BRASS_INGOTS)).offerTo(exporter);
			ShapedRecipeJsonFactory.create(AssemblyBlocks.TREE_TAP).pattern(" S ").pattern("IPP").pattern("  P").input('S', Items.STICK).input('I', Items.IRON_INGOT).input('P', ItemTags.PLANKS).criterion("has_iron", conditionsFrom(Items.IRON_INGOT)).offerTo(exporter);
			ShapedRecipeJsonFactory.create(AssemblyBlocks.SPIGOT).pattern(" I ").pattern("BBB").pattern("  B").input('I', Items.IRON_INGOT).input('B', AssemblyItemTags.BRASS_INGOTS).criterion("has_iron", conditionsFrom(Items.IRON_INGOT)).offerTo(exporter);

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
			return new EnterBlockCriterion.Conditions(EntityPredicate.Extended.EMPTY, block, StatePredicate.ANY);
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
			ShapedRecipeJsonFactory.create(itemConvertible, 3).input('#', Items.STICK).input('W', planks).pattern("W#W").pattern("W#W").group("wooden_fence").criterion("has_planks", conditionsFrom(planks)).offerTo(consumer);
		}

		private void addWoodenFenceGate(Consumer<RecipeJsonProvider> consumer, ItemConvertible itemConvertible, ItemConvertible planks) {
			ShapedRecipeJsonFactory.create(itemConvertible).input('#', Items.STICK).input('W', planks).pattern("#W#").pattern("#W#").group("wooden_fence_gate").criterion("has_planks", conditionsFrom(planks)).offerTo(consumer);
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
			this.drops(AssemblyBlocks.STEAM_PRESS, block -> BlockLootTableGenerator.createForMultiblock(AssemblyBlocks.STEAM_PRESS, SteamPressBlock.HALF, DoubleBlockHalf.LOWER));

			this.drops(AssemblyBlocks.COPPER_ORE);

			this.drops(AssemblyBlocks.CRUSHED_COAL_ORE, BlockLootTableCreator.dropsWithSilkTouch(AssemblyBlocks.CRUSHED_COAL_ORE));
			this.drops(AssemblyBlocks.CRUSHED_COPPER_ORE, BlockLootTableCreator.dropsWithSilkTouch(AssemblyBlocks.CRUSHED_COPPER_ORE));
			this.drops(AssemblyBlocks.CRUSHED_IRON_ORE, BlockLootTableCreator.dropsWithSilkTouch(AssemblyBlocks.CRUSHED_IRON_ORE));
			this.drops(AssemblyBlocks.CRUSHED_GOLD_ORE, BlockLootTableCreator.dropsWithSilkTouch(AssemblyBlocks.CRUSHED_GOLD_ORE));
			this.drops(AssemblyBlocks.CRUSHED_REDSTONE_ORE, BlockLootTableCreator.dropsWithSilkTouch(AssemblyBlocks.CRUSHED_REDSTONE_ORE));
			this.drops(AssemblyBlocks.CRUSHED_LAPIS_ORE, BlockLootTableCreator.dropsWithSilkTouch(AssemblyBlocks.CRUSHED_LAPIS_ORE));
			this.drops(AssemblyBlocks.CRUSHED_DIAMOND_ORE, BlockLootTableCreator.dropsWithSilkTouch(AssemblyBlocks.CRUSHED_DIAMOND_ORE));
			this.drops(AssemblyBlocks.CRUSHED_EMERALD_ORE, BlockLootTableCreator.dropsWithSilkTouch(AssemblyBlocks.CRUSHED_EMERALD_ORE));
			this.drops(AssemblyBlocks.CRUSHED_NETHER_GOLD_ORE, BlockLootTableCreator.dropsWithSilkTouch(AssemblyBlocks.CRUSHED_NETHER_GOLD_ORE));

			this.drops(AssemblyBlocks.COPPER_BLOCK);
			this.drops(AssemblyBlocks.ZINC_BLOCK);
			this.drops(AssemblyBlocks.BRASS_BLOCK);

			this.drops(AssemblyBlocks.CAPROCK);
			this.drops(AssemblyBlocks.HALITE, (block) -> BlockLootTableCreator.dropsWithSilkTouch(AssemblyBlocks.HALITE, BlockLootTableCreator.addExplosionDecayCondition(block, ItemEntry.builder(AssemblyItems.SALT).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))).withFunction(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE)))));

			this.drops(AssemblyBlocks.CONVEYOR_BELT);
			this.drops(AssemblyBlocks.FLUID_HOPPER);
			this.drops(AssemblyBlocks.SPIGOT);

		}
	}
}
