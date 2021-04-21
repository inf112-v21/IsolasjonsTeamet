package inf112.isolasjonsteamet.roborally.network.s2cpackets;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import inf112.isolasjonsteamet.roborally.network.ByteBufHelper;
import inf112.isolasjonsteamet.roborally.network.Codec;
import io.netty.buffer.ByteBuf;

/**
 * Sent from the server to all clients when a client wants to chat.
 */
public class ServerChatPacket implements Server2ClientPacket {

	private final String player;
	private final String message;

	public ServerChatPacket(String player, String message) {
		this.player = player;
		this.message = message;
	}

	public String getPlayer() {
		return player;
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
		ServerChatPacket that = (ServerChatPacket) o;
		return Objects.equal(player, that.player) && Objects.equal(message, that.message);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(player, message);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("player", player)
				.add("message", message)
				.toString();
	}

	@SuppressWarnings("checkstyle:MissingJavadocType")
	public enum PacketCodec implements Codec<ServerChatPacket> {
		INSTANCE;

		@Override
		public ServerChatPacket read(ByteBuf in) {
			return new ServerChatPacket(ByteBufHelper.readString(in, 1), ByteBufHelper.readString(in));
		}

		@Override
		public void write(ServerChatPacket msg, ByteBuf buf) {
			ByteBufHelper.writeString(msg.player, buf, 1);
			ByteBufHelper.writeString(msg.message, buf);
		}
	}
}
