package org.gnubridge.core.bidding.rules;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.Call;
import org.gnubridge.core.bidding.ResponseCalculator;
import org.gnubridge.core.deck.NoTrump;
import org.gnubridge.core.deck.Suit;
import org.gnubridge.core.deck.Trump;

public class RebidTakeoutDouble extends BiddingRule {

	protected Bid response;
	protected Bid opening;

	public RebidTakeoutDouble(Auctioneer a, Hand h) {
		super(a, h);
	}

	@Override
	protected boolean applies() {
		Call responderCall = auction.getPartnersLastCall();
		if (responderCall != null && responderCall.getBid().hasTrump()) {
			Call myOpeningBid = auction.getPartnersCall(responderCall);
			if (myOpeningBid != null) {
				opening = myOpeningBid.getBid();
				if (opening.isDouble() && auction.isOvercall(opening)) {
					response = responderCall.getBid();
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected Bid prepareBid() {
		Bid doubledBid = auction.getDoubledCall(false).getBid();
		ResponseCalculator calc = new ResponseCalculator(hand, response);
		int level = response.getValue();
		Trump trump = response.getTrump();
		Trump dblTrump = doubledBid.getTrump();
		int points = calc.getCombinedPoints();
		if (trump.isNoTrump()) {
			if (level == 1) {
				if (points >= 15) {
					if (points >= 16) {
						for (Suit suit : hand.getGood5LengthSuits()) {
							if (!suit.equals(dblTrump) && auction.isValid(new Bid(2, suit))) {
								return new Bid(2, suit);
							}
						}
					}
					if (calc.isSemiBalanced() && haveStopperInEnemySuit()) {
						return new Bid(2, NoTrump.i());
					}
				}
			} else if (level == 2) {
				if (points >= 14) {
					for (Suit suit : hand.getGood5LengthSuits()) {
						if (!suit.equals(dblTrump) && auction.isValid(new Bid(3, suit))) {
							return new Bid(3, suit);
						}
					}
					if (calc.isSemiBalanced() && haveStopperInEnemySuit()) {
						return new Bid(3, NoTrump.i());
					}
				}
			}
		} else if (new Bid(doubledBid.getValue() + 1, dblTrump).greaterThan(response)) {
			if (points >= 16) {
				Bid result = null;
				for (Suit suit : hand.getDecent5LengthSuits()) {
					if (!suit.equals(dblTrump)) {
						result = makeCheapestBid(suit);
						if (auction.isValid(result)) {
							return result;
						}
					}
				}
			}
			if (points >= 19 && calc.isSemiBalanced() && haveStopperInEnemySuit()) {
				return makeCheapestBid(NoTrump.i());
			}
		} else {
			if (points >= 16) {
				if (hand.getSuitLength(trump.asSuit()) >= 3) { //aggressive
					if (trump.isMajorSuit()) {
						return new Bid(4, trump);
					} else if (calc.getHighCardPoints(hand.getSuitHi2Low(trump.asSuit())) >= 6) {
						return new Bid(5, trump);
					}
				}
				if (points >= 18 && calc.isBalanced() && haveStopperInEnemySuit()) {
					return makeCheapestBid(NoTrump.i());
				}
				for (Suit suit : hand.getGood5LengthSuits()) {
					if (!suit.equals(dblTrump) && auction.isValid(new Bid(3, suit))) {
						return new Bid(3, suit);
					}
				}
			}
		}
		return null;
	}

	private Bid makeCheapestBid(Trump trump) {
		if (trump == null) {
			return null;
		}
		Bid candidate = new Bid(response.getValue(), trump);
		if (auction.isValid(candidate)) {
			return candidate;
		} else {
			return new Bid(response.getValue() + 1, trump);
		}
	}

}
