package org.gnubridge.core.bidding.rules;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.PointCalculator;
import org.gnubridge.core.deck.NoTrump;

public class Open1NT extends BiddingRule {

	private PointCalculator pc;

	public Open1NT(Auctioneer a, Hand h) {
		super(a, h);
		pc = new PointCalculator(hand);
	}

	@Override
	protected Bid prepareBid() {
		return new Bid(1, NoTrump.i());
	}

	@Override
	protected boolean applies() {
		return auction.isOpeningBid() && pc.getHighCardPoints() >= 15
				&& pc.getHighCardPoints() <= 17 && pc.isBalanced();
	}

}
