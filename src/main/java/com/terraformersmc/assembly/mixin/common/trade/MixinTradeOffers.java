package com.terraformersmc.assembly.mixin.common.trade;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.item.map.MapIcon;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(TradeOffers.class)
public class MixinTradeOffers {
	@Inject(method = "method_16929", at = @At("TAIL"))
	private static void onAddTrade(HashMap<VillagerProfession, Int2ObjectMap<TradeOffers.Factory[]>> map, CallbackInfo info) {
		map.get(VillagerProfession.CARTOGRAPHER).compute(1, (integer, factories) -> ArrayUtils.add(factories, new TradeOffers.SellMapFactory(5, "assembly:salt_dome", MapIcon.Type.RED_X, 8, 3)));
	}
}
