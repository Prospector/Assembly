package teamreborn.assembly.tags;

import net.minecraft.block.Block;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import teamreborn.assembly.Assembly;

public class AssemblyBlockTags {
	public static final Tag<Block> RUBBER_LOGS = new Tag<>(new Identifier(Assembly.MOD_ID, "rubber_logs"));
}
