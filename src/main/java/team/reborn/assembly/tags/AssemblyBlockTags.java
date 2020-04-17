package team.reborn.assembly.tags;

import net.minecraft.block.Block;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import team.reborn.assembly.Assembly;
import team.reborn.assembly.mixin.common.tag.BlockTagsInvoker;
import team.reborn.assembly.util.AssemblyConstants;

public class AssemblyBlockTags {
	public static final Tag<Block> HEVEA_LOGS = ofAssembly("hevea_logs");

	public static Tag<Block> of(Identifier id) {
		return BlockTagsInvoker.register(id.toString());
	}

	public static Tag<Block> ofAssembly(String path) {
		return of(new Identifier(Assembly.MOD_ID, path));
	}

	public static Tag<Block> ofCommon(String path) {
		return of(new Identifier(AssemblyConstants.COMMON_NAMESPACE, path));
	}
}
