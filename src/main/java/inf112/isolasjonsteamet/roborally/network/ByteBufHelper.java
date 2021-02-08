package inf112.isolasjonsteamet.roborally.network;

import static com.google.common.base.Preconditions.checkArgument;

import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ByteBufHelper {

	@SuppressWarnings("checkstyle:Indentation")
	@Nullable
	public static String readString(ByteBuf buf, int maxSize) {
		int length = switch (maxSize) {
			case 1 -> buf.readUnsignedByte();
			case 2 -> buf.readUnsignedShort();
			case 3 -> buf.readUnsignedMedium();
			case 4 -> buf.readInt();
			default -> throw new IllegalArgumentException("Illegal max size %d specified".formatted(maxSize));
		};

		return length == -1 ? null : buf.readCharSequence(length, StandardCharsets.UTF_8).toString();
	}

	@Nullable
	public static String readString(ByteBuf buf) {
		return readString(buf, 4);
	}

	public static void writeString(@Nullable String string, ByteBuf buf, int maxSize) {
		int toWrite = string == null ? -1 : string.length();

		switch (maxSize) {
			case 1 -> writeUnsignedByte((short) toWrite, buf);
			case 2 -> writeUnsignedShort(toWrite, buf);
			case 3 -> writeUnsignedMedium(toWrite, buf);
			case 4 -> writeUnsignedInt(toWrite, buf);
			default -> throw new IllegalArgumentException("Illegal max size %d specified".formatted(maxSize));
		}

		if (string != null) {
			buf.writeCharSequence(string, StandardCharsets.UTF_8);
		}
	}

	public static void writeString(@Nullable String string, ByteBuf buf) {
		writeString(string, buf, 4);
	}

	public static void writeUnsignedByte(short ubyte, ByteBuf buf) {
		checkArgument(ubyte >= 0, "Tried to write negative ubyte");
		buf.writeByte(ubyte);
	}

	public static void writeUnsignedShort(int ushort, ByteBuf buf) {
		checkArgument(ushort >= 0, "Tried to write negative ushort");
		//Yes, really, this is correct
		buf.writeChar(ushort);
	}

	public static void writeUnsignedMedium(int umedium, ByteBuf buf) {
		checkArgument(umedium >= 0, "Tried to write negative umedium");
		checkArgument(umedium <= 16_777_215, "Tried to write overflowing umedium");
		buf.writeMedium(umedium);
	}

	public static void writeUnsignedInt(long uint, ByteBuf buf) {
		checkArgument(uint >= 0, "Tried to write negative uint");
		buf.writeInt((int) uint);
	}

	public static <T extends Enum<T>> T readEnum(Class<T> enumClass, ByteBuf buf) {
		int size = enumClass.getEnumConstants().length;
		int ordinal;
		if (size < 255) {
			ordinal = buf.readUnsignedByte();
		} else {
			// It should be impossible to have an enum larger than an
			// unsigned short (or even a normal short)
			ordinal = buf.readUnsignedShort();
		}

		return enumClass.getEnumConstants()[ordinal];
	}

	public static <T extends Enum<T>> void writeEnum(T enumValue, ByteBuf buf) {
		int ordinal = enumValue.ordinal();
		int size = enumValue.getClass().getEnumConstants().length;
		if (size < 255) {
			writeUnsignedByte((short) ordinal, buf);
		} else {
			// It should be impossible to have an enum larger than an
			// unsigned short (or even a normal short)
			writeUnsignedShort(ordinal, buf);
		}
	}
}
