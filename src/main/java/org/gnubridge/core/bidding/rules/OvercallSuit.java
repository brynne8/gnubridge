package org.gnubridge.core.bidding.rules;

import java.util.Collection;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.PointCalculator;
import org.gnubridge.core.deck.Suit;

public class OvercallSuit extends BiddingRule {

	private final PointCalculator pc;
	boolean isFourthOvercall = false;

	public OvercallSuit(Auctioneer a, Hand h) {
		super(a, h);
		pc = new PointCalculator(hand);
	}

	@Override
	protected boolean applies() {
		if (pc.getCombinedPoints() >= 8) {
			if (auction.may2ndOvercall()) {
				return true;
			} else if (auction.may4thOvercall()) {
				isFourthOvercall = true;
				return true;
			}
		}
		return false;
	}

	@Override
	protected Bid prepareBid() {
		int points = pc.getCombinedPoints();
		if (points < 11) {
			return firstValidBid( //
					bidSuit(1, hand.getSuitsWithAtLeastCards(6)), //
					bidSuit(1, hand.getDecent5LengthSuits()));
		}
		if ((points >= 11 && points <= 17) ||
				(isFourthOvercall && points >= 8 && points <= 14)) {
			return firstValidBid( //
					bidSuit(1, hand.getSuitsWithAtLeastCards(5)), //
					bidSuit(2, hand.getSuitsWithAtLeastCards(6)), //
					bidSuit(2, hand.getGood5LengthSuits()));
		}

		return null;
	}

	private Bid bidSuit(int bidLevel, Collection<Suit> suits) {
		for (Suit suit : suits) {
			if (auction.isValid(new Bid(bidLevel, suit))) {
				return new Bid(bidLevel, suit);
			}
		}
		return null;
	}

	private Bid firstValidBid(Bid... bids) {
		for (Bid bid : bids) {
			if (bid != null) {
				return bid;
			}
		}
		return null;
	}

}
