package org.gnubridge.core.bidding.rules;

import java.util.List;

import org.gnubridge.core.Card;
import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.deck.Hearts;
import org.gnubridge.core.deck.Spades;
import org.gnubridge.core.deck.Suit;

public class PreemptiveBid extends BiddingRule {

	private Suit longestSuit;

	public PreemptiveBid(Auctioneer a, Hand h) {
		super(a, h);
	}

	@Override
	protected boolean applies() {
		if (auction.isOpeningBid() || (auction.may2ndOvercall() || auction.may4thOvercall())) {
			longestSuit = hand.getLongestSuit();
			if (hand.getSuitLength(longestSuit) < 6) {
				return false;
			}
			if (!longestSuit.equals(Hearts.i()) && hand.getSuitLength(Hearts.i()) >= 4
					&& hand.getSuitHi2Low(Hearts.i()).get(0).getValue() >= Card.QUEEN) {
				return false;
			}
			if (!longestSuit.equals(Spades.i()) && hand.getSuitLength(Spades.i()) >= 4
					&& hand.getSuitHi2Low(Spades.i()).get(0).getValue() >= Card.QUEEN) {
				return false;
			}
			int tricks = playingTricks();
			int vulnerabilityIndex = auction.getVulnerabilityIndex();
			if (vulnerabilityIndex == 2) {
				tricks += 2;
			} else if (vulnerabilityIndex == 1) {
				tricks += 4;
			} else {
				tricks += 3;
			}
			if (tricks >= 9) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected Bid prepareBid() {
		return new Bid(3, longestSuit);
	}
	
	private int playingTricks() {
		int trickCount = 0;
		for (Suit color : Suit.list) {
			trickCount += doublePlayingTricks(color);
		}
		return trickCount / 2;
	}
	
	private int doublePlayingTricks(Suit color) {
		int tricks = 0;
		List<Card> cards = hand.getSuitHi2Low(color);
		int length = cards.size();
		if (length >= 1) {
			if (cards.get(0).getValue() == Card.ACE) tricks += 2;
			if (length >= 2) {
				if (tricks == 2) {
					if (cards.get(1).getValue() == Card.KING) tricks += 2;
					else if (cards.get(1).getValue() == Card.QUEEN) tricks++;
					if (length >= 3) {
						if (tricks == 4) {
							if (cards.get(2).getValue() == Card.QUEEN) tricks += 2;
							else if (cards.get(2).getValue() == Card.JACK) tricks++;
						} else if (tricks == 3) {
							if (cards.get(2).getValue() == Card.JACK) tricks += 2;
							else if (cards.get(2).getValue() == Card.TEN) tricks++;
						} else if (cards.get(1).getValue() == Card.JACK) {
							tricks += 1;
						}
					}
				} else if (cards.get(0).getValue() == Card.KING) {
					if (cards.get(1).getValue() == Card.QUEEN || cards.get(1).getValue() == Card.JACK) {
						if (length >= 3) {
							if (cards.get(1).getValue() == Card.QUEEN) tricks += 3;
							else if (cards.get(2).getValue() == Card.TEN) tricks += 3;
						} else if (cards.get(1).getValue() == Card.TEN) {
							tricks += 2;
						}
					}
				} else if (cards.get(0).getValue() == Card.QUEEN) {
					if (length >= 3) {
						if (cards.get(1).getValue() == Card.JACK) tricks += 2;
						else tricks++;
					}
				} else if (cards.get(0).getValue() == Card.JACK && cards.get(1).getValue() == Card.TEN) {
					tricks++;
				}
			}
			if (length > 3) {
				tricks += 2 * (length - 3);
			}
		}
		return tricks;
	}

}