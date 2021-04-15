package inf112.isolasjonsteamet.roborally.network.impl;

import inf112.isolasjonsteamet.roborally.network.c2spackets.ClientDisconnectingPacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.GameJoinPacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.game.UpdatePlayerStatePacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.game.UpdateRoundReadyPacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.lobby.LobbyReadyUpdatePacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.lobby.RequestLobbyInfoPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.GameInfoPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.GameJoinResultPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.KickedPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.OtherPlayerKickedPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.PlayerJoinedGamePacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.PlayerLeftGamePacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.ServerClosingPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.game.DealNewCardsPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.game.RunRoundPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.game.UpdatePlayerStatesPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.lobby.GameStartingPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.lobby.LobbyInfoPacket;

/**
 * Holds everything known about the wire protocol the game uses to communicate.
 *
 * <p>
 * {@link #PROTOCOL} must be incremented any time an binary incompatible change is made to the wire protcol, which an
 * previous version might not understand.
 * </p>
 */
public class PacketProtocol {

	public static final int PROTOCOL = 1;
	public static final String REQUIRED_VERSION = "0.0.1";

	public static final PacketRegistration<?>[] REGISTRATIONS = new PacketRegistration[]{
			//Client -> Server packets
			//Game
			PacketRegistration.findCodec(UpdatePlayerStatePacket.class),
			PacketRegistration.findCodec(UpdateRoundReadyPacket.class),

			//Lobby
			PacketRegistration.findCodec(LobbyReadyUpdatePacket.class),
			PacketRegistration.findCodec(RequestLobbyInfoPacket.class),

			//Misc
			PacketRegistration.findCodec(GameJoinPacket.class),
			PacketRegistration.findCodec(ClientDisconnectingPacket.class),

			//Server -> Client packets
			//Game
			PacketRegistration.findCodec(DealNewCardsPacket.class),
			PacketRegistration.findCodec(RunRoundPacket.class),
			PacketRegistration.findCodec(UpdatePlayerStatesPacket.class),

			//Lobby
			PacketRegistration.findCodec(GameStartingPacket.class),
			PacketRegistration.findCodec(LobbyInfoPacket.class),

			//Misc
			PacketRegistration.findCodec(GameInfoPacket.class),
			PacketRegistration.findCodec(GameJoinResultPacket.class),
			PacketRegistration.findCodec(KickedPacket.class),
			PacketRegistration.findCodec(OtherPlayerKickedPacket.class),
			PacketRegistration.findCodec(PlayerJoinedGamePacket.class),
			PacketRegistration.findCodec(PlayerLeftGamePacket.class),
			PacketRegistration.findCodec(ServerClosingPacket.class),
	};
}
