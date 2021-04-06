package inf112.isolasjonsteamet.roborally.players;

import inf112.isolasjonsteamet.roborally.network.Client;
import org.checkerframework.checker.nullness.qual.Nullable;

public class PlayerInfo {

	private final String name;
	private final @Nullable Client client;
	private final boolean isLocallyPlayerControlled;

	public PlayerInfo(String name, @Nullable Client client, boolean isLocallyPlayerControlled) {
		this.name = name;
		this.client = client;
		this.isLocallyPlayerControlled = isLocallyPlayerControlled;
	}

	public String getName() {
		return name;
	}

	public Client getClient() {
		return client;
	}

	public boolean isLocallyPlayerControlled() {
		return isLocallyPlayerControlled;
	}
}
