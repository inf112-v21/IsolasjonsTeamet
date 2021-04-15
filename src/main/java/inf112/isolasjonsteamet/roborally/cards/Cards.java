package inf112.isolasjonsteamet.roborally.cards;

import com.badlogic.gdx.graphics.Texture;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import inf112.isolasjonsteamet.roborally.actions.MoveForward;
import inf112.isolasjonsteamet.roborally.actions.RotateLeft;
import inf112.isolasjonsteamet.roborally.actions.RotateRight;
import java.util.function.Supplier;

/**
 * Holds all the different known card types.
 */
public class Cards {

	private static final BiMap<Integer, Card> cardRegistry = HashBiMap.create();
	private static int nextValidId = 0;

	public static final Card NO_CARD = register(new Card("", 0, texture("transparent1px.png")));

	public static final Card MOVE_ONE =
			register(new Card("Move one", 18, texture("move_1.jpg"), new MoveForward(1)));
	public static final Card MOVE_TWO =
			register(new Card("Move two", 12, texture("move_2.jpg"), new MoveForward(2)));
	public static final Card MOVE_THREE =
			register(new Card("Move three", 6, texture("move_3.jpg"), new MoveForward(3)));
	public static final Card BACK_UP =
			register(new Card("Back up", 6, texture("back_up.jpg"), new MoveForward(-1)));
	public static final Card ROTATE_RIGHT =
			register(new Card("Rotate right", 18, texture("rotate_right.jpg"), new RotateRight()));
	public static final Card ROTATE_LEFT =
			register(new Card("Rotate left", 18, texture("rotate_left.jpg"), new RotateLeft()));
	public static final Card U_TURN =
			register(new Card("U-Turn", 6, texture("u_turn.jpg"), new RotateRight()));

	private static Supplier<Texture> texture(String file) {
		return () -> new Texture(file);
	}

	private static Card register(Card card) {
		cardRegistry.put(nextValidId++, card);
		return card;
	}

	public static int getIdForCard(Card card) {
		return cardRegistry.inverse().get(card);
	}

	public static Card getCardFromRegistry(int registryId) {
		return cardRegistry.get(registryId);
	}
}