package team.reborn.assembly.mixin.common.tag;

import net.minecraft.item.Item;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemTags.class)
public interface ItemTagsInvoker {
	@Invoker("register")
	static Tag.Identified<Item> register(String id) {
		return null;
	}
}
