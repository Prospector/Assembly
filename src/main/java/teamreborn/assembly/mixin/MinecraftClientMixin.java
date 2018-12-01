package teamreborn.assembly.mixin;

import net.fabricmc.fabric.client.render.BlockEntityRendererRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import teamreborn.assembly.blockentity.SappingBarrelBlockEntity;
import teamreborn.assembly.client.SappingBarrelBlockEntityRenderer;

@Mixin(BlockEntityRendererRegistry.class)
public class MinecraftClientMixin {
	@Inject(at = @At("RETURN"), method = "initialize(Ljava/util/Map;)V")
	public void init(CallbackInfo callbackInfo) {
		BlockEntityRendererRegistry.INSTANCE.register(SappingBarrelBlockEntity.class, new SappingBarrelBlockEntityRenderer());
	}
}
