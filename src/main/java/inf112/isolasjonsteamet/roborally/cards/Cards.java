package inf112.isolasjonsteamet.roborally.cards;

/**
 * Holds all the different known card types.
 */
public class Cards {

	//public static final CardType FOO = new CardType(123, new MoveForward(1), new RotateLeft());
	public static final CardType MOVE_ONE = new CardType(18, new MoveForwardAction(1));
	public static final CardType MOVE_TWO = new CardType(12, new MoveForwardAction(2));
	public static final CardType MOVE_THREE = new CardType(6, new MoveForwardAction(3));
	public static final CardType BACK_UP = new CardType(6, new MoveForwardAction(-1));
	public static final CardType ROTATE_RIGHT = new CardType(18, new RotateRightAction());
	public static final CardType ROTATE_LEFT = new CardType(18, new RotateLeftAction());
	public static final CardType U_TURN = new CardType(6, new UTurnAction());
}