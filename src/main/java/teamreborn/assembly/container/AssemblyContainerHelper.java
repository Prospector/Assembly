package teamreborn.assembly.container;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.networking.CustomPayloadHandlerRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.packet.CustomPayloadClientPacket;
import net.minecraft.client.player.ClientPlayerEntity;
import net.minecraft.container.ContainerProvider;
import net.minecraft.container.ListenerContainer;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import teamreborn.assembly.Assembly;
import teamreborn.assembly.blockentity.GrinderBlockEntity;
import teamreborn.assembly.client.gui.GrinderGui;

public class AssemblyContainerHelper implements ModInitializer {

	public static final Identifier OPEN_CONTAINER = new Identifier(Assembly.MOD_ID, "open_container");

	public static void init() {
		CustomPayloadHandlerRegistry.CLIENT.register(OPEN_CONTAINER, (packetContext, packetByteBuf) -> {
			Identifier identifier = new Identifier(packetByteBuf.readString(64));
			BlockPos pos = packetByteBuf.readBlockPos();
			openGui(identifier, (ClientPlayerEntity) packetContext.getPlayer(), pos);
		});
	}

	public static void openGui(FabricContainerProvider containerProvider, BlockPos pos, ServerPlayerEntity playerEntity) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeString(containerProvider.getContainerIdentifier().toString());
		buf.writeBlockPos(pos);
		playerEntity.networkHandler.sendPacket(new CustomPayloadClientPacket(OPEN_CONTAINER, buf));

		playerEntity.container = ((ContainerProvider) playerEntity.getWorld().getBlockEntity(pos)).createContainer(playerEntity.inventory, playerEntity);
		playerEntity.container.addListener(playerEntity);

	}

	private static void openGui(Identifier container, ClientPlayerEntity playerEntity, BlockPos pos) {
		//TODO have some form of registry for this


		MinecraftClient.getInstance().execute(() -> {
			GrinderBlockEntity entity = (GrinderBlockEntity) playerEntity.world.getBlockEntity(pos);
			MinecraftClient.getInstance().openGui(new GrinderGui(playerEntity, entity));
		});

	}

	@Override
	public void onInitialize() {
		init();
	}
}
