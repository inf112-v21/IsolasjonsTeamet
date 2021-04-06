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

	private static final BiMap<Integer, CardType> cardRegistry = HashBiMap.create();
	private static int nextValidId = 0;

	public static final CardType NO_CARD = register(new CardType("", 0, texture("transparent1px.png")));

	public static final CardType MOVE_ONE =
			register(new CardType("Move one", 18, texture("move_1.jpg"), new MoveForward(1)));
	public static final CardType MOVE_TWO =
			register(new CardType("Move two", 12, texture("move_2.jpg"), new MoveForward(2)));
	public static final CardType MOVE_THREE =
			register(new CardType("Move three", 6, texture("move_3.jpg"), new MoveForward(3)));
	public static final CardType BACK_UP =
			register(new CardType("Back up", 6, texture("back_up.jpg"), new MoveForward(-1)));
	public static final CardType ROTATE_RIGHT =
			register(new CardType("Rotate right", 18, texture("rotate_right.jpg"), new RotateRight()));
	public static final CardType ROTATE_LEFT =
			register(new CardType("Rotate left", 18, texture("rotate_left.jpg"), new RotateLeft()));
	public static final CardType U_TURN =
			register(new CardType("U-Turn", 6, texture("u_turn.jpg"), new RotateRight()));

	private static Supplier<Texture> texture(String file) {
		return () -> new Texture(file);
	}

	private static CardType register(CardType card) {
		cardRegistry.put(nextValidId++, card);
		return card;
	}

	public static int getIdForCard(CardType card) {
		return cardRegistry.inverse().get(card);
	}

	public static CardType getCardFromRegistry(int registryId) {
		return cardRegistry.get(registryId);
	}
}