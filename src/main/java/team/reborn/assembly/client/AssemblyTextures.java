package team.reborn.assembly.client;

import com.terraformersmc.terraform.registry.SpriteIdentifierRegistry;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.util.SpriteIdentifier;
import team.reborn.assembly.block.AssemblyBlocks;

public class AssemblyTextures {
	public static void register() {
		SpriteIdentifierRegistry.INSTANCE.addIdentifier(new SpriteIdentifier(TexturedRenderLayers.SIGNS_ATLAS_TEXTURE, AssemblyBlocks.HEVEA_SIGN.getTexture()));
	}
}
