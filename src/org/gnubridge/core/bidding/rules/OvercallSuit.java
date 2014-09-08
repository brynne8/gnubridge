package org.gnubridge.core.bidding.rules;

import java.util.Collection;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.PointCalculator;
import org.gnubridge.core.deck.Suit;

public class OvercallSuit extends BiddingRule {

	private final PointCalculator calc;

	public OvercallSuit(Auctioneer a, Hand h) {
		super(a, h);
		calc = new PointCalculator(hand);
	}

	@Override
	protected boolean applies() {
		return (auction.may2ndOvercall() || auction.may4thOvercall())
				&& calc.getCombinedPoints() >= 10;
	}

	@Override
	protected Bid prepareBid() {
		Bid result = null;
		if (calc.getCombinedPoints() < 13) {
			return firstValidBid( //
					bidSuit(1, hand.getSuitsWithAtLeastCards(6)), //
					bidSuit(1, hand.getDecent5LengthSuits()));
		} else if (calc.getCombinedPoints() < 16) {
			return firstValidBid( //
					bidSuit(1, hand.getSuitsWithAtLeastCards(5)), //
					bidSuit(2, hand.getSuitsWithAtLeastCards(6)), //
					bidSuit(2, hand.getGood5LengthSuits()));
		} else if (calc.getCombinedPoints() < 19) {
			return firstValidBid( //
					bidSuit(1, hand.getSuitsWithAtLeastCards(5)), //
					bidSuit(2, hand.getSuitsWithAtLeastCards(5)));
		}
		return result;
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
