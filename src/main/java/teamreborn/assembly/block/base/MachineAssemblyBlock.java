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
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import prospector.silk.block.SilkBlockWithEntity;
import teamreborn.assembly.container.AssemblyContainerHelper;
import teamreborn.assembly.container.FabricContainerProvider;
import teamreborn.assembly.util.block.BlockWithEntitySettings;
import teamreborn.assembly.util.block.MachinePlacementContext;

public abstract class MachineAssemblyBlock extends SilkBlockWithEntity {
	public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
	public BlockWithEntitySettings machineSettings;

	public MachineAssemblyBlock(Settings settings) {
		super(settings);
		setDefaultState(stateFactory.getDefaultState());
		if (machineSettings.hasActive()) {
			this.setDefaultState(getDefaultState().with(ACTIVE, false));
		}
		if (machineSettings.hasFacing()) {
			this.setDefaultState(getDefaultState().with(machineSettings.getFacingProperty(), machineSettings.getDefaultFacing()));
		}
	}

	public abstract BlockWithEntitySettings createMachineSettings();

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return machineSettings.createBlockEntity();
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		if (machineSettings == null) {
			this.machineSettings = createMachineSettings();
		}
		if (machineSettings.hasActive()) {
			builder.with(ACTIVE);
		}
		if (machineSettings.hasFacing()) {
			builder.with(machineSettings.getFacingProperty());
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		if (machineSettings.hasFacing()) {
			return machineSettings.getPlacementLogic().apply(new MachinePlacementContext(this, machineSettings.getFacingProperty(), context));
		}
		return super.getPlacementState(context);
	}

	@Override
	public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction facing, float hitX, float hitY, float hitZ) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof FabricContainerProvider) {
			if (!world.isRemote) {
				AssemblyContainerHelper.openGui((FabricContainerProvider) blockEntity, pos, (ServerPlayerEntity) player);
			}
			return true;
		}
		return false;
	}
}
