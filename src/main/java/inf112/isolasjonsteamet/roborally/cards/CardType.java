package inf112.isolasjonsteamet.roborally.cards;

import com.badlogic.gdx.graphics.Texture;
import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.actions.Action;
import java.util.List;
import java.util.function.Supplier;

/**
 * Describes a unique card type.
 */
public class CardType {

	private final String name;
	private final int priority;
	private final List<Action> actions;

	private final Supplier<Texture> makeTexture;
	private Texture texture;

	public CardType(String name, int priority, Supplier<Texture> makeTexture, List<Action> actions) {
		this.name = name;
		this.priority = priority;
		this.actions = ImmutableList.copyOf(actions);
		this.makeTexture = makeTexture;
	}

	public CardType(String name, int priority, Supplier<Texture> makeTexture, Action... actions) {
		this.name = name;
		this.priority = priority;
		this.makeTexture = makeTexture;
		this.actions = ImmutableList.copyOf(actions);
	}

	public String getName() {
		return name;
	}

	public int getPriority() {
		return priority;
	}

	public List<Action> getActions() {
		return actions;
	}

	/**
	 * Initializes the texture for this card, and returns it. Must only be called on the client side.
	 */
	public Texture getTexture() {
		if (texture == null) {
			synchronized (this) {
				if (texture == null) {
					texture = makeTexture.get();
				}
			}
		}

		return texture;
	}

	@Override
	public String toString() {
		return actions.toString();
	}
}
