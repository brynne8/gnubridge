package org.gnubridge.core.bidding.rules;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.PointCalculator;
import org.gnubridge.core.deck.Clubs;
import org.gnubridge.core.deck.Diamonds;
import org.gnubridge.core.deck.Hearts;
import org.gnubridge.core.deck.Spades;
import org.gnubridge.core.deck.Trump;

public class RebidStayman extends PRebidNoTrump {

	public RebidStayman(Auctioneer a, Hand h) {
		super(a, h);
	}

	@Override
	protected boolean applies() {
		return super.applies() && response.getTrump().equals(Clubs.i())
				&& response.getValue() == 2 && rebid.getTrump().isSuit();
	}

	@Override
	protected Bid prepareBid() {
		PointCalculator pc = new PointCalculator(hand);
		Trump trump = rebid.getTrump();
		int hearts = hand.getSuitLength(Hearts.i());
		int spades = hand.getSuitLength(Spades.i());
		if (trump.equals(Diamonds.i())) {
			if (hearts == 5 && spades == 4) {
				return new Bid(2, Hearts.i());
			} else if (spades == 5 && hearts == 4) {
				return new Bid(2, Spades.i());
			}
			//TODO: other bidding
		} else if (trump.isMajorSuit()) {
			if (trump.equals(Hearts.i())) {
				if (hearts == 4 && spades == 5) {
					return new Bid(2, Spades.i());
				}
			}
			int points = pc.getCombinedPoints();
			if (hand.getSuitLength(trump.asSuit()) >= 4) {
				if (points >= 8 && points <= 9) {
					return new Bid(3, trump);
				} else if (points >= 10 && points <= 14) {
					return new Bid(4, trump);
				}
			}
			if (points >= 12) {
				if (hand.getSuitLength(Clubs.i()) >= 5) {
					return new Bid(3, Clubs.i());
				} else if (hand.getSuitLength(Diamonds.i()) >= 5) {
					return new Bid(3, Diamonds.i());
				}
			}
		}
		
		return null;
	}

}
