package org.gnubridge.core.bidding.rules;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;

public abstract class RebidToLevel2Response extends Rebid {

	public RebidToLevel2Response(Auctioneer a, Hand h) {
		super(a, h);
	}

	@Override
	protected boolean applies() {
		return super.applies() && opening.getValue() == 1 && response.getValue() == 2;
	}

}
