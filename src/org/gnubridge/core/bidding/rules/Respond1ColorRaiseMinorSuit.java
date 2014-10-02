package org.gnubridge.core.bidding.rules;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.ResponseCalculator;
import org.gnubridge.core.deck.Trump;

public class Respond1ColorRaiseMinorSuit extends Response {

	private ResponseCalculator calc;

	public Respond1ColorRaiseMinorSuit(Auctioneer a, Hand h) {
		super(a, h);
	}

	@Override
	protected boolean applies() {
		boolean result = false;
		if (super.applies()) {
			calc = new ResponseCalculator(hand, partnersOpeningBid);
			if (partnersOpeningBid.getTrump().isMinorSuit() && partnersOpeningBid.getValue() == 1
					&& hand.getSuitLength(partnersOpeningBid.getTrump().asSuit()) >= 4) {
				result = true;
			}
		}
		return result;
	}

	@Override
	protected Bid prepareBid() {
		int points = calc.getCombinedPoints();
		Trump trump = partnersOpeningBid.getTrump();
		if (points >= 10 && (hand.getSuitLength(trump.asSuit()) != 4 
				|| calc.getHighCardPoints(hand.getSuitHi2Low(trump.asSuit())) > 4)) {
			return new Bid(2, trump);
		}
		int vulnerabilityIndex = auction.getVulnerabilityIndex();
		if (hand.getSuitLength(trump.asSuit()) >= 5 && (
				(vulnerabilityIndex >= 2 && points >= 5 && points <= 9) ||
				(vulnerabilityIndex == 1 && points <= 8) ||
				(vulnerabilityIndex == 0 && points >= 5 && points <= 8))) {
			return new Bid(3, trump);
		}
		return null;
	}

}
