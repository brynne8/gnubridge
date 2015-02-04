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

public class PRebidAfter1NT extends PartnersRebid {

	public PRebidAfter1NT(Auctioneer a, Hand h) {
		super(a, h);
	}

	@Override
	protected boolean applies() {
		if (super.applies()) {
			return rebid.equals(new Bid(1, NoTrump.i()));
		}
		return false;
	}
	
	@Override
	protected Bid prepareBid() {
		PointCalculator pc = new PointCalculator(hand);
		
		int points = pc.getCombinedPoints();
		if (response.getTrump().isMinorSuit()) {
			if (points >= 11) {
				if (points >= 13) {
					if (hand.getSuitLength(Diamonds.i()) == 4) {
						if (hand.getSuitLength(Hearts.i()) >= 5) {
							return new Bid(2, Hearts.i());
						} else if (hand.getSuitLength(Spades.i()) >= 5) {
							return new Bid(2, Spades.i());
						}
					}
				}
				if (hand.getSuitLength(Diamonds.i()) >= 5) {
					if (hand.getSuitLength(Diamonds.i()) != 5) {
						return new Bid(3, Diamonds.i());
					} else if (hand.getSuitLength(Clubs.i()) >= 4) {
						return new Bid(3, Clubs.i());
					}
				}
				if (pc.isSemiBalanced()) {
					return new Bid(2, NoTrump.i());
				}
			}
			if (hand.getSuitLength(Diamonds.i()) >= 6) {
				return new Bid(2, Diamonds.i());
			} else if (hand.getSuitLength(Clubs.i()) >= 3) {
				return new Bid(2, Clubs.i());
			}
		} else {
			int hearts = hand.getSuitLength(Hearts.i());
			int spades = hand.getSuitLength(Spades.i());
			if (points <= 10) {
				if (response.getTrump().equals(Spades.i())) {
					if (spades == 5 && hearts >= 4) {
						return new Bid(2, Hearts.i());
					}
				} else if (hearts >= 5) {
					return new Bid(2, Hearts.i());
				} else if (spades >= 5) {
					return new Bid(2, Spades.i());
				}
			} else {
				if (response.getTrump().equals(Hearts.i())) {
					if (hearts >= 6) {
						return new Bid(3, Hearts.i());
					} else if (spades == 4 && hearts == 4) {
						return new Bid(2, Spades.i());
					}
				} else if (spades >= 5) {
					if (spades >= 6) {
						return new Bid(3, Spades.i());
					} else {
						return new Bid(2, Spades.i());
					}
				} else if (pc.isSemiBalanced()) {
					return new Bid(2, NoTrump.i());
				}
				//TODO: Checkback Stayman
			}
		}
		return null;
	}

}
