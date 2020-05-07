package com.terraformersmc.assembly.util;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.Objects;

public enum ObjectBufUtils {

	STRING(String.class, (string, buffer) -> {
		buffer.writeInt(string.length());
		buffer.writeString(string);
	}, buffer -> {
		return buffer.readString(buffer.readInt());
	}),
	INT(Integer.class, (value, buffer) -> {
		buffer.writeInt(value);
	}, PacketByteBuf::readInt),
	BLOCK_POS(BlockPos.class, (pos, buffer) -> {
		buffer.writeBlockPos(pos);
	}, PacketByteBuf::readBlockPos),
	FLUID_VOLUME(FluidVolume.class, (volume, buffer) -> {
		buffer.writeCompoundTag(volume.toTag());
	}, buffer -> FluidVolume.fromTag(buffer.readCompoundTag())),
	FLUID_AMOUNT(FluidAmount.class, FluidAmount::toMcBuffer, FluidAmount::fromMcBuffer);

	Class clazz;
	ObjectWriter writer;
	ObjectReader reader;

	<T> ObjectBufUtils(Class<T> clazz, ObjectWriter<T> writer, ObjectReader<T> reader) {
		this.clazz = clazz;
		this.writer = writer;
		this.reader = reader;
	}

	public static void writeObject(Object object, PacketByteBuf buffer) {
		if (object instanceof FluidVolume) {
			buffer.writeInt(FLUID_VOLUME.ordinal());
			FLUID_VOLUME.writer.write(object, buffer);
		} else {
			ObjectBufUtils utils = Arrays.stream(values()).filter(objectBufUtils -> objectBufUtils.clazz == object.getClass()).findFirst().orElse(null);
			Objects.requireNonNull(utils, "No support found for " + object.getClass());
			buffer.writeInt(utils.ordinal());
			utils.writer.write(object, buffer);
		}
	}

	public static Object readObject(PacketByteBuf buffer) {
		ObjectBufUtils utils = values()[buffer.readInt()];
		Objects.requireNonNull(utils, "Could not find reader");
		return utils.reader.read(buffer);
	}

	private interface ObjectWriter<T> {
		void write(T object, PacketByteBuf buffer);
	}

	private interface ObjectReader<T> {
		T read(PacketByteBuf buffer);
	}

}
