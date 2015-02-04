package org.gnubridge.search;

import junit.framework.TestCase;

import org.gnubridge.core.Direction;

public class NodeTest extends TestCase {
	public void testGetMovesRoot() {
		Node root = new Node(null);
		assertEquals(0, root.getMoves().size());
	}

	public void testGetMovesFirstChild() {
		Node root = new Node(null);
		Node child = new Node(root);
		assertEquals(1, child.getMoves().size());
	}

	public void testGetMovesSecondChild() {
		Node root = new Node(null);
		@SuppressWarnings("unused")
		Node child1 = new Node(root);
		Node child2 = new Node(root);
		assertEquals(1, child2.getMoves().size());
	}

	public void testGetMovesGrandchild() {
		Node root = new Node(null);
		@SuppressWarnings("unused")
		Node child1 = new Node(root);
		Node child2 = new Node(root);
		Node grandChild = new Node(child2);
		assertEquals(2, grandChild.getMoves().size());
	}

	public void testPrunedIfParentThenChild() {
		Node root = new Node(null);
		Node child = new Node(root);
		assertFalse(child.isPruned());
		root.pruneAsAlpha();
		assertTrue(child.isPruned());
	}

	public void testIsAlpha() {
		Node root = new Node(null);
		root.setPlayerTurn(Direction.WEST_DEPRECATED);
		Node n_1 = new Node(root);
		n_1.setPlayerTurn(Direction.NORTH_DEPRECATED);
		Node n_1_1 = new Node(n_1);
		n_1_1.setPlayerTurn(Direction.EAST_DEPRECATED);
		Node n_1_1_1 = new Node(n_1_1);
		n_1_1_1.setPlayerTurn(Direction.SOUTH_DEPRECATED);
		Node n_1_1_1_1 = new Node(n_1_1_1);
		n_1_1_1_1.setPlayerTurn(Direction.SOUTH_DEPRECATED);
		Node n_1_1_1_2 = new Node(n_1_1_1);
		n_1_1_1_2.setPlayerTurn(Direction.EAST_DEPRECATED);
		assertTrue(root.isAlpha());
		assertFalse(n_1.isAlpha());
		assertTrue(n_1_1.isAlpha());
		assertFalse(n_1_1_1.isAlpha());
		assertFalse(n_1_1_1_1.isAlpha());
		assertTrue(n_1_1_1_2.isAlpha());
	}

	public void testIsAlphaBetaPruned() {
		Node root = new Node(null);
		Node child = new Node(root);
		root.pruneAsAlpha();
		assertTrue(child.isAlphaPruned());
		assertTrue(child.isPruned());
		assertFalse(child.isBetaPruned());

		root.pruneAsBeta();
		assertTrue(child.isBetaPruned());
		assertTrue(child.isPruned());
		assertFalse(child.isAlphaPruned());
	}

}

class MockNode extends Node {

	public MockNode(Node parent) {
		super(parent);
	}

	public MockNode(Node parent, boolean visit, boolean leaf) {
		this(parent);
		visited = visit;
		isLeaf = leaf;
	}

	private boolean visited = false;

	private boolean isLeaf;

	public void visit() {
		this.visited = true;
	}

	@Override
	public boolean visited() {
		return this.visited;
	}

	public boolean isLeaf() {
		return isLeaf;
	}

}
