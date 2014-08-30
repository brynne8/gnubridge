package org.gnubridge.core.bidding.rules;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.PointCalculator;
import org.gnubridge.core.deck.Diamonds;
import org.gnubridge.core.deck.Hearts;
import org.gnubridge.core.deck.NoTrump;
import org.gnubridge.core.deck.Spades;
import org.gnubridge.core.deck.Trump;

public class RebidJacobyTransfer extends PRebidNoTrump {

	public RebidJacobyTransfer(Auctioneer a, Hand h) {
		super(a, h);
	}

	@Override
	protected boolean applies() {
		return super.applies() && response.getValue() == level + 1 
				&& (response.getTrump().equals(Diamonds.i())
						|| response.getTrump().equals(Hearts.i()));
	}

	@Override
	protected Bid prepareBid() {
		PointCalculator pc = new PointCalculator(hand);
		
		Trump transfer = response.getTrump();
		Trump trump = rebid.getTrump();
		int INVITATION = (level == 1) ? 10 : 5; 
		if (transfer.equals(Diamonds.i())) {
			if (trump.equals(Spades.i())) {
				if (pc.getHighCardPoints() >= 8) {
					return new Bid(4, Hearts.i());
				} else {
					return new Bid(3, NoTrump.i());
				}
			} else {
				if (pc.getHighCardPoints() >= INVITATION) {
					if (hand.getSuitLength(Hearts.i()) >= 3) {
						return new Bid(4, Hearts.i());
					} else {
						return new Bid(3, NoTrump.i());
					}
				}
			}
		} else {
			if (trump.equals(NoTrump.i())) {
				if (pc.getHighCardPoints() >= 8) {
					return new Bid(4, Spades.i());
				} else {
					return new Bid(3, NoTrump.i());
				}
			} else {
				if (pc.getHighCardPoints() >= INVITATION) {
					if (hand.getSuitLength(Hearts.i()) >= 3) {
						return new Bid(4, Spades.i());
					} else {
						return new Bid(3, NoTrump.i());
					}
				}
			}
		}
		
		return null;
	}

}
