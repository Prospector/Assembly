package team.reborn.assembly.tags;

import net.minecraft.block.Block;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;
import team.reborn.assembly.Assembly;
import team.reborn.assembly.mixin.tag.AccessorBlockTags;
import team.reborn.assembly.util.AssemblyConstants;

import java.util.Collection;
import java.util.Optional;

public class AssemblyBlockTags {
	private static TagContainer<Block> container = new TagContainer((identifier) -> {
		return Optional.empty();
	}, "", false, "");
	private static int latestVersion;

	public static final Tag<Block> HEVEA_LOGS = ofAssembly("hevea_logs");

	public static Tag<Block> of(Identifier id) {
		return AccessorBlockTags.register(id.toString());
	}

	public static Tag<Block> ofAssembly(String path) {
		return of(new Identifier(Assembly.MOD_ID, path));
	}

	public static Tag<Block> ofCommon(String path) {
		return of(new Identifier(AssemblyConstants.COMMON_NAMESPACE, path));
	}

	public static class CachingTag extends Tag<Block> {
		private int version = -1;
		private Tag<Block> delegate;

		public CachingTag(Identifier identifier) {
			super(identifier);
		}

		public boolean contains(Block block) {
			if (this.version != AssemblyBlockTags.latestVersion) {
				this.delegate = BlockTags.getContainer().getOrCreate(this.getId());
				this.version = AssemblyBlockTags.latestVersion;
			}

			return this.delegate.contains(block);
		}

		public Collection<Block> values() {
			if (this.version != AssemblyBlockTags.latestVersion) {
				this.delegate = BlockTags.getContainer().getOrCreate(this.getId());
				this.version = AssemblyBlockTags.latestVersion;
			}

			return this.delegate.values();
		}

		public Collection<Tag.Entry<Block>> entries() {
			if (this.version != AssemblyBlockTags.latestVersion) {
				this.delegate = BlockTags.getContainer().getOrCreate(this.getId());
				this.version = AssemblyBlockTags.latestVersion;
			}

			return this.delegate.entries();
		}
	}
}
