package org.gnubridge.core.bidding.rules;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.ResponseCalculator;
import org.gnubridge.core.deck.NoTrump;
import org.gnubridge.core.deck.Suit;

public class Respond1ColorWithNewSuit extends Response {

	private ResponseCalculator pc;
	private Suit highestOver4;

	public Respond1ColorWithNewSuit(Auctioneer a, Hand h) {
		super(a, h);
	}

	@Override
	protected boolean applies() {
		boolean result = false;
		if (super.applies() && partnerBid1Color()) {
			pc = new ResponseCalculator(hand, partnersOpeningBid);
			highestOver4 = findHighestColorWithFourOrMoreCards();
			if (pc.getCombinedPoints() >= 6 && highestOver4 != null) {
				result = true;
			}
		}
		return result;
	}

	@Override
	protected Bid prepareBid() {
		Bid result = null;
		if (pc.getCombinedPoints() >= 17 && hand.getSuitLength(highestOver4) >= 5) {
			result = new Bid(jumpPartnersBid(), highestOver4);
			result.makeGameForcing();
		} else {
			result = new Bid(1, highestOver4);
			if (!auction.isValid(result) && pc.getCombinedPoints() >= 13
					&& hand.getSuitLength(highestOver4) >= 5) {
				result = new Bid(2, highestOver4);
			}
			result.makeForcing();
		}

		return result;
	}

	private int jumpPartnersBid() {
		if (partnersOpeningBid.greaterThan(new Bid(partnersOpeningBid.getValue(), highestOver4))) {
			return partnersOpeningBid.getValue() + 2;
		} else {
			return partnersOpeningBid.getValue() + 1;
		}
	}

	private boolean partnerBid1Color() {
		if (!NoTrump.i().equals(partnersOpeningBid.getTrump()) && 1 == partnersOpeningBid.getValue()) {
			return true;
		} else {
			return false;
		}
	}

	private Suit findHighestColorWithFourOrMoreCards() {
		Suit longer = null;
		for (Suit color : Suit.list) {
			if (hand.getSuitLength(color) >= 4 && hand.AisStronger(color, longer)
					&& !color.equals(partnersOpeningBid.getTrump())) {
				longer = color;
			}
		}
		return longer;
	}

}
