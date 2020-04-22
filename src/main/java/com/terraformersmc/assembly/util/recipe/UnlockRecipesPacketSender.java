package com.terraformersmc.assembly.util.recipe;

import net.minecraft.network.packet.s2c.play.UnlockRecipesS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;

public interface UnlockRecipesPacketSender {
	void sendUnlockRecipesPacket(UnlockRecipesS2CPacket.Action action, ServerPlayerEntity player, List<Identifier> recipeIds);
}
