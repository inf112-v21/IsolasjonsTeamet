package inf112.isolasjonsteamet.roborally.network.c2spackets;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import inf112.isolasjonsteamet.roborally.network.ByteBufHelper;
import inf112.isolasjonsteamet.roborally.network.Codec;
import io.netty.buffer.ByteBuf;

//@formatter:off
/**
 * Sent by a client to the server when it wants to join a game.
 * A {@link inf112.isolasjonsteamet.roborally.network.s2cpackets.GameJoinResultPacket}
 * is sent to the player in reply to this. If the player joins successfully, a {@link
 * inf112.isolasjonsteamet.roborally.network.s2cpackets.PlayerJoinedGamePacket} is sent to the existing players of the
 * game, and an updated {@link inf112.isolasjonsteamet.roborally.network.s2cpackets.GameInfoPacket} is sent to non
 * player connections.
 */
//@formatter:on
public class GameJoinPacket implements Client2ServerPacket {

	private final String playerName;

	/**
	 * Construct a new game join packet.
	 *
	 * @param playerName The username to use. Must be under 255 bytes.
	 */
	public GameJoinPacket(String playerName) {
		//TODO: Replace with proper player name validation elsewhere in time
		checkArgument(playerName.length() <= 255, "Playername too long");
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

	@SuppressWarnings("checkstyle:MissingJavadocType")
	public enum PacketCodec implements Codec<GameJoinPacket> {
		INSTANCE;

		@Override
		public GameJoinPacket read(ByteBuf in) {
			return new GameJoinPacket(java.util.Objects.requireNonNull(ByteBufHelper.readString(in, 1)));
		}

		@Override
		public void write(GameJoinPacket msg, ByteBuf buf) {
			ByteBufHelper.writeString(msg.playerName, buf, 1);
		}
	}
}
