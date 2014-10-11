package org.gnubridge.core.bidding.rules;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.PointCalculator;
import org.gnubridge.core.deck.Diamonds;
import org.gnubridge.core.deck.Suit;
import org.gnubridge.core.deck.Clubs;
import org.gnubridge.core.deck.NoTrump;

public class Respond2C extends Response {

	private PointCalculator pc;

	public Respond2C(Auctioneer a, Hand h) {
		super(a, h);
	}

	@Override
	protected Bid prepareBid() {
		pc = new PointCalculator(hand);
		Bid result = null;
		if (pc.getHighCardPoints() >= 8) {
			Suit longest = null;
			for (Suit color : Suit.mmList) {
				if (hand.getSuitLength(color) >= 5) {
					if (hand.AisStronger(color, longest)) {
						longest = color;
					}
				}
			}
			if (longest != null) {
				if (longest.isMajorSuit()) {
					result = new Bid(2, longest);
					result.makeGameForcing();
				} else {
					result = new Bid(3, longest);
					result.makeGameForcing();
				}
			}
			if (result == null && pc.isBalanced()) {
				result = new Bid(2, NoTrump.i());
				result.makeGameForcing();
			}
		}
		if (result == null) {
			result = new Bid(2, Diamonds.i());
		}
		return result;
	}

	@Override
	protected boolean applies() {
		return super.applies() && new Bid(2, Clubs.i()).equals(partnersOpeningBid)
				&& auction.isOpening(partnersOpeningBid);
	}
}
