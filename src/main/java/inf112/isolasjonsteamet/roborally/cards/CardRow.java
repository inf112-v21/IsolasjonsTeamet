package inf112.isolasjonsteamet.roborally.cards;

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
