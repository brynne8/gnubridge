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

		Bid partnersBid = auction.getPartnersLastCall().getBid();
		int rank = partnersBid.getValue();
		Trump trump = partnersBid.getTrump();
		if (rank == 2) {
			if (trump.equals(Clubs.i())) {
				if (hand.getSuitLength(Hearts.i()) == 4) {
					result = new Bid(2, Hearts.i());
				} else if (hand.getSuitLength(Spades.i()) == 4) {
					result = new Bid(2, Spades.i());
				} else {
					result = new Bid(2, Diamonds.i());
				}
			} else if (trump.isNoTrump()) {
				PointCalculator pc = new PointCalculator(hand);
				if (pc.getHighCardPoints() == 17) {
					result = new Bid(3, NoTrump.i());
				} else {
					result = new Pass();
				}
			} else {
				if (trump.equals(Diamonds.i())) {
					result = new Bid(2, Hearts.i());
				} else {
					result = new Bid(2, Spades.i());
				}
			}
		} else if (rank == 3) {
			result = new Bid(3, NoTrump.i()); //TODO
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
