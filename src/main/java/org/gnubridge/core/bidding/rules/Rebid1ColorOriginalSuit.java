package org.gnubridge.core.bidding.rules;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.PointCalculator;
import org.gnubridge.core.deck.NoTrump;

public class Rebid1ColorOriginalSuit extends RebidToLevel1Response {

	public Rebid1ColorOriginalSuit(Auctioneer a, Hand h) {
		super(a, h);
	}

	@Override
	protected boolean applies() {
		return super.applies() && opening.getTrump().isSuit()
				&& !response.getTrump().equals(opening.getTrump());
	}

	@Override
	protected Bid prepareBid() {
		int points = new PointCalculator(hand).getCombinedPoints();
		if (opening.getTrump().isMajorSuit()) {
			if (response.equals(new Bid(3, NoTrump.i()))) {
				return new Bid(4, opening.getTrump());
			}
			if (points >= 19 && hand.getSuitLength(opening.getTrump().asSuit()) >= 7) {
				return new Bid(4, opening.getTrump());
			}
		}
		if (hand.getSuitLength(opening.getTrump().asSuit()) >= 6) {
			if (points <= 15) {
				return new Bid(2, opening.getTrump());
			} else if (points <= 18) {
				return new Bid(3, opening.getTrump());
			}
		}
		return null;
	}

}
