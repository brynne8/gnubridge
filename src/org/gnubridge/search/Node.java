package org.gnubridge.search;

import java.util.ArrayList;
import java.util.List;

import org.gnubridge.core.Card;
import org.gnubridge.core.Deal;
import org.gnubridge.core.Player;
import org.gnubridge.core.Trick;

public class Node {
	// Static values
	public static final byte UNITNITIALIZED = -1;
	public static final byte ALPHA_UNINIT   = -1;
	public static final byte BETA_UNINIT    = 14;

	public static enum PruneType {
		NO_PRUNE, PRUNE_ALPHA, PRUNE_BETA, PRUNE_SEQUENCE_SIBLINGS,
		PRUNE_SEQUENCE_SIBLINGS_PLAYED, PRUNE_DUPLICATE_POSITION
	}

	//Data of the node
	private Deal         position;                   //Current position
	        int          value;                      //Unknown meaning
	        Node         parent;                     //Parent node
	        List<Node>   children;                   //ArrayList of child nodes
	private byte         playerTurn;                 //The player to play
	private final byte[] tricksTaken = new byte[2];  //Tricks WE|NS:
	private Card         cardPlayed;                 //The card played on this node
	private Player       playerCardPlayed;           //The player who played the card
	private byte[]       identicalTwin;              //The tricks taken by an identical twin
	private Node         alphaAtPruneTime;           //The local alpha node while pruning

	//Flags
	        boolean   visited   = false;
	        boolean   pruned    = false;
	private boolean   isLeaf    = false;
	private PruneType pruneType = PruneType.NO_PRUNE;
	private boolean   valueSet  = false;

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
		if (isRoot()) {
			return new ArrayList<Card>();
		} else {
			List<Card> result = parent.getMoves();
			result.add(cardPlayed);
			return result;
		}
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
			if (sibling == null || sibling.pruned || sibling.visited()) {
				continue;
			} else if (sibling == child) {
				hasThisChild = true;
			} else {
				return false;
			}
		}
		return hasThisChild;
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
			if (move != null && !move.pruned
					&& move.getTricksTaken(getCurrentPair()) == max) {
				return move;
			}
		}
		return null;
	}

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
		if (isLeaf) {
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

	public void setLeaf(boolean b) {
		isLeaf = b;
	}

	private void setPruned(boolean b, PruneType type) {
		this.pruned = b;
		this.pruneType = type;
	}

	public boolean isPruned() {
		if (isRoot()) {
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
		if (isRoot()) {
			return false;
		} else if (parent.isAlpha()) {
			return true;
		} else {
			return parent.hasAlphaAncestor();
		}
	}

	public boolean hasBetaAncestor() {
		if (isRoot()) {
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
		parent.setTricksTaken(Player.WEST_EAST, getTricksTaken(Player.WEST_EAST));
		parent.setTricksTaken(Player.NORTH_SOUTH, getTricksTaken(Player.NORTH_SOUTH));
		parent.pruneAsBeta();
	}

	public void alphaPrune() {
		parent.setTricksTaken(Player.WEST_EAST, getTricksTaken(Player.WEST_EAST));
		parent.setTricksTaken(Player.NORTH_SOUTH, getTricksTaken(Player.NORTH_SOUTH));
		parent.pruneAsAlpha();
		alphaAtPruneTime = parent.getLocalAlphaNode();
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

	/* This function is not a recursive function */
	/* At most it will be called twice */
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
		if (!isRoot()) {
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
		return valueSet && !isRoot() && !parent.isRoot() && hasAlphaAncestor() && !parent.isAlpha()
				&& (getTricksTaken(getMaxPlayer()) <= parent.getLocalAlpha());
	}

	public boolean shouldBeBetaPruned() {
		return valueSet && !isRoot() && !parent.isRoot() && hasBetaAncestor() && !parent.isBeta()
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
		return pruned && (getPruneType() == PruneType.PRUNE_SEQUENCE_SIBLINGS);
	}

	public List<Card> getSiblingCards() {
		Deal g = parent.position;
		return g.getNextToPlay().getPossibleMoves(g.getCurrentTrick());
	}

	public boolean isPlayedSequencePruned() {
		return pruned && (getPruneType() == PruneType.PRUNE_SEQUENCE_SIBLINGS_PLAYED);
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
					&& !child.pruned
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
		if (hasIdenticalTwin()) {
			calculateValueFromIdenticalTwin();
		} else if (position.getCurrentTrick().isStart()) {
			calculateValueFromPosition();
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
		return !isRoot() && !parent.isPruned() && parent.isLastVisitedChild(this);
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
			if (!child.pruned) {
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

	// Utilities (Non-modifying)

	/** Get index for a child node.
	 * @param node a child node
	 * @return     index of the node  */
	private int getMyIndex(Node node) {
		return children.indexOf(node);
	}

	private boolean isRoot() {
		return parent == null;
	}

	public boolean valueSet() {
		return valueSet;
	}

	public boolean visited() {
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
	
	public Node getParent() {
		return parent;
	}

}
