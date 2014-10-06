package org.gnubridge.core.bidding;

import java.util.List;

import org.gnubridge.core.Card;
import org.gnubridge.core.Hand;
import org.gnubridge.core.deck.Suit;

public class TrickCalculator {
	protected Hand hand;

	public TrickCalculator(Hand hand) {
		this.hand = hand;
	}
	
	public int playingTricks() {
		int trickCount = 0;
		for (Suit color : Suit.list) {
			trickCount += doublePlayingTricks(color);
		}
		return trickCount / 2;
	}
	
	public int doublePlayingTricks(Suit color) {
		int tricks = 0;
		List<Card> cards = hand.getSuitHi2Low(color);
		int length = cards.size();
		if (length >= 1) {
			if (cards.get(0).getValue() == Card.ACE) tricks += 2;
			if (length >= 2) {
				if (tricks == 2) {
					if (cards.get(1).getValue() == Card.KING) tricks += 2;
					else if (cards.get(1).getValue() == Card.QUEEN) tricks++;
					if (length >= 3) {
						if (tricks == 4) {
							if (cards.get(2).getValue() == Card.QUEEN) tricks += 2;
							else if (cards.get(2).getValue() == Card.JACK) tricks++;
						} else if (tricks == 3) {
							if (cards.get(2).getValue() == Card.JACK) tricks += 2;
							else if (cards.get(2).getValue() == Card.TEN) tricks++;
						} else if (cards.get(1).getValue() == Card.JACK) {
							tricks += 1;
						}
					}
				} else if (cards.get(0).getValue() == Card.KING) {
					if (cards.get(1).getValue() == Card.QUEEN || cards.get(1).getValue() == Card.JACK) {
						if (length >= 3) {
							if (cards.get(1).getValue() == Card.QUEEN) tricks += 3;
							else if (cards.get(2).getValue() == Card.TEN) tricks += 3;
						} else if (cards.get(1).getValue() == Card.TEN) {
							tricks += 2;
						}
					}
				} else if (cards.get(0).getValue() == Card.QUEEN) {
					if (length >= 3) {
						if (cards.get(1).getValue() == Card.JACK) tricks += 2;
						else tricks++;
					}
				} else if (cards.get(0).getValue() == Card.JACK && cards.get(1).getValue() == Card.TEN) {
					tricks++;
				}
			}
			if (length > 3) {
				tricks += 2 * (length - 3);
			}
		}
		return tricks;
	}

}
