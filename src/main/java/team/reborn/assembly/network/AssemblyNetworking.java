package team.reborn.assembly.network;

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
import team.reborn.assembly.blockentity.WoodenBarrelBlockEntity;
import team.reborn.assembly.container.builder.ExtendedMenuListener;
import team.reborn.assembly.util.ObjectBufUtils;
import team.reborn.assembly.Assembly;

public class AssemblyNetworking {

	public static final Identifier SYNC_BARREL_FLUID = new Identifier(Assembly.MOD_ID, "sync_barrel_fluid");
	public static final Identifier REQUEST_BARREL_SYNC = new Identifier(Assembly.MOD_ID, "request_barrel_sync");
	public static final Identifier CONTAINER_SYNC = new Identifier(Assembly.MOD_ID, "container_sync");

	public static void register() {
		ClientSidePacketRegistry.INSTANCE.register(SYNC_BARREL_FLUID, (context, buf) -> {
			BlockPos pos = buf.readBlockPos();
			FluidInstance instance = new FluidInstance(buf.readCompoundTag());
			if (context.getPlayer() != null && context.getPlayer().getEntityWorld() != null) {
				BlockEntity blockEntity = context.getPlayer().getEntityWorld().getBlockEntity(pos);
				if (blockEntity instanceof WoodenBarrelBlockEntity) {
					((GenericFluidContainer<Direction>) blockEntity).setFluid(null, instance);
				}
			}
		});
		ServerSidePacketRegistry.INSTANCE.register(REQUEST_BARREL_SYNC, (context, buf) -> {
			BlockPos pos = buf.readBlockPos();
			BlockEntity blockEntity = context.getPlayer().getEntityWorld().getBlockEntity(pos);
			if (blockEntity instanceof WoodenBarrelBlockEntity) {
				syncBarrelFluid((WoodenBarrelBlockEntity) blockEntity, (ServerPlayerEntity) context.getPlayer());
			}
		});
		ClientSidePacketRegistry.INSTANCE.register(CONTAINER_SYNC, (context, buf) -> {
			Screen gui = MinecraftClient.getInstance().currentScreen;
			if (gui instanceof AbstractContainerScreen) {
				Container container = ((AbstractContainerScreen) gui).getContainer();
				if (container instanceof ExtendedMenuListener) {
					((ExtendedMenuListener) container).handleObject(buf.readInt(), ObjectBufUtils.readObject(buf));
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
