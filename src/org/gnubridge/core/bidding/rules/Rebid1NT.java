package org.gnubridge.core.bidding.rules;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.Pass;
import org.gnubridge.core.bidding.PointCalculator;
import org.gnubridge.core.deck.Clubs;
import org.gnubridge.core.deck.Diamonds;
import org.gnubridge.core.deck.Hearts;
import org.gnubridge.core.deck.NoTrump;
import org.gnubridge.core.deck.Spades;
import org.gnubridge.core.deck.Trump;

public class Rebid1NT extends Rebid {

	public Rebid1NT(Auctioneer a, Hand h) {
		super(a, h);
	}

	@Override
	protected Bid prepareBid() {
		Bid result = null;
		PointCalculator pc = new PointCalculator(hand);

		int rank = response.getValue();
		Trump trump = response.getTrump();
		if (rank == 2) {
			if (trump.equals(Clubs.i())) {
				if (hand.getSuitLength(Hearts.i()) >= 4) {
					result = new Bid(2, Hearts.i());
				} else if (hand.getSuitLength(Spades.i()) >= 4) {
					result = new Bid(2, Spades.i());
				} else {
					result = new Bid(2, Diamonds.i());
				}
			} else if (trump.equals(Spades.i())) {
				if (hand.getSuitLength(Clubs.i()) >= 4) {
					result = new Bid(3, Clubs.i());
				} else if (hand.getSuitLength(Diamonds.i()) >= 4) {
					result = new Bid(3, Diamonds.i());
				} else {
					result = new Bid(2, NoTrump.i());
				}
			} else if (trump.isNoTrump()) {
				if (pc.getHighCardPoints() == 17) {
					result = new Bid(3, NoTrump.i());
				} else {
					result = new Pass();
				}
			} else if (trump.equals(Diamonds.i())) {
				if (pc.getCombinedPoints() >= 17) {
					result = new Bid(2, Spades.i());
				} else {
					result = new Bid(2, Hearts.i());
				}
			} else {
				if (pc.getCombinedPoints() >= 17) {
					result = new Bid(2, NoTrump.i());
				} else {
					result = new Bid(2, Spades.i());
				}
			}
		} else if (rank == 3) {
			if (trump.isNoTrump()) {
				result = new Pass();
			} else if (hand.getSuitLength(trump.asSuit()) >= 2) {
				if (trump.isMajorSuit()) {
					result = new Bid(4, trump);
				} else if (pc.getHighCardPoints() == 17) {
					result = new Bid(3, NoTrump.i());
				}
			}
		}

		return result;
	}

	private boolean partnerWasRespondingToMy1NT() {
		return super.applies() && new Bid(1, NoTrump.i()).equals(opening);
	}

	@Override
	protected boolean applies() {
		return partnerWasRespondingToMy1NT();
	}

}
