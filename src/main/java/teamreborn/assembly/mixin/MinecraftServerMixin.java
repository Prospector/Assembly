package teamreborn.assembly.mixin;

import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

	@Shadow
	@Final
	private ReloadableResourceManager dataManager;

	@Inject(method = "<init>", at = @At("RETURN"))
	public void init(CallbackInfo info) {
		//		dataManager.addListener(MachineRecipeManager.INSTANCE);
	}

}
