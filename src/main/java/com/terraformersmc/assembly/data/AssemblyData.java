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
import net.minecraft.loot.*;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.entry.AlternativeEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;

import java.security.InvalidParameterException;
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
			this.add(AssemblyBlockTags.IRON_CONCENTRATE_BLOCKS, AssemblyBlocks.IRON_CONCENTRATE_BLOCK);
			this.add(AssemblyBlockTags.GOLD_CONCENTRATE_BLOCKS, AssemblyBlocks.GOLD_CONCENTRATE_BLOCK);
			this.add(AssemblyBlockTags.COPPER_CONCENTRATE_BLOCKS, AssemblyBlocks.COPPER_CONCENTRATE_BLOCK);
			this.add(AssemblyBlockTags.ZINC_CONCENTRATE_BLOCKS, AssemblyBlocks.ZINC_CONCENTRATE_BLOCK);
			this.add(AssemblyBlockTags.BRASS_BLEND_BLOCKS, AssemblyBlocks.BRASS_BLEND_BLOCK);
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
			this.copyFromBlock(AssemblyItemTags.IRON_CONCENTRATE_BLOCKS, AssemblyBlockTags.IRON_CONCENTRATE_BLOCKS);
			this.copyFromBlock(AssemblyItemTags.GOLD_CONCENTRATE_BLOCKS, AssemblyBlockTags.GOLD_CONCENTRATE_BLOCKS);
			this.copyFromBlock(AssemblyItemTags.COPPER_CONCENTRATE_BLOCKS, AssemblyBlockTags.COPPER_CONCENTRATE_BLOCKS);
			this.copyFromBlock(AssemblyItemTags.ZINC_CONCENTRATE_BLOCKS, AssemblyBlockTags.ZINC_CONCENTRATE_BLOCKS);
			this.copyFromBlock(AssemblyItemTags.BRASS_BLEND_BLOCKS, AssemblyBlockTags.BRASS_BLEND_BLOCKS);
			this.add(ItemTags.LOGS_THAT_BURN, AssemblyItemTags.HEVEA_LOGS);
			this.add(AssemblyItemTags.COPPER_INGOTS, AssemblyItems.COPPER_INGOT);
			this.add(AssemblyItemTags.ZINC_INGOTS, AssemblyItems.ZINC_INGOT);
			this.add(AssemblyItemTags.BRASS_INGOTS, AssemblyItems.BRASS_INGOT);
			this.add(AssemblyItemTags.BRASS_PLATES, AssemblyItems.BRASS_PLATE);
			this.add(AssemblyItemTags.IRON_CONCENTRATES, AssemblyItems.IRON_CONCENTRATE);
			this.add(AssemblyItemTags.GOLD_CONCENTRATES, AssemblyItems.GOLD_CONCENTRATE);
			this.add(AssemblyItemTags.COPPER_CONCENTRATES, AssemblyItems.COPPER_CONCENTRATE);
			this.add(AssemblyItemTags.ZINC_CONCENTRATES, AssemblyItems.ZINC_CONCENTRATE);
			this.add(AssemblyItemTags.BRASS_BLENDS, AssemblyItems.BRASS_BLEND);
			this.add(AssemblyItemTags.HAMMERS, AssemblyItems.WOODEN_HAMMER, AssemblyItems.STONE_HAMMER, AssemblyItems.IRON_HAMMER, AssemblyItems.GOLDEN_HAMMER, AssemblyItems.DIAMOND_HAMMER);
		}
	}

	private static class AssemblyRecipesGenerator extends RecipesDossier {
		@Override
		protected void addRecipes(Consumer<RecipeJsonProvider> exporter) {
			BoilingRecipeJsonFactory.create(Fluids.WATER, FluidAmount.of(1, 1), AssemblyFluids.STEAM).criterion("has_water", conditionsFrom(Items.WATER_BUCKET)).offerTo(exporter);

			PressingRecipeJsonFactory.createSteamPressing(Ingredient.fromTag(AssemblyItemTags.BRASS_INGOTS), AssemblyItems.BRASS_PLATE).criterion("has_brass_ingot", conditionsFrom(AssemblyItemTags.BRASS_INGOTS)).offerTo(exporter);

			FluidInjectingRecipeJsonFactory.create(Ingredient.ofItems(Items.SPONGE), FluidIngredient.of(FluidTags.WATER, FluidAmount.BUCKET), Blocks.WET_SPONGE).criterion("has_sponge", conditionsFrom(Items.SPONGE)).offerTo(exporter);

			ShapelessRecipeJsonFactory.create(AssemblyItems.FORMIC_ACID).input(Items.GLASS_BOTTLE).input(AssemblyItems.VENOMOUS_FANG).criterion("has_fang", conditionsFrom(AssemblyItems.VENOMOUS_FANG)).offerTo(exporter);
			ShapelessRecipeJsonFactory.create(AssemblyItems.BRASS_PLATE).input(AssemblyItemTags.BRASS_BLOCKS).input(AssemblyItemTags.HAMMERS).criterion("has_brass", conditionsFrom(AssemblyItemTags.BRASS_INGOTS)).offerTo(exporter);

			ShapedRecipeJsonFactory.create(AssemblyBlocks.BOILER_CHAMBER).pattern("XXX").pattern("IBI").pattern("XXX").input('X', Blocks.IRON_BLOCK).input('I', Items.IRON_INGOT).input('B', Items.BUCKET).criterion("has_bucket", conditionsFrom(Items.BUCKET)).offerTo(exporter);
			ShapedRecipeJsonFactory.create(AssemblyBlocks.STEAM_PRESS).pattern(" X ").pattern("BIB").pattern("PPP").input('X', Blocks.IRON_BLOCK).input('I', Items.IRON_INGOT).input('B', AssemblyItemTags.BRASS_INGOTS).input('P', AssemblyItemTags.BRASS_PLATES).criterion("has_brass", conditionsFrom(AssemblyItemTags.BRASS_INGOTS)).offerTo(exporter);
			ShapedRecipeJsonFactory.create(AssemblyBlocks.FLUID_INJECTOR).pattern("BIB").pattern("PPP").input('I', Items.IRON_INGOT).input('B', Items.BUCKET).input('P', AssemblyItemTags.BRASS_PLATES).criterion("has_brass", conditionsFrom(AssemblyItemTags.BRASS_INGOTS)).offerTo(exporter);

			ShapedRecipeJsonFactory.create(AssemblyBlocks.COPPER_BLOCK).pattern("XXX").pattern("XXX").pattern("XXX").input('X', AssemblyItemTags.COPPER_INGOTS).criterion("has_copper", conditionsFrom(AssemblyItemTags.COPPER_INGOTS)).offerTo(exporter);
			ShapedRecipeJsonFactory.create(AssemblyBlocks.ZINC_BLOCK).pattern("XXX").pattern("XXX").pattern("XXX").input('X', AssemblyItemTags.ZINC_INGOTS).criterion("has_zinc", conditionsFrom(AssemblyItemTags.ZINC_INGOTS)).offerTo(exporter);
			ShapedRecipeJsonFactory.create(AssemblyBlocks.BRASS_BLOCK).pattern("XXX").pattern("XXX").pattern("XXX").input('X', AssemblyItemTags.BRASS_INGOTS).criterion("has_brass", conditionsFrom(AssemblyItemTags.BRASS_INGOTS)).offerTo(exporter);

			ShapedRecipeJsonFactory.create(AssemblyBlocks.IRON_CONCENTRATE_BLOCK).pattern("XX").pattern("XX").input('X', AssemblyItemTags.IRON_CONCENTRATES).criterion("has_brass_concentrate", conditionsFrom(AssemblyItemTags.IRON_CONCENTRATES)).offerTo(exporter);
			ShapedRecipeJsonFactory.create(AssemblyBlocks.GOLD_CONCENTRATE_BLOCK).pattern("XX").pattern("XX").input('X', AssemblyItemTags.GOLD_CONCENTRATES).criterion("has_gold_concentrate", conditionsFrom(AssemblyItemTags.GOLD_CONCENTRATES)).offerTo(exporter);
			ShapedRecipeJsonFactory.create(AssemblyBlocks.COPPER_CONCENTRATE_BLOCK).pattern("XX").pattern("XX").input('X', AssemblyItemTags.COPPER_CONCENTRATES).criterion("has_copper_concentrate", conditionsFrom(AssemblyItemTags.COPPER_CONCENTRATES)).offerTo(exporter);
			ShapedRecipeJsonFactory.create(AssemblyBlocks.ZINC_CONCENTRATE_BLOCK).pattern("XX").pattern("XX").input('X', AssemblyItemTags.ZINC_CONCENTRATES).criterion("has_zinc_concentrate", conditionsFrom(AssemblyItemTags.ZINC_CONCENTRATES)).offerTo(exporter);
			ShapedRecipeJsonFactory.create(AssemblyBlocks.BRASS_BLEND_BLOCK).pattern("XX").pattern("XX").input('X', AssemblyItemTags.BRASS_BLENDS).criterion("has_brass_concentrate", conditionsFrom(AssemblyItemTags.BRASS_BLENDS)).offerTo(exporter);

			ShapedRecipeJsonFactory.create(AssemblyItems.EXOFRAME_HEADPIECE).pattern("XXX").pattern("X X").input('X', AssemblyItemTags.BRASS_PLATES).criterion("has_brass_plate", conditionsFrom(AssemblyItemTags.BRASS_PLATES)).offerTo(exporter);
			ShapedRecipeJsonFactory.create(AssemblyItems.EXOFRAME_CHESTPIECE).pattern("X X").pattern("X X").pattern("XXX").input('X', AssemblyItemTags.BRASS_PLATES).criterion("has_brass_plate", conditionsFrom(AssemblyItemTags.BRASS_PLATES)).offerTo(exporter);
			ShapedRecipeJsonFactory.create(AssemblyItems.EXOFRAME_LEGPIECE).pattern("X X").pattern("X X").pattern("X X").input('X', AssemblyItemTags.BRASS_PLATES).criterion("has_brass_plate", conditionsFrom(AssemblyItemTags.BRASS_PLATES)).offerTo(exporter);
			ShapedRecipeJsonFactory.create(AssemblyItems.EXOFRAME_FOOTPIECE).pattern("X X").pattern("X X").input('X', AssemblyItemTags.BRASS_PLATES).criterion("has_brass_plate", conditionsFrom(AssemblyItemTags.BRASS_PLATES)).offerTo(exporter);

			ShapedRecipeJsonFactory.create(AssemblyItems.WOODEN_HAMMER).pattern("XSX").pattern(" S ").pattern(" S ").input('X', ItemTags.PLANKS).input('S', Items.STICK).criterion("has_planks", conditionsFrom(ItemTags.PLANKS)).offerTo(exporter);
			ShapedRecipeJsonFactory.create(AssemblyItems.STONE_HAMMER).pattern("XSX").pattern(" S ").pattern(" S ").input('X', ItemTags.STONE_TOOL_MATERIALS).input('S', Items.STICK).criterion("has_stone", conditionsFrom(ItemTags.STONE_TOOL_MATERIALS)).offerTo(exporter);
			ShapedRecipeJsonFactory.create(AssemblyItems.IRON_HAMMER).pattern("XSX").pattern(" S ").pattern(" S ").input('X', Items.IRON_INGOT).input('S', Items.STICK).criterion("has_iron", conditionsFrom(Items.IRON_INGOT)).offerTo(exporter);
			ShapedRecipeJsonFactory.create(AssemblyItems.GOLDEN_HAMMER).pattern("XSX").pattern(" S ").pattern(" S ").input('X', Items.GOLD_INGOT).input('S', Items.STICK).criterion("has_gold", conditionsFrom(Items.GOLD_INGOT)).offerTo(exporter);
			ShapedRecipeJsonFactory.create(AssemblyItems.DIAMOND_HAMMER).pattern("XSX").pattern(" S ").pattern(" S ").input('X', Items.DIAMOND).input('S', Items.STICK).criterion("has_diamond", conditionsFrom(Items.DIAMOND)).offerTo(exporter);

			ShapedRecipeJsonFactory.create(AssemblyBlocks.TINKERING_TABLE).pattern(" i ").pattern("BIB").pattern("III").input('i', Items.IRON_INGOT).input('B', AssemblyItemTags.BRASS_INGOTS).input('I', Items.IRON_BLOCK).criterion("has_iron_block", conditionsFrom(Items.IRON_BLOCK)).offerTo(exporter);

			ShapedRecipeJsonFactory.create(AssemblyBlocks.FLUID_HOPPER).pattern("I I").pattern("IBI").pattern(" I ").input('I', AssemblyItemTags.BRASS_INGOTS).input('B', Items.BUCKET).criterion("has_brass", conditionsFrom(AssemblyItemTags.BRASS_INGOTS)).offerTo(exporter);
			ShapedRecipeJsonFactory.create(AssemblyBlocks.TREE_TAP).pattern(" S ").pattern("IPP").pattern("  P").input('S', Items.STICK).input('I', Items.IRON_INGOT).input('P', ItemTags.PLANKS).criterion("has_iron", conditionsFrom(Items.IRON_INGOT)).offerTo(exporter);
			ShapedRecipeJsonFactory.create(AssemblyBlocks.SPIGOT).pattern(" I ").pattern("BBB").pattern("  B").input('I', Items.IRON_INGOT).input('B', AssemblyItemTags.BRASS_INGOTS).criterion("has_iron", conditionsFrom(Items.IRON_INGOT)).offerTo(exporter);

			addOreSmelting(exporter, AssemblyItems.COPPER_INGOT, AssemblyItemTags.COPPER_ORES, "has_copper_ore");
			addOreSmelting(exporter, AssemblyItems.COPPER_INGOT, AssemblyItemTags.COPPER_CONCENTRATE_BLOCKS, "has_copper_concentrate_block", "copper_ingot_from_concentrate");
			addOreSmelting(exporter, AssemblyItems.ZINC_INGOT, AssemblyItemTags.ZINC_CONCENTRATE_BLOCKS, "has_zinc_concentrate_block", "zinc_ingot_from_concentrate");
			addOreSmelting(exporter, AssemblyItems.BRASS_INGOT, AssemblyItemTags.BRASS_BLEND_BLOCKS, "has_brass_blend_block", "brass_ingot_from_blend");
			addOreSmelting(exporter, Items.IRON_INGOT, AssemblyItemTags.IRON_CONCENTRATE_BLOCKS, "has_iron_concentrate_block", "iron_ingot_from_concentrate");
			addOreSmelting(exporter, Items.GOLD_INGOT, AssemblyItemTags.GOLD_CONCENTRATE_BLOCKS, "has_gold_concentrate_block", "gold_ingot_from_concentrate");

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

		private void addOreSmelting(Consumer<RecipeJsonProvider> consumer, ItemConvertible output, Tag<Item> input, String criteronName, String id) {
			CookingRecipeJsonFactory.createBlasting(Ingredient.fromTag(input), output, 0.7F, 100).criterion(criteronName, conditionsFrom(input)).offerTo(consumer, new Identifier(Assembly.MOD_ID, id));
			CookingRecipeJsonFactory.createSmelting(Ingredient.fromTag(input), output, 0.7F, 200).criterion(criteronName, conditionsFrom(input)).offerTo(consumer, new Identifier(Assembly.MOD_ID, id));

		}

		private void addOreSmelting(Consumer<RecipeJsonProvider> consumer, ItemConvertible output, Tag<Item> input, String criteronName) {
			addOreSmelting(consumer, output, input, criteronName, Registry.ITEM.getId(output.asItem()).getPath());
		}

		private static EnterBlockCriterion.Conditions requireEnteringFluid(Block block) {
			return new EnterBlockCriterion.Conditions(EntityPredicate.Extended.EMPTY, block, StatePredicate.ANY);
		}

		private void addPlanks(Consumer<RecipeJsonProvider> consumer, ItemConvertible
				itemConvertible, Tag<Item> tag) {
			ShapelessRecipeJsonFactory.create(itemConvertible, 4).input(tag).group("planks").criterion("has_logs", conditionsFrom(tag)).offerTo(consumer);
		}

		private void addWood(Consumer<RecipeJsonProvider> consumer, ItemConvertible
				itemConvertible, ItemConvertible log) {
			ShapedRecipeJsonFactory.create(itemConvertible, 3).input('#', log).pattern("##").pattern("##").group("bark").criterion("has_log", conditionsFrom(log)).offerTo(consumer);
		}

		private void addBoat(Consumer<RecipeJsonProvider> consumer, ItemConvertible
				itemConvertible, ItemConvertible planks) {
			ShapedRecipeJsonFactory.create(itemConvertible).input('#', planks).pattern("# #").pattern("###").group("boat").criterion("in_water", requireEnteringFluid(Blocks.WATER)).offerTo(consumer);
		}

		private void addWoodenButton(Consumer<RecipeJsonProvider> consumer, ItemConvertible
				itemConvertible, ItemConvertible planks) {
			ShapelessRecipeJsonFactory.create(itemConvertible).input(planks).group("wooden_button").criterion("has_planks", conditionsFrom(planks)).offerTo(consumer);
		}

		private void addWoodenDoor(Consumer<RecipeJsonProvider> consumer, ItemConvertible
				itemConvertible, ItemConvertible planks) {
			ShapedRecipeJsonFactory.create(itemConvertible, 3).input('#', planks).pattern("##").pattern("##").pattern("##").group("wooden_door").criterion("has_planks", conditionsFrom(planks)).offerTo(consumer);
		}

		private void addWoodenFence(Consumer<RecipeJsonProvider> consumer, ItemConvertible
				itemConvertible, ItemConvertible planks) {
			ShapedRecipeJsonFactory.create(itemConvertible, 3).input('#', Items.STICK).input('W', planks).pattern("W#W").pattern("W#W").group("wooden_fence").criterion("has_planks", conditionsFrom(planks)).offerTo(consumer);
		}

		private void addWoodenFenceGate(Consumer<RecipeJsonProvider> consumer, ItemConvertible
				itemConvertible, ItemConvertible planks) {
			ShapedRecipeJsonFactory.create(itemConvertible).input('#', Items.STICK).input('W', planks).pattern("#W#").pattern("#W#").group("wooden_fence_gate").criterion("has_planks", conditionsFrom(planks)).offerTo(consumer);
		}

		private void addWoodenPressurePlate(Consumer<RecipeJsonProvider> consumer, ItemConvertible
				itemConvertible, ItemConvertible planks) {
			ShapedRecipeJsonFactory.create(itemConvertible).input('#', planks).pattern("##").group("wooden_pressure_plate").criterion("has_planks", conditionsFrom(planks)).offerTo(consumer);
		}

		private void addWoodenSlab(Consumer<RecipeJsonProvider> consumer, ItemConvertible
				itemConvertible, ItemConvertible planks) {
			ShapedRecipeJsonFactory.create(itemConvertible, 6).input('#', planks).pattern("###").group("wooden_slab").criterion("has_planks", conditionsFrom(planks)).offerTo(consumer);
		}

		private void addWoodenStairs(Consumer<RecipeJsonProvider> consumer, ItemConvertible
				itemConvertible, ItemConvertible planks) {
			ShapedRecipeJsonFactory.create(itemConvertible, 4).input('#', planks).pattern("#  ").pattern("## ").pattern("###").group("wooden_stairs").criterion("has_planks", conditionsFrom(planks)).offerTo(consumer);
		}

		private void addWoodenTrapdoor(Consumer<RecipeJsonProvider> consumer, ItemConvertible
				itemConvertible, ItemConvertible planks) {
			ShapedRecipeJsonFactory.create(itemConvertible, 2).input('#', planks).pattern("###").pattern("###").group("wooden_trapdoor").criterion("has_planks", conditionsFrom(planks)).offerTo(consumer);
		}

		private void addWoodenSign(Consumer<RecipeJsonProvider> consumer, ItemConvertible
				itemConvertible, ItemConvertible planks) {
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

			this.drops(AssemblyBlocks.IRON_CONCENTRATE_BLOCK);
			this.drops(AssemblyBlocks.GOLD_CONCENTRATE_BLOCK);
			this.drops(AssemblyBlocks.COPPER_CONCENTRATE_BLOCK);
			this.drops(AssemblyBlocks.ZINC_CONCENTRATE_BLOCK);
			this.drops(AssemblyBlocks.BRASS_BLEND_BLOCK);

			this.drops(AssemblyBlocks.FLUID_BARREL);

			this.drops(AssemblyBlocks.BOILER, Blocks.FURNACE);
			this.drops(AssemblyBlocks.BOILER_CHAMBER);
			this.drops(AssemblyBlocks.STEAM_PRESS, block -> BlockLootTableGenerator.createForMultiblock(AssemblyBlocks.STEAM_PRESS, SteamPressBlock.HALF, DoubleBlockHalf.LOWER));

			this.oreDrops(Blocks.IRON_ORE, new Pair<>(AssemblyItems.IRON_CONCENTRATE, UniformLootTableRange.between(1.0F, 5.0F)), new Pair<>(AssemblyItems.COPPER_CONCENTRATE, BinomialLootTableRange.create(2, 0.25F)));
			this.oreDrops(Blocks.GOLD_ORE, new Pair<>(AssemblyItems.GOLD_CONCENTRATE, UniformLootTableRange.between(1.0F, 5.0F)), new Pair<>(AssemblyItems.ZINC_CONCENTRATE, BinomialLootTableRange.create(1, 0.25F)));
			this.oreDrops(AssemblyBlocks.COPPER_ORE, new Pair<>(AssemblyItems.COPPER_CONCENTRATE, UniformLootTableRange.between(1.0F, 5.0F)), new Pair<>(AssemblyItems.ZINC_CONCENTRATE, UniformLootTableRange.between(0.0F, 1.0F)));

			this.drops(AssemblyBlocks.COPPER_BLOCK);
			this.drops(AssemblyBlocks.ZINC_BLOCK);
			this.drops(AssemblyBlocks.BRASS_BLOCK);

			this.drops(AssemblyBlocks.CAPROCK);
			this.drops(AssemblyBlocks.HALITE, (block) -> BlockLootTableCreator.dropsWithSilkTouch(AssemblyBlocks.HALITE, BlockLootTableCreator.addExplosionDecayCondition(block, ItemEntry.builder(AssemblyItems.SALT).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))).withFunction(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE)))));

			this.drops(AssemblyBlocks.TINKERING_TABLE);
			this.drops(AssemblyBlocks.CONVEYOR_BELT);
			this.drops(AssemblyBlocks.FLUID_HOPPER);
			this.drops(AssemblyBlocks.SPIGOT);
			this.drops(AssemblyBlocks.FLUID_INJECTOR);
		}

		public void oreDrops(Block block, Pair<ItemConvertible, LootTableRange>... hammerDrops) {
			if (hammerDrops.length < 1) {
				throw new InvalidParameterException("Hammer drops cannot be empty for the loot table for " + Registry.BLOCK.getId(block));
			}
			this.drops(block, drop -> {
				LootTable.Builder lootTable = LootTable.builder();
				boolean first = true;
				for (Pair<ItemConvertible, LootTableRange> hammerDropEntry : hammerDrops) {
					ItemConvertible hammerDrop = hammerDropEntry.getLeft();
					LootTableRange hammerDropCount = hammerDropEntry.getRight();
					if (first) {
						lootTable
								.withPool(LootPool.builder()
										.withRolls(ConstantLootTableRange.create(1))
										.withEntry(AlternativeEntry.builder(
												BlockLootTableCreator.addExplosionDecayCondition(hammerDrop, ItemEntry.builder(hammerDrop).withFunction(SetCountLootFunction.builder(hammerDropCount)).withFunction(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE)))
														.withCondition(MatchToolLootCondition.builder(ItemPredicate.Builder.create().tag(AssemblyItemTags.HAMMERS))))
												.withChild(BlockLootTableCreator.addSurvivesExplosionCondition(drop, ItemEntry.builder(drop)))));
						first = false;
					} else {
						lootTable
								.withPool(LootPool.builder()
										.withRolls(ConstantLootTableRange.create(1))
										.withEntry(
												BlockLootTableCreator.addExplosionDecayCondition(hammerDrop, ItemEntry.builder(hammerDrop).withFunction(SetCountLootFunction.builder(hammerDropCount)).withFunction(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE)))
														.withCondition(MatchToolLootCondition.builder(ItemPredicate.Builder.create().tag(AssemblyItemTags.HAMMERS)))));
					}
				}
				return lootTable;
			});

		}
	}
}
