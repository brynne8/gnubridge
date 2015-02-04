package org.gnubridge.search.pruning;

import org.gnubridge.search.Node;

public class AlphaBeta implements PruningStrategy {

	@Override
	public void prune(Node node) {
		node.canApplyAlphaBeta();
	}

}
