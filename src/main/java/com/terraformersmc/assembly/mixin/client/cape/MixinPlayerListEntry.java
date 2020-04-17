package com.terraformersmc.assembly.mixin.client.cape;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.terraformersmc.assembly.util.AssemblyConstants;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(PlayerListEntry.class)
public class MixinPlayerListEntry {
	@Shadow
	@Final
	private GameProfile profile;

	@Shadow
	@Final
	private Map<MinecraftProfileTexture.Type, Identifier> textures;

	@Inject(method = "loadTextures", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/PlayerSkinProvider;loadSkin(Lcom/mojang/authlib/GameProfile;Lnet/minecraft/client/texture/PlayerSkinProvider$SkinTextureAvailableCallback;Z)V"))
	private void onLoadTextures(CallbackInfo info) {
		if (AssemblyConstants.AUTHORS.contains(profile.getId().toString()) || "ProspectorDev".equals(profile.getName())) {
			textures.put(MinecraftProfileTexture.Type.CAPE, AssemblyConstants.Ids.AUTHOR_CAPE);
			textures.put(MinecraftProfileTexture.Type.ELYTRA, AssemblyConstants.Ids.AUTHOR_CAPE);
		}
	}
}
