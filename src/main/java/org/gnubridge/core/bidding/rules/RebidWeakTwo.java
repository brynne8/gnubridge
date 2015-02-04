package org.gnubridge.core.bidding.rules;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.PointCalculator;
import org.gnubridge.core.deck.Clubs;
import org.gnubridge.core.deck.Diamonds;
import org.gnubridge.core.deck.Hearts;
import org.gnubridge.core.deck.NoTrump;
import org.gnubridge.core.deck.Spades;
import org.gnubridge.core.deck.Suit;

public class RebidWeakTwo extends Rebid {

	public RebidWeakTwo(Auctioneer a, Hand h) {
		super(a, h);
	}

	@Override
	protected boolean applies() {
		return super.applies() && opening.getValue() == 2 && !opening.getTrump().equals(Clubs.i())
				&& response.getValue() == 2 && response.getTrump().isNoTrump();
	}

	@Override
	protected Bid prepareBid() {
		PointCalculator pc = new PointCalculator(hand);

		int HCP = pc.getHighCardPoints();
		if (opening.getTrump().equals(Diamonds.i())) {
			if (HCP >= 9) {
				if (pc.isSemiBalanced() && hand.isGood5LengthSuits(Diamonds.i())) {
					return new Bid(3, NoTrump.i());
				}
				for (Suit color : Suit.reverseList) {
					if (hand.getSuitLength(color) <= 1) {
						return new Bid(3, color);
					}
				}
			}
			return new Bid(3, Diamonds.i());
		} else {
			Suit color = opening.getTrump().asSuit();
			if (HCP >= 9) {
				if (pc.getHighCardPoints(hand.getSuitHi2Low(color)) >= 8) {
					return new Bid(3, NoTrump.i());
				}
				if (hand.isGood5LengthSuits(color)) {
					return new Bid(3, Spades.i());
				} else {
					return new Bid(3, Hearts.i());
				}
			} else {
				if (hand.isGood5LengthSuits(color)) {
					return new Bid(3, Diamonds.i());
				} else {
					return new Bid(3, Clubs.i());
				}
			}
		}

	}

}
