package teamreborn.assembly.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.FacingProperty;
import net.minecraft.util.math.Facing;

public abstract class MachineBaseBlock extends BlockWithEntity {
	public static final FacingProperty FACING = HorizontalFacingBlock.field_11177;
	public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

	protected MachineBaseBlock(Builder builder) {
		super(builder);
		this.setDefaultState(this.stateFactory.getDefaultState()
			.with(FACING, Facing.NORTH)
			.with(ACTIVE, false)
		);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.with(FACING, ACTIVE);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext var1) {
		return this.getDefaultState().with(FACING, var1.getPlayerHorizontalFacing().getOpposite());
	}
}
