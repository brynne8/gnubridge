package org.gnubridge.core.bidding.rules;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.Auctioneer;

public abstract class PRebidNoTrump extends PartnersRebid {
	
	protected int level;
	protected boolean fourthOvercalled = false;

	public PRebidNoTrump(Auctioneer a, Hand h) {
		super(a, h);
	}

	@Override
	protected boolean applies() {
		if (super.applies()) {
			level = opening.getValue();
			if (opening.getTrump().isNoTrump() && level < 3) {
				if (auction.isFourthOvercall(opening)) {
					fourthOvercalled = true;
				}
				return true;
			}
		}
		return false;
	}

}
