package org.gnubridge.core.bidding.rules;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.Pass;
import org.gnubridge.core.bidding.PointCalculator;
import org.gnubridge.core.deck.Clubs;
import org.gnubridge.core.deck.Diamonds;
import org.gnubridge.core.deck.Suit;
import org.gnubridge.core.deck.Hearts;
import org.gnubridge.core.deck.NoTrump;
import org.gnubridge.core.deck.Spades;

public class Respond2NT extends Response {

	private PointCalculator pc;

	public Respond2NT(Auctioneer a, Hand h) {
		super(a, h);
	}

	@Override
	protected Bid prepareBid() {
		pc = new PointCalculator(hand);
		Bid result = null;
		Suit longer = Hearts.i();
		int length = hand.getSuitLength(Hearts.i());
		if (hand.getSuitLength(Spades.i()) > length) {
			longer = Spades.i();
			length = hand.getSuitLength(Spades.i());
		}
		int points = pc.getCombinedPoints();
		if (length > 3) {
			if (length == 4) {
				if (points >= 5) {
					result = new Bid(3, Clubs.i());
				}
			} else if (longer.equals(Hearts.i())) {
				result = new Bid(3, Diamonds.i());
			} else {
				result = new Bid(3, Hearts.i());
			}
		}
		if (result == null) {
			if (pc.getHighCardPoints() >= 5) {
				result = new Bid(3, NoTrump.i());
			} else {
				result = new Pass();
			}
		}
		return result;
	}

	@Override
	protected boolean applies() {
		return super.applies() && new Bid(2, NoTrump.i()).equals(partnersOpeningBid);
	}
}
