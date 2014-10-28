package org.gnubridge.search;

import java.util.ArrayList;
import java.util.List;

import org.gnubridge.core.Card;
import org.gnubridge.core.Deal;
import org.gnubridge.core.Player;
import org.gnubridge.core.Trick;

public class Node {
	public static final byte UNITNITIALIZED = -1;

	private static final byte ALPHA_UNINIT = -1;

	public static final byte BETA_UNINIT = 14;

	public static enum PruneType {
		NO_PRUNE, PRUNE_ALPHA, PRUNE_BETA, PRUNE_SEQUENCE_SIBLINGS,
		PRUNE_SEQUENCE_SIBLINGS_PLAYED, PRUNE_DUPLICATE_POSITION
	}

	int value;

	Node parent;

	List<Node> children;

	private byte playerTurn;

	private final byte[] tricksTaken = new byte[2];

	private Card cardPlayed;

	boolean visited = false;

	private boolean isLeaf = false;

	private boolean pruned = false;

	private PruneType pruneType = PruneType.NO_PRUNE;

	private Player playerCardPlayed;

	private boolean valueSet = false;

	private Deal position;

	private byte[] identicalTwin;

	private Node alphaAtPruneTime;

	public Node(Node parent) {
		this.parent = parent;
		children = new ArrayList<Node>();
		if (parent != null) {
			parent.children.add(this);
		}
		tricksTaken[Player.WEST_EAST] = UNITNITIALIZED;
		tricksTaken[Player.NORTH_SOUTH] = UNITNITIALIZED;
	}

	public Node(Node parent, int playerTurn) {
		this(parent);
		setPlayerTurn(playerTurn);
	}

	public List<Card> getMoves() {
		if (parent == null) {
			return new ArrayList<Card>();
		} else {
			List<Card> result = parent.getMoves();
			result.add(cardPlayed);
			return result;
		}
	}

	private int getMyIndex(Node node) {
		return children.indexOf(node);
	}

	public void setPlayerTurn(int direction) {
		this.playerTurn = (byte) direction;

	}

	public void setTricksTaken(int pair, int i) {
		valueSet = true;
		tricksTaken[pair] = (byte) i;
	}

	public boolean isLastVisitedChild(Node child) {
		boolean hasThisChild = false;
		for (Node sibling : children) {
			if (sibling == null || sibling.isPruned() || sibling.visited()) {
				continue;
			} else if (sibling == child) {
				hasThisChild = true;
			} else {
				return false;
			}
		}
		return hasThisChild;
	}

	private boolean isLeaf() {
		return isLeaf;
	}

	private boolean visited() {
		return visited;
	}

	public int getCurrentPair() {
		return Player.matchPair(getPlayerTurn());
	}

	public int getPlayerTurn() {
		return this.playerTurn;
	}

	public int getTricksTaken(int pair) {
		return tricksTaken[pair];
	}

	public byte[] getTricksTaken() {
		return tricksTaken;
	}

	public void setCardPlayed(Card card) {
		this.cardPlayed = card;

	}

	public Card getCardPlayed() {
		return this.cardPlayed;
	}

	public Node getBestMove() {
		if (children.size() == 0) {
			return this;
		}
		int max = getTricksTaken(getCurrentPair());
		for (Node move : children) {
			if (move != null && !move.isPruned()
					&& move.getTricksTaken(getCurrentPair()) == max) {
				return move;
			}
		}
		return null;
	}

	//private Node getNodeWithLowestValueCard(List<Node> nodes) {
	//	Node lowest = null;
	//	for (Node node : nodes) {
	//		if (lowest == null || node.cardPlayed.getValue() < lowest.cardPlayed.getValue()) {
	//			lowest = node;
	//		}
	//	}
	//	return lowest;
	//}

	public void printOptimalPath(Deal g) {
		Node move = getBestMove();
		if (move == this) {
			for (Card card : getMoves()) {
				Trick currentTrick = g.getCurrentTrick();
				System.out.println(g.getNextToPlay() + ": " + card);
				g.doNextCard(card);
				if (currentTrick.isDone()) {
					System.out.println("  Trick taken by " + g.getPlayer(g.getWinnerIndex(currentTrick)));
				}
			}
		} else {
			move.printOptimalPath(g);
		}

	}

	public void printLeafs() {
		if (isLeaf()) {
			System.out.println("*********\nNode: " + getMoves());
			System.out.println(printMoves());
		} else {
			for (Node child : children) {
				if (child != null) {
					child.printLeafs();
				}
			}
		}
	}

	public String printMoves() {
		if (isRoot()) {
			return "";
		} else {
			return parent.printMoves() + getPlayerCardPlayed() + ": " + getCardPlayed()
					+ (isPruned() ? " (pruned " + pruneTypeToString() + ")" : "") + "\n";
		}
	}

	private String pruneTypeToString() {
		String result = "";
		switch (pruneType) {
		case PRUNE_ALPHA: result = "ALPHA"; break;
		case PRUNE_BETA: result = "BETA"; break;
		case PRUNE_DUPLICATE_POSITION: result = "DUPLICATE POSITION"; break;
		case PRUNE_SEQUENCE_SIBLINGS: result = "SIBLING SEQUENCE"; break;
		case PRUNE_SEQUENCE_SIBLINGS_PLAYED: result = "SIBLING IN PLAYED SEQUENCE"; break;
		default: result = "UNKNOWN";
		}
		return result;
	}

	private boolean isRoot() {
		return parent == null;
	}

	public void setLeaf(boolean b) {
		isLeaf = b;

	}

	private void setPruned(boolean b, PruneType type) {
		this.pruned = b;
		this.pruneType = type;
	}

	public boolean isPruned() {
		if (parent == null) {
			return pruned;
		} else if (pruned) {
			return true;
		} else {
			return parent.isPruned();
		}
	}

	public boolean isAlpha() {
		return getMaxPlayer() == getCurrentPair();
	}

	Node getRoot() {
		if (parent == null) {
			return this;
		} else {
			return parent.getRoot();
		}
	}

	public boolean isAlphaPruned() {
		return isPruned() && (getPruneType() == PruneType.PRUNE_ALPHA);
	}

	public boolean isBetaPruned() {
		return isPruned() && (getPruneType() == PruneType.PRUNE_BETA);
	}

	public PruneType getPruneType() {
		if (parent == null) {
			return pruneType;
		} else if (pruned) {
			return pruneType;
		} else {
			return parent.getPruneType();
		}
	}

	public boolean hasAlphaAncestor() {
		if (parent == null) {
			return false;
		} else if (parent.isAlpha()) {
			return true;
		} else {
			return parent.hasAlphaAncestor();
		}
	}

	public boolean hasBetaAncestor() {
		if (parent == null) {
			return false;
		} else if (parent.isBeta()) {
			return true;
		} else {
			return parent.hasBetaAncestor();
		}
	}

	boolean isBeta() {
		return !isAlpha();
	}

	public void betaPrune() {
		if (parent != null && !parent.isBeta()) { //&& !parent.parent.isRoot() - always true for beta pruning
			parent.setTricksTaken(Player.WEST_EAST, getTricksTaken(Player.WEST_EAST));
			parent.setTricksTaken(Player.NORTH_SOUTH, getTricksTaken(Player.NORTH_SOUTH));
			parent.setPruned(true, PruneType.PRUNE_BETA);
			parent.betaPrune();
		}

	}

	public void alphaPrune() {
		if (parent != null && !parent.isAlpha() && !parent.parent.isRoot()) {
			parent.setTricksTaken(Player.WEST_EAST, getTricksTaken(Player.WEST_EAST));
			parent.setTricksTaken(Player.NORTH_SOUTH, getTricksTaken(Player.NORTH_SOUTH));
			parent.setPruned(true, PruneType.PRUNE_ALPHA);
			alphaAtPruneTime = parent.getLocalAlphaNode();
			parent.alphaPrune();
		}

	}

	public void setPlayerCardPlayed(Player player) {
		playerCardPlayed = player;

	}

	public Player getPlayerCardPlayed() {
		return playerCardPlayed;
	}

	public boolean hasAncestor(Node ancestor) {
		if (this == ancestor) {
			return true;
		} else if (parent == null) {
			return false;
		} else {
			return parent.hasAncestor(ancestor);
		}
	}

	public int getLocalAlpha() {
		Node localAlpha = getLocalAlphaNode();
		if (localAlpha != null) {
			return getLocalAlphaNode().getTricksTaken(getMaxPlayer());
		} else {
			return ALPHA_UNINIT;
		}
	}

	private Node getLocalAlphaNode() {
		if (isAlpha()) {
			int max = ALPHA_UNINIT;
			Node result = null;
			for (Node child : children) {
				if (child.getTricksTaken(getMaxPlayer()) > max) {
					max = child.getTricksTaken(getMaxPlayer());
					result = child;
				}
			}
			return result;
		} else {
			return parent.getLocalAlphaNode();
		}
	}

	private String getUniqueId() {
		int myIndex = 0;
		if (parent != null) {
			myIndex = parent.getMyIndex(this);
		}
		return getDepth() + "-" + myIndex;
	}

	public int getLocalBeta() {
		if (isBeta()) {
			int min = BETA_UNINIT;
			for (Node child : children) {
				if (child.getTricksTaken(getMaxPlayer()) != -1 && child.getTricksTaken(getMaxPlayer()) < min) {
					min = child.getTricksTaken(getMaxPlayer());
				}
			}
			return min;
		} else {
			return parent.getLocalBeta();
		}
	}

	private int getMaxPlayer() {
		return getRoot().getCurrentPair();
	}

	public boolean shouldBeAlphaPruned() {
		return valueSet && parent != null && parent.parent != null && hasAlphaAncestor() && !parent.isAlpha()
				&& (getTricksTaken(getMaxPlayer()) <= parent.getLocalAlpha());
	}

	public boolean shouldBeBetaPruned() {
		return valueSet && parent != null && parent.parent != null && hasBetaAncestor() && !parent.isBeta()
				&& (getTricksTaken(getMaxPlayer()) >= parent.getLocalBeta());

	}

	@Override
	public String toString() {
		return "Node " + getMoves().toString() + " / pruning status: " + isPruned() + " " + pruneTypeToString() + " / "
				+ getPlayerCardPlayed() + ": " + getCardPlayed() + " Tricks WE|NS: " + getTricksTaken()[0] + "|"
				+ getTricksTaken()[1];
	}

	private List<Node> siblings() {
		List<Node> result = new ArrayList<Node>();
		if (parent != null) {
			for (Node node : parent.children) {
				if (!node.equals(this)) {
					result.add(node);
				}
			}
		}
		return result;
	}

	public boolean isSequencePruned() {
		return isPruned() && (getPruneType() == PruneType.PRUNE_SEQUENCE_SIBLINGS);
	}

	public List<Card> getSiblingCards() {
		Deal g = parent.position;
		return g.getNextToPlay().getPossibleMoves(g.getCurrentTrick());
	}

	public boolean isPlayedSequencePruned() {
		return isPruned() && (getPruneType() == PruneType.PRUNE_SEQUENCE_SIBLINGS_PLAYED);
	}

	public void pruneAsDuplicatePosition() {
		setPruned(false, PruneType.PRUNE_DUPLICATE_POSITION);

	}

	public boolean isPrunedDuplicatePosition() {
		// return isPruned() && (getPruneType() == PRUNE_DUPLICATE_POSITION);
		// return (pruneType == PRUNE_DUPLICATE_POSITION);
		return hasIdenticalTwin();
	}

	public String toDebugString() {
		String result = "";
		result += "Node: " + parent.getMyIndex(this) + ", " + cardPlayed + "\n";
		result += "pruned? " + isPruned() + "\n";
		result += "   alpha/beta: " + isAlphaPruned() + "/" + isBetaPruned() + "\n";
		result += "   sequence/played sequence: " + isSequencePruned() + "/" + isPlayedSequencePruned() + "\n";

		return result;
	}

	public String printAsTree() {
		String result = "";
		result = padSpaces(getDepth()) + getUniqueId() + " " + getPlayerCardPlayed() + ": " + cardPlayed + ", max: "
				+ getTricksTaken(getMaxPlayer()) + getPruned();
		for (Node child : children) {
			if (child != null) {
				result += "\n" + child.printAsTree();
			} else {
				result += "\n NULL";
			}
		}
		return result;
	}

	private String getPruned() {
		if (isAlphaPruned()) {
			String betterMove = "no better move available";
			if (alphaAtPruneTime != null) {
				betterMove = alphaAtPruneTime.getUniqueId() + ": " + alphaAtPruneTime.getCardPlayed() + " with max "
						+ alphaAtPruneTime.getTricksTaken(getMaxPlayer());
			}
			return ", alpha pruned (" + betterMove + ")";
		} else if (isBetaPruned()) {
			return ", beta pruned";
		}
		return "";
	}

	private String padSpaces(int depth) {
		String result = "";
		for (int i = 0; i < depth; i++) {
			result += "   ";
		}
		return result;
	}

	private int getDepth() {
		if (parent == null) {
			return 0;
		} else {
			return 1 + parent.getDepth();
		}
	}

	Node getUnprunedChildWithMostTricksForCurrentPair() {
		Node maxChild = null;
		for (Node child : children) {
			if (child != null
					&& !child.isPruned()
					&& (maxChild == null || child.getTricksTaken(getCurrentPair()) > maxChild
							.getTricksTaken(getCurrentPair()))) {
				maxChild = child;
			}
		}
		return maxChild;
	}

	public void calculateValueFromChild() {
		Node maxChild = getUnprunedChildWithMostTricksForCurrentPair();
		if (maxChild != null) {
			setTricksTaken(Player.WEST_EAST, maxChild.getTricksTaken(Player.WEST_EAST));
			setTricksTaken(Player.NORTH_SOUTH, maxChild.getTricksTaken(Player.NORTH_SOUTH));

		}

	}

	public void calculateValueFromPosition() {
		setTricksTaken(Player.WEST_EAST, position.getTricksTaken(Player.WEST_EAST));
		setTricksTaken(Player.NORTH_SOUTH, position.getTricksTaken(Player.NORTH_SOUTH));

	}

	public void setPosition(Deal position) {
		this.position = position;
	}

	public void calculateValue() {
		if (isLeaf()) {
			if (hasIdenticalTwin()) {
				calculateValueFromIdenticalTwin();
			} else {
				calculateValueFromPosition();
			}
		} else {
			calculateValueFromChild();
		}
	}

	private void calculateValueFromIdenticalTwin() {
		setTricksTaken(Player.NORTH_SOUTH, identicalTwin[Player.NORTH_SOUTH]);
		setTricksTaken(Player.WEST_EAST, identicalTwin[Player.WEST_EAST]);
	}

	boolean hasIdenticalTwin() {
		return identicalTwin != null;
	}

	public boolean canPrune() {
		return parent != null && (parent.isLastVisitedChild(this));
	}

	public void setIdenticalTwin(byte[] node) {
		identicalTwin = node;

	}

	public Node getSiblingNodeForCard(Card card) {
		for (Node sibling : siblings()) {
			if (sibling.getCardPlayed().equals(card)) {
				return sibling;
			}
		}
		throw new RuntimeException("Cannot find appropriate sibling node");
	}

	public int getUnprunedChildCount() {
		int unprunedChildCount = 0;
		for (Node child : children) {
			if (!child.isPruned()) {
				unprunedChildCount++;
			}
		}
		return unprunedChildCount;
	}

	public void pruneAsSequenceSibling() {
		setPruned(true, PruneType.PRUNE_SEQUENCE_SIBLINGS);
	}

	public void pruneAsSequenceSiblingPlayed() {
		setPruned(true, PruneType.PRUNE_SEQUENCE_SIBLINGS_PLAYED);
	}

	public void pruneAsAlpha() {
		setPruned(true, PruneType.PRUNE_ALPHA);
	}

	public void pruneAsBeta() {
		setPruned(true, PruneType.PRUNE_BETA);
	}

	public Node getParent() {
		return parent;
	}

}
