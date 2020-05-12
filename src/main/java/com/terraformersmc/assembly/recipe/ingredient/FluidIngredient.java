package com.terraformersmc.assembly.recipe.ingredient;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.google.common.collect.Lists;
import com.google.gson.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.fluid.Fluid;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class FluidIngredient implements Predicate<FluidVolume> {
	public static final FluidIngredient EMPTY = new FluidIngredient(Stream.empty());
	private final FluidIngredient.Entry[] entries;
	private FluidVolume[] matchingVolumes;
	private FluidAmount amount;

	private FluidIngredient(Stream<? extends FluidIngredient.Entry> entries) {
		this.entries = entries.toArray(Entry[]::new);
	}

	@Environment(EnvType.CLIENT)
	public FluidVolume[] getMatchingVolumesClient() {
		this.cacheMatchingVolumes();
		return this.matchingVolumes;
	}

	private void cacheMatchingVolumes() {
		if (this.matchingVolumes == null) {
			this.matchingVolumes = Arrays.stream(this.entries).flatMap((entry) -> entry.getVolumes().stream()).distinct().toArray(FluidVolume[]::new);
			Arrays.stream(this.matchingVolumes).map(FluidVolume::getAmount_F).forEach(amount -> {
				if (this.amount == null) {
					this.amount = amount;
				} else if (!this.amount.equals(amount)) {
					throw new RuntimeException("Matching volume for Fluid Ingredient has a Fluid Amount mismatch. Cached amount:" + amount + "; Amount in volume: " + amount);
				}
			});
		}
	}

	public FluidFilter getFilter() {
		return key -> {
			this.cacheMatchingVolumes();
			if (this.matchingVolumes.length != 0) {
				FluidVolume[] matches = this.matchingVolumes;
				for (FluidVolume match : matches) {
					if (match.getFluidKey() == key) {
						return true;
					}
				}
			}
			return false;
		};
	}

	public FluidAmount getAmount() {
		this.cacheMatchingVolumes();
		return amount;
	}

	@Override
	public boolean test(@Nullable FluidVolume volume) {
		if (volume == null) {
			return false;
		} else {
			this.cacheMatchingVolumes();
			if (this.matchingVolumes.length == 0) {
				return volume.isEmpty();
			} else {
				FluidVolume[] matches = this.matchingVolumes;

				for (FluidVolume match : matches) {
					if (match.getFluidKey().equals(volume.getFluidKey()) && volume.getAmount_F().isGreaterThanOrEqual(getAmount())) {
						return true;
					}
				}
				return false;
			}
		}
	}

	public void toPacket(PacketByteBuf buf) {
		this.cacheMatchingVolumes();
		buf.writeVarInt(this.matchingVolumes.length);
		for (FluidVolume matchingVolume : this.matchingVolumes) {
			buf.writeCompoundTag(matchingVolume.toTag());
		}
		amount.toMcBuffer(buf);
	}

	public JsonElement toJson() {
		if (this.entries.length == 1) {
			return this.entries[0].toJson();
		} else {
			JsonArray jsonArray = new JsonArray();
			for (Entry entry : this.entries) {
				jsonArray.add(entry.toJson());
			}
			return jsonArray;
		}
	}

	public boolean isEmpty() {
		return this.entries.length == 0 && (this.matchingVolumes == null || this.matchingVolumes.length == 0);
	}

	private static FluidIngredient ofEntries(Stream<? extends FluidIngredient.Entry> entries) {
		FluidIngredient ingredient = new FluidIngredient(entries);
		return ingredient.entries.length == 0 ? EMPTY : ingredient;
	}

	public static FluidIngredient of(FluidAmount amount, Fluid... fluids) {
		return of(Arrays.stream(fluids).map(fluid -> FluidKeys.get(fluid).withAmount(amount)));
	}

	@Environment(EnvType.CLIENT)
	public static FluidIngredient of(FluidVolume... volumes) {
		return of(Arrays.stream(volumes));
	}

	public static FluidIngredient of(Stream<FluidVolume> stream) {
		return ofEntries(stream.filter((volume) -> !volume.isEmpty()).map(VolumeEntry::new));
	}

	public static FluidIngredient of(Tag<Fluid> tag, FluidAmount amount) {
		return ofEntries(Stream.of(new FluidIngredient.FluidTagEntry(tag, amount)));
	}

	public static FluidIngredient fromPacket(PacketByteBuf buf) {
		int volumeCount = buf.readVarInt();
		return ofEntries(Stream.generate(() -> new VolumeEntry(FluidKey.fromTag(buf.readCompoundTag()).withAmount(FluidAmount.fromMcBuffer(buf)))).limit(volumeCount));
	}

	public static FluidIngredient fromJson(@Nullable JsonElement json) {
		if (json != null && !json.isJsonNull()) {
			if (json.isJsonObject()) {
				return ofEntries(Stream.of(entryFromJson(json.getAsJsonObject())));
			} else {
				throw new JsonSyntaxException("Expected fluid to be object");
			}
		} else {
			throw new JsonSyntaxException("Fluid cannot be null");
		}
	}

	private static FluidIngredient.Entry entryFromJson(JsonObject json) {
		if (json.has("fluid_volume") && json.has("tag")) {
			throw new JsonParseException("A Fluid Ingredient entry can only be of one type, not multiple");
		} else {
			if (json.has("fluid_volume")) {
				return new FluidIngredient.VolumeEntry(FluidKey.fromJson(json).withAmount(FluidAmount.parse(json.get("amount").getAsString())));
			} else if (json.has("tag")) {
				Identifier id = new Identifier(JsonHelper.getString(json, "tag"));
				FluidAmount amount = FluidVolume.parseAmount(json.get("amount"));
				Tag<Fluid> tag = FluidTags.getContainer().get(id);
				if (tag == null) {
					throw new JsonSyntaxException("Unknown item tag '" + id + "'");
				} else {
					return new FluidTagEntry(tag, amount);
				}
			} else {
				throw new JsonParseException("An ingredient entry needs either a tag or a fluid volume");
			}
		}
	}

	static class FluidTagEntry implements FluidIngredient.Entry {
		private final Tag<Fluid> tag;
		private final FluidAmount amount;

		private FluidTagEntry(Tag<Fluid> tag, FluidAmount amount) {
			this.tag = tag;
			this.amount = amount;
		}

		@Override
		public Collection<FluidVolume> getVolumes() {
			List<FluidVolume> volumes = Lists.newArrayList();
			for (Fluid fluid : this.tag.values()) {
				volumes.add(FluidKeys.get(fluid).withAmount(FluidAmount.ONE));
			}
			return volumes;
		}

		@Override
		public JsonObject toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("tag", FluidTags.getContainer().getId(this.tag).toString());
			jsonObject.addProperty("amount", this.amount.toParseableString());
			return jsonObject;
		}
	}

	static class VolumeEntry implements FluidIngredient.Entry {
		private final FluidVolume volume;

		private VolumeEntry(FluidVolume volume) {
			this.volume = volume;
		}

		@Override
		public Collection<FluidVolume> getVolumes() {
			return Collections.singleton(this.volume);
		}

		@Override
		public JsonObject toJson() {
			return this.volume.toJson();
		}
	}

	interface Entry {
		Collection<FluidVolume> getVolumes();

		JsonObject toJson();
	}
}
