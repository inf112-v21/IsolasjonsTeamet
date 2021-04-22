package inf112.isolasjonsteamet.roborally.network.c2spackets;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import inf112.isolasjonsteamet.roborally.network.ByteBufHelper;
import inf112.isolasjonsteamet.roborally.network.Codec;
import io.netty.buffer.ByteBuf;

/**
 * Sent from the client to the server when a client sends a chat message.
 */
public class ClientChatPacket implements Client2ServerPacket {

	private final String message;

	public ClientChatPacket(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ClientChatPacket that = (ClientChatPacket) o;
		return Objects.equal(message, that.message);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(message);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("message", message)
				.toString();
	}

	@SuppressWarnings("checkstyle:MissingJavadocType")
	public enum PacketCodec implements Codec<ClientChatPacket> {
		INSTANCE;

		@Override
		public ClientChatPacket read(ByteBuf in) {
			return new ClientChatPacket(ByteBufHelper.readString(in));
		}

		@Override
		public void write(ClientChatPacket msg, ByteBuf buf) {
			ByteBufHelper.writeString(msg.message, buf);
		}
	}
}
