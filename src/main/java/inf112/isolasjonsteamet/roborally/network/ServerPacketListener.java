package inf112.isolasjonsteamet.roborally.network;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import inf112.isolasjonsteamet.roborally.network.c2spackets.Client2ServerPacket;
import java.util.function.Consumer;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A listener for packets sent from the clients.
 *
 * @param <T> The packet to listen to.
 */
public interface ServerPacketListener<T extends Client2ServerPacket> {

	/**
	 * Creates a listener which listens for a single packet type.
	 *
	 * @param clazz The packet type to listen for.
	 * @param handler A function to execute whenever a packet of the given type is received.
	 */
	static <T extends Client2ServerPacket> ServerPacketListener<T> of(
			Class<T> clazz, Consumer<ServerPacket<T>> handler
	) {
		return new ClassServerPacketListener<>(clazz) {
			@Override
			public void handle(String player, T packet) {
				handler.accept(new ServerPacket<>(player, packet));
			}
		};
	}

	/**
	 * Called for all packets this listener accepts.
	 *
	 * @param player The player associated with the packet, or null if the sender hasn't joined the game yet.
	 */
	void handle(@Nullable String player, T packet);

	/**
	 * Refine any packet into those this handler can handle.
	 *
	 * @param packet The packet to refine
	 * @return The packet in a handleable form, or null if this listener can't handle the given packet.
	 */
	@Nullable
	T refine(Client2ServerPacket packet);

	/**
	 * Handle the given packet if this listener supports it.
	 */
	default void handleIfPossible(@Nullable String player, Client2ServerPacket msg) {
		T refined = refine(msg);
		if (refined != null) {
			handle(player, refined);
		}
	}

	/**
	 * A {@link ServerPacketListener} which listens for a single type specified by the class it takes.
	 */
	abstract class ClassServerPacketListener<T extends Client2ServerPacket> implements ServerPacketListener<T> {

		private final Class<T> clazz;

		public ClassServerPacketListener(Class<T> clazz) {
			this.clazz = clazz;
		}

		@Override
		public @Nullable T refine(Client2ServerPacket packet) {
			if (clazz.isInstance(packet)) {
				return clazz.cast(packet);
			}

			return null;
		}
	}

	/**
	 * A data type which combines all the parameters passed to {@link #handle(String, Client2ServerPacket)}.
	 */
	class ServerPacket<T> {

		private final String player;
		private final T packet;

		public ServerPacket(String player, T packet) {
			this.player = player;
			this.packet = packet;
		}

		public String getPlayer() {
			return player;
		}

		public T getPacket() {
			return packet;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			ServerPacket<?> that = (ServerPacket<?>) o;
			return Objects.equal(player, that.player)
					&& Objects.equal(packet, that.packet);
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(player, packet);
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("player", player)
					.add("packet", packet)
					.toString();
		}
	}
}
