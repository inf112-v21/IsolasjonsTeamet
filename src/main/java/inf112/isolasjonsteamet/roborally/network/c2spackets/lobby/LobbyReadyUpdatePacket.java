package inf112.isolasjonsteamet.roborally.network.c2spackets.lobby;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import inf112.isolasjonsteamet.roborally.network.Codec;
import inf112.isolasjonsteamet.roborally.network.c2spackets.Client2ServerPacket;
import io.netty.buffer.ByteBuf;

/**
 * Sends to the server if this player is ready to continue to the game. The host is in the end in charge of when the
 * game starts.
 */
public class LobbyReadyUpdatePacket implements Client2ServerPacket {

	private final boolean ready;

	public LobbyReadyUpdatePacket(boolean ready) {
		this.ready = ready;
	}

	public boolean isReady() {
		return ready;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		LobbyReadyUpdatePacket that = (LobbyReadyUpdatePacket) o;
		return ready == that.ready;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(ready);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("ready", ready)
				.toString();
	}

	@SuppressWarnings("checkstyle:MissingJavadocType")
	public enum PacketCodec implements Codec<LobbyReadyUpdatePacket> {
		INSTANCE;

		@Override
		public LobbyReadyUpdatePacket read(ByteBuf in) {
			return new LobbyReadyUpdatePacket(in.readBoolean());
		}

		@Override
		public void write(LobbyReadyUpdatePacket msg, ByteBuf buf) {
			buf.writeBoolean(msg.ready);
		}
	}
}
