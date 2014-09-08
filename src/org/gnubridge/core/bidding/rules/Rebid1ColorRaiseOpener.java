package org.gnubridge.core.bidding.rules;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.ResponseCalculator;
import org.gnubridge.core.deck.Trump;

public class Rebid1ColorRaiseOpener extends RebidToLevel2Response {

	public Rebid1ColorRaiseOpener(Auctioneer a, Hand h) {
		super(a, h);
	}

	@Override
	protected boolean applies() {
		return super.applies() && response.getTrump().isSuit()
				&& response.getTrump().equals(opening.getTrump());
	}

	@Override
	protected Bid prepareBid() {
		Trump trump = response.getTrump();
		if (trump.isMinorSuit()) {
			return new Bid(3, trump);
		} else {
			//TODO: new suit
			ResponseCalculator calc = new ResponseCalculator(hand, response);
			int points = calc.getCombinedPoints();
			if (points >= 19) {
				return new Bid(4, trump);
			} else if (points >= 16) {
				return new Bid(3, trump);
			}
		}
		return null;
	}

}
