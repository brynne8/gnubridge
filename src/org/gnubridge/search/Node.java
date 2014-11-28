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
	        Node         parent;                     //Parent node
	        List<Node>   children;                   //ArrayList of child nodes
	private byte         playerTurn;                 //The player to play
	private final byte[] tricksTaken = new byte[2];  //Tricks WE|NS
	private Card         cardPlayed;                 //The card played on this node
	private Player       playerCardPlayed;           //The player who played the card
	private byte[]       identicalTwin;              //The tricks taken by an identical twin

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
			if (sibling == null || sibling.visited()) {
				continue;
			} else if (sibling == child) {
				hasThisChild = true;
			} else if (!sibling.isPruned()) {
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
		int max = getTricksTaken(getOtherPair());
		Node lowest = null;
		for (Node move : children) {
			if (move != null && !move.pruned
					&& move.getTricksTaken(getOtherPair()) == max) {
				if (lowest == null) {
					lowest = move;
				} else {
					Card lowCard = lowest.getCardPlayed();
					Card curCard = move.getCardPlayed();
					if (lowCard.trumps(curCard, position.getTrump())) {
						lowest = move;
					} else if (curCard.hasSameColorAs(lowCard) && !curCard.hasGreaterValueThan(lowCard)) {
						lowest = move;
					}
				}
			}
		}
		return lowest;
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

	Node getRoot() {
		Node node = this;
		while (node.parent != null) {
			node = node.parent;
		}
		return node;
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

	public void setPlayerCardPlayed(Player player) {
		playerCardPlayed = player;
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
		return getRoot().getOtherPair();
	}

	@Override
	public String toString() {
		return "Node " + getMoves().toString() + " / pruning status: " + isPruned() + " " + pruneTypeToString() + " / "
				+ getPlayerCardPlayed() + ": " + getCardPlayed() + " Tricks WE|NS: " + getTricksTaken()[0] + "|"
				+ getTricksTaken()[1];
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
			return ", alpha pruned";
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
		if (valueSet()) {
			return;
		}
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

	/** Calculates the value before current move is played */
	public void calculateValue() {
		if (isLeaf) {
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

	public void setIdenticalTwin(byte[] node) {
		identicalTwin = node;
	}

	public boolean isSingleton() {
		int unprunedChildCount = 0;
		for (Node child : children) {
			if (unprunedChildCount > 1) {
				break;
			}
			if (!child.pruned) {
				unprunedChildCount++;
			}
		}
		return unprunedChildCount == 1;
	}

	/** Parent should be pruned among its siblings */
	public void canApplyAlphaBeta() {
		if (valueSet && !isRoot() && !parent.isRoot()) {
			if (isAlpha()) {
				alphaPrune();
			} else {
				betaPrune();
			}
		}
	}

	private void alphaPrune() {
		Node grandParent = parent.parent;
		int maxPlayer = getMaxPlayer();
		int tricks = getTricksTaken(maxPlayer);
		if (parent.valueSet() && tricks <= parent.getTricksTaken(maxPlayer)) {
			return;
		}
		parent.setTricksTaken(Player.WEST_EAST, getTricksTaken(Player.WEST_EAST));
		parent.setTricksTaken(Player.NORTH_SOUTH, getTricksTaken(Player.NORTH_SOUTH));
		//shallow pruning
		for (Node node : grandParent.children) {
			if (!node.pruned && !node.equals(parent) && node.valueSet()) {
				if (node.getTricksTaken(maxPlayer) <= tricks) {
					parent.pruneAsAlpha();
					return;
				}
			}
		}
		//deep pruning
		if (!grandParent.isRoot() && !grandParent.parent.isRoot()) {
			Node ancestor = grandParent.parent.parent;
			for (Node node : ancestor.children) {
				if (!node.pruned && !node.equals(grandParent.parent) && node.valueSet()) {
					if (node.getTricksTaken(maxPlayer) <= tricks) {
						parent.pruneAsAlpha();
						return;
					}
				}
			}
		}
	}

	private void betaPrune() {
		Node grandParent = parent.parent;
		int maxPlayer = getMaxPlayer();
		int tricks = getTricksTaken(maxPlayer);
		if (parent.valueSet() && tricks >= parent.getTricksTaken(maxPlayer)) {
			return;
		}
		parent.setTricksTaken(Player.WEST_EAST, getTricksTaken(Player.WEST_EAST));
		parent.setTricksTaken(Player.NORTH_SOUTH, getTricksTaken(Player.NORTH_SOUTH));
		//shallow pruning
		for (Node node : grandParent.children) {
			if (!node.pruned && !node.equals(parent) && node.valueSet()) {
				if (node.getTricksTaken(maxPlayer) >= tricks) {
					parent.pruneAsBeta();
					return;
				}
			}
		}
		//deep pruning
		if (!grandParent.isRoot() && !grandParent.parent.isRoot()) {
			Node ancestor = grandParent.parent.parent;
			for (Node node : ancestor.children) {
				if (!node.pruned && !node.equals(grandParent.parent) && node.valueSet()) {
					if (node.getTricksTaken(maxPlayer) >= tricks) {
						parent.pruneAsAlpha();
						return;
					}
				}
			}
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

	public void pruneAsSequenceSibling() {
		setPruned(true, PruneType.PRUNE_SEQUENCE_SIBLINGS);
	}

	public void pruneAsSequenceSiblingPlayed() {
		setPruned(true, PruneType.PRUNE_SEQUENCE_SIBLINGS_PLAYED);
	}

	public void pruneAsDuplicatePosition() {
		setPruned(false, PruneType.PRUNE_DUPLICATE_POSITION);
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

	public boolean isAlpha() {
		return getMaxPlayer() == getCurrentPair();
	}

	boolean isBeta() {
		return !isAlpha();
	}

	public boolean valueSet() {
		return valueSet;
	}

	public boolean visited() {
		return visited;
	}

	boolean hasIdenticalTwin() {
		return identicalTwin != null;
	}

	public boolean isLast() {
		return !isRoot() && parent.isLastVisitedChild(this);
	}

	public int getPlayerTurn() {
		return this.playerTurn;
	}

	public Player getPlayerCardPlayed() {
		return playerCardPlayed;
	}

	public int getCurrentPair() {
		if (isRoot()) {
			return getOtherPair() == 0 ? 1 : 0;
		}
		return Player.matchPair(playerCardPlayed.pair());
	}

	public int getOtherPair() {
		return Player.matchPair(getPlayerTurn());
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
