package com.terraformersmc.assembly.client;

import com.terraformersmc.assembly.block.AssemblyBlocks;
import com.terraformersmc.assembly.fluid.AssemblyFluids;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

public class AssemblyRenderLayers {
	public static void register() {
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), AssemblyBlocks.HEVEA_DOOR, AssemblyBlocks.HEVEA_TRAPDOOR, AssemblyBlocks.HEVEA_SAPLING);
		AssemblyFluids.getFluids().values().forEach(fluid -> BlockRenderLayerMap.INSTANCE.putFluid(fluid, RenderLayer.getTranslucent()));
	}
}
