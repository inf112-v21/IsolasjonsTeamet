package inf112.isolasjonsteamet.roborally.cards;

import com.badlogic.gdx.graphics.Texture;
import inf112.isolasjonsteamet.roborally.actions.MoveForward;
import inf112.isolasjonsteamet.roborally.actions.RotateLeft;
import inf112.isolasjonsteamet.roborally.actions.RotateRight;
import java.util.function.Supplier;

/**
 * Holds all the different known card types.
 */
public class Cards {

	public static final CardType MOVE_ONE =
			new CardType("Move one", 18, texture("move_1.jpg"), new MoveForward(1));
	public static final CardType MOVE_TWO =
			new CardType("Move two", 12, texture("move_2.jpg"), new MoveForward(2));
	public static final CardType MOVE_THREE =
			new CardType("Move three", 6, texture("move_3.jpg"), new MoveForward(3));
	public static final CardType BACK_UP =
			new CardType("Back up", 6, texture("back_up.jpg"), new MoveForward(-1));
	public static final CardType ROTATE_RIGHT =
			new CardType("Rotate right", 18, texture("rotate_right.jpg"), new RotateRight());
	public static final CardType ROTATE_LEFT =
			new CardType("Rotate left", 18, texture("rotate_left.jpg"), new RotateLeft());
	public static final CardType U_TURN =
			new CardType("U-Turn", 6, texture("u_turn.jpg"), new RotateRight());

	private static Supplier<Texture> texture(String file) {
		return () -> new Texture(file);
	}
}