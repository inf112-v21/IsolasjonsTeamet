package inf112.isolasjonsteamet.roborally.network;

import inf112.isolasjonsteamet.roborally.network.s2cpackets.GameInfoPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.GameJoinResultPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.KickedPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.OtherPlayerKickedPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.PlayerJoinedGamePacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.PlayerLeftGamePacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.Server2ClientPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.ServerChatPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.ServerClosingPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.game.DealNewCardsPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.game.RunRoundPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.game.UpdatePlayerStatesPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.lobby.GameStartingPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.lobby.LobbyInfoPacket;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * An adapter for packets sent from the server to the client,
 * such that instanceof checks are not needed.
 */
@SuppressWarnings("unused")
public abstract class ClientPacketAdapter implements ClientPacketListener<Server2ClientPacket> {

	public void onGameInfo(GameInfoPacket packet) {
	}

	public void onGameJoinResult(GameJoinResultPacket packet) {
	}

	public void onKicked(KickedPacket packet) {
	}

	public void onOtherPlayerKicked(OtherPlayerKickedPacket packet) {
	}

	public void onPlayerJoinedGame(PlayerJoinedGamePacket packet) {
	}

	public void onPlayerLeftGame(PlayerLeftGamePacket packet) {
	}

	public void onServerChat(ServerChatPacket packet) {
	}

	public void onServerClosing(ServerClosingPacket packet) {
	}

	//Game packets

	public void onDealNewCards(DealNewCardsPacket packet) {
	}

	public void onRunRound(RunRoundPacket packet) {
	}

	public void onUpdatePlayerStates(UpdatePlayerStatesPacket packet) {
	}

	//Lobby packets

	public void onGameStarting(GameStartingPacket packet) {
	}

	public void onLobbyInfo(LobbyInfoPacket packet) {
	}

	@Override
	public void handle(Server2ClientPacket packet) {
		if (packet instanceof GameStartingPacket) {
			onGameStarting((GameStartingPacket) packet);
		} else if (packet instanceof LobbyInfoPacket) {
			onLobbyInfo((LobbyInfoPacket) packet);
		} else if (packet instanceof DealNewCardsPacket) {
			onDealNewCards((DealNewCardsPacket) packet);
		} else if (packet instanceof RunRoundPacket) {
			onRunRound((RunRoundPacket) packet);
		} else if (packet instanceof UpdatePlayerStatesPacket) {
			onUpdatePlayerStates((UpdatePlayerStatesPacket) packet);
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
		} else if (packet instanceof ServerChatPacket) {
			onServerChat((ServerChatPacket) packet);
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
