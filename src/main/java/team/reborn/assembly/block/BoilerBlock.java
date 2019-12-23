package team.reborn.assembly.block;

import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.Container;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import team.reborn.assembly.blockentity.AssemblyBlockEntities;
import team.reborn.assembly.blockentity.BoilerBlockEntity;
import team.reborn.assembly.container.AssemblyContainers;

import javax.annotation.Nullable;

public class BoilerBlock extends HorizontalFacingBlock implements BlockEntityProvider {
	public static final BooleanProperty LIT = Properties.LIT;

	public BoilerBlock(Settings settings) {
		super(settings);
		setDefaultState(this.getStateManager().getDefaultState().with(LIT, false));
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			ContainerProviderRegistry.INSTANCE.openContainer(AssemblyContainers.BOILER, player, buf -> buf.writeBlockPos(pos));
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
}
