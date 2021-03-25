package inf112.isolasjonsteamet.roborally.network;

import inf112.isolasjonsteamet.roborally.network.s2cpackets.Server2ClientPacket;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A listener for packets sent from the server.
 *
 * @param <T> The packet to listen to.
 */
public interface ClientPacketListener<T extends Server2ClientPacket> {

	/**
	 * Creates a listener which listens for a single packet type.
	 *
	 * @param clazz The packet type to listen for.
	 * @param handler A function to execute whenever a packet of the given type is received.
	 */
	static <T extends Server2ClientPacket> ClientPacketListener<T> of(Class<T> clazz, Consumer<T> handler) {
		return new ClassClientPacketListener<>(clazz) {
			@Override
			public void handle(T packet) {
				handler.accept(packet);
			}
		};
	}

	/**
	 * Grabs the next packet of packages in in a {@link CompletableFuture} form.
	 *
	 * @param client The client to listen for the pack on.
	 * @param clazz The pack type to listen for.
	 * @return A promise of the next packet of the given type.
	 */
	static <T extends Server2ClientPacket> CompletableFuture<T> next(Client client, Class<T> clazz) {
		var promise = new CompletableFuture<T>();
		client.addListener(new ClassClientPacketListener<>(clazz) {
			@Override
			public void handle(T packet) {
				promise.complete(packet);
				client.removeListener(this);
			}
		});

		return promise;
	}

	/**
	 * Called for all packets this listener accepts.
	 */
	void handle(T packet);

	/**
	 * Refine any packet into those this handler can handle.
	 *
	 * @param packet The packet to refine
	 * @return The packet in a handleable form, or null if this listener can't handle the given packet.
	 */
	@Nullable
	T refine(Server2ClientPacket packet);

	/**
	 * Handle the given packet if this listener supports it.
	 */
	default void handleIfPossible(Server2ClientPacket msg) {
		T refined = refine(msg);
		if (refined != null) {
			handle(refined);
		}
	}

	/**
	 * A {@link ClientPacketListener} which listens for a single type specified by the class it takes.
	 */
	abstract class ClassClientPacketListener<T extends Server2ClientPacket> implements ClientPacketListener<T> {

		private final Class<T> clazz;

		protected ClassClientPacketListener(Class<T> clazz) {
			this.clazz = clazz;
		}

		@Override
		public @Nullable T refine(Server2ClientPacket packet) {
			if (clazz.isInstance(packet)) {
				return clazz.cast(packet);
			}

			return null;
		}
	}
}
