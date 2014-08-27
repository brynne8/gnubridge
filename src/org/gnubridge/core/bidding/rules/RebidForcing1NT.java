package org.gnubridge.core.bidding.rules;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.Pass;
import org.gnubridge.core.bidding.PointCalculator;
import org.gnubridge.core.deck.Clubs;
import org.gnubridge.core.deck.Diamonds;
import org.gnubridge.core.deck.Hearts;
import org.gnubridge.core.deck.NoTrump;
import org.gnubridge.core.deck.Spades;
import org.gnubridge.core.deck.Suit;

public class RebidForcing1NT extends RebidToLevel1Response {

	public RebidForcing1NT(Auctioneer a, Hand h) {
		super(a, h);
	}

	@Override
	protected boolean applies() {
		return super.applies() && response.getTrump().isNoTrump() && opening.getTrump().isMajorSuit();
	}

	@Override
	protected Bid prepareBid() {
		PointCalculator pc = new PointCalculator(hand);
		
		int HCP = pc.getHighCardPoints();
		Suit open = opening.getTrump().asSuit();
		if (HCP >= 19) {
			Bid result = null;
			for (Suit color : Suit.mmlist) {
				if (!color.equals(open) && !color.equals(Spades.i())  
						&& hand.getSuitLength(color) >= 4) {
					result = new Bid(3, color);
					if (auction.isValid(result)) {
						return result;
					}
					result = null;
				}
			}
		} else {
			if (hand.getSuitLength(open) >= 6) {
				if (hand.getSuitLength(open) == 6 && HCP >= 12 && HCP <= 15) {
					return new Bid(2, open);
				} else if (HCP >= 15 && HCP <= 18) {
					return new Bid(3, open);
				}
			}
		}
		
		if (open.equals(Hearts.i())) {
			if (HCP >= 17 && hand.getSuitLength(open) >= 4
					&& hand.getSuitLength(Spades.i()) >= 5) {
				return new Bid(2, Spades.i());
			}
		}
		
		if (HCP >= 18 && pc.isBalanced()) {
			return new Bid(2, NoTrump.i());
		}
		
		if (HCP >= 12) {
			if (open.equals(Spades.i()) && hand.getSuitLength(Hearts.i()) >= 4) {
				return new Bid(2, Hearts.i());
			}
			Suit longer = Clubs.i();
			if (hand.AisStronger(Diamonds.i(), longer)) {
				longer = Diamonds.i();
			}
			if (hand.getSuitLength(longer) >= 3) {
				return new Bid(2, longer);
			}
		}
		return new Pass();
	}

}
