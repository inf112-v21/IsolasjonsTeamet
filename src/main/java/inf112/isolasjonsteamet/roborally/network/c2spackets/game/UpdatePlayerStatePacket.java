package inf112.isolasjonsteamet.roborally.network.c2spackets.game;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import inf112.isolasjonsteamet.roborally.network.ByteBufHelper;
import inf112.isolasjonsteamet.roborally.network.Codec;
import inf112.isolasjonsteamet.roborally.network.c2spackets.Client2ServerPacket;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;
import io.netty.buffer.ByteBuf;

public class UpdatePlayerStatePacket implements Client2ServerPacket {

	private final Coordinate position;
	private final Orientation rotation;

	public UpdatePlayerStatePacket(Coordinate position, Orientation rotation) {
		this.position = position;
		this.rotation = rotation;
	}

	public Coordinate getPosition() {
		return position;
	}

	public Orientation getRotation() {
		return rotation;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		UpdatePlayerStatePacket that = (UpdatePlayerStatePacket) o;
		return Objects.equal(position, that.position) && rotation == that.rotation;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(position, rotation);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("position", position)
				.add("rotation", rotation)
				.toString();
	}

	public enum PacketCodec implements Codec<UpdatePlayerStatePacket> {
		INSTANCE;

		@Override
		public UpdatePlayerStatePacket read(ByteBuf in) {
			int x = in.readInt();
			int y = in.readInt();
			Orientation rotation = ByteBufHelper.readEnum(Orientation.class, in);

			return new UpdatePlayerStatePacket(new Coordinate(x, y), rotation);
		}

		@Override
		public void write(UpdatePlayerStatePacket msg, ByteBuf buf) {
			buf.writeInt(msg.position.getX());
			buf.writeInt(msg.position.getY());
			ByteBufHelper.writeEnum(msg.rotation, buf);
		}
	}
}
