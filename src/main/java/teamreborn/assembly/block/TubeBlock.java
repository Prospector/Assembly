package teamreborn.assembly.block;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import reborncore.common.fluid.container.GenericFluidContainer;
import teamreborn.assembly.api.AttachableFluidContainer;
import teamreborn.assembly.block.base.AssemblyBlockWithEntity;
import teamreborn.assembly.util.NullableDirection;
import teamreborn.assembly.util.block.AssemblyProperties;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class TubeBlock extends AssemblyBlockWithEntity {
	public static final VoxelShape CORE_SHAPE = Block.createCuboidShape(4, 4, 4, 16 - 4, 16 - 4, 16 - 4);
	public static final EnumMap<Direction, VoxelShape> CONNECTION_SHAPES = new EnumMap<>(Direction.class);
	public static final Map<BlockState, VoxelShape> STATE_SHAPE_CACHE = new HashMap<>();

	static {
		CONNECTION_SHAPES.put(Direction.NORTH, Block.createCuboidShape(4, 4, 0, 12, 12, 4));
		CONNECTION_SHAPES.put(Direction.SOUTH, Block.createCuboidShape(4, 4, 16, 12, 12, 4));
		CONNECTION_SHAPES.put(Direction.EAST, Block.createCuboidShape(16, 4, 4, 4, 12, 12));
		CONNECTION_SHAPES.put(Direction.WEST, Block.createCuboidShape(0, 4, 4, 4, 12, 12));
		CONNECTION_SHAPES.put(Direction.UP, Block.createCuboidShape(4, 16, 4, 12, 4, 12));
		CONNECTION_SHAPES.put(Direction.DOWN, Block.createCuboidShape(4, 0, 4, 12, 4, 12));
	}

	public static final EnumProperty<NullableDirection> CONNECTION_1 = AssemblyProperties.CONNECTION_1;
	public static final EnumProperty<NullableDirection> CONNECTION_2 = AssemblyProperties.CONNECTION_2;

	public TubeBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(CONNECTION_1, NullableDirection.NONE).with(CONNECTION_2, NullableDirection.NONE));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, EntityContext entityPosition) {
		return getCullingShape(state, world, pos);
	}

	@Override
	public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
		if (STATE_SHAPE_CACHE.containsKey(state)) {
			return STATE_SHAPE_CACHE.get(state);
		}

		VoxelShape shape = CORE_SHAPE;
		NullableDirection d1 = state.get(CONNECTION_1);
		if (d1 != NullableDirection.NONE) {
			shape = VoxelShapes.union(shape, CONNECTION_SHAPES.get(d1.getDirection()));
		}
		NullableDirection d2 = state.get(CONNECTION_2);
		if (d2 != NullableDirection.NONE) {
			shape = VoxelShapes.union(shape, CONNECTION_SHAPES.get(d2.getDirection()));
		}

		STATE_SHAPE_CACHE.put(state, shape);
		return getCullingShape(state, world, pos);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder.add(CONNECTION_1, CONNECTION_2));
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if (getAvailableConnection(state, direction) != null) {
			BlockState connected = tryConnect(world, pos, direction, state);
			if (connected != null) {
				return connected;
			}
		}
		BlockState disconnected = tryDisconnect(world, pos, direction, state);
		if (disconnected != null) {
			return disconnected;
		}
		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		BlockState state = super.getPlacementState(context);
		for (Direction side : Direction.values()) {
			BlockState connected = tryConnect(context.getWorld(), context.getBlockPos(), side, state);
			if (connected != null) {
				state = connected;
			}
		}
		return state;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView var1) {
		return null;
//		return AssemblyBlockEntities.TUBE.instantiate();
	}

	public static EnumProperty<NullableDirection> getAvailableConnection(BlockState state, Direction direction) {
		if (state.get(CONNECTION_1) == NullableDirection.NONE && state.get(CONNECTION_2) != NullableDirection.get(direction)) {
			return CONNECTION_1;
		} else if (state.get(CONNECTION_2) == NullableDirection.NONE && state.get(CONNECTION_1) != NullableDirection.get(direction)) {
			return CONNECTION_2;
		} else {
			return null;
		}
	}

	public static EnumProperty<NullableDirection> getConnection(BlockState state, Direction direction) {
		if (state.get(CONNECTION_1) == NullableDirection.get(direction)) {
			return CONNECTION_1;
		} else if (state.get(CONNECTION_2) == NullableDirection.get(direction)) {
			return CONNECTION_2;
		} else {
			return null;
		}
	}

	public static BlockState tryConnect(BlockView world, BlockPos pos, Direction direction, BlockState state) {
		BlockEntity blockEntity = world.getBlockEntity(pos.offset(direction));
		if (blockEntity instanceof GenericFluidContainer) {
			if (blockEntity instanceof AttachableFluidContainer) {
				if (!((AttachableFluidContainer) blockEntity).canAttach(direction.getOpposite())) {
					return null;
				}
			}
			EnumProperty<NullableDirection> property = getAvailableConnection(state, direction);
			if (property != null) {
				return state.with(property, NullableDirection.get(direction));
			}
		}
		return null;
	}

	public static BlockState tryDisconnect(BlockView world, BlockPos pos, Direction direction, BlockState state) {
		if (!(world.getBlockEntity(pos.offset(direction)) instanceof GenericFluidContainer)) {
			EnumProperty<NullableDirection> property = getConnection(state, direction);
			if (property != null) {
				return state.with(property, NullableDirection.NONE);
			}
		}
		return null;
	}
}
