package org.gnubridge.core.bidding.rules;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.Call;

public abstract class Rebid extends BiddingRule {

	protected Bid response;
	protected Bid opening;

	public Rebid(Auctioneer a, Hand h) {
		super(a, h);
	}

	@Override
	protected boolean applies() {
		if (auction.biddingSequenceLength() == 2) {
			Call getCall = auction.getPartnersLastCall();
			response = getCall.getBid();
			opening = auction.getPartnersCall(getCall).getBid();
			if (auction.isOpening(opening)) {
				return true;
			}
		}
		return false;
	}

	@Override
	abstract protected Bid prepareBid();

}
