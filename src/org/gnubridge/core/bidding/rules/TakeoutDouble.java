package org.gnubridge.core.bidding.rules;

import java.util.Set;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.Double;
import org.gnubridge.core.bidding.PointCalculator;
import org.gnubridge.core.deck.Suit;
import org.gnubridge.core.deck.Trump;

public class TakeoutDouble extends BiddingRule {
	
	private final PointCalculator pc;

	public TakeoutDouble(Auctioneer a, Hand h) {
		super(a, h);
		pc = new PointCalculator(hand);
	}

	@Override
	protected boolean applies() {
		if (((auction.may2ndOvercall() && pc.getHighCardPoints() >= 12) ||
				(auction.may4thOvercall() && pc.getHighCardPoints() >= 8))
				&& EachUnbidSuitWithAtLeast3Cards()) {
			return true;
		}
		return false;
	}

	@Override
	protected Bid prepareBid() {
		return new Double();
	}
	
	private boolean EachUnbidSuitWithAtLeast3Cards() {
		for (Suit color : Suit.list) {
			if (hasNotBeenBid(color) && hand.getSuitLength(color) < 3) {
				return false;
			}
		}
		return true;
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

}
