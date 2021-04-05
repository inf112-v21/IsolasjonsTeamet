package inf112.isolasjonsteamet.roborally.network;

import inf112.isolasjonsteamet.roborally.network.s2cpackets.GameInfoPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.GameJoinResultPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.KickedPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.OtherPlayerKickedPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.PlayerJoinedGamePacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.PlayerLeftGamePacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.Server2ClientPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.ServerClosingPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.lobby.GameStartingPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.lobby.LobbyInfoPacket;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * An adapter for packets sent from the server to the client,
 * such that instanceof checks are not needed.
 */
@SuppressWarnings("unused")
public class ClientPacketAdapter implements ClientPacketListener<Server2ClientPacket> {

	public void onServerClosing(ServerClosingPacket packet) {
	}

	public void onPlayerLeftGame(PlayerLeftGamePacket packet) {
	}

	public void onPlayerJoinedGame(PlayerJoinedGamePacket packet) {
	}

	public void onOtherPlayerKicked(OtherPlayerKickedPacket packet) {
	}

	public void onKicked(KickedPacket packet) {
	}

	public void onGameJoinResult(GameJoinResultPacket packet) {
	}

	public void onGameInfo(GameInfoPacket packet) {
	}

	public void onLobbyInfo(LobbyInfoPacket packet) {
	}

	public void onGameStarting(GameStartingPacket packet) {
	}

	@Override
	public void handle(Server2ClientPacket packet) {
		if (packet instanceof GameStartingPacket) {
			onGameStarting((GameStartingPacket) packet);
		} else if (packet instanceof LobbyInfoPacket) {
			onLobbyInfo((LobbyInfoPacket) packet);
		} else if (packet instanceof GameInfoPacket) {
			onGameInfo((GameInfoPacket) packet);
		} else if (packet instanceof GameJoinResultPacket) {
			onGameJoinResult((GameJoinResultPacket) packet);
		} else if (packet instanceof KickedPacket) {
			onKicked((KickedPacket) packet);
		} else if (packet instanceof OtherPlayerKickedPacket) {
			onOtherPlayerKicked((OtherPlayerKickedPacket) packet);
		} else if (packet instanceof PlayerJoinedGamePacket) {
			onPlayerJoinedGame((PlayerJoinedGamePacket) packet);
		} else if (packet instanceof PlayerLeftGamePacket) {
			onPlayerLeftGame((PlayerLeftGamePacket) packet);
		} else if (packet instanceof ServerClosingPacket) {
			onServerClosing((ServerClosingPacket) packet);
		} else {
			throw new IllegalArgumentException("Unknown S2C packet type " + packet.getClass());
		}
	}

	@Override
	public @Nullable Server2ClientPacket refine(Server2ClientPacket packet) {
		return packet;
	}
}
