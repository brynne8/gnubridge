package org.gnubridge.core.bidding.rules;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.Pass;
import org.gnubridge.core.deck.Clubs;
import org.gnubridge.core.deck.Diamonds;
import org.gnubridge.core.deck.Hearts;
import org.gnubridge.core.deck.NoTrump;
import org.gnubridge.core.deck.Spades;
import org.gnubridge.core.deck.Trump;

public class Rebid2NT extends Rebid {

	public Rebid2NT(Auctioneer a, Hand h) {
		super(a, h);
	}

	@Override
	protected Bid prepareBid() {
		Bid result = null;

		int rank = response.getValue();
		Trump trump = response.getTrump();
		if (rank == 3) {
			if (trump.isNoTrump()) {
				result = new Pass();
			}
			if (trump.equals(Clubs.i())) {
				if (hand.getSuitLength(Hearts.i()) >= 4) {
					result = new Bid(3, Hearts.i());
				} else if (hand.getSuitLength(Spades.i()) >= 4) {
					result = new Bid(3, Spades.i());
				} else {
					result = new Bid(3, Diamonds.i());
				}
			} else {
				if (trump.equals(Diamonds.i())) {
					result = new Bid(3, Hearts.i());
				} else if (trump.equals(Hearts.i())) {
					result = new Bid(3, Spades.i());
				}
			}
		}

		return result;
	}

	private boolean partnerWasRespondingToMy2NT() {
		return super.applies() && new Bid(2, NoTrump.i()).equals(opening);
	}

	@Override
	protected boolean applies() {
		return partnerWasRespondingToMy2NT();
	}

}
