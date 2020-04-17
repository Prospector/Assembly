package com.terraformersmc.assembly.util.fluid;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import alexiil.mc.lib.attributes.fluid.volume.PotionFluidKey;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class FluidUtil {
	public static JsonObject volumeToJson(FluidVolume volume) {
		JsonObject volumeObject = new JsonObject();
		FluidKey key = volume.getFluidKey();
		if (key.getRawFluid() != null) {
			volumeObject.addProperty("fluid", Registry.FLUID.getId(key.getRawFluid()).toString());
		} else if (key instanceof PotionFluidKey) {
			volumeObject.addProperty("potion", Registry.POTION.getId(((PotionFluidKey) key).potion).toString());
		}
		volumeObject.add("amount", amountToJson(volume.getAmount_F()));
		return volumeObject;
	}

	public static FluidVolume volumeFromJson(JsonObject volumeObject) {
		FluidKey key = null;
		if (volumeObject.has("fluid")) {
			key = FluidKeys.get(Registry.FLUID.get(new Identifier(volumeObject.get("fluid").getAsString())));
		} else if (volumeObject.has("potion")) {
			key = FluidKeys.get(Registry.POTION.get(new Identifier(volumeObject.get("potion").getAsString())));
		}
		return key == null ? null : key.withAmount(amountFromJson(volumeObject.getAsJsonObject("amount")));
	}

	public static JsonObject amountToJson(FluidAmount amount) {
		JsonObject amountObject = new JsonObject();
		amountObject.addProperty("whole", amount.whole);
		amountObject.addProperty("numerator", amount.numerator);
		amountObject.addProperty("denominator", amount.denominator);
		return amountObject;
	}

	public static FluidAmount amountFromJson(JsonObject amountObject) {
		int whole = JsonHelper.getInt(amountObject, "whole", 0);
		if (!amountObject.has("numerator") || !amountObject.has("denominator")) {
			return FluidAmount.ofWhole(whole);
		}
		return FluidAmount.of(whole, JsonHelper.getInt(amountObject, "numerator"), JsonHelper.getInt(amountObject, "denominator"));
	}
}
