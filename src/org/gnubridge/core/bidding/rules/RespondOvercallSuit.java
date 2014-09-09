package org.gnubridge.core.bidding.rules;

import static org.gnubridge.core.deck.Trump.*;

import java.util.List;

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
		if (hand.getSuitLength(partnersOpeningBid.getTrump().asSuit()) >= 3) {
			if (points >= 8 && points <= 11) {
				return new Bid(partnersOpeningBid.getValue() + 1, partnersOpeningBid.getTrump());
			}
			if (points >= 12 && points <= 14) {
				return new Bid(partnersOpeningBid.getValue() + 2, partnersOpeningBid.getTrump());
			}
			if (points >= 15) { //Pavlicek is unclear in lesson 7, see tests && partnersOpeningBid.getTrump().isMajorSuit()) {
				return new Bid(MAJOR_SUIT_GAME, partnersOpeningBid.getTrump());
			}

		}
		List<Suit> suitsWithAtLeast5Cards = hand.getSuitsWithAtLeastCards(5);
		if (suitsWithAtLeast5Cards.size() > 0) {
			Suit color = suitsWithAtLeast5Cards.get(0);
			if (points >= 8 && points <= 11) {
				return makeCheapestBid(color);
			}
			if (points >= 12 && points <= 14) {
				Bid bid = makeCheapestBid(color);
				return new Bid(bid.getValue() + 1, color);
			}
			if (points >= 15) { //Pavlicek is unclear in lesson 7, see tests && highestSuitWithAtLeast5Cards.isMajorSuit()) {
				return new Bid(MAJOR_SUIT_GAME, color);
			}
		}

		if (haveStopperInEnemySuit()) {
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
