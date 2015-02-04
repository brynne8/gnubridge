package org.gnubridge.core.bidding.rules;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.Pass;
import org.gnubridge.core.bidding.TrickCalculator;
import org.gnubridge.core.deck.NoTrump;
import org.gnubridge.core.deck.Suit;

public class RespondPreemptiveBid extends Response {

	public RespondPreemptiveBid(Auctioneer a, Hand h) {
		super(a, h);
	}

	@Override
	protected boolean applies() {
		if (super.applies() && (auction.isOpening(partnersOpeningBid) || 
				(auction.isOvercall(partnersOpeningBid)
						&& auction.enemyCallBeforePartner(null).getBid().is1Suit()))) {
			return partnersOpeningBid.getValue() == 3 && partnersOpeningBid.getTrump().isSuit()
					&& hand.getSuitLength(partnersOpeningBid.getTrump().asSuit()) != 0;
		}
		return false;
	}

	@Override
	protected Bid prepareBid() {
		TrickCalculator calc = new TrickCalculator(hand);
		Suit suit = partnersOpeningBid.getTrump().asSuit();
		int vulnerabilityIndex = auction.getVulnerabilityIndex();
		int doubledTricks = 0;
		for (Suit color : Suit.list) {
			doubledTricks += calc.doublePlayingTricks(color);
		}
		if (suit.isMajorSuit()) {
			if ((vulnerabilityIndex == 2 && doubledTricks > 4) ||
					(vulnerabilityIndex == 1 && doubledTricks > 8) ||
					(vulnerabilityIndex == 0 && doubledTricks > 6)) {
				return new Bid(4, suit);
			}
		} else {
			if ((vulnerabilityIndex == 2 && doubledTricks > 6) ||
					(vulnerabilityIndex == 1 && doubledTricks > 10) ||
					(vulnerabilityIndex == 0 && doubledTricks > 8)) {
				return new Bid(5, suit);
			}
		}
		if ((vulnerabilityIndex == 2 && doubledTricks > 2) ||
				(vulnerabilityIndex == 1 && doubledTricks > 6) ||
				(vulnerabilityIndex == 0 && doubledTricks > 4)) {
			if (hand.getSuitLength(suit) >= 3) {
				boolean possible = true;
				for (Suit color : Suit.list) {
					if (!color.equals(suit) && (hand.getSuitLength(color) < 2
							 || !hand.haveStrongStopper(color))) {
						possible = false;
						break;
					}
				}
				if (possible) {
					return new Bid(3, NoTrump.i());
				}
			}
			for (Suit color : Suit.mmList) {
				if (!color.equals(suit) && hand.getSuitLength(color) >= 6
						&& hand.isDecent5LengthSuits(color)) {
					if (auction.isValid(new Bid(3, color))) {
						return new Bid(3, color);
					}
				}
			}
		}
		return new Pass();
		
	}

}
