package org.gnubridge.core.bidding.rules;

import static org.gnubridge.core.bidding.Bid.*;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.PointCalculator;

public class Overcall1NT extends BiddingRule {

	private final PointCalculator pc;

	public Overcall1NT(Auctioneer a, Hand h) {
		super(a, h);
		pc = new PointCalculator(hand);
	}

	@Override
	protected boolean applies() {
		return ((auction.may2ndOvercall() && pc.getHighCardPoints() >= 15 && pc.getHighCardPoints() <= 18) ||
				(auction.may4thOvercall() && pc.getHighCardPoints() >= 12 && pc.getHighCardPoints() <= 15))
				&& pc.isBalanced() && haveStopperInEnemySuit();
	}

	@Override
	protected Bid prepareBid() {
		return ONE_NOTRUMP;
	}

}
