package team.reborn.assembly.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import team.reborn.assembly.blockentity.BoilerBlockEntity;

import javax.annotation.Nullable;

public class BoilerChamberBlock extends Block {

	public static final VoxelShape SHAPE;

	public BoilerChamberBlock(Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext ePos) {
		return SHAPE;
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		super.onPlaced(world, pos, state, placer, itemStack);
		if (!world.isClient) {
			BlockPos downPos = pos.down();
			updateBoiler(world, downPos, world.getBlockState(downPos), world.getBlockEntity(downPos));
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean moved) {
		super.neighborUpdate(state, world, pos, block, neighborPos, moved);
		if (!world.isClient) {
			if (neighborPos.equals(pos.down())) {
				updateBoiler(world, neighborPos, world.getBlockState(neighborPos), world.getBlockEntity(neighborPos));
			}
		}
	}

	public void updateBoiler(World world, BlockPos pos, BlockState state, BlockEntity blockEntity) {
		if (state.getBlock() == Blocks.FURNACE && blockEntity instanceof FurnaceBlockEntity) {
			FurnaceBlockEntity furnace = ((FurnaceBlockEntity) blockEntity);
			ItemStack fuelStack = furnace.getInvStack(1).copy();
			boolean empty = true;
			for (int i = 0; i < furnace.getInvSize(); i++) {
				if (i != 1 && !furnace.getInvStack(i).isEmpty()) {
					empty = false;
					break;
				}
			}
			if (empty) {
				furnace.setInvStack(1, ItemStack.EMPTY);
				world.setBlockState(pos, AssemblyBlocks.BOILER.getDefaultState().with(BoilerBlock.FACING, state.get(FurnaceBlock.FACING)));
				if (world.getBlockEntity(pos) instanceof BoilerBlockEntity) {
					((BoilerBlockEntity) world.getBlockEntity(pos)).setInvStack(0, fuelStack);
				}
			}

		}
	}

	static {
		VoxelShape bottom = Block.createCuboidShape(2, 0, 2, 14, 3, 14);
		VoxelShape middle = Block.createCuboidShape(3, 3, 3, 13, 13, 13);
		VoxelShape top = Block.createCuboidShape(2, 13, 2, 14, 16, 14);
		SHAPE = VoxelShapes.union(bottom, middle, top);
	}
}
