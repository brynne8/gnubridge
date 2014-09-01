package org.gnubridge.core.bidding.rules;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.PointCalculator;
import org.gnubridge.core.deck.NoTrump;
import org.gnubridge.core.deck.Trump;

public class Respond1ColorWithNT extends Response {

	private PointCalculator calculator;

	public Respond1ColorWithNT(Auctioneer a, Hand h) {
		super(a, h);
	}

	@Override
	protected boolean applies() {
		boolean result = false;
		if (super.applies()) {
			calculator = new PointCalculator(hand);
			if (partnersOpeningBid.getTrump().isSuit() && partnersOpeningBid.getValue() == 1
					&& calculator.getHighCardPoints() >= 6) {
				result = true;
			}
		}
		return result;
	}

	@Override
	protected Bid prepareBid() {
		Bid result = null;
		Trump trump = partnersOpeningBid.getTrump();
		int HCP = calculator.getHighCardPoints();
		if (trump.isMajorSuit()) {
			if (HCP >= 12 && calculator.isBalanced()) {
				if (HCP <= 15 && hand.getSuitLength(trump.asSuit()) >= 4) {
					result = new Bid(3, NoTrump.i());
				} else if (HCP >= 13) {
					result = new Bid(2, NoTrump.i());
				}
			} else if (HCP <= 12) {
				result = new Bid(1, NoTrump.i());
				result.makeForcing();
			}
		} else {
			if (HCP <= 10) {
				result = new Bid(1, NoTrump.i());
			} else if (HCP >= 11 && HCP <= 12 && calculator.isBalanced()) {
				result = new Bid(2, NoTrump.i());
			} else if (HCP >= 13 && HCP <= 15 && calculator.isBalanced()) {
				result = new Bid(3, NoTrump.i());
			}
		}
		return result;
	}

}
