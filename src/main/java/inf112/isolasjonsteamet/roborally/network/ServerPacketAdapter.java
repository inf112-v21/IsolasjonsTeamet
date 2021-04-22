package inf112.isolasjonsteamet.roborally.network;

import inf112.isolasjonsteamet.roborally.network.c2spackets.Client2ServerPacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.ClientChatPacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.ClientDisconnectingPacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.GameJoinPacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.KickPlayerPacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.game.UpdatePlayerStatePacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.game.UpdateRoundReadyPacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.lobby.LobbyReadyUpdatePacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.lobby.RequestLobbyInfoPacket;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * An adapter for packets sent from the client to the server,
 * such that instanceof checks are not needed.
 */
@SuppressWarnings("unused")
public abstract class ServerPacketAdapter implements ServerPacketListener<Client2ServerPacket> {

	//Misc packets

	public void onClientChat(@Nullable String player, ClientChatPacket packet) {
	}

	public void onClientDisconnecting(@Nullable String player, ClientDisconnectingPacket packet) {
	}

	public void onGameJoin(@Nullable String player, GameJoinPacket packet) {
	}

	public void onKickPlayer(@Nullable String player, KickPlayerPacket packet) {
	}

	//Game packets

	public void onUpdatePlayerState(@Nullable String player, UpdatePlayerStatePacket packet) {
	}

	public void onUpdateRoundReady(@Nullable String player, UpdateRoundReadyPacket packet) {
	}

	//Lobby packets

	public void onRequestLobbyInfo(@Nullable String player, RequestLobbyInfoPacket packet) {
	}

	public void onLobbyReadyUpdate(@Nullable String player, LobbyReadyUpdatePacket packet) {
	}

	@Override
	public void handle(@Nullable String player, Client2ServerPacket packet) {
		if (packet instanceof LobbyReadyUpdatePacket) {
			onLobbyReadyUpdate(player, (LobbyReadyUpdatePacket) packet);
		} else if (packet instanceof RequestLobbyInfoPacket) {
			onRequestLobbyInfo(player, (RequestLobbyInfoPacket) packet);
		} else if (packet instanceof UpdatePlayerStatePacket) {
			onUpdatePlayerState(player, (UpdatePlayerStatePacket) packet);
		} else if (packet instanceof UpdateRoundReadyPacket) {
			onUpdateRoundReady(player, (UpdateRoundReadyPacket) packet);
		} else if (packet instanceof ClientChatPacket) {
			onClientChat(player, (ClientChatPacket) packet);
		} else if (packet instanceof ClientDisconnectingPacket) {
			onClientDisconnecting(player, (ClientDisconnectingPacket) packet);
		} else if (packet instanceof GameJoinPacket) {
			onGameJoin(player, (GameJoinPacket) packet);
		} else if (packet instanceof KickPlayerPacket) {
			onKickPlayer(player, (KickPlayerPacket) packet);
		} else {
			throw new IllegalArgumentException("Unknown C2S packet type " + packet.getClass());
		}
	}

	@Override
	public @Nullable Client2ServerPacket refine(Client2ServerPacket packet) {
		return packet;
	}
}
