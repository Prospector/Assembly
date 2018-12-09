package teamreborn.assembly.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Facing;
import net.minecraft.util.shape.VoxelShapeContainer;
import net.minecraft.world.BlockView;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import prospector.silk.block.SilkBlockWithEntity;
import prospector.silk.fluid.FluidContainer;
import teamreborn.assembly.api.SapSource;
import teamreborn.assembly.blockentity.TreeTapBlockEntity;
import teamreborn.assembly.util.block.AssemblyProperties;

import java.util.Map;

public class TreeTapBlock extends SilkBlockWithEntity {
	private static final Map<Facing, VoxelShapeContainer> BOUNDING_SHAPES = Maps.newEnumMap(ImmutableMap.of(
		Facing.NORTH, Block.createCubeShape(6.5D, 1.0D, 0.0D, 10.5D, 5.5D, 6.5D),
		Facing.SOUTH, Block.createCubeShape(16 - 6.5D, 1.0D, 16 - 0.0D, 16 - 10.5D, 5.5D, 16 - 6.5D).method_1097(),
		Facing.WEST, Block.createCubeShape(0.0D, 1.0D, 5.5D, 6.5D, 5.5D, 9.5D),
		Facing.EAST, Block.createCubeShape(16 - 0.0D, 1.0D, 16 - 5.5D, 16 - 6.5D, 5.5D, 16 - 9.5D)));

	public TreeTapBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(Properties.FACING_HORIZONTAL, Facing.NORTH).with(AssemblyProperties.POURING, false));
	}

	@Override
	public VoxelShapeContainer getBoundingShape(BlockState state, BlockView world, BlockPos pos) {
		return getBoundingShapeForState(state);
	}

	public static VoxelShapeContainer getBoundingShapeForState(BlockState state) {
		return BOUNDING_SHAPES.get(state.get(Properties.FACING_HORIZONTAL));
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos) {
		super.neighborUpdate(state, world, pos, neighborBlock, neighborPos);
		if (!(world.getBlockEntity(pos.down()) instanceof FluidContainer)) {
			if (state.get(AssemblyProperties.POURING)) {
				world.setBlockState(pos, state.with(AssemblyProperties.POURING, false));
			}
		}
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(Properties.FACING_HORIZONTAL, AssemblyProperties.POURING);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new TreeTapBlockEntity();
	}

	@Override
	public boolean canPlaceAt(BlockState state, ViewableWorld world, BlockPos pos) {
		Facing side = state.get(Properties.FACING_HORIZONTAL);
		BlockState placedOn = world.getBlockState(pos.offset(side));
		Block placedOnBlock = placedOn.getBlock();
		return placedOnBlock instanceof SapSource && ((SapSource) placedOnBlock).isSideSapSource(world, pos, placedOn, side.getOpposite());
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		Facing direction = context.getFacing().getOpposite();
		return Facing.class_2353.HORIZONTAL.contains(direction) ? getDefaultState().with(Properties.FACING_HORIZONTAL, direction) : null;
	}

	@Override
	public BlockState applyRotation(BlockState state, Rotation rotation) {
		return state.with(Properties.FACING_HORIZONTAL, rotation.method_10503(state.get(Properties.FACING_HORIZONTAL)));
	}

	@Override
	public BlockState applyMirror(BlockState state, Mirror mirror) {
		return state.applyRotation(mirror.getRotation(state.get(Properties.FACING_HORIZONTAL)));
	}
}
