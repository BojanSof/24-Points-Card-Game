package game;

public class Card {
	private final String card;
	private final String value;
	
	public Card(String card, String value) {
		this.card = card;
		this.value = value;
	}
	
	public String getCard() {
		return this.card;
	}
	
	public String getValue() {
		return this.value;
	}
}
