package inf112.isolasjonsteamet.roborally.cards;

/**
 * Players have two rows of cards. Their chosen cards, which are the cards they have chosen to play, in that order, and
 * their given cards, which is what was dealt to them by the server.
 */
public enum CardRow {
	CHOSEN("Chosen"), GIVEN("Given");

	private final String name;

	CardRow(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
