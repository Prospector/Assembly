package teamreborn.assembly.util.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.FacingProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Facing;

import java.util.function.Function;
import java.util.function.Supplier;

public class BlockWithEntitySettings {
	public static final Function<MachinePlacementContext, BlockState> HIT_FACING = (context) -> context.block.getDefaultState().with(context.property, context.getFacing().getOpposite());
	public static final Function<MachinePlacementContext, BlockState> PLAYER_FACING = (context) -> context.block.getDefaultState().with(context.property, context.getPlayerFacing().getOpposite());
	public static final Function<MachinePlacementContext, BlockState> PLAYER_FACING_HORIZONTAL = (context) -> context.block.getDefaultState().with(context.property, context.getPlayerHorizontalFacing().getOpposite());

	protected boolean hasActive = true;
	protected boolean hasFacing = true;
	protected FacingProperty facingProperty = Properties.FACING_HORIZONTAL;
	protected Facing defaultFacing = Facing.NORTH;
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
		return setFacing(Properties.FACING_HORIZONTAL, Facing.NORTH, PLAYER_FACING_HORIZONTAL);
	}

	public BlockWithEntitySettings setFacingPlayerPlacement() {
		return setFacing(Properties.FACING, Facing.NORTH, PLAYER_FACING);
	}

	public BlockWithEntitySettings setFacingHitPlacement() {
		return setFacing(Properties.FACING, Facing.NORTH, HIT_FACING);
	}

	public BlockWithEntitySettings setFacing(FacingProperty property, Facing defaultFacing, Function<MachinePlacementContext, BlockState> placementLogic) {
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

	public FacingProperty getFacingProperty() {
		return facingProperty;
	}

	public Facing getDefaultFacing() {
		return defaultFacing;
	}
}
