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
	protected boolean applies() {
		PointCalculator pc = new PointCalculator(hand);
		return super.applies() && pc.isBalanced();
	}

	@Override
	protected Bid prepareBid() {
		PointCalculator pc = new PointCalculator(hand);
		int HCP = pc.getHighCardPoints();
		if (HCP >= 12 && HCP <= 14) {
			return new Bid(1, NoTrump.i());
		} else if (HCP >= 18 && HCP <= 19) {
			return new Bid(2, NoTrump.i());
		} else {
			return null;
		}
	}

}
