package team.reborn.assembly.mixin.tag;

import net.minecraft.block.Block;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockTags.class)
public interface AccessorBlockTags {
	@SuppressWarnings("PublicStaticMixinMember")
	@Invoker("register")
	static Tag<Block> register(String id) {
		return null;
	}
}
