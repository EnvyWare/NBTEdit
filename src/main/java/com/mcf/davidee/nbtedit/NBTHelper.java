package com.mcf.davidee.nbtedit;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.EncoderException;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

public class NBTHelper {

	public static CompoundNBT nbtRead(DataInputStream in) throws IOException {
		return CompressedStreamTools.read(in);
	}

	public static void nbtWrite(CompoundNBT compound, DataOutput out) throws IOException {
		CompressedStreamTools.write(compound, out);
	}

	public static Map<String, INBT> getMap(CompoundNBT tag) {
		return ObfuscationReflectionHelper.getPrivateValue(CompoundNBT.class, tag, "tags");
	}

	public static void writeToBuffer(CompoundNBT nbt, ByteBuf buf) {
		if (nbt == null) {
			buf.writeByte(0);
		} else {
			try {
				CompressedStreamTools.write(nbt, new ByteBufOutputStream(buf));
			} catch (IOException e) {
				throw new EncoderException(e);
			}
		}
	}

	public static CompoundNBT readNbtFromBuffer(ByteBuf buf) {
		int index = buf.readerIndex();
		byte isNull = buf.readByte();

		if (isNull == 0) {
			return null;
		} else {
			// restore index after checking to make sure the tag wasn't null/
			buf.readerIndex(index);
			try {
				return CompressedStreamTools.read(new ByteBufInputStream(buf), new NBTSizeTracker(2097152L));
			} catch (IOException ioexception) {
				throw new EncoderException(ioexception);
			}
		}
	}
}
