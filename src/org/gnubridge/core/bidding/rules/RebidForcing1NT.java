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
		int level = rebid.getValue();
		int HCP = pc.getHighCardPoints();
		Trump open  = opening.getTrump();
		Trump trump = rebid.getTrump();
		int lengthOfTrump = hand.getSuitLength(open.asSuit());
		
		if (level == 2) {
			if (trump.isNoTrump()) {
				if (hand.getSuitLength(open.asSuit()) >= 3) {
					if (HCP >= 8) {
						return new Bid(3, open);
					} else {
						return new Bid(4, open);
					}
				}
				if (HCP >= 9) {
					Suit unbidSuit = getLongestUnbidSuit();
					if (unbidSuit != null) {
						return new Bid(3, unbidSuit);
					} else if (pc.isBalanced()) {
						return new Bid(3, NoTrump.i());
					}
				}
			} else if (trump.isMinorSuit()) {
				if (lengthOfTrump >= 2) {
					if (lengthOfTrump == 2 || HCP <= 7) {
						return new Bid(2, open);
					} else {
						return new Bid(3, open);
					}
				}
				if (hand.getSuitLength(trump.asSuit()) >= 5) {
					if (HCP >= 8) {
						return new Bid(3, trump);
					} //TODO
				}
				if (HCP >= 10 && HCP <= 12) {
					return new Bid(2, NoTrump.i());
				}
				if (HCP <= 10) {
					Suit color = getUnbidSuitWithAtLeast5Cards();
					if (color != null) {
						return new Bid(2, color);
					}
				}
			} else {
				if (trump.equals(open)) {
					if (HCP >= 10) {
						return new Bid(3, open);
					}
				} else {
					if (trump.equals(Hearts.i())) {
						if (HCP >= 11 && hand.getSuitLength(Hearts.i()) >= 5) {
							return new Bid(4, Hearts.i());
						} else if (HCP >= 8 && hand.getSuitLength(Hearts.i()) >= 4) {
							return new Bid(3, Hearts.i());
						}
					} else {
						if (HCP >= 8) {
							if (hand.getSuitLength(Hearts.i()) >= 3) {
								return new Bid(4, Hearts.i());
							} else if (hand.getSuitLength(Spades.i()) >= 4) {
								return new Bid(4, Spades.i());
							}
						} else {
							if (hand.getSuitLength(Hearts.i()) >= 3) {
								return new Bid(3, Hearts.i());
							} else if (hand.getSuitLength(Spades.i()) >= 4) {
								return new Bid(3, Spades.i());
							}
						}
						if (pc.isBalanced()) {
							if (HCP <= 9) {
								return new Bid(2, NoTrump.i());
							} else if (hand.haveStopper(Clubs.i())
									&& hand.haveStopper(Diamonds.i())) {
								return new Bid(3, NoTrump.i());
							}
						}
					}
				}
			}
		}
		return null;
	}

	private Suit getUnbidSuitWithAtLeast5Cards() {
		for (Suit color : Suit.reverseList) {
			if (hand.getSuitLength(color) >= 5 && hasNotBeenBid(color)) {
				return color;
			}
		}
		return null;
	}

	private Suit getLongestUnbidSuit() {
		Suit longer = null;
		for (Suit color : Suit.mmList) {
			if (hand.getSuitLength(color) >= 5 && hand.AisStronger(color, longer)
					&& !color.equals(opening.getTrump())) {
				longer = color;
			}
		}
		return longer;
	}

	private boolean hasNotBeenBid(Suit suit) {
		return !suit.equals(rebid.getTrump()) && !suit.equals(opening.getTrump());
	}

}
