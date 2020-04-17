package team.reborn.assembly.block;

import alexiil.mc.lib.attributes.AttributeProvider;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import team.reborn.assembly.block.enums.ValveState;
import team.reborn.assembly.blockentity.AssemblyBlockEntities;
import team.reborn.assembly.util.AssemblyConstants;
import team.reborn.assembly.util.math.ShapeUtil;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class SpigotBlock extends HorizontalFacingBlock implements BlockEntityProvider {
	public static final EnumProperty<ValveState> VALVE = AssemblyConstants.Properties.VALVE;
	public static final BooleanProperty POURING = AssemblyConstants.Properties.POURING;
	public static final IntProperty EXTENSION = AssemblyConstants.Properties.EXTENSION;

	private static final Map<BlockState, VoxelShape> SHAPE_CACHE = new HashMap<>();
	private static final VoxelShape FAUCET = Block.createCuboidShape(6, 5, 5, 10, 9, 9);
	private static final VoxelShape HANDLE_CONNECTOR = Block.createCuboidShape(7.5, 9, 6.5, 8.5, 10, 7.5);
	private static final VoxelShape PIPE = Block.createCuboidShape(6, 6, 0, 10, 9, 5);
	private static final VoxelShape CLOSED_RIGHT_HANDLE = Block.createCuboidShape(7, 10, 6, 14, 11, 8);
	private static final VoxelShape CLOSED_LEFT_HANDLE = Block.createCuboidShape(2, 10, 6, 9, 11, 8);
	private static final VoxelShape OPEN_HANDLE = Block.createCuboidShape(7, 10, 6, 9, 11, 13);

	private static final Int2ObjectMap<VoxelShape> EXTENSION_SHAPES = new Int2ObjectOpenHashMap<>(5);
	private static final Int2ObjectMap<VoxelShape> EXTENSION_SHAPES_DETECTION = new Int2ObjectOpenHashMap<>(5);

	public SpigotBlock(Settings settings) {
		super(settings);
		this.setDefaultState(getStateManager().getDefaultState().with(POURING, false).with(VALVE, ValveState.OPEN).with(EXTENSION, 0));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ePos) {
		return SHAPE_CACHE.getOrDefault(state, cacheOutlineShape(state));
	}

	public VoxelShape cacheOutlineShape(BlockState state) {
		VoxelShape handle = VoxelShapes.empty();
		switch (state.get(VALVE)) {
			case OPEN: {
				handle = OPEN_HANDLE;
				break;
			}
			case CLOSED_LEFT: {
				handle = CLOSED_LEFT_HANDLE;
				break;
			}
			case CLOSED_RIGHT: {
				handle = CLOSED_RIGHT_HANDLE;
				break;
			}
		}
		VoxelShape shape = ShapeUtil.rotate90(VoxelShapes.union(FAUCET, PIPE, HANDLE_CONNECTOR, EXTENSION_SHAPES.getOrDefault(state.get(EXTENSION).intValue(), VoxelShapes.empty()), handle), Direction.NORTH, state.get(FACING), Direction.Axis.Y);
		SHAPE_CACHE.put(state, shape);
		return shape;
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		Direction side = context.getSide().getOpposite();
		if (side.getHorizontal() != -1) {
			World world = context.getWorld();
			BlockState state = getDefaultState();
			BlockPos attachedPos = context.getBlockPos().offset(side);
			BlockState attachedState = world.getBlockState(attachedPos);
			int extension = 0;
			VoxelShape outlineShape = attachedState.getOutlineShape(world, attachedPos);
			if (outlineShape != null && !outlineShape.isEmpty()) {
				for (Int2ObjectMap.Entry<VoxelShape> shapeEntry : EXTENSION_SHAPES_DETECTION.int2ObjectEntrySet()) {
					if (!VoxelShapes.combineAndSimplify(ShapeUtil.rotate90(shapeEntry.getValue(), Direction.NORTH, side), outlineShape, BooleanBiFunction.AND).simplify().isEmpty()) {
						extension = shapeEntry.getIntKey() - 1;
						break;
					}
				}
			}
			if (extension < 0) {
				extension = 0;
			} else if (extension > 4) {
				extension = 4;
			}
			return state.with(FACING, side).with(EXTENSION, extension);
		} else {
			return null;
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState
			neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		return facing == state.get(FACING) && !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return world.getBlockState(pos.offset(state.get(FACING))).getBlock() instanceof AttributeProvider;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		return AssemblyBlockEntities.SPIGOT.instantiate();
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand
			hand, BlockHitResult hit) {
		if (!world.isClient) {
			if (state.get(VALVE).isOpen()) {
				boolean away = player.getMainArm() == Arm.LEFT;
				Direction side = player.getHorizontalFacing().getOpposite();
				Direction direction = state.get(FACING);
				if (away) {
					direction = direction.getOpposite();
				}
				if (direction == side.rotateYClockwise()) {
					world.setBlockState(pos, state.with(VALVE, ValveState.CLOSED_LEFT));
				} else if (direction == side.rotateYCounterclockwise()) {
					world.setBlockState(pos, state.with(VALVE, ValveState.CLOSED_RIGHT));
				} else {
					world.setBlockState(pos, state.with(VALVE, ValveState.randomClosed(player.getRandom())));
				}
			} else {
				world.setBlockState(pos, state.with(VALVE, ValveState.OPEN));
			}
		}
		return ActionResult.SUCCESS;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, VALVE, POURING, EXTENSION);
	}

	static {
		EXTENSION_SHAPES.put(1, Block.createCuboidShape(6, 6, 0, 10, 9, -1));
		EXTENSION_SHAPES.put(2, Block.createCuboidShape(6, 6, 0, 10, 9, -2));
		EXTENSION_SHAPES.put(3, Block.createCuboidShape(6, 6, 0, 10, 9, -3));
		EXTENSION_SHAPES.put(4, Block.createCuboidShape(6, 6, 0, 10, 9, -4));

		EXTENSION_SHAPES_DETECTION.put(1, Block.createCuboidShape(6, 6, 0, 10, 9, 1));
		EXTENSION_SHAPES_DETECTION.put(2, Block.createCuboidShape(6, 6, 0, 10, 9, 2));
		EXTENSION_SHAPES_DETECTION.put(3, Block.createCuboidShape(6, 6, 0, 10, 9, 3));
		EXTENSION_SHAPES_DETECTION.put(4, Block.createCuboidShape(6, 6, 0, 10, 9, 4));
		EXTENSION_SHAPES_DETECTION.put(5, Block.createCuboidShape(6, 6, 0, 10, 9, 5));
	}
}
