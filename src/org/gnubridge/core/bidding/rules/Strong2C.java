package org.gnubridge.core.bidding.rules;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.PointCalculator;
import org.gnubridge.core.deck.Clubs;

public class Strong2C extends BiddingRule {

	private PointCalculator pc;

	public Strong2C(Auctioneer a, Hand h) {
		super(a, h);
		pc = new PointCalculator(hand);
	}

	@Override
	protected Bid prepareBid() {
		return new Bid(2, Clubs.i());
	}

	@Override
	protected boolean applies() {
		return auction.isOpeningBid() && pc.getCombinedPoints() >= 23;
	}

}