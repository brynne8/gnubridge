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
import org.gnubridge.core.deck.Trump;

public class Rebid1NT extends Rebid {

	boolean fourthOvercalled = false;

	public Rebid1NT(Auctioneer a, Hand h) {
		super(a, h);
	}

	@Override
	protected Bid prepareBid() {
		Bid result = null;
		PointCalculator pc = new PointCalculator(hand);

		int level = response.getValue();
		Trump trump = response.getTrump();
		int maximum = fourthOvercalled ? 14 : 17;
		if (level == 2) {
			if (trump.equals(Clubs.i())) {
				if (hand.getSuitLength(Hearts.i()) >= 4) {
					result = new Bid(2, Hearts.i());
				} else if (hand.getSuitLength(Spades.i()) >= 4) {
					result = new Bid(2, Spades.i());
				} else {
					result = new Bid(2, Diamonds.i());
				}
			} else if (trump.equals(Spades.i())) {
				if (hand.getSuitLength(Clubs.i()) >= 4) {
					result = new Bid(3, Clubs.i());
				} else if (hand.getSuitLength(Diamonds.i()) >= 4) {
					result = new Bid(3, Diamonds.i());
				} else {
					result = new Bid(2, NoTrump.i());
				}
			} else if (trump.isNoTrump()) {
				if (pc.getHighCardPoints() >= maximum) {
					result = new Bid(3, NoTrump.i());
				} else {
					result = new Pass();
				}
			} else if (trump.equals(Diamonds.i())) {
				if (pc.getCombinedPoints() >= maximum) {
					result = new Bid(2, Spades.i());
				} else {
					result = new Bid(2, Hearts.i());
				}
			} else {
				if (pc.getCombinedPoints() >= maximum) {
					result = new Bid(2, NoTrump.i());
				} else {
					result = new Bid(2, Spades.i());
				}
			}
		} else if (level == 3) {
			if (trump.isNoTrump()) {
				result = new Pass();
			} else if (hand.getSuitLength(trump.asSuit()) >= 2) {
				if (trump.isMajorSuit()) {
					result = new Bid(4, trump);
				} else if (pc.getHighCardPoints() >= maximum) {
					result = new Bid(3, NoTrump.i());
				}
			}
		}

		return result;
	}

	private boolean partnerWasRespondingToMy1NT() {
		if (super.applies()) {
			return new Bid(1, NoTrump.i()).equals(opening);
		} else {
			if (auction.getPartnersLastCall() != null) {
				opening = auction.getPartnersLastCall().getBid();
				if (auction.isOvercall(opening)) {
					if (auction.isFourthOvercall(opening)) {
						fourthOvercalled = true;
					}
					return new Bid(1, NoTrump.i()).equals(opening);
				}
			}
			return false;
		}
	}

	@Override
	protected boolean applies() {
		return partnerWasRespondingToMy1NT();
	}

}
