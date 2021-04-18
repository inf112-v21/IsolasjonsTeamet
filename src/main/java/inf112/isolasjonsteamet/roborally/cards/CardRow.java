package inf112.isolasjonsteamet.roborally.cards;

/**
 * Players have two rows of cards. Their chosen cards, which are the cards they have chosen to play, in that order, and
 * their given cards, which is what was dealt to them by the server.
 */
public enum CardRow {
	CHOSEN(5, "Chosen"), GIVEN(9, "Given");

	private final int size;
	private final String name;

	CardRow(int size, String name) {
		this.size = size;
		this.name = name;
	}

	public int getSize() {
		return size;
	}

	public String getName() {
		return name;
	}
}
