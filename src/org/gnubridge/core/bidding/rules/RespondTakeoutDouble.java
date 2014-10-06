package org.gnubridge.core.bidding.rules;

import java.util.Set;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.Pass;
import org.gnubridge.core.bidding.PointCalculator;
import org.gnubridge.core.deck.Hearts;
import org.gnubridge.core.deck.NoTrump;
import org.gnubridge.core.deck.Spades;
import org.gnubridge.core.deck.Suit;
import org.gnubridge.core.deck.Trump;

public class RespondTakeoutDouble extends BiddingRule {

	private PointCalculator pc;
	private Bid lastBid;
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
		Bid partnersBid = auction.getPartnersLastCall().getBid();
		if (auction.isOvercall(partnersBid) && partnersBid.isDouble()) {
			lastBid = auction.enemyCallBeforePartner(null).getBid();
			if (!lastBid.getTrump().isNoTrump()) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected Bid prepareBid() {
		Bid result = null;
		highest = longestSuit();
		
		int HCP = pc.getHighCardPoints();
		if (HCP <= 12 && highest != null) {
			result = new Bid(levelToBid(), highest);
			if (!auction.isValid(result)) {
				if (HCP >= 9) {
					result = new Bid(levelToBid() + 1, highest);
				} else {
					if (hand.getSuitLength(Hearts.i()) >= 4) {
						highest = Hearts.i();
						result = new Bid(levelToBid(), highest);
					}
					if (!auction.isValid(result) && hand.getSuitLength(Spades.i()) >= 4) {
						highest = Spades.i();
						result = new Bid(levelToBid(), highest);
						if (!auction.isValid(result)) {
							result = null;
						}
					}
				}
			}
			if (result != null) {
				return result;
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

		Suit enemy = lastBid.getTrump().asSuit();
		if (HCP >= 10) {
			return new Bid(lastBid.getValue() + 1, enemy);
		} else if (hand.isGood5LengthSuits(enemy)) {
			return new Pass();
		} else {
			return makeCheapestBid(desperateSuit());
		}
	}

	private Suit longestSuit() {
		Suit longest = null;
		for (Suit color : Suit.list) {
			if (hasNotBeenBid(color) && (hand.getSuitLength(color) >= 5
					|| (hand.getSuitLength(color)) == 4 && color.isMajorSuit())) {
				if (hand.AisStronger(color, longest)) {
					longest = color;
				}
			}
		}
		return longest;
	}

	private Suit desperateSuit() {
		Suit longest = null;
		for (Suit color : Suit.list) {
			if (hasNotBeenBid(color) && hand.getSuitLength(color) >= 3) {
				if (hand.AisStronger(color, longest)) {
					longest = color;
				}
			}
		}
		return longest;
	}

	private boolean hasNotBeenBid(Suit suit) {
		Set<Trump> enemyTrumps = auction.getEnemyTrumps();
		for (Trump trump : enemyTrumps) {
			if (suit.equals(trump)) {
				return true;
			}
		}
		return false;
	}

	private int levelToBid() {
		if (lastBid.greaterThan(new Bid(lastBid.getValue(), highest))) {
			return lastBid.getValue() + 1;
		} else {
			return lastBid.getValue();
		}
	}

	private Bid makeCheapestBid(Trump trump) {
		if (trump == null) {
			return null;
		}
		Bid candidate = new Bid(lastBid.getValue(), trump);
		if (auction.isValid(candidate)) {
			return candidate;
		} else {
			return new Bid(lastBid.getValue() + 1, trump);
		}
	}

}
