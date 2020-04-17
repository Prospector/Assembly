package com.terraformersmc.assembly.mixin.common.tag;

import net.minecraft.block.Block;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockTags.class)
public interface BlockTagsInvoker {
	@SuppressWarnings("PublicStaticMixinMember")
	@Invoker("register")
	static Tag.Identified<Block> register(String id) {
		return null;
	}
}
