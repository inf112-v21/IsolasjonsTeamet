package inf112.isolasjonsteamet.roborally.network.s2cpackets.lobby;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import inf112.isolasjonsteamet.roborally.network.ByteBufHelper;
import inf112.isolasjonsteamet.roborally.network.Codec;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.Server2ClientPacket;
import io.netty.buffer.ByteBuf;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Sent to players to inform them of the current state of the lobby. Contains the current host, all the players, and if
 * they are ready or not.
 */
public class LobbyInfoPacket implements Server2ClientPacket {

	private final Map<String, Boolean> isPlayerReady;
	private final String host;
	private final @Nullable String serverPlayer;

	/**
	 * Creates a new lobby packet. All the players should be included in isPlayerReady.
	 */
	public LobbyInfoPacket(Map<String, Boolean> isPlayerReady, String host, @Nullable String serverPlayer) {
		this.isPlayerReady = isPlayerReady;
		this.host = host;
		this.serverPlayer = serverPlayer;
	}

	public Map<String, Boolean> getIsPlayerReady() {
		return isPlayerReady;
	}

	public String getHost() {
		return host;
	}

	@Nullable
	public String getServerPlayer() {
		return serverPlayer;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		LobbyInfoPacket that = (LobbyInfoPacket) o;
		//@formatter:off
		return Objects.equal(isPlayerReady, that.isPlayerReady)
			&& Objects.equal(host, that.host)
			&& Objects.equal(serverPlayer, that.serverPlayer);
		//@formatter:on
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(isPlayerReady, host, serverPlayer);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("isPlayerReady", isPlayerReady)
				.add("host", host)
				.add("serverPlayer", serverPlayer)
				.toString();
	}

	@SuppressWarnings("checkstyle:MissingJavadocType")
	public enum PacketCodec implements Codec<LobbyInfoPacket> {
		INSTANCE;

		@Override
		public LobbyInfoPacket read(ByteBuf in) {
			var count = in.readByte();
			var builder = ImmutableMap.<String, Boolean>builder();

			for (int i = 0; i < count; i++) {
				String player = ByteBufHelper.readString(in, 1);
				if (player == null) {
					throw new IllegalArgumentException("Encoded null player");
				}

				boolean ready = in.readBoolean();
				builder.put(player, ready);
			}
			var host = ByteBufHelper.readString(in, 1);
			var serverPlayer = ByteBufHelper.readString(in, 1);

			return new LobbyInfoPacket(builder.build(), host, serverPlayer);
		}

		@Override
		public void write(LobbyInfoPacket msg, ByteBuf buf) {
			buf.writeByte(msg.isPlayerReady.size());

			msg.isPlayerReady.forEach((player, ready) -> {
				ByteBufHelper.writeString(player, buf, 1);
				buf.writeBoolean(ready);
			});

			ByteBufHelper.writeString(msg.host, buf, 1);
			ByteBufHelper.writeString(msg.serverPlayer, buf, 1);
		}
	}
}
