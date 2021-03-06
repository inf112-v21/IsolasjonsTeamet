package inf112.isolasjonsteamet.roborally.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.cards.Card;
import inf112.isolasjonsteamet.roborally.cards.CardRow;
import inf112.isolasjonsteamet.roborally.cards.Cards;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Encapsulates a table which manages both card rows for a player. The cards can be draged between the different
 * locations.
 */
public class CardArea {

	private final Table table;
	private final DragAndDrop dragAndDrop = new DragAndDrop();

	private final Map<CardRow, List<Stack>> rowStacks = new HashMap<>();
	private final Map<CardRow, List<Card>> cards = new HashMap<>();
	private final Map<CardRow, Map<Integer, Source>> sources = new HashMap<>();

	private final CardUpdateListener cardUpdateListener;

	/**
	 * Creates and initializes the table with an empty set of cards.
	 */
	public CardArea(Skin skin, CardUpdateListener cardUpdateListener) {
		this.table = new Table(skin);
		this.cardUpdateListener = cardUpdateListener;

		var noCardDrawable = new TextureRegionDrawable(new Texture("no_card.png"));

		for (CardRow row : CardRow.values()) {
			List<Stack> stacks = new ArrayList<>();
			rowStacks.put(row, stacks);
			cards.put(row, new ArrayList<>());
			sources.put(row, new HashMap<>());

			table.add(row.getName() + " cards:");
			table.row();
			HorizontalGroup cardsGroup = new HorizontalGroup();
			table.add(cardsGroup).padBottom(10F);
			table.row();

			for (int col = 0; col < row.getSize(); col++) {
				var stack = new Stack();
				stacks.add(stack);
				stack.add(new Image(noCardDrawable));

				var stackContainer = new Container<>(stack);
				stackContainer.prefSize(64, 89);
				dragAndDrop.addTarget(new CardAreaTarget(stackContainer, row, col));

				cardsGroup.addActor(stackContainer);
			}
		}
	}

	public void setChosenCards(List<Card> cards) {
		updateCards(cards, CardRow.CHOSEN);
	}

	public void setGivenCards(List<Card> cards) {
		updateCards(cards, CardRow.GIVEN);
	}

	private void updateCards(List<Card> cards, CardRow row) {
		var oldCards = this.cards.get(row);
		var newCards = ImmutableList.copyOf(cards);
		this.cards.put(row, newCards);
		recalculateCards(oldCards, newCards, row);
	}

	private void recalculateCards(List<Card> oldCards, List<Card> newCards, CardRow row) {
		int cardsSize = Math.max(oldCards.size(), newCards.size());

		for (int i = 0; i < cardsSize; i++) {
			Card oldCard = Cards.NO_CARD;
			Card newCard = Cards.NO_CARD;

			if (oldCards.size() > i) {
				oldCard = oldCards.get(i);
			}
			if (newCards.size() > i) {
				newCard = newCards.get(i);
			}

			if (oldCard.equals(newCard)) {
				continue;
			}

			Stack stack = rowStacks.get(row).get(i);

			if (!oldCard.equals(Cards.NO_CARD)) {
				stack.getChildren().pop().remove();
				Source oldSource = sources.get(row).remove(i);
				if (oldSource != null) {
					dragAndDrop.removeSource(oldSource);
				}
			}

			if (!newCard.equals(Cards.NO_CARD)) {
				var newImage = new Image(newCard.getTexture());
				var newSource = new CardAreaSource(newImage, newCard, row, i);
				dragAndDrop.addSource(newSource);
				stack.addActor(newImage);
			}
		}
	}

	/**
	 * An listener for when the location of cards change.
	 */
	public interface CardUpdateListener {

		void moveCard(CardRow fromRow, int fromCol, CardRow toRow, int toCol);
	}

	private class CardAreaTarget extends Target {

		private final CardRow row;
		private final int col;

		public CardAreaTarget(Actor actor, CardRow row, int col) {
			super(actor);
			this.row = row;
			this.col = col;
		}

		@Override
		public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
			List<Card> rowCards = cards.get(row);
			return rowCards.size() <= col || rowCards.get(col).equals(Cards.NO_CARD);
		}

		@Override
		public void drop(Source source, Payload payload, float x, float y, int pointer) {
			if (!(source instanceof CardAreaSource)) {
				throw new IllegalStateException("Unknown source");
			}

			CardAreaSource cardSource = (CardAreaSource) source;
			cardUpdateListener.moveCard(cardSource.row, cardSource.col, row, col);
		}
	}

	private static class CardAreaSource extends Source {

		private final Card card;
		private final CardRow row;
		private final int col;

		public CardAreaSource(Actor actor, Card newCard, CardRow row, int col) {
			super(actor);
			this.card = newCard;
			this.row = row;
			this.col = col;
		}

		@Override
		public Payload dragStart(InputEvent event, float x, float y, int pointer) {
			var payload = new Payload();

			var dragCard = new Image(card.getTexture());
			dragCard.setSize(64, 89);
			payload.setDragActor(dragCard);

			return payload;
		}
	}

	public Table getTable() {
		return table;
	}
}
