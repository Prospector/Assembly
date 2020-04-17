package com.terraformersmc.assembly.data;

import com.terraformersmc.assembly.Assembly;
import com.terraformersmc.assembly.block.AssemblyBlocks;
import com.terraformersmc.assembly.tag.AssemblyBlockTags;
import com.terraformersmc.assembly.tag.AssemblyItemTags;
import com.terraformersmc.dossier.DossierProvider;
import com.terraformersmc.dossier.Dossiers;
import com.terraformersmc.dossier.generator.BlockTagsDossier;
import com.terraformersmc.dossier.generator.ItemTagsDossier;
import com.terraformersmc.dossier.generator.LootTablesDossier;
import com.terraformersmc.dossier.generator.RecipesDossier;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;

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
			add(AssemblyBlockTags.HEVEA_LOGS, AssemblyBlocks.HEVEA_LOG, AssemblyBlocks.STRIPPED_HEVEA_LOG, AssemblyBlocks.HEVEA_WOOD, AssemblyBlocks.STRIPPED_HEVEA_WOOD);
			add(BlockTags.LOGS_THAT_BURN, AssemblyBlockTags.HEVEA_LOGS);
		}
	}

	private static class AssemblyItemTagsGenerator extends ItemTagsDossier {
		@Override
		protected void addItemTags() {
			copyFromBlock(AssemblyItemTags.HEVEA_LOGS, AssemblyBlockTags.HEVEA_LOGS);
			add(ItemTags.LOGS_THAT_BURN, AssemblyItemTags.HEVEA_LOGS);
		}
	}

	private static class AssemblyRecipesGenerator extends RecipesDossier {
		@Override
		protected void addRecipes(Consumer<RecipeJsonProvider> provider) {

		}
	}


	private static class AssemblyLootTablesGenerator extends LootTablesDossier {
		@Override
		protected void addLootTables() {
			drops(AssemblyBlocks.HEVEA_PLANKS);
			drops(AssemblyBlocks.HEVEA_SAPLING);
			pottedPlantDrops(AssemblyBlocks.POTTED_HEVEA_SAPLING);
			drops(AssemblyBlocks.STRIPPED_HEVEA_LOG);
			drops(AssemblyBlocks.STRIPPED_HEVEA_WOOD);
			drops(AssemblyBlocks.HEVEA_LOG);
			drops(AssemblyBlocks.HEVEA_WOOD);
			drops(AssemblyBlocks.HEVEA_LEAVES);
			drops(AssemblyBlocks.HEVEA_SLAB);
			drops(AssemblyBlocks.HEVEA_PRESSURE_PLATE);
			drops(AssemblyBlocks.HEVEA_FENCE);
			drops(AssemblyBlocks.HEVEA_TRAPDOOR);
			drops(AssemblyBlocks.HEVEA_FENCE_GATE);
			drops(AssemblyBlocks.HEVEA_STAIRS);
			drops(AssemblyBlocks.HEVEA_BUTTON);
			drops(AssemblyBlocks.HEVEA_DOOR);
			drops(AssemblyBlocks.HEVEA_SIGN);
			drops(AssemblyBlocks.HEVEA_WALL_SIGN);

			drops(AssemblyBlocks.FLUID_BARREL);

			drops(AssemblyBlocks.BOILER, Blocks.FURNACE);
			drops(AssemblyBlocks.BOILER_CHAMBER);
			drops(AssemblyBlocks.STEAM_PRESS);

			drops(AssemblyBlocks.COPPER_ORE);

			drops(AssemblyBlocks.CRUSHED_COAL_ORE);
			drops(AssemblyBlocks.CRUSHED_COPPER_ORE);
			drops(AssemblyBlocks.CRUSHED_IRON_ORE);
			drops(AssemblyBlocks.CRUSHED_GOLD_ORE);
			drops(AssemblyBlocks.CRUSHED_REDSTONE_ORE);
			drops(AssemblyBlocks.CRUSHED_LAPIS_ORE);
			drops(AssemblyBlocks.CRUSHED_DIAMOND_ORE);
			drops(AssemblyBlocks.CRUSHED_EMERALD_ORE);
			drops(AssemblyBlocks.CRUSHED_NETHER_GOLD_ORE);

			drops(AssemblyBlocks.COPPER_BLOCK);
			drops(AssemblyBlocks.ZINC_BLOCK);
			drops(AssemblyBlocks.BRASS_BLOCK);

			drops(AssemblyBlocks.CAPROCK);
			drops(AssemblyBlocks.HALITE);

			drops(AssemblyBlocks.CONVEYOR_BELT);
			drops(AssemblyBlocks.FLUID_HOPPER);
			drops(AssemblyBlocks.SPIGOT);

		}
	}
}
