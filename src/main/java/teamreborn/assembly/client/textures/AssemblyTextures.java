package teamreborn.assembly.client.textures;

import com.terraformersmc.terraform.registry.SpriteIdentifierRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.entity.BoatEntityRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import teamreborn.assembly.block.AssemblyBlocks;
import teamreborn.assembly.entity.AssemblyEntities;

public class AssemblyTextures {
	public static void register() {
		EntityRendererRegistry.INSTANCE.register(AssemblyEntities.HEVEA_BOAT, (dispatcher, context) -> new BoatEntityRenderer(dispatcher));

		Identifier texture = AssemblyBlocks.HEVEA_SIGN.getTexture();
		SpriteIdentifierRegistry.INSTANCE.addIdentifier(new SpriteIdentifier(TexturedRenderLayers.SIGNS_ATLAS_TEXTURE, texture));
	}
}
