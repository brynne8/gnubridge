package org.gnubridge.core.bidding.rules;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.PointCalculator;
import org.gnubridge.core.deck.NoTrump;

public class Rebid1ColorWithNT extends RebidToLevel1Response {

	public Rebid1ColorWithNT(Auctioneer a, Hand h) {
		super(a, h);
	}

	@Override
	protected Bid prepareBid() {
		Bid result = null;
		PointCalculator pc = new PointCalculator(hand);
		int HCP = pc.getHighCardPoints();
		if (response.getTrump().isNoTrump()) {
			if (HCP >= 19 && (pc.isTame() || pc.isBalanced())) {
				result = new Bid(3, NoTrump.i());
			} else if (HCP >= 16 && pc.isTame()) {
				result = new Bid(2, NoTrump.i());
			}
		} else if (pc.isBalanced()) {
			if (HCP >= 12 && HCP <= 14) {
				result = new Bid(1, NoTrump.i());
			} else if (HCP >= 19 && HCP <= 20) {
				result = new Bid(2, NoTrump.i());
			}
		}
		return result;
	}

}
