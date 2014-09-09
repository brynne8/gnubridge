package org.gnubridge.core.bidding;

import static org.gnubridge.core.Direction.*;
import static org.gnubridge.core.bidding.Bid.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gnubridge.core.Direction;
import org.gnubridge.core.deck.Trump;

public class Auctioneer {
	private Direction nextToBid;
	private int passCount;
	private Bid highBid;
	private int bidCount;
	private Call last;
	private Call beforeLast;
	private final List<Call> calls;

	public Auctioneer(Direction firstToBid) {
		this.nextToBid = firstToBid;
		bidCount = 0;
		last = null;
		beforeLast = null;
		calls = new ArrayList<Call>();
	}

	public Direction getNextToBid() {
		return nextToBid;
	}

	public List<Call> getCalls() {
		ArrayList<Call> result = new ArrayList<Call>();
		result.addAll(calls);
		return result;
	}

	public void bid(Bid b) {
		Bid bid = Bid.cloneBid(b);
		beforeLast = last;
		last = new Call(bid, nextToBid);
		calls.add(last);
		bidCount++;
		if (bid.isPass()) {
			passCount++;
		} else {
			passCount = 0;
			if (DOUBLE.equals(bid)) {
				getHighBid().makeDoubled();
			} else {
				highBid = bid;
			}
		}

		nextToBid = nextToBid.clockwise();
	}

	public boolean biddingFinished() {
		return (passCount == 3 && highBid != null) || passCount == 4;
	}

	public Bid getHighBid() {
		return highBid;
	}

	public boolean isOpeningBid() {
		for (Call call : calls) {
			if (!call.isPass()) {
				return false;
			}
		}
		return true;
	}

	public Call getPartnersLastCall() {
		return beforeLast;
	}

	public Call getPartnersCall(Call playerCall) {
		int current = calls.indexOf(playerCall);
		if (current >= 2) {
			return calls.get(current - 2);
		} else {
			return null;
		}
	}

	public Call getLastCall() {
		return last;
	}

	public boolean isValid(Bid candidate) {
		boolean result = false;
		if (candidate != null) {
			if (candidate.equals(DOUBLE)) {
				if (getHighCall() != null && !getHighCall().pairMatches(nextToBid) && !getHighBid().isDoubled()) {
					return true;
				}
			} else if (candidate.isPass() || candidate.greaterThan(getHighBid())) {
				result = true;
			}
		}
		return result;
	}

	public Direction getDummy() {
		Direction result = null;
		if (biddingFinished() && getHighCall() != null) {
			for (Call call : calls) {
				if (call.getBid().hasTrump() && call.getTrump().equals(getHighCall().getTrump())
						&& call.pairMatches(getHighCall().getDirection())) {
					result = call.getDirection().opposite();
					break;
				}
			}
		}
		return result;
	}

	public Call getHighCall() {
		Bid highBid = this.highBid;
		for (Call call : calls) {
			if (call.getBid().equals(highBid)) {
				return call;
			}
		}
		return null;
	}
	
	public Call getDoubledCall() {
		Call doubledCall = calls.get(bidCount - 3);
		if (doubledCall.isPass()) {
			doubledCall = calls.get(bidCount - 5);
		}
		return doubledCall;
	}

	/**
	 *      The parties in bidding are referred to by directions of the world, but
	 *      these are not the same directions as the ones during play. This method
	 *      provides a way to find the offset from what this class considers a
	 *      direction and what direction ends up being when the contract is played.
	 *      
	 *      ie: if auction's West becomes the dummy (South during play), the offset
	 *      is 1 move clockwise, and when given South as parameter, this method 
	 *      returns West.
	 */
	public Direction getDummyOffsetDirection(Direction original) {
		Direction d = getDummy();
		Direction offset = original;
		for (int i = 0; i < 4; i++) {
			if (d.equals(NORTH)) {
				break;
			} else {
				d = d.clockwise();
				offset = offset.clockwise();
			}
		}
		return offset;
	}

	public boolean may2ndOvercall() {
		if (bidCount == 1) {
			if (firstBid().is1Suit()) {
				return true;
			}
		} else if (bidCount == 2) {
			if (firstBid().isPass() && secondBid().is1Suit()) {
				return true;
			}
		} else if (bidCount == 3) {
			if (firstBid().isPass() && secondBid().isPass() && thirdBid().is1Suit()) {
				return true;
			}
		}
		return false;
	}

	public boolean may4thOvercall() {
		if (passCount != 2 || bidCount < 3 || bidCount > 6) {
			return false;
		}
		Bid opening = calls.get(bidCount - 3).getBid();
		if (isOpening(opening) && opening.is1Suit()) {
			return true;
		}
		return false;
	}

	private Bid thirdBid() {
		return calls.get(2).getBid();
	}

	private Bid secondBid() {
		return calls.get(1).getBid();
	}

	private Bid firstBid() {
		return calls.get(0).getBid();
	}

	private int getCallOrderZeroBased(Bid bid) {
		int result = -1;
		for (Call call : calls) {
			result++;
			if (bid.equals(call.getBid())) {
				return result;
			}
		}
		return -1;
	}

	private int OvercallIndex(Bid bid) {
		if (PASS.equals(bid)) {
			return 0;
		}
		int countPass = 0;
		int callOrder = getCallOrderZeroBased(bid);
		if (isOpening(calls.get(callOrder).getBid())) {
			return 0;
		}
		boolean ourBid = false;
		while (callOrder != 0) {
			callOrder--;
			Call call = calls.get(callOrder);
			if (!call.isPass()) {
				if (ourBid || !isOpening(call.getBid())) {
					countPass = -1;
				}
				break;
			}
			countPass++;
			ourBid = !ourBid;
		}
		if (countPass == 0) {
			return 1;
		} else if (countPass == 2) {
			return -1;
		} else {
			return 0;
		}
	}

	public boolean isOvercall(Bid bid) {
		return OvercallIndex(bid) != 0;
	}

	public boolean isFourthOvercall(Bid bid) {
		return OvercallIndex(bid) == -1;
	}

	public Set<Trump> getEnemyTrumps() {
		Set<Trump> result = new HashSet<Trump>();
		List<Call> reversedCalls = getCalls();
		Collections.reverse(reversedCalls);
		boolean enemyBid = true;
		for (Call call : reversedCalls) {
			if (call.getBid().hasTrump() && enemyBid) {
				result.add(call.getTrump());
			}
			enemyBid = !enemyBid;
		}

		return result;
	}
	
	public int biddingSequenceLength() {
		List<Call> reversedCalls = getCalls();
		Collections.reverse(reversedCalls);
		boolean ourBid = false;
		int seqLength = 0;
		for (Call call : reversedCalls) {
			if (ourBid) {
				if (call.getBid().hasTrump()) {
					seqLength++;
				} else {
					break;
				}
			}
			ourBid = !ourBid;
		}
		return seqLength;
	}

	public boolean isOpening(Bid bidWithTrump) {
		int index = getCallOrderZeroBased(bidWithTrump);
		if (index > 3) {
			return false;
		}
		if (index == 0) {
			return true;
		}
		if (index == 1 && calls.get(0).isPass()) {
			return true;
		}
		if (index == 2 && calls.get(0).isPass() && calls.get(1).isPass()) {
			return true;
		}
		if (index == 3 && calls.get(0).isPass() && calls.get(1).isPass() && calls.get(2).isPass()) {
			return true;
		}
		return false;
	}

}
