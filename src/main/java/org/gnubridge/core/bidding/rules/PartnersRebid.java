package org.gnubridge.core.bidding.rules;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.Call;

public abstract class PartnersRebid extends BiddingRule {
	protected Bid rebid;
	protected Bid response;
	protected Bid opening;

	public PartnersRebid(Auctioneer a, Hand h) {
		super(a, h);
	}

	@Override
	protected boolean applies() {
		if (auction.biddingSequenceLength() == 3) {
			Call getCall = auction.getPartnersLastCall();
			rebid = getCall.getBid();
			getCall = auction.getPartnersCall(getCall);
			response = getCall.getBid();
			opening = auction.getPartnersCall(getCall).getBid();
			return true;
		}
		return false;
	}

	@Override
	abstract protected Bid prepareBid();

}
