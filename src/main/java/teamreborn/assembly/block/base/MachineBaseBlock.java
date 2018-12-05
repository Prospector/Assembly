package teamreborn.assembly.block.base;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Facing;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import prospector.silk.block.SilkBlockWithEntity;
import teamreborn.assembly.container.AssemblyContainerHelper;
import teamreborn.assembly.container.FabricContainerProvider;
import teamreborn.assembly.util.block.MachineBlockProperties;
import teamreborn.assembly.util.block.MachinePlacementContext;

public abstract class MachineBaseBlock extends SilkBlockWithEntity {
	public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
	public final MachineBlockProperties machineProperties;

	protected MachineBaseBlock(MachineBlockProperties machineProperties, Builder builder) {
		super(builder);
		this.machineProperties = machineProperties;
		setDefaultState(stateFactory.getDefaultState());
		if (machineProperties.hasActive()) {
			this.setDefaultState(getDefaultState().with(ACTIVE, false));
		}
		if (machineProperties.hasFacing()) {
			this.setDefaultState(getDefaultState().with(machineProperties.getFacingProperty(), machineProperties.getDefaultFacing()));
		}
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return machineProperties.createBlockEntity();
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		if (machineProperties.hasActive()) {
			builder.with(ACTIVE);
		}
		if (machineProperties.hasFacing()) {
			builder.with(machineProperties.getFacingProperty());
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		if (machineProperties.hasFacing()) {
			return machineProperties.getPlacementLogic().apply(new MachinePlacementContext(this, machineProperties.getFacingProperty(), context));
		}
		return super.getPlacementState(context);
	}

	@Override
	public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Facing facing, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof FabricContainerProvider) {
				AssemblyContainerHelper.openGui((FabricContainerProvider) blockEntity, pos, (ServerPlayerEntity) player);
				return true;
			}
		}
		return false;
	}
}
