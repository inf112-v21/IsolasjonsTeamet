package inf112.isolasjonsteamet.roborally.network.c2spackets.lobby;

import com.google.common.base.MoreObjects;
import inf112.isolasjonsteamet.roborally.network.Codec;
import inf112.isolasjonsteamet.roborally.network.c2spackets.Client2ServerPacket;
import io.netty.buffer.ByteBuf;

/**
 * Sent by the client to indicate it wants info about the current lobby room it has connected to.
 */
public enum RequestLobbyInfoPacket implements Client2ServerPacket {
	INSTANCE;

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.toString();
	}

	@SuppressWarnings("checkstyle:MissingJavadocType")
	public enum PacketCodec implements Codec<RequestLobbyInfoPacket> {
		INSTANCE;

		@Override
		public RequestLobbyInfoPacket read(ByteBuf in) {
			return RequestLobbyInfoPacket.INSTANCE;
		}

		@Override
		public void write(RequestLobbyInfoPacket msg, ByteBuf buf) {
		}
	}
}
