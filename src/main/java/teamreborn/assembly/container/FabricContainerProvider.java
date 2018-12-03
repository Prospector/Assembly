package teamreborn.assembly.container;

import net.minecraft.container.ContainerProvider;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;

public interface FabricContainerProvider extends ContainerProvider {

	@Override
	default String getContainerId() {
		return getContainerIdentifier().toString();
	}

	@Override
	default boolean hasCustomName() {
		return false;
	}

	@Override
	default TextComponent getName() {
		return new TranslatableTextComponent(getContainerIdentifier().toString());
	}

	Identifier getContainerIdentifier();
}
