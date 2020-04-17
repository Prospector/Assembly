//package team.reborn.assembly.recipe.ingredient;
//
//import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
//import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
//import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
//import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
//import alexiil.mc.lib.attributes.fluid.volume.PotionFluidKey;
//import com.google.common.collect.Lists;
//import com.google.gson.*;
//import it.unimi.dsi.fastutil.ints.IntArrayList;
//import it.unimi.dsi.fastutil.ints.IntComparators;
//import it.unimi.dsi.fastutil.ints.IntList;
//import net.fabricmc.api.EnvType;
//import net.fabricmc.api.Environment;
//import net.minecraft.fluid.Fluid;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemConvertible;
//import net.minecraft.item.ItemStack;
//import net.minecraft.recipe.RecipeFinder;
//import net.minecraft.tag.FluidTags;
//import net.minecraft.tag.Tag;
//import net.minecraft.util.Identifier;
//import net.minecraft.util.JsonHelper;
//import net.minecraft.util.registry.Registry;
//
//import javax.annotation.Nullable;
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.List;
//import java.util.function.Predicate;
//import java.util.stream.Stream;
//import java.util.stream.StreamSupport;
//
//public final class FluidIngredient implements Predicate<ItemStack> {
//	private static final Predicate<? super FluidIngredient.Entry> NON_EMPTY = (entry) -> {
//		return !entry.getKeys().stream().allMatch(ItemStack::isEmpty);
//	};
//	public static final FluidIngredient EMPTY = new FluidIngredient(Stream.empty());
//	private final FluidIngredient.Entry[] entries;
//	private ItemStack[] matchingStacks;
//	private IntList ids;
//
//	private FluidIngredient(Stream<? extends FluidIngredient.Entry> entries) {
//		this.entries = entries.filter(NON_EMPTY).toArray(Entry[]::new);
//	}
//
//	@Environment(EnvType.CLIENT)
//	public ItemStack[] getMatchingStacksClient() {
//		this.cacheMatchingStacks();
//		return this.matchingStacks;
//	}
//
//	private void cacheMatchingStacks() {
//		if (this.matchingStacks == null) {
//			this.matchingStacks = (ItemStack[]) Arrays.stream(this.entries).flatMap((entry) -> {
//				return entry.getKeys().stream();
//			}).distinct().toArray((i) -> {
//				return new ItemStack[i];
//			});
//		}
//
//	}
//
//	@Override
//    public boolean test(@Nullable ItemStack itemStack) {
//		if (itemStack == null) {
//			return false;
//		} else if (this.entries.length == 0) {
//			return itemStack.isEmpty();
//		} else {
//			this.cacheMatchingStacks();
//			ItemStack[] var2 = this.matchingStacks;
//			int var3 = var2.length;
//
//			for (int var4 = 0; var4 < var3; ++var4) {
//				ItemStack itemStack2 = var2[var4];
//				if (itemStack2.getItem() == itemStack.getItem()) {
//					return true;
//				}
//			}
//
//			return false;
//		}
//	}
//
//	public IntList getIds() {
//		if (this.ids == null) {
//			this.cacheMatchingStacks();
//			this.ids = new IntArrayList(this.matchingStacks.length);
//			ItemStack[] var1 = this.matchingStacks;
//			int var2 = var1.length;
//
//			for (int var3 = 0; var3 < var2; ++var3) {
//				ItemStack itemStack = var1[var3];
//				this.ids.add(RecipeFinder.getItemId(itemStack));
//			}
//
//			this.ids.sort(IntComparators.NATURAL_COMPARATOR);
//		}
//
//		return this.ids;
//	}
//
//	public void write(PacketByteBuf buf) {
//		this.cacheMatchingStacks();
//		buf.writeVarInt(this.matchingStacks.length);
//
//		for (int i = 0; i < this.matchingStacks.length; ++i) {
//			buf.writeItemStack(this.matchingStacks[i]);
//		}
//
//	}
//
//	public JsonElement toJson() {
//		if (this.entries.length == 1) {
//			return this.entries[0].toJson();
//		} else {
//			JsonArray jsonArray = new JsonArray();
//			net.minecraft.recipe.Ingredient.Entry[] var2 = this.entries;
//			int var3 = var2.length;
//
//			for (int var4 = 0; var4 < var3; ++var4) {
//				net.minecraft.recipe.Ingredient.Entry entry = var2[var4];
//				jsonArray.add(entry.toJson());
//			}
//
//			return jsonArray;
//		}
//	}
//
//	public boolean isEmpty() {
//		return this.entries.length == 0 && (this.matchingStacks == null || this.matchingStacks.length == 0) && (this.ids == null || this.ids.isEmpty());
//	}
//
//	private static net.minecraft.recipe.Ingredient ofEntries(Stream<? extends net.minecraft.recipe.Ingredient.Entry> entries) {
//		net.minecraft.recipe.Ingredient ingredient = new net.minecraft.recipe.Ingredient(entries);
//		return ingredient.entries.length == 0 ? EMPTY : ingredient;
//	}
//
//	public static net.minecraft.recipe.Ingredient ofItems(ItemConvertible... items) {
//		return ofEntries(Arrays.stream(items).map((item) -> {
//			return new net.minecraft.recipe.Ingredient.StackEntry(new ItemStack(item));
//		}));
//	}
//
//	@Environment(EnvType.CLIENT)
//	public static net.minecraft.recipe.Ingredient ofStacks(ItemStack... stacks) {
//		return ofEntries(Arrays.stream(stacks).map((stack) -> {
//			return new net.minecraft.recipe.Ingredient.StackEntry(stack);
//		}));
//	}
//
//	public static net.minecraft.recipe.Ingredient fromTag(Tag<Item> tag) {
//		return ofEntries(Stream.of(new net.minecraft.recipe.Ingredient.TagEntry(tag)));
//	}
//
//	public static FluidIngredient fromPacket(PacketByteBuf buf) {
//		int i = buf.readVarInt();
//		return ofEntries(Stream.generate(() -> {
//			return new FluidVolumeEntry(buf.readItemStack());
//		}).limit((long) i));
//	}
//
//	public static net.minecraft.recipe.Ingredient fromJson(@Nullable JsonElement json) {
//		if (json != null && !json.isJsonNull()) {
//			if (json.isJsonObject()) {
//				return ofEntries(Stream.of(entryFromJson(json.getAsJsonObject())));
//			} else if (json.isJsonArray()) {
//				JsonArray jsonArray = json.getAsJsonArray();
//				if (jsonArray.size() == 0) {
//					throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
//				} else {
//					return ofEntries(StreamSupport.stream(jsonArray.spliterator(), false).map((jsonElement) -> {
//						return entryFromJson(JsonHelper.asObject(jsonElement, "item"));
//					}));
//				}
//			} else {
//				throw new JsonSyntaxException("Expected item to be object or array of objects");
//			}
//		} else {
//			throw new JsonSyntaxException("Item cannot be null");
//		}
//	}
//
//	public static FluidIngredient.Entry entryFromJson(JsonObject json) {
//		if (json.has("item") && json.has("tag")) {
//			throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
//		} else {
//			Identifier id;
//			if (json.has("item")) {
//				id = new Identifier(JsonHelper.getString(json, "fluid"));
//				Fluid fluid = Registry.FLUID.getOrEmpty(id).orElseThrow(() -> new JsonSyntaxException("Unknown fluid '" + id + "'"));
//				return new FluidVolumeEntry(FluidKeys.get(fluid));
//			} else if (json.has("tag")) {
//				id = new Identifier(JsonHelper.getString(json, "tag"));
//				Tag<Fluid> tag = FluidTags.getContainer().get(id);
//				if (tag == null) {
//					throw new JsonSyntaxException("Unknown item tag '" + id + "'");
//				} else {
//					return new TagEntry(tag);
//				}
//			} else {
//				throw new JsonParseException("An ingredient entry needs either a tag or an item");
//			}
//		}
//	}
//
//	static class TagEntry implements FluidIngredient.Entry {
//		private final Tag<Fluid> tag;
//		private final FluidAmount amount;
//
//		private TagEntry(Tag<Fluid> tag) {
//			this.tag = tag;
//		}
//
//		@Override
//		public Collection<FluidVolume> getVolumes() {
//			List<FluidKey> list = Lists.newArrayList();
//
//			for (Fluid fluid : this.tag.values()) {
//				list.add(FluidKeys.get(fluid));
//			}
//
//			return list;
//		}
//
//		@Override
//		public JsonObject toJson() {
//			JsonObject jsonObject = new JsonObject();
//			jsonObject.addProperty("tag", this.tag.getId().toString());
//			return jsonObject;
//		}
//	}
//
//	static class FluidVolumeEntry implements FluidIngredient.Entry {
//		private final FluidKey key;
//		private final FluidAmount amount;
//
//		private FluidVolumeEntry(FluidKey fluid, FluidAmount amount) {
//			this.key = fluid;
//			this.amount = amount;
//		}
//
//		@Override
//		public Collection<FluidVolume> getVolumes() {
//			return Collections.singleton(key.withAmount(amount));
//		}
//
//		@Override
//		public JsonObject toJson() {
//			JsonObject jsonObject = new JsonObject();
//			jsonObject.add("key", key.toJson());
//			jsonObject.add("amount", amount.toNbt());
//			return jsonObject;
//		}
//	}
//
//	interface Entry {
//		Collection<FluidVolume> getVolumes();
//
//		JsonObject toJson();
//	}
//}
