package org.gnubridge.core.bidding;

import static org.gnubridge.core.bidding.Bid.*;

/**
 * 
 * contributed by Alexander Misel
 *
 */
public class Mytest extends BiddingAgentTestCase {

	public void test1() {
		givenBidding(ONE_DIAMONDS, DOUBLE, PASS);
		andPlayersCards("J,8,6", "7,6", "K,10,5", "A,J,5,3,2");
		expectPlayerToBid(THREE_CLUBS);
	}

}
