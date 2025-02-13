package com.mcf.davidee.nbtedit;

import com.google.common.base.Strings;
import com.mcf.davidee.nbtedit.nbt.NamedNBT;
import net.minecraft.nbt.*;

public class NBTStringHelper {

	public static final char SECTION_SIGN = '\u00A7';

	public static String getNBTName(NamedNBT namedNBT) {
		String name = namedNBT.getName();
		INBT obj = namedNBT.getNBT();

		String s = toString(obj);
		return Strings.isNullOrEmpty(name) ? "" + s : name + ": " + s;
	}

	public static String getNBTNameSpecial(NamedNBT namedNBT) {
		String name = namedNBT.getName();
		INBT obj = namedNBT.getNBT();

		String s = toString(obj);
		return Strings.isNullOrEmpty(name) ? "" + s : name + ": " + s + SECTION_SIGN + 'r';
	}

	public static INBT newTag(byte type) {
		switch (type) {
			case 0:
				return EndNBT.INSTANCE;
			case 1:
				return ByteNBT.valueOf((byte) 0);
			case 2:
				return ShortNBT.valueOf((short) 0);
			case 3:
				return IntNBT.valueOf(0);
			case 4:
				return LongNBT.valueOf(0);
			case 5:
				return FloatNBT.valueOf(0);
			case 6:
				return DoubleNBT.valueOf(0);
			case 7:
				return new ByteArrayNBT(new byte[0]);
			case 8:
				return StringNBT.valueOf("");
			case 9:
				return new ListNBT();
			case 10:
				return new CompoundNBT();
			case 11:
				return new IntArrayNBT(new int[0]);
			default:
				return null;
		}
	}

	public static String toString(INBT base) {
		switch (base.getId()) {
			case 1:
				return "" + ((ByteNBT) base).getAsByte();
			case 2:
				return "" + ((ShortNBT) base).getAsShort();
			case 3:
				return "" + ((IntNBT) base).getAsInt();
			case 4:
				return "" + ((LongNBT) base).getAsLong();
			case 5:
				return "" + ((FloatNBT) base).getAsFloat();
			case 6:
				return "" + ((DoubleNBT) base).getAsDouble();
			case 7:
				return base.toString();
			case 8:
				return base.getAsString();
			case 9:
				return "(TagList)";
			case 10:
				return "(TagCompound)";
			case 11:
				return base.toString();
			default:
				return "?";
		}
	}

	public static String getButtonName(byte id) {
		switch (id) {
			case 1:
				return "Byte";
			case 2:
				return "Short";
			case 3:
				return "Int";
			case 4:
				return "Long";
			case 5:
				return "Float";
			case 6:
				return "Double";
			case 7:
				return "Byte[]";
			case 8:
				return "String";
			case 9:
				return "List";
			case 10:
				return "Compound";
			case 11:
				return "Int[]";
			case 12:
				return "Edit";
			case 13:
				return "Delete";
			case 14:
				return "Copy";
			case 15:
				return "Cut";
			case 16:
				return "Paste";
			default:
				return "Unknown";
		}
	}
}
