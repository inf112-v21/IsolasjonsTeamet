package inf112.isolasjonsteamet.roborally.network.impl;

import inf112.isolasjonsteamet.roborally.network.c2spackets.ClientDisconnectingPacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.GameJoinPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.GameInfoPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.GameJoinResultPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.ServerClosingPacket;

public class PacketProtocol {

	public static final int PROTOCOL = 1;
	public static final String REQUIRED_VERSION = "0.0.1";

	public static final PacketRegistration<?>[] REGISTRATIONS = new PacketRegistration[]{
			//Client -> Server packets
			PacketRegistration.makeCodec(GameJoinPacket.class, GameJoinPacket.PacketCodec.class),
			PacketRegistration.makeCodec(ClientDisconnectingPacket.class, ClientDisconnectingPacket.PacketCodec.class),
			//Server -> Client packets
			PacketRegistration.makeCodec(GameInfoPacket.class, GameInfoPacket.PacketCodec.class),
			PacketRegistration.makeCodec(GameJoinResultPacket.class, GameJoinResultPacket.PacketCodec.class),
			PacketRegistration.makeCodec(ServerClosingPacket.class, ServerClosingPacket.PacketCodec.class),
	};
}
