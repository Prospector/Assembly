package teamreborn.assembly.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.container.Container;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import reborncore.common.fluid.container.FluidInstance;
import reborncore.common.fluid.container.GenericFluidContainer;
import teamreborn.assembly.Assembly;
import teamreborn.assembly.blockentity.WoodenBarrelBlockEntity;
import teamreborn.assembly.container.builder.IExtendedContainerListener;
import teamreborn.assembly.util.ObjectBufUtils;

public class AssemblyNetworking implements ModInitializer {

	public static final Identifier SYNC_BARREL_FLUID = new Identifier(Assembly.MOD_ID, "sync_barrel_fluid");
	public static final Identifier REQUEST_BARREL_SYNC = new Identifier(Assembly.MOD_ID, "request_barrel_sync");
	public static final Identifier CONTAINER_SYNC = new Identifier(Assembly.MOD_ID, "container_sync");

	@Override
	public void onInitialize() {
		ClientSidePacketRegistry.INSTANCE.register(SYNC_BARREL_FLUID, (packetContext, packetByteBuf) -> {
			BlockPos pos = packetByteBuf.readBlockPos();
			FluidInstance instance = new FluidInstance(packetByteBuf.readCompoundTag());
			if (packetContext.getPlayer() != null && packetContext.getPlayer().getEntityWorld() != null) {
				BlockEntity blockEntity = packetContext.getPlayer().getEntityWorld().getBlockEntity(pos);
				if (blockEntity instanceof WoodenBarrelBlockEntity) {
					((GenericFluidContainer<Direction>) blockEntity).setFluid(null, instance);
				}
			}
		});
		ServerSidePacketRegistry.INSTANCE.register(REQUEST_BARREL_SYNC, (packetContext, packetByteBuf) -> {
			BlockPos pos = packetByteBuf.readBlockPos();
			BlockEntity blockEntity = packetContext.getPlayer().getEntityWorld().getBlockEntity(pos);
			if (blockEntity instanceof WoodenBarrelBlockEntity) {
				syncBarrelFluid((WoodenBarrelBlockEntity) blockEntity, (ServerPlayerEntity) packetContext.getPlayer());
			}
		});
		ClientSidePacketRegistry.INSTANCE.register(CONTAINER_SYNC, (packetContext, packetByteBuf) -> {
			Screen gui = MinecraftClient.getInstance().currentScreen;
			if (gui instanceof AbstractContainerScreen) {
				Container container = ((AbstractContainerScreen) gui).getContainer();
				if (container instanceof IExtendedContainerListener) {
					((IExtendedContainerListener) container).handleObject(packetByteBuf.readInt(), ObjectBufUtils.readObject(packetByteBuf));
				}
			}
		});
	}

	public static void syncBarrelFluid(WoodenBarrelBlockEntity woodenBarrel, ServerPlayerEntity player) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeBlockPos(woodenBarrel.getPos());
		buf.writeCompoundTag(woodenBarrel.getFluidInstance(null).write());
		player.networkHandler.sendPacket(new CustomPayloadS2CPacket(SYNC_BARREL_FLUID, buf));
	}

	@Environment(EnvType.CLIENT)
	public static void requestBarrelSync(WoodenBarrelBlockEntity woodenBarrel) {
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
