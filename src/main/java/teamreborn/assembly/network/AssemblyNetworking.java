package teamreborn.assembly.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.networking.CustomPayloadHandlerRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.packet.CustomPayloadClientPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.packet.CustomPayloadServerPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import prospector.silk.fluid.FluidContainer;
import prospector.silk.fluid.FluidInstance;
import teamreborn.assembly.Assembly;
import teamreborn.assembly.blockentity.WoodenBarrelBlockEntity;

public class AssemblyNetworking implements ModInitializer {

	public static final Identifier SYNC_BARREL_FLUID = new Identifier(Assembly.MOD_ID, "sync_barrel_fluid");
	public static final Identifier REQUEST_BARREL_SYNC = new Identifier(Assembly.MOD_ID, "request_barrel_sync");

	@Override
	public void onInitialize() {
		CustomPayloadHandlerRegistry.CLIENT.register(SYNC_BARREL_FLUID, (packetContext, packetByteBuf) -> {
			BlockPos pos = packetByteBuf.readBlockPos();
			FluidInstance instance = new FluidInstance(packetByteBuf.readCompoundTag());
			if (packetContext.getPlayer() != null && packetContext.getPlayer().getWorld() != null) {
				BlockEntity blockEntity = packetContext.getPlayer().getWorld().getBlockEntity(pos);
				if (blockEntity instanceof WoodenBarrelBlockEntity) {
					((FluidContainer) blockEntity).setFluid(null, instance);
				}
			}
		});
		CustomPayloadHandlerRegistry.SERVER.register(REQUEST_BARREL_SYNC, (packetContext, packetByteBuf) -> {
			BlockPos pos = packetByteBuf.readBlockPos();
			BlockEntity blockEntity = packetContext.getPlayer().getWorld().getBlockEntity(pos);
			if (blockEntity instanceof WoodenBarrelBlockEntity) {
				syncBarrelFluid((WoodenBarrelBlockEntity) blockEntity, (ServerPlayerEntity) packetContext.getPlayer());
			}
		});
	}

	@Environment(EnvType.SERVER)
	public static void syncBarrelFluid(WoodenBarrelBlockEntity woodenBarrel, ServerPlayerEntity player) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeBlockPos(woodenBarrel.getPos());
		buf.writeCompoundTag(woodenBarrel.getFluids(null)[0].serialize(new CompoundTag()));
		player.networkHandler.sendPacket(new CustomPayloadClientPacket(SYNC_BARREL_FLUID, buf));
	}

	@Environment(EnvType.CLIENT)
	public static void requestBarrelSync(WoodenBarrelBlockEntity woodenBarrel) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeBlockPos(woodenBarrel.getPos());
		MinecraftClient.getInstance().getNetworkHandler().getClientConnection().sendPacket(new CustomPayloadServerPacket(REQUEST_BARREL_SYNC, buf));
	}
}
