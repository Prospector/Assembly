package teamreborn.assembly.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.networking.CustomPayloadHandlerRegistry;
import net.minecraft.client.network.packet.CustomPayloadClientPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import teamreborn.assembly.Assembly;

public class AssemblyNetworking implements ModInitializer {

	public static final Identifier SYNC_SAPPING_BARREL_FLUID = new Identifier(Assembly.MOD_ID, "sync_sapping_barrel_fluid");

	@Override
	public void onInitialize() {
//		CustomPayloadHandlerRegistry.CLIENT.register(SYNC_SAPPING_BARREL_FLUID, (packetContext, packetByteBuf) -> {
//			BlockPos pos = null;
//			if (packetByteBuf.readBoolean()) {
//				pos = packetByteBuf.readBlockPos();
//			}
//			updateClientBedLocation(packetContext.getPlayer(), pos);
//		});
	}

	public static CustomPayloadClientPacket createBedStatusPacket(BlockPos pos) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeBoolean(pos != null);
		if (pos != null) {
			buf.writeBlockPos(pos);
		}
		CustomPayloadClientPacket packet = new CustomPayloadClientPacket(SYNC_SAPPING_BARREL_FLUID, buf);
		return packet;
	}

	public static CustomPayloadClientPacket createSetSpawnPacket() {
		return new CustomPayloadClientPacket(SYNC_SAPPING_BARREL_FLUID, new PacketByteBuf(Unpooled.buffer()));
	}
}
