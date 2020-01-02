package team.reborn.assembly.tags;

import net.minecraft.item.Item;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import team.reborn.assembly.Assembly;
import team.reborn.assembly.util.AssemblyConstants;

public class AssemblyItemTags {

	public static Tag<Item> of(Identifier id) {
		return new ItemTags.CachingTag(id);
	}

	public static Tag<Item> ofAssembly(String path) {
		return of(new Identifier(Assembly.MOD_ID, path));
	}

	public static Tag<Item> ofCommon(String path) {
		return of(new Identifier(AssemblyConstants.COMMON_NAMESPACE, path));
	}
}
