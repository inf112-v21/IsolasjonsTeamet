package inf112.isolasjonsteamet.roborally.network;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class ByteBufHelperSpec {

	@ParameterizedTest(name = "Read-write unsigned byte with value {argumentsWithNames}")
	@ValueSource(shorts = {0, 1, Byte.MAX_VALUE, Byte.MAX_VALUE + 1, Byte.MAX_VALUE * 2, Byte.MAX_VALUE * 2 + 1})
	public void readWriteUnsignedByte(short value) {
		ByteBuf buffer = Unpooled.buffer();
		ByteBufHelper.writeUnsignedByte(value, buffer);
		assertEquals(value, buffer.readUnsignedByte());
	}

	@ParameterizedTest(name = "Read-write unsigned short with value {argumentsWithNames}")
	@ValueSource(ints = {0, 1, Short.MAX_VALUE, Short.MAX_VALUE + 1, Short.MAX_VALUE * 2, Short.MAX_VALUE * 2 + 1})
	public void readWriteUnsignedShort(int value) {
		ByteBuf buffer = Unpooled.buffer();
		ByteBufHelper.writeUnsignedShort(value, buffer);
		assertEquals(value, buffer.readUnsignedShort());
	}

	@ParameterizedTest(name = "Read-write unsigned medium with value {argumentsWithNames}")
	@ValueSource(ints = {0, 1, (1 << 23) - 1, (1 << 23), (1 << 24) - 2, (1 << 24) - 1})
	public void readWriteUnsignedMedium(int value) {
		ByteBuf buffer = Unpooled.buffer();
		ByteBufHelper.writeUnsignedMedium(value, buffer);
		assertEquals(value, buffer.readUnsignedMedium());
	}

	@ParameterizedTest(name = "Read-write unsigned int with value {argumentsWithNames}")
	@ValueSource(longs = {0, 1, Integer.MAX_VALUE, Integer.MAX_VALUE + 1L, Integer.MAX_VALUE * 2L, Integer.MAX_VALUE * 2L + 1})
	public void readWriteUnsignedInt(long value) {
		ByteBuf buffer = Unpooled.buffer();
		ByteBufHelper.writeUnsignedInt(value, buffer);
		assertEquals(value, buffer.readUnsignedInt());
	}

	@ParameterizedTest(name = "Read-write byte length string with value {argumentsWithNames}")
	@ValueSource(strings = {"foo", "", "null"})
	public void readWriteByteString(String s) {
		//Workaround
		if (s.equals("null")) {
			s = null;
		}

		ByteBuf buffer = Unpooled.buffer();
		ByteBufHelper.writeString(s, buffer, 1);
		assertEquals(s, ByteBufHelper.readString(buffer, 1));
	}

	@ParameterizedTest(name = "Read-write short length string with value {argumentsWithNames}")
	@ValueSource(strings = {"foo", "", "null"})
	public void readWriteShortString(String s) {
		//Workaround
		if (s.equals("null")) {
			s = null;
		}

		ByteBuf buffer = Unpooled.buffer();
		ByteBufHelper.writeString(s, buffer, 2);
		assertEquals(s, ByteBufHelper.readString(buffer, 2));
	}

	@ParameterizedTest(name = "Read-write medium length string with value {argumentsWithNames}")
	@ValueSource(strings = {"foo", "", "null"})
	public void readWriteMediumString(String s) {
		//Workaround
		if (s.equals("null")) {
			s = null;
		}

		ByteBuf buffer = Unpooled.buffer();
		ByteBufHelper.writeString(s, buffer, 3);
		assertEquals(s, ByteBufHelper.readString(buffer, 3));
	}

	@ParameterizedTest(name = "Read-write int length string with value {argumentsWithNames}")
	@ValueSource(strings = {"foo", "", "null"})
	public void readWriteIntString(String s) {
		//Workaround
		if (s.equals("null")) {
			s = null;
		}

		ByteBuf buffer = Unpooled.buffer();
		ByteBufHelper.writeString(s, buffer, 4);
		assertEquals(s, ByteBufHelper.readString(buffer, 4));
	}
}
