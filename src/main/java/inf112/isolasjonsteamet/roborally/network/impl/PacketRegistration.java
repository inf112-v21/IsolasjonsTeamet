package inf112.isolasjonsteamet.roborally.network.impl;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import inf112.isolasjonsteamet.roborally.network.Codec;
import inf112.isolasjonsteamet.roborally.network.Packet;
import java.lang.reflect.InvocationTargetException;

public class PacketRegistration<T extends Packet> {

	private final Class<T> clazz;
	private final Codec<T> codec;

	public static <T extends Packet> PacketRegistration<T> of(Class<T> clazz, Codec<T> codec) {
		return new PacketRegistration<>(clazz, codec);
	}

	public static <T extends Packet> PacketRegistration<T> makeCodec(
			Class<T> clazz, Class<? extends Codec<T>> codecClass
	) {
		try {
			return new PacketRegistration<>(clazz, codecClass.getDeclaredConstructor().newInstance());
		} catch (InstantiationException
				| IllegalAccessException
				| InvocationTargetException
				| NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	public PacketRegistration(Class<T> clazz, Codec<T> codec) {
		this.clazz = clazz;
		this.codec = codec;
	}

	public Class<T> getClazz() {
		return clazz;
	}

	public Codec<T> getCodec() {
		return codec;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		PacketRegistration<?> that = (PacketRegistration<?>) o;
		return Objects.equal(clazz, that.clazz) && Objects.equal(codec, that.codec);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(clazz, codec);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("clazz", clazz)
				.add("codec", codec)
				.toString();
	}
}
