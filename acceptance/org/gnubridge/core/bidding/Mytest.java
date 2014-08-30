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
	
	public void test5() {
		givenBidding(ONE_DIAMONDS, PASS, ONE_SPADES, PASS);
		andPlayersCards("K,J,9", "K,Q,7,6,5", "K,Q,9", "A,J,4");
		expectPlayerToBid(TWO_NOTRUMP);
	}
	
	public void test6() {
		givenBidding(ONE_NOTRUMP, PASS, TWO_CLUBS, PASS, TWO_SPADES, PASS);
		andPlayersCards("K,9,8,5", "K,7,6", "K,Q,9,3", "10,7,2");
		expectPlayerToBid(FOUR_SPADES);
	}
	
	public void test7() {
		givenBidding(ONE_NOTRUMP, PASS, TWO_DIAMONDS, PASS, TWO_SPADES, PASS);
		andPlayersCards("K,9,8,5", "K,7,6", "K,Q,9,3", "10,7,2");
		expectPlayerToBid(FOUR_HEARTS);
	}

}
