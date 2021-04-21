package inf112.isolasjonsteamet.roborally.network;

import static com.google.common.base.Preconditions.checkArgument;

import io.netty.buffer.ByteBuf;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Misc helpers to work with {@link ByteBuf}s.
 */
public class ByteBufHelper {

	/**
	 * Read a nullable string from the given byte buffer with a limited about of bytes to read the length.
	 *
	 * @param maxSize How many bytes the length should use. Must be one of 1, 2, 3, 4, 8
	 */
	@SuppressWarnings("checkstyle:Indentation")
	@Nullable
	public static String readString(ByteBuf buf, int maxSize) {
		long length = switch (maxSize) {
			case 1 -> buf.readUnsignedByte();
			case 2 -> buf.readUnsignedShort();
			case 3 -> buf.readUnsignedMedium();
			case 4 -> buf.readUnsignedInt();
			default -> throw new IllegalArgumentException("Illegal max size %d specified".formatted(maxSize));
		};
		int intLength = (int) length;
		Charset utf8 = StandardCharsets.UTF_8;

		var res = switch (maxSize) {
			case 1 -> length == (short) (Byte.MAX_VALUE * 2 + 1) ? null : buf.readCharSequence(intLength, utf8);
			case 2 -> length == Short.MAX_VALUE * 2 + 1 ? null : buf.readCharSequence(intLength, utf8);
			case 3 -> length == (1 << 24) - 1 ? null : buf.readCharSequence(intLength, utf8);
			case 4 -> length == Integer.MAX_VALUE * 2L + 1 ? null : buf.readCharSequence(intLength, utf8);
			default -> throw new IllegalArgumentException("Illegal max size %d specified".formatted(maxSize));
		};

		return res != null ? res.toString() : null;
	}

	/**
	 * Read a nullable string from the given byte buffer.
	 */
	@Nullable
	public static String readString(ByteBuf buf) {
		return readString(buf, 4);
	}

	/**
	 * Writes a nullable string to the given byte buffer with a limited about of bytes to write the length.
	 *
	 * @param maxSize How many bytes the length should use. Must be one of 1, 2, 3, 4, 8
	 */
	public static void writeString(@Nullable String string, ByteBuf buf, int maxSize) {
		int len = -1;
		if (string != null) {
			len = string.length();
		}

		switch (maxSize) {
			case 1 -> writeUnsignedByte(string == null ? (short) (Byte.MAX_VALUE * 2 + 1) : (short) len, buf);
			case 2 -> writeUnsignedShort(string == null ? Short.MAX_VALUE * 2 + 1 : len, buf);
			case 3 -> writeUnsignedMedium(string == null ? (1 << 24) - 1 : len, buf);
			case 4 -> writeUnsignedInt(string == null ? Integer.MAX_VALUE * 2L + 1 : len, buf);
			default -> throw new IllegalArgumentException("Illegal max size %d specified".formatted(maxSize));
		}

		if (string != null) {
			buf.writeCharSequence(string, StandardCharsets.UTF_8);
		}
	}

	/**
	 * Writes a nullable string to the given byte buffer.
	 */
	public static void writeString(@Nullable String string, ByteBuf buf) {
		writeString(string, buf, 4);
	}

	/**
	 * Write an unsigned byte to the given byte buffer.
	 */
	public static void writeUnsignedByte(short ubyte, ByteBuf buf) {
		checkArgument(ubyte >= 0, "Tried to write negative ubyte");
		buf.writeByte(ubyte);
	}

	/**
	 * Write an unsigned short to the given byte buffer.
	 */
	public static void writeUnsignedShort(int ushort, ByteBuf buf) {
		checkArgument(ushort >= 0, "Tried to write negative ushort");
		//Yes, really, this is correct
		buf.writeChar(ushort);
	}

	/**
	 * Write an unsigned medium to the given byte buffer.
	 */
	public static void writeUnsignedMedium(int umedium, ByteBuf buf) {
		checkArgument(umedium >= 0, "Tried to write negative umedium");
		checkArgument(umedium <= 16_777_215, "Tried to write overflowing umedium");
		buf.writeMedium(umedium);
	}

	/**
	 * Write an unsigned int to the given byte buffer.
	 */
	public static void writeUnsignedInt(long uint, ByteBuf buf) {
		checkArgument(uint >= 0, "Tried to write negative uint");
		buf.writeInt((int) uint);
	}

	/**
	 * Read an enum from the given byte buffer.
	 *
	 * @param enumClass The class of the enum to read.
	 */
	public static <T extends Enum<T>> T readEnum(Class<T> enumClass, ByteBuf buf) {
		int size = enumClass.getEnumConstants().length;
		int ordinal;
		if (size <= 255) {
			ordinal = buf.readUnsignedByte();
		} else {
			// It should be impossible to have an enum larger than an
			// unsigned short (or even a normal short)
			ordinal = buf.readUnsignedShort();
		}

		return enumClass.getEnumConstants()[ordinal];
	}

	/**
	 * Writes an enum to the given byte buffer.
	 */
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
