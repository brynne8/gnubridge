package org.gnubridge.core.bidding.rules;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.PointCalculator;
import org.gnubridge.core.deck.Clubs;
import org.gnubridge.core.deck.Diamonds;
import org.gnubridge.core.deck.Spades;

public class RebidMinorSuitStayman extends PRebidNoTrump {

	public RebidMinorSuitStayman(Auctioneer a, Hand h) {
		super(a, h);
	}

	@Override
	protected boolean applies() {
		return super.applies() && response.getTrump().equals(Spades.i())
				&& response.getValue() == 2;
	}

	@Override
	protected Bid prepareBid() {
		PointCalculator pc = new PointCalculator(hand);
		Bid result = null;

		int points = fourthOvercalled ? pc.getCombinedPoints() - 3 : pc.getCombinedPoints();
		int clubs = hand.getSuitLength(Clubs.i());
		int diamonds = hand.getSuitLength(Diamonds.i());
		if (points <= 7 && diamonds >= 5) {
			if (diamonds >= 6) {
				result = new Bid(3, Diamonds.i());
			} else if (clubs >= 5) {
				result = new Bid(3, Clubs.i());
			}
			if (auction.isValid(result)) {
				return result;
			}
		}

		return result;
	}

}
