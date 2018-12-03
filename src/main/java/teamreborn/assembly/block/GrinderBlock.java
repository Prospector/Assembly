package teamreborn.assembly.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.ListenerContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Facing;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import teamreborn.assembly.blockentity.GrinderBlockEntity;
import teamreborn.assembly.container.AssemblyContainerHelper;

public class GrinderBlock extends MachineBaseBlock {
	public GrinderBlock(Builder builder) {
		super(builder);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new GrinderBlockEntity();
	}

	@Override
	public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, Facing facing, float v, float v1, float v2) {
		if(!world.isRemote){
			GrinderBlockEntity grinderBlockEntity = (GrinderBlockEntity) world.getBlockEntity(blockPos);
			AssemblyContainerHelper.openGui(grinderBlockEntity, blockPos, (ServerPlayerEntity) playerEntity);
		}
		return true;
	}
}
