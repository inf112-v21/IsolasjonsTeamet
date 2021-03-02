public class CardDeck {

	private int deckSize = 84; // Max amount of cards in deck
	private List<CardType> deck; // The deck itself

	public CardDeck() {
		deck = new List<CardType>(deckSize); // Create new deck with room for 84 cards
		fillDeck();
	}

	// Fills deck with cards
	void fillDeck() {
		// Some code that fills the deck with the correct cards
		// like:
		// for loop {
		// add cards that enable left turn, with priority and all that
		// }
		// etc for right turn, move 3, 2, 1, u-turn
	}

	// Shuffles deck randomly
	void shuffle() {
		Collections.shuffle(deck); // Randomly shuffle the deck
	}

	List<CardType> grabCards(int amount) {
		List<CardType> grabbedCards = new List<CardType>();

		for (int i = 0; i <= amount; i++) {
			grabbedCards.add(deck.get(0)); // Add the picked card from deck into list of new cards added to hand
			deck.remove(0); // Delete the picked card from the deck
		}

		return grabbedCards; // Return the list of cards to be added to player hand
	}

	// Removes all cards from deck and fills it again
	void reset() {
		deck.removeAll(); // Or something else
		fillDeck();
	}
}