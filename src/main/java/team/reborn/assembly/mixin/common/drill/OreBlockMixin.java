package team.reborn.assembly.mixin.common.drill;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.OreBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.reborn.assembly.mixintf.DrillableOre;

import javax.annotation.Nullable;

@Mixin(OreBlock.class)
public class OreBlockMixin extends Block implements DrillableOre {
	public OreBlockMixin(Settings settings) {
		super(settings);
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void onInit(CallbackInfo info) {
		this.setDefaultState(getDefaultState().with(PLACED, false));
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return modifyPlacementState(super.getPlacementState(ctx));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(appendProperty(builder));
	}
}
