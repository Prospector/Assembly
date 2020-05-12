package com.terraformersmc.assembly.networking;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.terraformersmc.assembly.Assembly;
import com.terraformersmc.assembly.blockentity.FluidBarrelBlockEntity;
import com.terraformersmc.assembly.mixin.common.screenhandler.ScreenHandlerListenersAccessor;
import com.terraformersmc.assembly.screen.builder.ExtendedScreenHandlerListener;
import com.terraformersmc.assembly.screen.builder.ScreenSyncer;
import com.terraformersmc.assembly.util.ObjectBufUtils;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;

public class AssemblyNetworking {

	private static final Identifier SYNC_BARREL_FLUID = new Identifier(Assembly.MOD_ID, "sync_barrel_fluid");
	private static final Identifier REQUEST_BARREL_SYNC = new Identifier(Assembly.MOD_ID, "request_barrel_sync");
	private static final Identifier SCREEN_SYNC = new Identifier(Assembly.MOD_ID, "screen_sync");
	private static final Identifier REQUEST_SCREEN_SYNC = new Identifier(Assembly.MOD_ID, "request_screen_sync");
	private static final Identifier SCREEN_CLICK_TANK = new Identifier(Assembly.MOD_ID, "screen_click_tank");

	public static void registerServersideHandlers() {
		ServerSidePacketRegistry.INSTANCE.register(REQUEST_BARREL_SYNC, (context, buf) -> {
			BlockPos pos = buf.readBlockPos();
			BlockEntity blockEntity = context.getPlayer().getEntityWorld().getBlockEntity(pos);
			if (blockEntity instanceof FluidBarrelBlockEntity) {
				syncBarrelFluid((FluidBarrelBlockEntity) blockEntity, (ServerPlayerEntity) context.getPlayer());
			}
		});
		ServerSidePacketRegistry.INSTANCE.register(REQUEST_SCREEN_SYNC, (context, buf) -> {
			ScreenHandler screenHandler = context.getPlayer().currentScreenHandler;
			if (screenHandler instanceof ScreenSyncer) {
				for (final ScreenHandlerListener listener : ((ScreenHandlerListenersAccessor) screenHandler).getListeners()) {
					((ScreenSyncer<?>) screenHandler).sync(listener, true);
				}
			}
		});
		ServerSidePacketRegistry.INSTANCE.register(SCREEN_CLICK_TANK, (context, buf) -> {
			ScreenHandler screenHandler = context.getPlayer().currentScreenHandler;
			if (screenHandler instanceof ScreenSyncer) {
				((ScreenSyncer<?>) screenHandler).onTankClick((ServerPlayerEntity) context.getPlayer(), buf.readInt());
			}
		});
	}

	public static void registerClientsideHandlers() {
		ClientSidePacketRegistry.INSTANCE.register(SYNC_BARREL_FLUID, (context, buf) -> {
			BlockPos pos = buf.readBlockPos();
			CompoundTag fluidVolumeTag = buf.readCompoundTag();
			FluidVolume instance;
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
		ClientSidePacketRegistry.INSTANCE.register(SCREEN_SYNC, (context, buf) -> {
			Screen gui = MinecraftClient.getInstance().currentScreen;
			if (gui instanceof HandledScreen) {
				ScreenHandler container = ((HandledScreen<?>) gui).getScreenHandler();
				if (container instanceof ExtendedScreenHandlerListener) {
					((ExtendedScreenHandlerListener) container).handleObject(buf.readInt(), ObjectBufUtils.readObject(buf));
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
	public static void clickTank(int tankIndex) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeInt(tankIndex);
		MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(SCREEN_CLICK_TANK, buf));
	}

	@Environment(EnvType.CLIENT)
	public static void requestBarrelSync(FluidBarrelBlockEntity woodenBarrel) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeBlockPos(woodenBarrel.getPos());
		ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
		if (networkHandler != null) {
			networkHandler.getConnection().send(new CustomPayloadC2SPacket(REQUEST_BARREL_SYNC, buf));
		}
	}

	@Environment(EnvType.CLIENT)
	public static <BE extends BlockEntity & Nameable> void requestScreenSync(ScreenSyncer<BE> syncer) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
		if (networkHandler != null) {
			networkHandler.getConnection().send(new CustomPayloadC2SPacket(REQUEST_SCREEN_SYNC, buf));
		}
	}

	public static void syncScreenHandler(ServerPlayerEntity player, int index, Object value) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeInt(index);
		ObjectBufUtils.writeObject(value, buf);
		player.networkHandler.sendPacket(new CustomPayloadS2CPacket(SCREEN_SYNC, buf));
	}
}
