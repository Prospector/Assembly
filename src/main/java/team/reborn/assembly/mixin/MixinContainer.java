package team.reborn.assembly.mixin;

import team.reborn.assembly.mixintf.GetMenuListeners;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(Container.class)
public class MixinContainer implements GetMenuListeners {
	@Final
	@Shadow
	private List<ContainerListener> listeners;

	@Override
	public List<ContainerListener> assembly_getListeners() {
		return listeners;
	}
}
