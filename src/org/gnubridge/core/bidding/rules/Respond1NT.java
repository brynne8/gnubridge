package org.gnubridge.core.bidding.rules;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.Pass;
import org.gnubridge.core.bidding.PointCalculator;
import org.gnubridge.core.deck.Clubs;
import org.gnubridge.core.deck.Diamonds;
import org.gnubridge.core.deck.Suit;
import org.gnubridge.core.deck.Hearts;
import org.gnubridge.core.deck.NoTrump;
import org.gnubridge.core.deck.Spades;

public class Respond1NT extends Response {

	private final PointCalculator pc;

	public Respond1NT(Auctioneer a, Hand h) {
		super(a, h);
		pc = new PointCalculator(hand);
	}

	@Override
	protected Bid prepareBid() {
		Bid result = null;
		Suit longer = Hearts.i();
		int hearts = hand.getSuitLength(Hearts.i());
		int spades = hand.getSuitLength(Spades.i());
		int length = hearts;
		if (spades > hearts) {
			longer = Spades.i();
			length = spades;
		}
		int points = pc.getCombinedPoints();
		if (length > 3) {
			if (length == 5 && (hearts == 4 || spades == 4)
					&& points >= 8 && points <= 9) {
				result = new Bid(2, Clubs.i());
			}
			if (length == 4) {
				if (points >= 8) {
					result = new Bid(2, Clubs.i());
				}
			} else if (length >= 6 && points >= 10 && points <=13) {
				result = new Bid(3, longer);
				result.makeGameForcing();
			} else {
				if (longer.equals(Hearts.i())) {
					result = new Bid(2, Diamonds.i());
				} else {
					result = new Bid(2, Hearts.i());
				}
			}
		}
		if (points >= 8 && points <= 10) {
			if (hand.getSuitLength(Clubs.i()) >= 6) {
				result = new Bid(3, Clubs.i());
			} else if (hand.getSuitLength(Diamonds.i()) >= 6) {
				result = new Bid(3, Diamonds.i());
			}
		}
		if (result == null) {
			if (pc.getHighCardPoints() <= 7) {
				result = new Pass();
			} else if (pc.getHighCardPoints() <= 9) {
				result = new Bid(2, NoTrump.i());
			} else if (pc.getHighCardPoints() <= 15) {
				result = new Bid(3, NoTrump.i());
			}
		}
		return result;
	}

	@Override
	protected boolean applies() {
		return super.applies() && new Bid(1, NoTrump.i()).equals(partnersOpeningBid);
	}
}
