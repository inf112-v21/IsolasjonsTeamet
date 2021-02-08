package inf112.isolasjonsteamet.roborally.network.impl;

import inf112.isolasjonsteamet.roborally.network.c2spackets.ClientDisconnectingPacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.GameJoinPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.GameInfoPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.GameJoinResultPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.PlayerJoinedGamePacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.ServerClosingPacket;

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
			PacketRegistration.findCodec(GameJoinPacket.class),
			PacketRegistration.findCodec(ClientDisconnectingPacket.class),
			//Server -> Client packets
			PacketRegistration.findCodec(GameInfoPacket.class),
			PacketRegistration.findCodec(GameJoinResultPacket.class),
			PacketRegistration.findCodec(PlayerJoinedGamePacket.class),
			PacketRegistration.findCodec(ServerClosingPacket.class),
	};
}
