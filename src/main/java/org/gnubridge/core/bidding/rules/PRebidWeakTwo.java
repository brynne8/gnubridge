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
import org.gnubridge.core.deck.Trump;

public class PRebidWeakTwo extends PartnersRebid {

	public PRebidWeakTwo(Auctioneer a, Hand h) {
		super(a, h);
	}

	@Override
	protected boolean applies() {
		if (super.applies()) {
			return opening.getValue() == 2 && !opening.getTrump().equals(Clubs.i())
					&& response.getValue() == 2 && response.getTrump().isNoTrump()
					&& rebid.getValue() == 3;
		}
		return false;
	}
	
	@Override
	protected Bid prepareBid() {
		Suit open = opening.getTrump().asSuit();
		Trump trump = rebid.getTrump();
		if (open.equals(Diamonds.i())) {
			if (trump.isNoTrump()) {
				return null;
			}
			if (!trump.equals(Diamonds.i())) {
				Bid result = null;
				for (Suit color : Suit.mmList) {
					if (!color.equals(trump) && hand.isDecent5LengthSuits(color)) {
						result = new Bid(3, color);
						if (!auction.isValid(result)) {
							result = new Bid(4, color);
						}
						return result;
					}
				}
			}
		} else { //TODO: revision
			ResponseCalculator calc = new ResponseCalculator(hand, opening);
			if (trump.isNoTrump()) {
				boolean allStopped = true;
				for (Suit color : Suit.list) {
					if (!color.equals(open) && !hand.haveStrongStopper(color)) {
						allStopped = false;
						break;
					}
				}
				if (allStopped) {
					return new Bid(3, NoTrump.i());
				}
			} else if (trump.equals(Spades.i())) {
				if (hand.getSuitLength(open) >= 2) {
					return new Bid(4, open);
				} else if (calc.getHighCardPoints() >= 16) {
					boolean allStopped = true;
					for (Suit color : Suit.list) {
						if (!color.equals(open) && !hand.haveStrongStopper(color)) {
							allStopped = false;
							break;
						}
					}
					if (allStopped) {
						return new Bid(3, NoTrump.i());
					}
				}
				if (hand.getSuitLength(open) != 0) {
					return new Bid(4, open);
				}
			} else if (trump.equals(Hearts.i())) {
				if (hand.getSuitLength(open) >= 2 && 
						calc.getHighCardPoints(hand.getSuitHi2Low(open)) >= 4) {
					return new Bid(4, open);
				} else {
					return new Bid(3, open);
				}
			} else {
				return new Bid(3, open);
			}
		}
		return null;
	}

}
