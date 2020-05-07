package com.terraformersmc.assembly.screen.builder;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;

public interface TextPositioner {
	ScreenPos position(int width, int height, Text text, TextRenderer textRenderer);
}
