package org.gnubridge.core.bidding.rules;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.PointCalculator;
import org.gnubridge.core.deck.Hearts;
import org.gnubridge.core.deck.NoTrump;
import org.gnubridge.core.deck.Suit;
import org.gnubridge.core.deck.Trump;

public class RebidForcing1NT extends PartnersRebid {

	public RebidForcing1NT(Auctioneer a, Hand h) {
		super(a, h);
	}

	@Override
	protected boolean applies() {
		return super.applies() && response.getTrump().isNoTrump() && response.getValue() == 1
				&& opening.getTrump().isMajorSuit() && rebid.hasTrump();
	}

	@Override
	protected Bid prepareBid() {
		PointCalculator pc = new PointCalculator(hand);
		int rank = rebid.getValue();
		int HCP = pc.getHighCardPoints();
		Trump open  = opening.getTrump();
		Trump trump = rebid.getTrump();
		int lengthOfTrump = hand.getSuitLength(open.asSuit());
		
		if (rank == 2) {
			if (trump.isMinorSuit()) {
				if (lengthOfTrump >= 2) {
					if (lengthOfTrump == 2 || HCP <= 7) {
						return new Bid(2, open);
					} else {
						return new Bid(3, open);
					}
				}
				if (hand.getSuitLength(trump.asSuit()) >= 5) {
					if (HCP >= 8 && HCP <= 12) {
						return new Bid(3, trump);
					} //TODO
				}
				if (HCP >= 10 && HCP <= 12) {
					return new Bid(2, NoTrump.i());
				}
				if (HCP <= 10) {
					Suit color = getLowerUnbidSuitWithAtLeast5Cards();
					if (color != null) {
						return new Bid(2, color);
					}
				}
			} else if (trump.isMajorSuit()) {
				if (trump.equals(open)) {
					if (HCP >= 10 && HCP <= 12) {
						return new Bid(3, open);
					} else if (HCP >= 13) {
						return new Bid(4, open);
					}
				} else {
					if (trump.equals(Hearts.i())) {
						if (HCP >= 11 && hand.getSuitLength(trump.asSuit()) >= 5) {
							return new Bid(4, trump);
						} else if (HCP >= 8 && hand.getSuitLength(trump.asSuit()) >= 4) {
							return new Bid(3, trump);
						}
					} else {
						return new Bid(4, open); //TODO
					}
				}
			}
		}
		return null;
	}

	private Suit getLowerUnbidSuitWithAtLeast5Cards() {
		for (Suit color : Suit.reverseList) {
			if (hand.getSuitLength(color) >= 5 && color.isLowerRankThan(opening.getTrump())
					&& hasNotBeenBid(color)) {
				return color;
			}
		}
		return null;
	}

	private boolean hasNotBeenBid(Suit suit) {
		return !suit.equals(rebid.getTrump()) && !suit.equals(opening.getTrump());
	}

}
