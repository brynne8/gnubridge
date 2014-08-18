package org.gnubridge.core.bidding.rules;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.PointCalculator;
import org.gnubridge.core.deck.NoTrump;

public class Open2NT extends BiddingRule {

	private PointCalculator pc;

	public Open2NT(Auctioneer a, Hand h) {
		super(a, h);
		pc = new PointCalculator(hand);
	}

	@Override
	protected Bid prepareBid() {
		return new Bid(2, NoTrump.i());
	}

	@Override
	protected boolean applies() {
		return auction.isOpeningBid() && pc.getHighCardPoints() >= 20
				&& pc.getHighCardPoints() <= 21 && pc.isBalanced();
	}

}
