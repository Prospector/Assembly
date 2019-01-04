package teamreborn.assembly.mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Profiler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import teamreborn.assembly.powernet.Powernet;
import teamreborn.assembly.powernet.PowernetConnector;
import teamreborn.assembly.powernet.PowernetSimulator;

@Mixin(World.class)
public abstract class WorldMixin implements PowernetSimulator {

	@Shadow
	@Final
	public boolean isClient;
	private Powernet powernet;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(CallbackInfo info) {
		if (!isClient) {
			powernet = new Powernet((World) (Object) this);
		}
	}

	@Inject(method = "addBlockEntity", at = @At("RETURN"))
	private void addBlockEntity(BlockEntity entity, CallbackInfoReturnable<Boolean> info) {
		if (!isClient) {
			if (entity instanceof PowernetConnector) {
				getPowernet().addConnector((PowernetConnector) entity);
			}
		}
	}

	@Inject(method = "removeBlockEntity", at = @At("HEAD"))
	private void removeBlockEntity(BlockPos pos, CallbackInfo info) {
		if (!isClient) {
			BlockEntity blockEntity = getBlockEntity(pos);
			if (blockEntity instanceof PowernetConnector) {
				getPowernet().removeConnector((PowernetConnector) blockEntity);
			}
		}
	}

	@Inject(method = "updateEntities", at = @At("RETURN"))
	private void updateEntities(CallbackInfo info) {
		if (!isClient) {
			getProfiler().begin("Powernet");
			getPowernet().simulate();
			getProfiler().end();
		}
	}

	@Override
	public Powernet getPowernet() {
		return powernet;
	}

	@Shadow
	public abstract BlockEntity getBlockEntity(BlockPos var1);

	@Shadow
	public abstract Profiler getProfiler();

}
