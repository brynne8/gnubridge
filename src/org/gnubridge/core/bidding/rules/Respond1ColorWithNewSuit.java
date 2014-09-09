package org.gnubridge.core.bidding.rules;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.ResponseCalculator;
import org.gnubridge.core.deck.Clubs;
import org.gnubridge.core.deck.Diamonds;
import org.gnubridge.core.deck.NoTrump;
import org.gnubridge.core.deck.Suit;

public class Respond1ColorWithNewSuit extends Response {

	private ResponseCalculator pc;
	private Suit unbidSuit;

	public Respond1ColorWithNewSuit(Auctioneer a, Hand h) {
		super(a, h);
	}

	@Override
	protected boolean applies() {
		boolean result = false;
		if (super.applies() && partnerBid1Color()) {
			pc = new ResponseCalculator(hand, partnersOpeningBid);
			unbidSuit = findHighestColorWithFourOrMoreCards();
			if (pc.getCombinedPoints() >= 6 && unbidSuit != null) {
				result = true;
			}
		}
		return result;
	}

	@Override
	protected Bid prepareBid() {
		int points = pc.getCombinedPoints();
		int length = hand.getSuitLength(unbidSuit);

		if (unbidSuit.equals(Diamonds.i()) && points >= 12 && partnersOpeningBid.equals(Clubs.i())) {
			if (hand.getSuitLength(Diamonds.i()) >= 5 && (length == 6 || hand.getSuitLength(Clubs.i()) >= 4)) {
				return new Bid(2, unbidSuit);
			}
		}
		if (length == 6) {
			if (points <= 7 && !unbidSuit.equals(Diamonds.i())) {
				return new Bid(2, unbidSuit);
			}
		}

		Bid result = new Bid(1, unbidSuit);
		if (!auction.isValid(result) && points >= 12) {
			unbidSuit = findTwoOverOneSuit();
			if (unbidSuit != null) {
				return new Bid(2, unbidSuit);
			}
		} else {
			return result;
		}
		
		unbidSuit = getLowerUnbidSuitWithAtLeast6Cards();
		if (unbidSuit != null && points >= 9 && points <= 11) {
			return new Bid(3, unbidSuit);
		}

		return null;
	}

	private boolean partnerBid1Color() {
		if (!NoTrump.i().equals(partnersOpeningBid.getTrump()) && 1 == partnersOpeningBid.getValue()) {
			return true;
		} else {
			return false;
		}
	}

	private Suit findHighestColorWithFourOrMoreCards() {
		Suit longer = null;
		for (Suit color : Suit.list) {
			if (hand.getSuitLength(color) >= 4 && hand.AisStronger(color, longer)
					&& !color.equals(partnersOpeningBid.getTrump())) {
				longer = color;
			}
		}
		return longer;
	}

	private Suit findTwoOverOneSuit() {
		Suit longer = null;
		for (Suit color : Suit.mmList) {
			if (color.isLowerRankThan(partnersOpeningBid.getTrump()) && hand.AisStronger(color, longer)) {
				longer = color;
			}
		}
		int length = hand.getSuitLength(longer);
		if (length < 4 || (length == 4 && !partnersOpeningBid.getTrump().equals(Diamonds.i()))) {
			longer = null;
		}
		return longer;
	}

	private Suit getLowerUnbidSuitWithAtLeast6Cards() {
		for (Suit color : Suit.mmList) {
			if (hand.getSuitLength(color) >= 6 && color.isLowerRankThan(partnersOpeningBid.getTrump())) {
				return color;
			}
		}
		return null;
	}

}
