package inf112.isolasjonsteamet.roborally.network.c2spackets;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import inf112.isolasjonsteamet.roborally.network.Codec;
import io.netty.buffer.ByteBuf;

public class GameJoinPacket implements Client2ServerPacket {

	private final String playerName;

	public GameJoinPacket(String playerName) {
		this.playerName = playerName;
	}

	public String getPlayerName() {
		return playerName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		GameJoinPacket that = (GameJoinPacket) o;
		return Objects.equal(playerName, that.playerName);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(playerName);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("playerName", playerName)
				.toString();
	}

	public static class PacketCodec implements Codec<GameJoinPacket> {

		@Override
		public GameJoinPacket read(ByteBuf in) {
			return null;
		}

		@Override
		public void write(GameJoinPacket msg, ByteBuf buf) {

		}
	}
}
