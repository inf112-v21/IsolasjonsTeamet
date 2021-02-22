package inf112.isolasjonsteamet.roborally.cards;

import inf112.isolasjonsteamet.roborally.actions.ActionImpl;
import inf112.isolasjonsteamet.roborally.actions.MoveForward;
import inf112.isolasjonsteamet.roborally.actions.RotateLeft;
import inf112.isolasjonsteamet.roborally.actions.RotateRight;
import javax.swing.plaf.basic.BasicArrowButton;

/**
 * Holds all the different known card types.
 */
public class Cards {

	public static final CardType MOVE_ONE = new CardType(18, new MoveForward(1));
	public static final CardType MOVE_TWO = new CardType(12, new MoveForward(2));
	public static final CardType MOVE_THREE = new CardType(6, new MoveForward(3));
	public static final CardType BACK_UP = new CardType(6, new MoveForward(-1));
	public static final CardType ROTATE_RIGHT = new CardType(18, new RotateRight());
	public static final CardType ROTATE_LEFT = new CardType(18, new RotateLeft());
	public static final CardType U_TURN = new CardType(6, new RotateRight());
}