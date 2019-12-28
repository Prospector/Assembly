package team.reborn.assembly.block;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.AttributeProvider;
import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.GroupedFluidInv;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.container.Container;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import team.reborn.assembly.attributes.IOFluidContainer;
import team.reborn.assembly.blockentity.AssemblyBlockEntities;
import team.reborn.assembly.blockentity.BoilerBlockEntity;
import team.reborn.assembly.container.AssemblyContainers;

import javax.annotation.Nullable;

public class BoilerBlock extends HorizontalFacingBlock implements BlockEntityProvider, AttributeProvider {
	public static final BooleanProperty LIT = Properties.LIT;

	public BoilerBlock(Settings settings) {
		super(settings);
		setDefaultState(this.getStateManager().getDefaultState().with(LIT, false));
	}

	@Override
	public void addAllAttributes(World world, BlockPos pos, BlockState state, AttributeList<?> to) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof BoilerBlockEntity) {
			BoilerBlockEntity boiler = (BoilerBlockEntity) be;
			to.offer(boiler.getInputTank().getInsertable().getPureInsertable(), VoxelShapes.fullCube());
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			//Fluid filling/draining
			ItemStack stack = player.getStackInHand(hand);
			BlockEntity boiler = world.getBlockEntity(pos);
			boolean filledTank = false;
			if (boiler instanceof BoilerBlockEntity) {
				//Todo: fill tank
			}
			if (!filledTank) {
				ContainerProviderRegistry.INSTANCE.openContainer(AssemblyContainers.BOILER, player, buf -> buf.writeBlockPos(pos));
			}
			return ActionResult.SUCCESS;
		}
	}

	public int getLuminance(BlockState state) {
		return state.get(LIT) ? super.getLuminance(state) : 0;
	}

	public BlockState getPlacementState(ItemPlacementContext context) {
		return this.getDefaultState().with(FACING, context.getPlayerFacing().getOpposite());
	}

	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (stack.hasCustomName()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof AbstractFurnaceBlockEntity) {
				((AbstractFurnaceBlockEntity) blockEntity).setCustomName(stack.getName());
			}
		}
	}

	public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof BoilerBlockEntity) {
				ItemScatterer.spawn(world, pos, (Inventory) blockEntity);
				world.updateHorizontalAdjacent(pos, this);
			}

			super.onBlockRemoved(state, world, pos, newState, moved);
		}
	}

	public boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return Container.calculateComparatorOutput(world.getBlockEntity(pos));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(LIT, FACING);
	}

	@Override
	public boolean hasBlockEntity() {
		return true;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		return AssemblyBlockEntities.BOILER.instantiate();
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean moved) {
		super.neighborUpdate(state, world, pos, block, neighborPos, moved);
		if (pos.up().equals(neighborPos)) {
			if (world.getBlockState(neighborPos).getBlock() != AssemblyBlocks.BOILER_CHAMBER) {
				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity instanceof BoilerBlockEntity) {
					ItemStack fuelStack = ((BoilerBlockEntity) blockEntity).getInvStack(0).copy();
					((BoilerBlockEntity) blockEntity).setInvStack(0, ItemStack.EMPTY);
					Text customName = ((BoilerBlockEntity) blockEntity).getCustomName();
					world.setBlockState(pos, Blocks.FURNACE.getDefaultState().with(AbstractFurnaceBlock.FACING, state.get(FACING)));
					blockEntity = world.getBlockEntity(pos);
					if (blockEntity instanceof FurnaceBlockEntity) {
						((FurnaceBlockEntity) blockEntity).setInvStack(1, fuelStack);
						((FurnaceBlockEntity) blockEntity).setCustomName(customName);
					}
				}
			}
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {

		return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
	}

	@Override
	public Item asItem() {
		return Blocks.FURNACE.asItem();
	}
}
