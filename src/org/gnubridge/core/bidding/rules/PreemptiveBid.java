package org.gnubridge.core.bidding.rules;

import org.gnubridge.core.Card;
import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.TrickCalculator;
import org.gnubridge.core.deck.Hearts;
import org.gnubridge.core.deck.Spades;
import org.gnubridge.core.deck.Suit;

public class PreemptiveBid extends BiddingRule {

	private Suit longestSuit;

	public PreemptiveBid(Auctioneer a, Hand h) {
		super(a, h);
	}

	@Override
	protected boolean applies() {
		if (auction.isOpeningBid() || (auction.may2ndOvercall() || auction.may4thOvercall())) {
			TrickCalculator calc = new TrickCalculator(hand);
			longestSuit = hand.getLongestSuit();
			if (hand.getSuitLength(longestSuit) < 6) {
				return false;
			}
			if (!longestSuit.equals(Hearts.i()) && hand.getSuitLength(Hearts.i()) >= 4
					&& hand.getSuitHi2Low(Hearts.i()).get(0).getValue() >= Card.QUEEN) {
				return false;
			}
			if (!longestSuit.equals(Spades.i()) && hand.getSuitLength(Spades.i()) >= 4
					&& hand.getSuitHi2Low(Spades.i()).get(0).getValue() >= Card.QUEEN) {
				return false;
			}
			int tricks = calc.playingTricks();
			int vulnerabilityIndex = auction.getVulnerabilityIndex();
			if (vulnerabilityIndex == 2) {
				tricks += 2;
			} else if (vulnerabilityIndex == 1) {
				tricks += 4;
			} else {
				tricks += 3;
			}
			if (tricks >= 9) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected Bid prepareBid() {
		return new Bid(3, longestSuit);
	}

}