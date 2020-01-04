package team.reborn.assembly.mixin.client.exoframe;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import team.reborn.assembly.item.CustomArmorTexture;
import team.reborn.assembly.item.CustomArmorTextureForStack;

import javax.annotation.Nullable;
import java.util.Map;

@Mixin(ArmorFeatureRenderer.class)
public abstract class MixinArmorFeatureRenderer<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> {
	@Shadow
	@Final
	private static Map<String, Identifier> ARMOR_TEXTURE_CACHE;

	@Inject(method = "renderArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/ArmorFeatureRenderer;renderArmorParts(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/item/ArmorItem;ZLnet/minecraft/client/render/entity/model/BipedEntityModel;ZFFFLjava/lang/String;)V", ordinal = 2), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private void modifyArmorRender(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, T livingEntity, float f, float g, float h, float i, float j, float k, EquipmentSlot equipmentSlot, int l, CallbackInfo info, ItemStack stack, ArmorItem item, A bipedEntityModel, boolean lowerParts, boolean renderGlint) {
		if (item instanceof CustomArmorTextureForStack) {
			this.renderCustomArmorParts(matrixStack, vertexConsumerProvider, l, (CustomArmorTextureForStack) item, stack, renderGlint, bipedEntityModel, lowerParts, 1.0F, 1.0F, 1.0F, null);
			info.cancel();
		}
	}

	@Inject(method = "getArmorTexture", at = @At("HEAD"), cancellable = true)
	private void modifyArmorTexture(ArmorItem armorItem, boolean lowerParts, @Nullable String suffix, CallbackInfoReturnable<Identifier> info) {
		if (armorItem instanceof CustomArmorTexture) {
			info.setReturnValue(getCachedId(((CustomArmorTexture) armorItem).getArmorTexture(armorItem, lowerParts, suffix)));
		}
	}

	@Unique
	private void renderCustomArmorParts(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CustomArmorTextureForStack item, ItemStack stack, boolean renderGlint, A bipedEntityModel, boolean lowerParts, float r, float g, float b, @Nullable String textureSuffix) {
		VertexConsumer vertexConsumer = ItemRenderer.getArmorVertexConsumer(vertexConsumerProvider, RenderLayer.getEntityCutoutNoCull(getCachedId(item.getArmorTexture(stack, lowerParts, textureSuffix))), false, renderGlint);
		bipedEntityModel.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, r, g, b, 1.0F);
	}

	@Unique
	private Identifier getCachedId(String texture) {
		return ARMOR_TEXTURE_CACHE.computeIfAbsent(texture, Identifier::new);
	}
}
