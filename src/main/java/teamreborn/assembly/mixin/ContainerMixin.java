package teamreborn.assembly.mixin;

import teamreborn.assembly.mixintf.GetContainerListeners;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(Container.class)
public class ContainerMixin implements GetContainerListeners {
	@Final
	@Shadow
	private List<ContainerListener> listeners;

	@Override
	public List<ContainerListener> assembly_getListeners() {
		return listeners;
	}
}
