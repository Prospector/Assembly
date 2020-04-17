package com.terraformersmc.assembly.mixin.common.ore;

import com.terraformersmc.assembly.util.CrushingUtil;
import net.fabricmc.fabric.api.tools.FabricToolTags;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class MixinItem {
	@Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
	private void onUseOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> info) {
		if (((Object) this) instanceof PickaxeItem || FabricToolTags.PICKAXES.contains((Item) (Object) this)) {
			info.setReturnValue(CrushingUtil.crushBlock(context));
		}
	}
}
