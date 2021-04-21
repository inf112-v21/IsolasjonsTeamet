package inf112.isolasjonsteamet.roborally.network.s2cpackets;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import inf112.isolasjonsteamet.roborally.network.ByteBufHelper;
import inf112.isolasjonsteamet.roborally.network.Codec;
import io.netty.buffer.ByteBuf;

/**
 * Sent by the server to all clients if it for some reason needs to elect a new host.
 */
public class SetNewHostPacket implements Server2ClientPacket {

	private final String newHost;

	public SetNewHostPacket(String newHost) {
		this.newHost = newHost;
	}

	public String getNewHost() {
		return newHost;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		SetNewHostPacket that = (SetNewHostPacket) o;
		return Objects.equal(newHost, that.newHost);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(newHost);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("newHost", newHost)
				.toString();
	}

	@SuppressWarnings("checkstyle:MissingJavadocType")
	public enum PacketCodec implements Codec<SetNewHostPacket> {
		INSTANCE;

		@Override
		public SetNewHostPacket read(ByteBuf in) {
			return new SetNewHostPacket(ByteBufHelper.readString(in, 1));
		}

		@Override
		public void write(SetNewHostPacket msg, ByteBuf buf) {
			ByteBufHelper.writeString(msg.newHost, buf, 1);
		}
	}
}
