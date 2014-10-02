package org.gnubridge.core.bidding.rules;

import java.util.List;

import org.gnubridge.core.Card;
import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.PointCalculator;
import org.gnubridge.core.deck.Clubs;
import org.gnubridge.core.deck.Hearts;
import org.gnubridge.core.deck.Spades;
import org.gnubridge.core.deck.Suit;

public class WeakTwo extends BiddingRule {

	private PointCalculator pc;
	private Suit sixCardSuit;

	public WeakTwo(Auctioneer a, Hand h) {
		super(a, h);
		pc = new PointCalculator(hand);
	}

	@Override
	protected boolean applies() {
		if (auction.isOpeningBid() && pc.getHighCardPoints() >= 6 && pc.getHighCardPoints() <= 11) {
			sixCardSuit = hand.getLongestSuit();
			List<Card> cards = hand.getSuitHi2Low(sixCardSuit);
			if (sixCardSuit.equals(Clubs.i()) || hand.getSuitLength(sixCardSuit) < 6
					|| cards.get(0).getValue() < Card.QUEEN) {
				return false;
			}
			if (auction.getVulnerabilityIndex() >= 2) {
				int bigThree = 0, bigFive = 0;
				for (int i = 0; i < 5; ++i) {
					int value = cards.get(i).getValue();
					if (value >= Card.QUEEN) {
						bigThree++;
					}
					if (value >= Card.TEN) {
						bigFive++;
					}
				}
				if (bigThree < 2 && bigFive < 3) {
					return false;
				}
			}
			if (!sixCardSuit.equals(Hearts.i()) && hand.getSuitLength(Hearts.i()) >= 4
					&& hand.getSuitHi2Low(Hearts.i()).get(0).getValue() >= Card.QUEEN) {
				return false;
			}
			if (!sixCardSuit.equals(Spades.i()) && hand.getSuitLength(Spades.i()) >= 4
					&& hand.getSuitHi2Low(Spades.i()).get(0).getValue() >= Card.QUEEN) {
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	protected Bid prepareBid() {
		return new Bid(2, sixCardSuit);
	}

}