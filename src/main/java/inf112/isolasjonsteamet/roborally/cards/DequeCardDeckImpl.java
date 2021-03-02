package inf112.isolasjonsteamet.roborally.cards;

import com.google.common.collect.ImmutableList;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Random;

/**
 * An implementation of {@link CardDeck} using a deque as the backing collection.
 */
public class DequeCardDeckImpl implements CardDeck {

	private final List<CardType> fullDeckContent;
	private final Deque<CardType> deck;
	private final Random rng;

	/**
	 * Creates a new instance using the contents of a list as what should appear in the deck.
	 *
	 * @param contents All the cards that should appear in the deck.
	 * @param rng The random instance to use when shuffling the list.
	 */
	public DequeCardDeckImpl(List<CardType> contents, Random rng) {
		fullDeckContent = contents;
		deck = new ArrayDeque<>(contents.size());
		this.rng = rng;
		reset();
	}

	/**
	 * Creates a new instance using the contents of a list as what should appear in the deck. Uses a generated random.
	 *
	 * @param contents All the cards that should appear in the deck.
	 */
	public DequeCardDeckImpl(List<CardType> contents) {
		this(contents, new Random());
	}

	/**
	 * {@inheritDoc}
	 */
	public List<CardType> grabCards(int amount) {
		List<CardType> grabbedCards = new ArrayList<>();

		for (int i = 0; i < amount; i++) {
			grabbedCards.add(deck.removeFirst());
		}

		return ImmutableList.copyOf(grabbedCards);
	}

	/**
	 * {@inheritDoc}
	 */
	public void reset() {
		deck.clear();

		var contentsCopy = new ArrayList<>(fullDeckContent);
		Collections.shuffle(contentsCopy, rng);
		deck.addAll(contentsCopy);
	}
}