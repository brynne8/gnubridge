package org.gnubridge.core.bidding.rules;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.ResponseCalculator;
import org.gnubridge.core.deck.Trump;

public class Respond1ColorRaiseMinorSuit extends Response {

	private ResponseCalculator calculator;

	public Respond1ColorRaiseMinorSuit(Auctioneer a, Hand h) {
		super(a, h);
	}

	@Override
	protected boolean applies() {
		boolean result = false;
		if (super.applies()) {
			calculator = new ResponseCalculator(hand, partnersOpeningBid);
			if (partnersOpeningBid.getTrump().isMinorSuit() && partnersOpeningBid.getValue() == 1
					&& calculator.getCombinedPoints() >= 5
					&& hand.getSuitLength(partnersOpeningBid.getTrump().asSuit()) >= 4) {
				result = true;
			}
		}
		return result;
	}

	@Override
	protected Bid prepareBid() {
		int points = calculator.getCombinedPoints();
		Trump trump = partnersOpeningBid.getTrump();
		if (points >= 10 && (hand.getSuitLength(trump.asSuit()) != 4 
				|| calculator.getHighCardPoints(hand.getSuitHi2Low(trump.asSuit())) > 4)) {
			return new Bid(2, trump);
		} else if (hand.getSuitLength(trump.asSuit()) >= 6) {
			return new Bid(3, trump);
		} else {
			return null;
		}
	}

}
