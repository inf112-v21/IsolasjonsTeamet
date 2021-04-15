package inf112.isolasjonsteamet.roborally.players;

import inf112.isolasjonsteamet.roborally.network.Client;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Basic info about a player before one has been constructed.
 */
public class PlayerInfo {

	private final String name;
	private final @Nullable Client client;
	private final boolean isLocallyManuallyControlled;

	/**
	 * Creates a new player info.
	 *
	 * @param name The name of the player.
	 * @param client If this is a locally controlled player, the client associated with it.
	 * @param isLocallyManuallyControlled Designates if this is a locally controlled player, if the it can be switched
	 * 		to, and controlled manually.
	 */
	public PlayerInfo(String name, @Nullable Client client, boolean isLocallyManuallyControlled) {
		this.name = name;
		this.client = client;
		this.isLocallyManuallyControlled = isLocallyManuallyControlled;
	}

	public String getName() {
		return name;
	}

	@Nullable
	public Client getClient() {
		return client;
	}

	public boolean isLocallyManuallyControlled() {
		return isLocallyManuallyControlled;
	}
}
