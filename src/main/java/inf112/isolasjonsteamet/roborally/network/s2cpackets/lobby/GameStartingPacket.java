package inf112.isolasjonsteamet.roborally.network.s2cpackets.lobby;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import inf112.isolasjonsteamet.roborally.network.Codec;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.Server2ClientPacket;
import io.netty.buffer.ByteBuf;

public class GameStartingPacket implements Server2ClientPacket {

	private final boolean gameStarting;

	public GameStartingPacket(boolean gameStarting) {
		this.gameStarting = gameStarting;
	}

	public boolean isGameStarting() {
		return gameStarting;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		GameStartingPacket that = (GameStartingPacket) o;
		return gameStarting == that.gameStarting;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(gameStarting);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("gameStarting", gameStarting)
				.toString();
	}

	public enum PacketCodec implements Codec<GameStartingPacket> {
		INSTANCE;

		@Override
		public GameStartingPacket read(ByteBuf in) {
			return new GameStartingPacket(in.readBoolean());
		}

		@Override
		public void write(GameStartingPacket msg, ByteBuf buf) {
			buf.writeBoolean(msg.gameStarting);
		}
	}
}
