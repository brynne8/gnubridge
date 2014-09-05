package org.gnubridge.core.bidding.rules;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.PointCalculator;
import org.gnubridge.core.deck.NoTrump;

public class RebidAfter1NT extends RebidToLevel1Response {

	public RebidAfter1NT(Auctioneer a, Hand h) {
		super(a, h);
	}

	@Override
	protected boolean applies() {
		return super.applies() && response.getTrump().isNoTrump();
	}

	@Override
	protected Bid prepareBid() {
		PointCalculator pc = new PointCalculator(hand);
		
		int HCP = pc.getHighCardPoints();
		if (HCP >= 19 && (pc.isTame() || pc.isBalanced())) {
			return new Bid(3, NoTrump.i());
		} else if (HCP >= 16 && pc.isTame()) {
			return new Bid(2, NoTrump.i());
		} else {
			return null;
		}

	}

}
