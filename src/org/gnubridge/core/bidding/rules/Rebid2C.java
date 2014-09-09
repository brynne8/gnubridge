package org.gnubridge.core.bidding.rules;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.PointCalculator;
import org.gnubridge.core.deck.Clubs;
import org.gnubridge.core.deck.Diamonds;
import org.gnubridge.core.deck.Hearts;
import org.gnubridge.core.deck.NoTrump;
import org.gnubridge.core.deck.Suit;
import org.gnubridge.core.deck.Trump;

public class Rebid2C extends Rebid {

	public Rebid2C(Auctioneer a, Hand h) {
		super(a, h);
	}

	@Override
	protected Bid prepareBid() {
		Bid result = null;
		PointCalculator pc = new PointCalculator(hand);

		int rank = response.getValue();
		Trump trump = response.getTrump();
		int HCP = pc.getHighCardPoints();
		if (rank == 2) {
			if (trump.equals(Diamonds.i())) {
				result = getLowestBid(2);
				if (result == null) {
					if (HCP <= 24) {
						if (pc.isBalanced()) {
							result = new Bid(2, NoTrump.i());
						}
					} else if (HCP <= 27) {
						result = new Bid(3, NoTrump.i());
					} else if (HCP <= 30) {
						result = new Bid(4, NoTrump.i());
					}
				}
			} else {
				if (HCP >= 3) {
					if (hand.getSuitLength(trump.asSuit()) >= 3) {
						result = new Bid(3, trump);
					} else {
						result = getLowestBid(2);
					}
				}
				if (result == null) {
					result = new Bid(2, NoTrump.i());
				}
			}
		} else if (rank == 3) {
			if (HCP >= 3) {
				if (hand.getSuitLength(trump.asSuit()) >= 3) {
					result = new Bid(3, trump);
				} else {
					result = getLowestBid(3);
				}
			}
			if (result == null) {
				if (trump.equals(Clubs.i())) {
					result = new Bid(3, Diamonds.i());
				} else if (trump.equals(Diamonds.i())) {
					result = new Bid(3, Hearts.i());
				}
			}
		}

		return result;
	}

	@Override
	protected boolean applies() {
		return super.applies() && new Bid(2, Clubs.i()).equals(opening);
	}
	
	private Bid getLowestBid(int level) {
		Bid lowest = null;
		for (Suit color : Suit.mmList) {
			if (hand.getSuitLength(color) >= 5) {
				lowest = new Bid(getLowestLevel(level, color), color);
				break;
			}
		}
		return lowest;
	}
	
	private int getLowestLevel(int base, Suit suit) {
		if (auction.isValid(new Bid(base, suit))) {
			return base;
		} else {
			return base + 1;
		}
	}

}
