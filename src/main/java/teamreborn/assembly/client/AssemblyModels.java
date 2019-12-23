package teamreborn.assembly.client;

import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import teamreborn.assembly.Assembly;

public class AssemblyModels {
	public static final ModelIdentifier STEAM_PRESS_UPPER_ARM = new ModelIdentifier(new Identifier(Assembly.MOD_ID, "steam_press_arm"), "");

	public static void register() {
		ModelLoadingRegistry.INSTANCE.registerAppender((manager, out) -> {
			out.accept(STEAM_PRESS_UPPER_ARM);
		});
	}
}
