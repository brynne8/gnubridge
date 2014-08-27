package org.gnubridge.core.bidding.rules;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.PointCalculator;
import org.gnubridge.core.deck.Diamonds;
import org.gnubridge.core.deck.Hearts;
import org.gnubridge.core.deck.Spades;
import org.gnubridge.core.deck.Suit;
import org.gnubridge.core.deck.Clubs;
import org.gnubridge.core.deck.NoTrump;

public class Respond2C extends Response {

	private final PointCalculator pc;
	public static final Suit[] mmlist = { Hearts.i(), Spades.i(), Clubs.i(), Diamonds.i() };

	public Respond2C(Auctioneer a, Hand h) {
		super(a, h);
		pc = new PointCalculator(hand);
	}

	@Override
	protected Bid prepareBid() {
		Bid result = null;
		if (pc.getHighCardPoints() >= 8) {
			if (pc.isBalanced()) {
				result = new Bid(2, NoTrump.i());
				result.makeGameForcing();
			} else {
				Suit longest = null;
				for (Suit color : mmlist) {
					if (hand.getSuitLength(color) >= 5) {
						if (hand.AisStronger(color, longest)) {
							longest = color;
						}
					}
				}
				if (longest != null) {
					if (longest.isMajorSuit()) {
						result = new Bid(2, longest);
						result.makeGameForcing();
					} else {
						result = new Bid(3, longest);
						result.makeGameForcing();
					}
				}
			}
		} else {
			result = new Bid(2, Diamonds.i());
		}
		return result;
	}

	@Override
	protected boolean applies() {
		return super.applies() && new Bid(2, Clubs.i()).equals(partnersOpeningBid);
	}
}
