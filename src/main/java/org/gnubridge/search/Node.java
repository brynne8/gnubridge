package org.gnubridge.search;

import java.util.ArrayList;
import java.util.List;

import org.gnubridge.core.Card;
import org.gnubridge.core.Deal;
import org.gnubridge.core.Player;
import org.gnubridge.core.Trick;

public class Node {
	// Static values
	public static final byte UNINITIALIZED = -1;
	public static final byte ALPHA_UNINIT  = -1;
	public static final byte BETA_UNINIT   = 14;
	public static final byte AB_UNINIT     = -2;

	public static enum PruneType {
		NO_PRUNE, PRUNE_ALPHA, PRUNE_BETA, 
		PRUNE_SEQUENCE_SIBLINGS, PRUNE_DUPLICATE_POSITION
	}

	//Data of the node
	private Deal         position;                   //Current position
	        Node         parent;                     //Parent node
	        List<Node>   children;                   //ArrayList of child nodes
	        byte         alphaBeta;                  //Local alpha or beta value
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
		alphaBeta = AB_UNINIT;
		tricksTaken[Player.WEST_EAST] = UNINITIALIZED;
		tricksTaken[Player.NORTH_SOUTH] = UNINITIALIZED;
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
		int max = getTricksTaken(getCurrentPair());
		Node lowest = null;
		for (Node move : children) {
			if (move != null && !move.pruned) {
				int tricksTaken = move.getTricksTaken(getCurrentPair());
				if (tricksTaken >= max) {
					if (lowest == null || tricksTaken > max) {
						lowest = move;
						max = tricksTaken;
					} else {
						Card lowCard = lowest.getCardPlayed();
						Card curCard = move.getCardPlayed();
						if (lowCard.trumps(curCard, position.getTrump())) {
							lowest = move;
						} else if (!curCard.hasGreaterValueThan(lowCard)) {
							lowest = move;
						}
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

	/** 'isPruned()' is not pruned, it is a recursive method */
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

	private int getMaxPlayer() {
		return getRoot().getCurrentPair();
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
		List<Card> result = new ArrayList<Card>();
		for (Node node : parent.children) {
			if (!node.equals(this)) {
				result.add(node.getCardPlayed());
			}
		}
		return result;
	}

	public String toDebugString() {
		String result = "";
		result += "Node: " + parent.getMyIndex(this) + ", " + cardPlayed + "\n";
		result += "pruned? " + isPruned() + "\n";
		result += "   alpha/beta: " + isAlphaPruned() + "/" + isBetaPruned() + "\n";
		result += "   sequence/played sequence: " + isSequencePruned() + "\n";

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

	public void calculateValueFromChild() {
		int average    = 0;
		int childCount = 0;
		int maxPlayer = getMaxPlayer();
		for (Node child : children) {
			if (child != null && !child.pruned && child.valueSet()) {
				childCount++;
				average += child.getTricksTaken(maxPlayer);
			}
		}
		if (childCount != 0) {
			average = (average + childCount / 2) / childCount;
			setTricksTaken(maxPlayer, average);
			setTricksTaken((maxPlayer + 1) % 2, 13 - average);
		} else {
			setPruned(true, PruneType.NO_PRUNE);
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
		if (!isPruned() && valueSet() && !isRoot()) {
			if (isAlpha()) {
				alphaPrune();
			} else {
				betaPrune();
			}
		}
	}

	private void alphaPrune() {
		int maxPlayer = getMaxPlayer();
		int tricks    = getTricksTaken(maxPlayer);

		if (parent.valueSet() && tricks <= parent.getTricksTaken(maxPlayer)) {
			return;
		}

		int  times    = 0;
		Node betaNode = this;
		do {
			betaNode = betaNode.parent;
			if (betaNode.isRoot()) {
				return;
			}
			if (betaNode.isAlpha()) {
				continue;
			}
			if (betaNode.alphaBeta == AB_UNINIT) {
				betaNode.alphaBeta = BETA_UNINIT;
			}
			for (Node node : betaNode.parent.children) {
				if (!node.pruned && node != betaNode && node.valueSet()) {
					int tricksTaken = node.getTricksTaken(maxPlayer);
					if (tricksTaken < betaNode.alphaBeta) {
						if (tricksTaken <= tricks) {
							parent.pruneAsAlpha();
							return;
						}
						betaNode.alphaBeta = (byte) tricksTaken;
					}
				}
			}
			times++;
		} while (times != 2);
		parent.setTricksTaken(Player.WEST_EAST, getTricksTaken(Player.WEST_EAST));
		parent.setTricksTaken(Player.NORTH_SOUTH, getTricksTaken(Player.NORTH_SOUTH));
	}

	private void betaPrune() {
		int maxPlayer = getMaxPlayer();
		int tricks    = getTricksTaken(maxPlayer);

		if (parent.valueSet() && tricks >= parent.getTricksTaken(maxPlayer)) {
			return;
		}

		int  times     = 0;
		Node alphaNode = this;
		do {
			alphaNode = alphaNode.parent;
			if (alphaNode.isRoot()) {
				return;
			}
			if (alphaNode.isBeta()) {
				continue;
			}
			if (alphaNode.alphaBeta == AB_UNINIT) {
				alphaNode.alphaBeta = ALPHA_UNINIT;
			}
			for (Node node : alphaNode.parent.children) {
				if (!node.pruned && node != alphaNode && node.valueSet()) {
					int tricksTaken = node.getTricksTaken(maxPlayer);
					if (tricksTaken > alphaNode.alphaBeta) {
						if (tricksTaken >= tricks) {
							parent.pruneAsBeta();
							return;
						}
						alphaNode.alphaBeta = (byte) tricksTaken;
					}
				}
			}
			times++;
		} while (times != 2);
		parent.setTricksTaken(Player.WEST_EAST, getTricksTaken(Player.WEST_EAST));
		parent.setTricksTaken(Player.NORTH_SOUTH, getTricksTaken(Player.NORTH_SOUTH));
	}

	@Deprecated
	public boolean hasAlphaAncestor() {
		if (isRoot()) {
			return false;
		} else if (parent.isAlpha()) {
			return true;
		} else {
			return parent.hasAlphaAncestor();
		}
	}

	@Deprecated
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
