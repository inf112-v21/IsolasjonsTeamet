package inf112.isolasjonsteamet.roborally.network.impl;

/**
 * Thrown to indicate that a client has tried to connect to an incompatible server.
 */
public class ProtocolException extends Exception {

	private final int protocol;
	private final String requiredVersion;

	/**
	 * Construct a protocol exception from the required version the server needs, in both a machine and human readable
	 * format.
	 *
	 * @param protocol The underlying protocol version the server requires.
	 * @param requiredVersion A human readable version which the server requires.
	 */
	public ProtocolException(int protocol, String requiredVersion) {
		super(("Tried to connect to server with protocol version %d, but our protocol version is %d. "
				+ "This server requires version %s").formatted(protocol, PacketProtocol.PROTOCOL, requiredVersion));
		this.protocol = protocol;
		this.requiredVersion = requiredVersion;
	}

	public int getProtocol() {
		return protocol;
	}

	public String getRequiredVersion() {
		return requiredVersion;
	}
}
