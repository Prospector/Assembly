package teamreborn.assembly.util.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

import java.util.function.Function;
import java.util.function.Supplier;

public class BlockWithEntitySettings {
	public static final Function<MachinePlacementContext, BlockState> HIT_FACING = (context) -> context.block.getDefaultState().with(context.property, context.getSide().getOpposite());
	public static final Function<MachinePlacementContext, BlockState> PLAYER_FACING = (context) -> context.block.getDefaultState().with(context.property, context.getPlayerFacing().getOpposite());
	public static final Function<MachinePlacementContext, BlockState> PLAYER_FACING_HORIZONTAL = (context) -> context.block.getDefaultState().with(context.property, context.getPlayerFacing().getOpposite());

	protected boolean hasActive = true;
	protected boolean hasFacing = true;
	protected DirectionProperty facingProperty = Properties.HORIZONTAL_FACING;
	protected Direction defaultFacing = Direction.NORTH;
	protected Function<MachinePlacementContext, BlockState> placementLogic = PLAYER_FACING_HORIZONTAL;
	protected Supplier<BlockEntity> blockEntity;

	public BlockWithEntitySettings(Supplier<BlockEntity> blockEntity) {
		this.blockEntity = blockEntity;
	}

	public BlockWithEntitySettings withoutActive() {
		this.hasActive = false;
		return this;
	}

	public BlockWithEntitySettings withoutFacing() {
		this.hasFacing = false;
		return this;
	}

	public BlockWithEntitySettings setHorizontalFacing() {
		return setFacing(Properties.HORIZONTAL_FACING, Direction.NORTH, PLAYER_FACING_HORIZONTAL);
	}

	public BlockWithEntitySettings setFacingPlayerPlacement() {
		return setFacing(Properties.FACING, Direction.NORTH, PLAYER_FACING);
	}

	public BlockWithEntitySettings setFacingHitPlacement() {
		return setFacing(Properties.FACING, Direction.NORTH, HIT_FACING);
	}

	public BlockWithEntitySettings setFacing(DirectionProperty property, Direction defaultFacing, Function<MachinePlacementContext, BlockState> placementLogic) {
		this.hasFacing = true;
		this.facingProperty = property;
		this.defaultFacing = defaultFacing;
		this.placementLogic = placementLogic;
		return this;
	}

	public BlockEntity createBlockEntity() {
		return blockEntity.get();
	}

	public boolean hasActive() {
		return hasActive;
	}

	public boolean hasFacing() {
		return hasFacing;
	}

	public Function<MachinePlacementContext, BlockState> getPlacementLogic() {
		return placementLogic;
	}

	public DirectionProperty getFacingProperty() {
		return facingProperty;
	}

	public Direction getDefaultFacing() {
		return defaultFacing;
	}
}
