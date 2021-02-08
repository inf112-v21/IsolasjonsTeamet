package inf112.isolasjonsteamet.roborally.network.c2spackets;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import inf112.isolasjonsteamet.roborally.network.ByteBufHelper;
import inf112.isolasjonsteamet.roborally.network.Codec;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Sent to the server before a client disconnects. If this packet is not sent before disconnecting, the server will
 * assume the client lost connection because of a network error.
 */
public class ClientDisconnectingPacket implements Client2ServerPacket {

	@Nullable
	private final String reason;

	public ClientDisconnectingPacket(@Nullable String reason) {
		this.reason = reason;
	}

	@Nullable
	public String getReason() {
		return reason;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ClientDisconnectingPacket that = (ClientDisconnectingPacket) o;
		return Objects.equal(reason, that.reason);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(reason);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("reason", reason)
				.toString();
	}

	public enum PacketCodec implements Codec<ClientDisconnectingPacket> {
		INSTANCE;

		@Override
		public ClientDisconnectingPacket read(ByteBuf in) {
			return new ClientDisconnectingPacket(ByteBufHelper.readString(in));
		}

		@Override
		public void write(ClientDisconnectingPacket msg, ByteBuf buf) {
			ByteBufHelper.writeString(msg.reason, buf);
		}
	}
}
