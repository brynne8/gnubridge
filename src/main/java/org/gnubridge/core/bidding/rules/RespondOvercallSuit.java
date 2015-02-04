package org.gnubridge.core.bidding.rules;

import static org.gnubridge.core.deck.Trump.*;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.ResponseCalculator;
import org.gnubridge.core.deck.Suit;
import org.gnubridge.core.deck.Trump;

public class RespondOvercallSuit extends Response {

	private static final int MAJOR_SUIT_GAME = 4;
	private static final int NOTRUMP_GAME = 3;

	public RespondOvercallSuit(Auctioneer a, Hand h) {
		super(a, h);
	}

	@Override
	protected boolean applies() {
		return super.applies() && auction.isOvercall(partnersOpeningBid)
				&& partnersOpeningBid.getTrump().isSuit();
	}

	@Override
	protected Bid prepareBid() {
		ResponseCalculator calc = new ResponseCalculator(hand, partnersOpeningBid);
		int points = calc.getCombinedPoints();
		int level = partnersOpeningBid.getValue();
		int length = hand.getSuitLength(partnersOpeningBid.getTrump().asSuit());
		if (length >= 3) {
			if (length != 3 && points <= 7 && level == 1 && auction.getVulnerabilityIndex() < 2) {
				return new Bid(3, partnersOpeningBid.getTrump());
			} else if (points >= 7 && points <= 14) {
				Bid result = new Bid(level + 1, partnersOpeningBid.getTrump());
				if (auction.isValid(result)) {
					return result;
				}
			} else if (points >= 15) { //Pavlicek is unclear in lesson 7, see tests && partnersOpeningBid.getTrump().isMajorSuit()) {
				return new Bid(MAJOR_SUIT_GAME, partnersOpeningBid.getTrump());
			}

		}
		if (points >= 8) {
			if (level == 1) {
				if (points >= 10) {
					for (Suit color : hand.getSuitsWithAtLeastCards(5)) {
						if (auction.isValid(new Bid(2, color))) {
							return new Bid(2, color);
						}
					}
					if (points <= 12 && calc.isBalanced()) {
						return makeCheapestBid(NOTRUMP);
					}
				} else if (calc.isBalanced() && haveStopperInEnemySuit()) {
					return makeCheapestBid(NOTRUMP);
				} else {
					for (Suit color : hand.getSuitsWithAtLeastCards(4)) {
						if (auction.isValid(new Bid(1, color))) {
							return new Bid(1, color);
						}
					}
				}
			}
			if (level == 2) {
				for (Suit color : hand.getDecent5LengthSuits()) {
					if (points >= 13) {
						return new Bid(3, color);
					} else if (auction.isValid(new Bid(2, color))) {
						return new Bid(2, color);
					}
				}
			}
		}

		if (level <= 3 && haveStopperInEnemySuit()) {
			if (level == 3) {
				if (points >= 18 && calc.isSemiBalanced()) {
					return new Bid(NOTRUMP_GAME, NOTRUMP);
				}
			} else {
				if (points >= 8 && points <= 11) {
					return makeCheapestBid(NOTRUMP);
				}
				if (points >= 12 && points <= 14) {
					Bid bid = makeCheapestBid(NOTRUMP);
					return new Bid(bid.getValue() + 1, NOTRUMP);
				}
				if (points >= 15) {
					return new Bid(NOTRUMP_GAME, NOTRUMP);
				}
			}
		}
		return null;
	}

	private Bid makeCheapestBid(Trump trump) {
		Bid candidate = new Bid(partnersOpeningBid.getValue(), trump);
		if (auction.isValid(candidate)) {
			return candidate;
		} else {
			return new Bid(partnersOpeningBid.getValue() + 1, trump);
		}
	}

}
