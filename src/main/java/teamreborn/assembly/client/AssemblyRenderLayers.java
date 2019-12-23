package teamreborn.assembly.client;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import teamreborn.assembly.block.AssemblyBlocks;

public class AssemblyRenderLayers {
	public static void register() {
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), AssemblyBlocks.HEVEA_DOOR, AssemblyBlocks.HEVEA_TRAPDOOR, AssemblyBlocks.HEVEA_SAPLING);
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), AssemblyBlocks.TUBE);
	}
}
