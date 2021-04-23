package inf112.isolasjonsteamet.roborally.network.impl;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import inf112.isolasjonsteamet.roborally.network.Codec;
import inf112.isolasjonsteamet.roborally.network.Packet;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * Represents a binding between a packet type and it's codec.
 *
 * @param <T> The packet type this registration is for.
 */
public class PacketRegistration<T extends Packet> {

	private final Class<T> clazz;
	private final Codec<T> codec;

	/** Make a packet registration from the class and codec directly. */
	public static <T extends Packet> PacketRegistration<T> of(Class<T> clazz, Codec<T> codec) {
		return new PacketRegistration<>(clazz, codec);
	}

	/**
	 * Make a registration from the packet's class, and the class of it's codec.
	 *
	 * <p>
	 * If the codec class is an enum, the first constant of the enum is grabbed as the codec. Otherwise this method will
	 * try to call a no args constructor on the codec class.
	 * </p>
	 *
	 * @param clazz The class of the packet being registered.
	 * @param codecClass The class of the codec for the packet being registered.
	 */
	public static <T extends Packet> PacketRegistration<T> makeCodec(
			Class<T> clazz, Class<? extends Codec<T>> codecClass
	) {
		if (codecClass.isEnum()) {
			final Codec<T> codec = codecClass.getEnumConstants()[0];
			return new PacketRegistration<>(clazz, codec);
		} else {
			try {
				return new PacketRegistration<>(clazz, codecClass.getDeclaredConstructor().newInstance());
			} catch (InstantiationException
					| IllegalAccessException
					| InvocationTargetException
					| NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Makes registration from the packet's class alone, trying instead to find the codec's class automatically.
	 *
	 * <p>
	 * This method will grab the first inner class which extends {@link Codec} it finds, and call {@link
	 * #makeCodec(Class, Class)} with it;
	 * </p>
	 *
	 * @see #makeCodec(Class, Class)
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Packet> PacketRegistration<T> findCodec(Class<T> clazz) {
		Class<? extends Codec<T>> codecClass =
				Arrays.stream(clazz.getClasses())
						.filter(Codec.class::isAssignableFrom)
						.map(c -> (Class<? extends Codec<T>>) c.asSubclass(Codec.class))
						.findFirst()
						.orElseThrow(() -> new RuntimeException("No codec found for class" + clazz));
		
		return makeCodec(clazz, codecClass);
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
