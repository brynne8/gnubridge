package org.gnubridge.core.bidding;

import java.util.ArrayList;
import java.util.List;

import org.gnubridge.core.Hand;
import org.gnubridge.core.bidding.rules.AlwaysPass;
import org.gnubridge.core.bidding.rules.BiddingRule;
import org.gnubridge.core.bidding.rules.Open1Color;
import org.gnubridge.core.bidding.rules.Open1NT;
import org.gnubridge.core.bidding.rules.Open2NT;
import org.gnubridge.core.bidding.rules.Overcall1NT;
import org.gnubridge.core.bidding.rules.OvercallSuit;
import org.gnubridge.core.bidding.rules.PreemptiveBid;
import org.gnubridge.core.bidding.rules.Rebid1ColorOriginalSuit;
import org.gnubridge.core.bidding.rules.Rebid1ColorRaiseOpener;
import org.gnubridge.core.bidding.rules.Rebid1ColorRaisePartner;
import org.gnubridge.core.bidding.rules.Rebid1ColorWithNT;
import org.gnubridge.core.bidding.rules.Rebid1ColorWithNewSuit;
import org.gnubridge.core.bidding.rules.Rebid1NT;
import org.gnubridge.core.bidding.rules.Rebid2C;
import org.gnubridge.core.bidding.rules.Rebid2NT;
import org.gnubridge.core.bidding.rules.RebidAfter1NT;
import org.gnubridge.core.bidding.rules.RebidAfterForcing1NT;
import org.gnubridge.core.bidding.rules.RebidForcing1NT;
import org.gnubridge.core.bidding.rules.RebidJacobyTransfer;
import org.gnubridge.core.bidding.rules.RebidMinorSuitStayman;
import org.gnubridge.core.bidding.rules.RebidStayman;
import org.gnubridge.core.bidding.rules.RebidTakeoutDouble;
import org.gnubridge.core.bidding.rules.Respond1ColorRaiseMajorSuit;
import org.gnubridge.core.bidding.rules.Respond1ColorRaiseMinorSuit;
import org.gnubridge.core.bidding.rules.Respond1ColorWithNT;
import org.gnubridge.core.bidding.rules.Respond1ColorWithNewSuit;
import org.gnubridge.core.bidding.rules.Respond1NT;
import org.gnubridge.core.bidding.rules.Respond2C;
import org.gnubridge.core.bidding.rules.Respond2NT;
import org.gnubridge.core.bidding.rules.RespondOvercallSuit;
import org.gnubridge.core.bidding.rules.RespondTakeoutDouble;
import org.gnubridge.core.bidding.rules.Strong2C;
import org.gnubridge.core.bidding.rules.TakeoutDouble;
import org.gnubridge.core.bidding.rules.WeakTwo;

public class BiddingAgent {

	private final List<BiddingRule> rules;

	public BiddingAgent(Auctioneer a, Hand h) {
		rules = new ArrayList<BiddingRule>();
		rules.add(new Strong2C(a, h));
		rules.add(new Open2NT(a, h));
		rules.add(new Open1NT(a, h));
		rules.add(new Open1Color(a, h));
		rules.add(new WeakTwo(a, h));
		rules.add(new OvercallSuit(a, h));
		rules.add(new Overcall1NT(a, h));
		rules.add(new TakeoutDouble(a, h));
		rules.add(new PreemptiveBid(a, h));
		rules.add(new RespondOvercallSuit(a, h));
		rules.add(new RespondTakeoutDouble(a, h));
		rules.add(new Respond2C(a, h));
		rules.add(new Respond2NT(a, h));
		rules.add(new Respond1NT(a, h));
		rules.add(new Respond1ColorRaiseMajorSuit(a, h));
		rules.add(new Respond1ColorWithNewSuit(a, h));
		rules.add(new Respond1ColorRaiseMinorSuit(a, h));
		rules.add(new Respond1ColorWithNT(a, h));
		rules.add(new RebidTakeoutDouble(a, h));
		rules.add(new Rebid2C(a, h));
		rules.add(new Rebid2NT(a, h));
		rules.add(new Rebid1NT(a, h));
		rules.add(new RebidAfterForcing1NT(a, h));
		rules.add(new Rebid1ColorRaiseOpener(a, h));
		rules.add(new Rebid1ColorRaisePartner(a, h));
		rules.add(new Rebid1ColorWithNT(a, h));
		rules.add(new Rebid1ColorWithNewSuit(a, h));
		rules.add(new Rebid1ColorOriginalSuit(a, h));
		rules.add(new RebidAfter1NT(a, h));
		rules.add(new RebidStayman(a, h));
		rules.add(new RebidJacobyTransfer(a, h));
		rules.add(new RebidMinorSuitStayman(a, h));
		rules.add(new RebidForcing1NT(a, h));
		rules.add(new AlwaysPass());
	}

	public Bid getBid() {
		Bid result = null;
		for (BiddingRule rule : rules) {
			result = rule.getBid();
			if (result != null) {
				System.out.println("rule: " + rule.getClass() + " recommends: " + result);
				break;
			}
		}
		return result;
	}

}
