package org.gnubridge.core.bidding.rules;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;

public abstract class PRebidNoTrump extends PartnersRebid {
	
	protected int level;

	public PRebidNoTrump(Auctioneer a, Hand h) {
		super(a, h);
	}

	@Override
	protected boolean applies() {
		if (super.applies()) {
			level = opening.getValue();
			return opening.getTrump().isNoTrump() && level < 3;
		}
		return false;
	}

}
