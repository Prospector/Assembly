package com.terraformersmc.assembly.client;

import com.terraformersmc.assembly.Assembly;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

public class AssemblyModels {
	public static final ModelIdentifier PRESS_UPPER_ARM = new ModelIdentifier(new Identifier(Assembly.MOD_ID, "press_arm"), "");

	public static void register() {
		ModelLoadingRegistry.INSTANCE.registerAppender((manager, out) -> {
			out.accept(PRESS_UPPER_ARM);
		});
	}
}
