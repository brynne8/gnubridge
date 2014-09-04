package org.gnubridge.core.bidding.rules;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.ResponseCalculator;
import org.gnubridge.core.deck.Trump;

public class Respond1ColorRaiseMajorSuit extends Response {

	private ResponseCalculator calculator;

	public Respond1ColorRaiseMajorSuit(Auctioneer a, Hand h) {
		super(a, h);
	}

	@Override
	protected boolean applies() {
		boolean result = false;
		if (super.applies()) {
			calculator = new ResponseCalculator(hand, partnersOpeningBid);
			if (partnersOpeningBid.hasTrump() && partnersOpeningBid.getTrump().isMajorSuit()
					&& partnersOpeningBid.getValue() == 1 && calculator.getCombinedPoints() >= 8
					&& hand.getSuitLength(partnersOpeningBid.getTrump().asSuit()) >= 3) {
				result = true;
			}
		}

		return result;
	}

	@Override
	protected Bid prepareBid() {
		int points = calculator.getCombinedPoints();
		Trump trump = partnersOpeningBid.getTrump();
		if (points >= 10 && points <= 12 && hand.getSuitLength(trump.asSuit()) >= 4) {
			return new Bid(3, trump);
		} else if (points >= 8 && points <= 10) {
			return new Bid(2, trump);
		}  else {
			return null;
		}
	}

}
