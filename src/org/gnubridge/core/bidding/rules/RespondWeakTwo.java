package org.gnubridge.core.bidding.rules;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.ResponseCalculator;
import org.gnubridge.core.deck.Clubs;
import org.gnubridge.core.deck.Diamonds;
import org.gnubridge.core.deck.Hearts;
import org.gnubridge.core.deck.NoTrump;
import org.gnubridge.core.deck.Spades;
import org.gnubridge.core.deck.Suit;

public class RespondWeakTwo extends Response {

	private ResponseCalculator calc;

	public RespondWeakTwo(Auctioneer a, Hand h) {
		super(a, h);
	}

	@Override
	protected boolean applies() {
		if (super.applies() && auction.isOpening(partnersOpeningBid)
				&& partnersOpeningBid.getValue() == 2 && !partnersOpeningBid.getTrump().equals(Clubs.i())
				&& partnersOpeningBid.getTrump().isSuit()) {
			calc = new ResponseCalculator(hand, partnersOpeningBid);
			return calc.getCombinedPoints() >= 8;
		}
		return false;
	}

	@Override
	protected Bid prepareBid() {
		int points = calc.getCombinedPoints();
		if (partnersOpeningBid.getTrump().equals(Diamonds.i())) {
			if (points < 10) {
				return null;
			}
			Bid result = null;
			for (Suit color : Suit.mmList) {
				if (!color.equals(Diamonds.i()) && (hand.getSuitLength(color) >= 6
						|| hand.isDecent5LengthSuits(color))) {
					if (points >= 16) {
						return new Bid(2, NoTrump.i());
					} else {
						if (color.equals(Clubs.i()) && hand.getSuitLength(Diamonds.i()) < 3) {
							result = new Bid(3, color);
						} else {
							result = new Bid(2, color);
						}
					}
				}
			}
			if (auction.isValid(result)) {
				return result;
			}
			if (hand.getSuitLength(Diamonds.i()) >= 3) {
				if (points >= 16) {
					return new Bid(2, NoTrump.i());
				} else {
					return new Bid(3, Diamonds.i());
				}
			}
		} else {
			Suit suit = partnersOpeningBid.getTrump().asSuit();
			if (points < 8) {
				return null;
			}
			if (hand.getSuitLength(suit) >= 3) {
				if (points <= 13) {
					return new Bid(3, suit);
				} else if (points <= 16) {
					return new Bid(2, NoTrump.i());
				} else if (points <= 19) {
					return new Bid(4, suit);
				} else {
					return new Bid(2, NoTrump.i());
				}
			}

			Bid result = null;
			for (Suit color : Suit.mmList) {
				if (!color.equals(suit) && (hand.getSuitLength(color) >= 6
						|| hand.isDecent5LengthSuits(color))) {
					if (points >= 16) {
						result = new Bid(2, NoTrump.i());
					} else {
						if (color.equals(Spades.i()) && suit.equals(Hearts.i())) {
							result = new Bid(2, color);
						} else if (hand.getSuitLength(suit) < 2) {
							result = new Bid(3, color);
						}
					}
				}
			}
			if (auction.isValid(result)) {
				return result;
			}
		}

		if (points >= 14) {
			if (points >= 16 && calc.isSemiBalanced()) {
				boolean allStopped = true;
				for (Suit color : Suit.list) {
					if (!color.equals(Diamonds.i()) && !hand.haveStopper(color)) {
						allStopped = false;
						break;
					}
				}
				if (allStopped) {
					return new Bid(3, NoTrump.i());
				}
			}
			return new Bid(2, NoTrump.i());
		}
		return null;
	}

}
