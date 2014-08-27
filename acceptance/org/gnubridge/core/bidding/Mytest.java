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
	
	public void test2() {
		givenBidding(ONE_HEARTS, PASS, ONE_NOTRUMP, PASS);
		andPlayersCards("A,6", "K,Q,J,7,6", "A,9,5", "5,3,2");
		expectPlayerToBid(TWO_CLUBS);
	}
	
	public void test3() {
		givenBidding(ONE_SPADES, PASS, ONE_NOTRUMP, PASS);
		andPlayersCards("A,K,8,5,2", "7,6", "A,Q,9,5", "5,3");
		expectPlayerToBid(TWO_DIAMONDS);
	}
	
	public void test4() {
		givenBidding(ONE_HEARTS, PASS, ONE_NOTRUMP, PASS);
		andPlayersCards("A,J,8,6", "A,Q,7,6,2", "K,9,5", "6,4");
		expectPlayerToBid(TWO_DIAMONDS);
	}

}
