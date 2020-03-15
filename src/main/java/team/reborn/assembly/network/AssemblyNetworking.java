package team.reborn.assembly.network;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.container.Container;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import team.reborn.assembly.Assembly;
import team.reborn.assembly.blockentity.FluidBarrelBlockEntity;
import team.reborn.assembly.menu.builder.ExtendedMenuListener;
import team.reborn.assembly.util.ObjectBufUtils;

public class AssemblyNetworking {

	private static final Identifier SYNC_BARREL_FLUID = new Identifier(Assembly.MOD_ID, "sync_barrel_fluid");
	private static final Identifier REQUEST_BARREL_SYNC = new Identifier(Assembly.MOD_ID, "request_barrel_sync");
	private static final Identifier CONTAINER_SYNC = new Identifier(Assembly.MOD_ID, "container_sync");

	public static void register() {
		ClientSidePacketRegistry.INSTANCE.register(SYNC_BARREL_FLUID, (context, buf) -> {
			BlockPos pos = buf.readBlockPos();
			CompoundTag fluidVolumeTag = buf.readCompoundTag();
			FluidVolume instance = null;
			if (fluidVolumeTag != null) {
				instance = FluidVolume.fromTag(fluidVolumeTag);
				if (context.getPlayer() != null && context.getPlayer().getEntityWorld() != null) {
					BlockEntity blockEntity = context.getPlayer().getEntityWorld().getBlockEntity(pos);
					if (blockEntity instanceof FluidBarrelBlockEntity) {
						((FluidBarrelBlockEntity) blockEntity).getTank().setInvFluid(0, instance, Simulation.ACTION);
					}
				}
			}
		});
		ServerSidePacketRegistry.INSTANCE.register(REQUEST_BARREL_SYNC, (context, buf) -> {
			BlockPos pos = buf.readBlockPos();
			BlockEntity blockEntity = context.getPlayer().getEntityWorld().getBlockEntity(pos);
			if (blockEntity instanceof FluidBarrelBlockEntity) {
				syncBarrelFluid((FluidBarrelBlockEntity) blockEntity, (ServerPlayerEntity) context.getPlayer());
			}
		});
		ClientSidePacketRegistry.INSTANCE.register(CONTAINER_SYNC, (context, buf) -> {
			Screen gui = MinecraftClient.getInstance().currentScreen;
			if (gui instanceof ContainerScreen) {
				Container container = ((ContainerScreen) gui).getContainer();
				if (container instanceof ExtendedMenuListener) {
					((ExtendedMenuListener) container).handleObject(buf.readInt(), ObjectBufUtils.readObject(buf));
				}
			}
		});
	}

	public static void syncBarrelFluid(FluidBarrelBlockEntity woodenBarrel, ServerPlayerEntity player) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeBlockPos(woodenBarrel.getPos());
		buf.writeCompoundTag(woodenBarrel.getTank().getInvFluid(0).toTag());
		player.networkHandler.sendPacket(new CustomPayloadS2CPacket(SYNC_BARREL_FLUID, buf));
	}

	@Environment(EnvType.CLIENT)
	public static void requestBarrelSync(FluidBarrelBlockEntity woodenBarrel) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeBlockPos(woodenBarrel.getPos());
		MinecraftClient.getInstance().getNetworkHandler().getConnection().send(new CustomPayloadC2SPacket(REQUEST_BARREL_SYNC, buf));
	}

	public static void syncContainer(ServerPlayerEntity player, int id, Object value) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeInt(id);
		ObjectBufUtils.writeObject(value, buf);
		player.networkHandler.sendPacket(new CustomPayloadS2CPacket(CONTAINER_SYNC, buf));
	}
}
