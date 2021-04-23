package inf112.isolasjonsteamet.roborally.network.s2cpackets.game;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.network.ByteBufHelper;
import inf112.isolasjonsteamet.roborally.network.Codec;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.Server2ClientPacket;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Updates the state of the game. Last ditch packet the server can send to synchronize everyone. Can add and remove
 * players, in addition to teleporting them around.
 */
public class UpdatePlayerStatesPacket implements Server2ClientPacket {

	private final List<State> states;

	public UpdatePlayerStatesPacket(List<State> states) {
		this.states = states;
	}

	/**
	 * Constructs a {@link UpdatePlayerStatesPacket} from a list of players.
	 */
	public static UpdatePlayerStatesPacket fromPlayers(List<Player> players) {
		var states = players.stream().map(p ->
				new State(p.getName(), p.getRobot().getPos(), p.getRobot().getDir())
		).collect(Collectors.toList());
		return new UpdatePlayerStatesPacket(states);
	}

	public List<State> getStates() {
		return states;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		UpdatePlayerStatesPacket that = (UpdatePlayerStatesPacket) o;
		return Objects.equal(states, that.states);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("states", states)
				.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(states);
	}

	/**
	 * The state sent with each player in the packet.
	 */
	public static class State {

		private final String name;
		private final Coordinate pos;
		private final Orientation dir;

		/** Construct a new state. */
		public State(String name, Coordinate pos, Orientation dir) {
			this.name = name;
			this.pos = pos;
			this.dir = dir;
		}

		public String getName() {
			return name;
		}

		public Coordinate getPos() {
			return pos;
		}

		public Orientation getDir() {
			return dir;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			State state = (State) o;
			return Objects.equal(name, state.name) && Objects.equal(pos, state.pos) && dir == state.dir;
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(name, pos, dir);
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("name", name)
					.add("pos", pos)
					.add("dir", dir)
					.toString();
		}
	}

	@SuppressWarnings("checkstyle:MissingJavadocType")
	public enum PacketCodec implements Codec<UpdatePlayerStatesPacket> {
		INSTANCE;

		@Override
		public UpdatePlayerStatesPacket read(ByteBuf in) {
			var count = in.readUnsignedByte();

			var states = ImmutableList.<State>builder();
			for (int i = 0; i < count; i++) {
				var name = ByteBufHelper.readString(in, 1);
				var x = in.readShort();
				var y = in.readShort();
				var dir = ByteBufHelper.readEnum(Orientation.class, in);
				states.add(new State(name, new Coordinate(x, y), dir));
			}

			return new UpdatePlayerStatesPacket(states.build());
		}

		@Override
		public void write(UpdatePlayerStatesPacket msg, ByteBuf buf) {
			ByteBufHelper.writeUnsignedByte((short) msg.states.size(), buf);

			for (State state : msg.states) {
				ByteBufHelper.writeString(state.name, buf, 1);
				buf.writeShort(state.pos.getX());
				buf.writeShort(state.pos.getY());
				ByteBufHelper.writeEnum(state.dir, buf);
			}
		}
	}
}
