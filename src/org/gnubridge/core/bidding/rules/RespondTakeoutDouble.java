package org.gnubridge.core.bidding.rules;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.PointCalculator;
import org.gnubridge.core.deck.Diamonds;
import org.gnubridge.core.deck.NoTrump;
import org.gnubridge.core.deck.Suit;
import org.gnubridge.core.deck.Trump;

public class RespondTakeoutDouble extends BiddingRule {

	private PointCalculator pc;
	private Bid opening;
	private Suit highest;

	public RespondTakeoutDouble(Auctioneer a, Hand h) {
		super(a, h);
		pc = new PointCalculator(hand);
	}

	@Override
	protected boolean applies() {
		if (auction.getPartnersLastCall() == null) {
			return false;
		}
		Bid partnersOpeningBid = auction.getPartnersLastCall().getBid();
		return auction.isOvercall(partnersOpeningBid) && partnersOpeningBid.isDouble();
	}

	@Override
	protected Bid prepareBid() {
		opening = auction.getDoubledCall().getBid();
		highest = null;
		
		int HCP = pc.getHighCardPoints();
		Bid lastBid = auction.getLastCall().getBid();
		if (HCP >= 9 && HCP <= 12) {
			int length = 4;
			for (Suit color : Suit.list) {
				if (color.equals(Diamonds.i())) {
					if (highest != null) {
						return new Bid(jumpPartnersBid(), highest);
					} else {
						highest = null;
						length = 4;
					}
				}
				if (!color.equals(opening.getTrump())
						&& (lastBid.isPass() || !color.equals(lastBid.getTrump()))) {
					if (hand.getSuitLength(color) >= length && hand.AisStronger(color, highest)) {
						highest = color;
					}
				}
			}
			if (highest != null) {
				return new Bid(jumpPartnersBid(), highest);
			}
		}
		
		if (HCP >= 6 && haveStopperInEnemySuit()) {
			if (HCP < 10) {
				return new Bid(1, NoTrump.i());
			} else if (HCP <= 12) {
				return new Bid(2, NoTrump.i());
			} else if (HCP <= 16) {
				return new Bid(3, NoTrump.i());
			}
		}
		
		if (HCP <= 10) {
			return makeCheapestBid(longestSuit(lastBid));
		}
		return null;
	}

	private int jumpPartnersBid() {
		if (opening.greaterThan(new Bid(opening.getValue(), highest))) {
			return opening.getValue() + 2;
		} else {
			return opening.getValue() + 1;
		}
	}
	
	private Suit longestSuit(Bid last) {
		Suit longest = null;
		for (Suit color : Suit.list) {
			if (!color.equals(opening.getTrump())
					&& (last.isPass() || !color.equals(last.getTrump()))
					&& hand.getSuitLength(color) >= 3) {
				if (hand.AisStronger(color, longest)) {
					longest = color;
				}
			}
		}
		return longest;
	}

	private Bid makeCheapestBid(Trump trump) {
		if (trump == null) {
			return null;
		}
		Bid candidate = new Bid(opening.getValue(), trump);
		if (auction.isValid(candidate)) {
			return candidate;
		} else {
			return new Bid(opening.getValue() + 1, trump);
		}
	}

}
