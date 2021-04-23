package inf112.isolasjonsteamet.roborally.network.s2cpackets;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import inf112.isolasjonsteamet.roborally.network.ByteBufHelper;
import inf112.isolasjonsteamet.roborally.network.Codec;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Sent by the server right before it closes gracefully.
 */
public class ServerClosingPacket implements Server2ClientPacket {

	@Nullable
	private final String reason;

	public ServerClosingPacket(@Nullable String reason) {
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
		ServerClosingPacket that = (ServerClosingPacket) o;
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

	@SuppressWarnings("checkstyle:MissingJavadocType")
	public enum PacketCodec implements Codec<ServerClosingPacket> {
		INSTANCE;

		@Override
		public ServerClosingPacket read(ByteBuf in) {
			return new ServerClosingPacket(ByteBufHelper.readString(in));
		}

		@Override
		public void write(ServerClosingPacket msg, ByteBuf buf) {
			ByteBufHelper.writeString(msg.reason, buf);
		}
	}
}
