package inf112.isolasjonsteamet.roborally.actions;

/**
 * An action which indicates the player will move forward.
 */
public class MoveForward implements Action {

	private final int amount;

	public MoveForward(int amount) {
		this.amount = amount;
	}
}
