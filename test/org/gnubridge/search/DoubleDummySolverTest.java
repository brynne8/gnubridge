package org.gnubridge.search;

import java.util.List;

import junit.framework.TestCase;

import org.gnubridge.core.Card;
import org.gnubridge.core.Deal;
import org.gnubridge.core.Direction;
import org.gnubridge.core.Hand;
import org.gnubridge.core.Player;
import org.gnubridge.core.deck.Ace;
import org.gnubridge.core.deck.Clubs;
import org.gnubridge.core.deck.Diamonds;
import org.gnubridge.core.deck.Eight;
import org.gnubridge.core.deck.Five;
import org.gnubridge.core.deck.Four;
import org.gnubridge.core.deck.Hearts;
import org.gnubridge.core.deck.Jack;
import org.gnubridge.core.deck.King;
import org.gnubridge.core.deck.Nine;
import org.gnubridge.core.deck.NoTrump;
import org.gnubridge.core.deck.Queen;
import org.gnubridge.core.deck.Seven;
import org.gnubridge.core.deck.Six;
import org.gnubridge.core.deck.Spades;
import org.gnubridge.core.deck.Ten;
import org.gnubridge.core.deck.Three;
import org.gnubridge.core.deck.Two;
import org.gnubridge.presentation.GameUtils;

public class DoubleDummySolverTest extends TestCase {

	public void testExaminePositionSetsNextToPlay() {
		Node node = new Node(null);
		Deal game = new Deal(NoTrump.i());
		GameUtils.initializeSingleColorSuits(game, 2);
		game.setNextToPlay(Direction.SOUTH_DEPRECATED);
		DoubleDummySolver s = new DoubleDummySolver(game);
		s.examinePosition(node);
		assertEquals(game.getNextToPlay().getDirection(), node.getPlayerTurn());
		assertEquals(Direction.SOUTH_DEPRECATED, node.getPlayerTurn());
	}

	public void testExaminePositionInitsChildren() {
		Node node = new Node(null);
		Deal game = new Deal(NoTrump.i());
		game.getPlayer(Direction.WEST_DEPRECATED).init(new String[] { "3", "10" });
		game.getPlayer(Direction.NORTH_DEPRECATED).init(new String[] { "2", "9" });
		game.getPlayer(Direction.SOUTH_DEPRECATED).init(new String[] { "A", "5" });
		game.getPlayer(Direction.EAST_DEPRECATED).init(new String[] { "K", "7" });
		game.setNextToPlay(Direction.SOUTH_DEPRECATED);
		DoubleDummySolver s = new DoubleDummySolver(game);
		s.examinePosition(node);
		assertEquals(2, node.children.size());
	}

	public void testExaminePositionPushesChildrenOnStack() {
		Node node = new Node(null);
		Deal game = new Deal(NoTrump.i());
		game.getPlayer(Direction.WEST_DEPRECATED).init(new String[] { "3", "10" });
		game.getPlayer(Direction.NORTH_DEPRECATED).init(new String[] { "2", "9" });
		game.getPlayer(Direction.SOUTH_DEPRECATED).init(new String[] { "A", "5" });
		game.getPlayer(Direction.EAST_DEPRECATED).init(new String[] { "K", "7" });
		game.setNextToPlay(Direction.SOUTH_DEPRECATED);
		DoubleDummySolver s = new DoubleDummySolver(game);
		s.examinePosition(node);
		assertTrue(s.getStack().contains(node.children.get(0)));
		assertTrue(s.getStack().contains(node.children.get(1)));
	}

	public void testDoNotExpandNodesBeyondTrickLimit() {
		Deal game = new Deal(NoTrump.i());
		game.getPlayer(Direction.WEST_DEPRECATED).init(new String[] { "3", "10", "4" });
		game.getPlayer(Direction.NORTH_DEPRECATED).init(new String[] { "2", "9", "6" });
		game.getPlayer(Direction.SOUTH_DEPRECATED).init(new String[] { "A", "5", "J" });
		game.getPlayer(Direction.EAST_DEPRECATED).init(new String[] { "K", "7", "Q" });
		game.setNextToPlay(Direction.SOUTH_DEPRECATED);
		DoubleDummySolver s = new DoubleDummySolver(game);
		s.setMaxTricks(1);
		Node node_0_0_0_0 = new Node(new Node(new Node(new Node(new Node(null)))));
		s.examinePosition(node_0_0_0_0);
		assertEquals(0, s.getStack().size());
	}

	public void testOnlyExpandOneCardInSequenceTwoCards() {
		Node node = new Node(null);
		Deal game = new Deal(NoTrump.i());
		// game.getPlayer(Direction.WEST).init(new String[] { "A", "5" });
		// game.getPlayer(Direction.NORTH).init(new String[] { "2", "9" });
		game.getPlayer(Direction.SOUTH_DEPRECATED).init(new String[] { "J", "10" });
		// game.getPlayer(Direction.EAST).init(new String[] { "K", "7" });
		game.setNextToPlay(Direction.SOUTH_DEPRECATED);
		DoubleDummySolver s = new DoubleDummySolver(game);
		s.examinePosition(node);
		assertEquals(2, s.getStack().size());
		assertTrue(s.getStack().contains(node.children.get(1)));
		assertTrue(node.children.get(0).isPruned());
		assertFalse(node.children.get(1).isPruned());
	}

	public void testOnlyExpandOneCardInSequenceThreeCards() {
		Node node = new Node(null);
		Deal game = new Deal(NoTrump.i());
		// game.getPlayer(Direction.WEST).init(new String[] { "A", "5", "Q" });
		// game.getPlayer(Direction.NORTH).init(new String[] { "2", "4", "6" });
		game.getPlayer(Direction.SOUTH_DEPRECATED).init(new String[] { "J", "9", "10" });
		// game.getPlayer(Direction.EAST).init(new String[] { "K", "7", "8" });
		game.setNextToPlay(Direction.SOUTH_DEPRECATED);
		DoubleDummySolver s = new DoubleDummySolver(game);
		s.examinePosition(node);
		assertEquals(3, s.getStack().size());
		assertTrue(s.getStack().contains(node.children.get(1)));
		assertTrue(node.children.get(1).isPruned());
		assertTrue(node.children.get(0).isPruned());
		assertFalse(node.children.get(2).isPruned());
		System.out.println(node.children.get(2).getCardPlayed());
	}

	public void testOnlyExpandFirstCardInSequenceThreeCardsOutOfOrder() {
		Node node = new Node(null);
		Deal game = new Deal(NoTrump.i());
		// game.getPlayer(Direction.WEST).init(new String[] { "A", "5", "Q" });
		// game.getPlayer(Direction.NORTH).init(new String[] { "2", "4", "6" });
		game.getPlayer(Direction.SOUTH_DEPRECATED).init(new String[] { "10", "9", "J" });
		// game.getPlayer(Direction.EAST).init(new String[] { "K", "7", "8" });
		game.setNextToPlay(Direction.SOUTH_DEPRECATED);
		DoubleDummySolver s = new DoubleDummySolver(game);
		s.examinePosition(node);
		assertEquals(3, s.getStack().size());
		assertTrue(s.getStack().contains(node.children.get(0)));
		assertTrue(node.children.get(0).isPruned());
		assertTrue(node.children.get(1).isPruned());
		assertFalse(node.children.get(2).isPruned());
	}

	public void testOnlyExpandFirstCardInSequenceThreeCardsOutOfOrder2() {
		Node node = new Node(null);
		Deal game = new Deal(NoTrump.i());
		// game.getPlayer(Direction.WEST).init(new String[] { "A", "5", "Q" });
		// game.getPlayer(Direction.NORTH).init(new String[] { "2", "4", "6" });
		game.getPlayer(Direction.SOUTH_DEPRECATED).init(new String[] { "9", "10", "J" });
		// game.getPlayer(Direction.EAST).init(new String[] { "K", "7", "8" });
		game.setNextToPlay(Direction.SOUTH_DEPRECATED);
		DoubleDummySolver s = new DoubleDummySolver(game);
		s.examinePosition(node);
		assertEquals(3, s.getStack().size());
		assertTrue(s.getStack().contains(node.children.get(0)));
		assertTrue(node.children.get(0).isPruned());
		assertTrue(node.children.get(1).isPruned());
		assertFalse(node.children.get(2).isPruned());
	}

	public void testDoNotCountCurrentTrickAsPlayedCardWhenPruningPlayedSequence() {
		Node node = new Node(null);
		Deal game = new Deal(NoTrump.i());
		game.getPlayer(Direction.NORTH_DEPRECATED).init(new Hand("10,6", "", "", ""));
		game.getPlayer(Direction.EAST_DEPRECATED).init(new Hand("9,J", "", "", ""));
		game.setNextToPlay(Direction.NORTH_DEPRECATED);
		game.play(Ten.of(Spades.i()));
		DoubleDummySolver s = new DoubleDummySolver(game);
		s.examinePosition(node);
		assertEquals(2, s.getStack().size());
		assertTrue(s.getStack().contains(node.children.get(0)));
		assertTrue(s.getStack().contains(node.children.get(1)));
		assertFalse(node.children.get(1).isPruned());
		assertFalse(node.children.get(0).isPruned());

	}

	// this pruning disabled for now 
	public void _testOnlyExpandFirstCardInSequenceCardPlayedBetweenTwoUnplayedCards() {
		Node node = new Node(null);
		Deal game = new Deal(NoTrump.i());
		game.getPlayer(Direction.WEST_DEPRECATED).init(new Hand("", "", "4,3,2", ""));
		game.getPlayer(Direction.NORTH_DEPRECATED).init(new Hand("K,10,3", "", "", ""));
		game.getPlayer(Direction.EAST_DEPRECATED).init(new Hand("Q,A,2", "", "", ""));
		game.getPlayer(Direction.SOUTH_DEPRECATED).init(new Hand("", "4,3,2", "", ""));
		game.setNextToPlay(Direction.NORTH_DEPRECATED);
		game.play(King.of(Spades.i()));
		game.play(Two.of(Spades.i()));
		game.play(Two.of(Hearts.i()));
		game.play(Two.of(Diamonds.i())); //north takes trick
		game.play(Ten.of(Spades.i()));
		DoubleDummySolver s = new DoubleDummySolver(game);
		s.examinePosition(node);
		assertEquals(2, s.getStack().size());
		assertTrue(s.getStack().contains(node.children.get(0)));
		assertEquals(Ace.of(Spades.i()), node.children.get(1).getCardPlayed());
		assertTrue(node.children.get(1).isPruned());
		assertTrue(node.children.get(1).isPlayedSequencePruned());
		assertEquals(Queen.of(Spades.i()), node.children.get(0).getCardPlayed());
		assertFalse(node.children.get(0).isPruned());
	}

	// this pruning disabled for now
	public void _testOnlyExpandFirstCardInSequenceCardTwoPlayedBetweenTwoUnplayedCards() {
		Node node = new Node(null);
		Deal game = new Deal(NoTrump.i());
		game.getPlayer(Direction.WEST_DEPRECATED).init(new Hand("", "", "4,3,2", ""));
		game.getPlayer(Direction.NORTH_DEPRECATED).init(new Hand("K,10,3", "", "", ""));
		game.getPlayer(Direction.EAST_DEPRECATED).init(new Hand("A,J,2", "", "", ""));
		game.getPlayer(Direction.SOUTH_DEPRECATED).init(new Hand("Q", "3,2", "", ""));
		game.setNextToPlay(Direction.NORTH_DEPRECATED);
		game.play(King.of(Spades.i()));
		game.play(Two.of(Spades.i()));
		game.play(Queen.of(Spades.i()));
		game.play(Two.of(Diamonds.i())); //north takes trick
		game.play(Ten.of(Spades.i()));
		DoubleDummySolver s = new DoubleDummySolver(game);

		s.examinePosition(node);
		assertEquals(2, s.getStack().size());
		assertTrue(s.getStack().contains(node.children.get(0)));
		assertEquals(Jack.of(Spades.i()), node.children.get(1).getCardPlayed());
		assertFalse(node.children.get(1).isPlayedSequencePruned());
		assertFalse(node.children.get(1).isPruned());
		assertEquals(Ace.of(Spades.i()), node.children.get(0).getCardPlayed());
		assertTrue(node.children.get(0).isPruned());
		assertTrue(node.children.get(0).isPlayedSequencePruned());
	}

	public void testWhenPruningPlayedSequenceDoNotConsiderCardsInCurrentTrickAsPlayed() {
		Deal game = new Deal(Spades.i());
		game.getWest().init(new Hand("", "A,Q", "", ""));
		game.getNorth().init(new Hand("", "K,J", "", ""));
		game.getEast().init(new Hand("", "3,2", "", ""));
		game.getSouth().init(new Hand("2", "4", "", ""));
		game.setNextToPlay(Direction.WEST_DEPRECATED);
		SolverConfigurator configurator = new SolverConfigurator();
		configurator.setUseAlphaBetaPruning(false);
		DoubleDummySolver search = new DoubleDummySolver(game, configurator);
		search.setUseDuplicateRemoval(false);
		search.search();
		assertEquals(1, search.getRoot().getTricksTaken(Player.WEST_EAST));
		assertEquals(Ace.of(Hearts.i()), search.getRoot().getBestMove().getCardPlayed());
	}

	public void testTricksTallyIsTrickLimit() {
		Deal game = new Deal(NoTrump.i());
		game.getPlayer(Direction.WEST_DEPRECATED).init(new String[] { "3", "A", "4" });
		game.getPlayer(Direction.NORTH_DEPRECATED).init(new String[] { "2", "9", "6" });
		game.getPlayer(Direction.SOUTH_DEPRECATED).init(new String[] { "10", "5", "J" });
		game.getPlayer(Direction.EAST_DEPRECATED).init(new String[] { "K", "7", "Q" });
		game.setNextToPlay(Direction.SOUTH_DEPRECATED);
		DoubleDummySolver s = new DoubleDummySolver(game);
		s.setMaxTricks(1);
		s.search();
		assertEquals(1, s.getRoot().getTricksTaken(Player.WEST_EAST) + s.getRoot().getTricksTaken(Player.NORTH_SOUTH));
	}

	public void testExaminePositionInitsChildMove() {
		Node node = new Node(null);
		Deal game = new Deal(NoTrump.i());
		game.getPlayer(Direction.WEST_DEPRECATED).init(new String[] { "3", "10" });
		game.getPlayer(Direction.NORTH_DEPRECATED).init(new String[] { "2", "9" });
		game.getPlayer(Direction.SOUTH_DEPRECATED).init(new String[] { "A", "5" });
		game.getPlayer(Direction.EAST_DEPRECATED).init(new String[] { "K", "7" });
		game.setNextToPlay(Direction.SOUTH_DEPRECATED);
		DoubleDummySolver s = new DoubleDummySolver(game);
		s.examinePosition(node);
		Node child1 = s.getStack().pop();
		Node child2 = s.getStack().pop();
		s.examinePosition(child1);
		assertEquals(Direction.WEST_DEPRECATED, child1.getPlayerTurn());
		s.examinePosition(child2);
		assertEquals(Direction.WEST_DEPRECATED, child2.getPlayerTurn());
	}

	public void testExaminePositionExpandsChild() {
		Node node = new Node(null);
		Deal game = new Deal(NoTrump.i());
		game.getPlayer(Direction.WEST_DEPRECATED).init(new String[] { "3" }, new String[] { "10" });
		game.getPlayer(Direction.NORTH_DEPRECATED).init(new String[] { "2", "9" });
		game.getPlayer(Direction.SOUTH_DEPRECATED).init(new String[] { "A", "5" });
		game.getPlayer(Direction.EAST_DEPRECATED).init(new String[] { "K", "7" });
		game.setNextToPlay(Direction.SOUTH_DEPRECATED);
		DoubleDummySolver s = new DoubleDummySolver(game);
		s.examinePosition(node);
		Node child1 = s.getStack().pop();
		Node child2 = s.getStack().pop();
		s.examinePosition(child1);
		assertEquals(1, child1.children.size());
		s.examinePosition(child2);
		assertEquals(1, child2.children.size());
	}

	public void testTwoTricks() {
		Deal game = new Deal(NoTrump.i());
		game.getPlayer(Direction.WEST_DEPRECATED).init(new String[] { "2" }, new String[] { "3" });
		game.getPlayer(Direction.NORTH_DEPRECATED).init(new String[] { "3" }, new String[] { "2" });
		game.getPlayer(Direction.SOUTH_DEPRECATED).init(new String[] {}, new String[] { "K", "10" });
		game.getPlayer(Direction.EAST_DEPRECATED).init(new String[] { "A" }, new String[] {}, new String[] { "J" });
		game.setNextToPlay(Direction.NORTH_DEPRECATED);
		DoubleDummySolver s = new DoubleDummySolver(game);
		s.search();
		List<Card> bestMoves = s.getBestMoves();
		assertEquals(1, bestMoves.size());
		assertEquals(Two.of(Hearts.i()), bestMoves.get(0));
	}

	public void testTwoTricks2() {
		Deal game = new Deal(NoTrump.i());
		game.getPlayer(Direction.WEST_DEPRECATED).init(new String[] { "3" }, new String[] { "2" });
		game.getPlayer(Direction.NORTH_DEPRECATED).init(new String[] { "2" }, new String[] { "3" });
		game.getPlayer(Direction.SOUTH_DEPRECATED).init(new String[] { "K", "10" });
		game.getPlayer(Direction.EAST_DEPRECATED).init(new String[] {}, new String[] { "A" }, new String[] { "J" });
		game.setNextToPlay(Direction.NORTH_DEPRECATED);
		DoubleDummySolver s = new DoubleDummySolver(game);
		s.search();
		List<Card> bestMoves = s.getBestMoves();
		assertEquals(1, bestMoves.size());
		assertEquals(Two.of(Spades.i()), bestMoves.get(0));
	}

	public void testOneTrick() {
		Deal game = new Deal(NoTrump.i());
		game.getPlayer(Direction.WEST_DEPRECATED).init(new String[] { "3" });
		game.getPlayer(Direction.NORTH_DEPRECATED).init(new String[] { "2" });
		game.getPlayer(Direction.SOUTH_DEPRECATED).init(new String[] { "K" });
		game.getPlayer(Direction.EAST_DEPRECATED).init(new String[] { "J" });
		game.setNextToPlay(Direction.NORTH_DEPRECATED);
		DoubleDummySolver s = new DoubleDummySolver(game);
		s.search();
		assertEquals(Player.NORTH_SOUTH, s.getRoot().getCurrentPair());
		assertEquals(1, s.getRoot().getTricksTaken(Player.NORTH_SOUTH));

	}

	public void testAlphaBetaScenario1() {

		Deal game = new Deal(NoTrump.i());
		game.getPlayer(Direction.WEST_DEPRECATED).init(
				new Card[] { Nine.of(Clubs.i()), Four.of(Spades.i()), Six.of(Spades.i()), Nine.of(Spades.i()) });
		game.getPlayer(Direction.NORTH_DEPRECATED).init(
				new Card[] { Seven.of(Spades.i()), Ace.of(Spades.i()), Eight.of(Spades.i()), Five.of(Clubs.i()) });
		game.getPlayer(Direction.EAST_DEPRECATED).init(
				new Card[] { Ten.of(Hearts.i()), Three.of(Hearts.i()), Two.of(Spades.i()), Eight.of(Clubs.i()) });
		game.getPlayer(Direction.SOUTH_DEPRECATED).init(
				new Card[] { Six.of(Hearts.i()), Two.of(Hearts.i()), Queen.of(Spades.i()), King.of(Clubs.i()) });

		DoubleDummySolver pruned = new DoubleDummySolver(game.duplicate());
		assertTrue(pruned.getConfigurator().isUseAlphaBetaPruning());
		pruned.search();
		assertEquals(2, pruned.getRoot().getTricksTaken(Player.WEST_EAST));
	}

	public void testAlphaBetaScenario2() {

		Deal game = new Deal(NoTrump.i());
		game.getPlayer(Direction.WEST_DEPRECATED).init(new Card[] { Six.of(Spades.i()), Nine.of(Spades.i()) });
		game.getPlayer(Direction.NORTH_DEPRECATED).init(new Card[] { Ace.of(Spades.i()), Eight.of(Spades.i()) });
		game.getPlayer(Direction.EAST_DEPRECATED).init(new Card[] { Ten.of(Hearts.i()), Three.of(Hearts.i()) });
		game.getPlayer(Direction.SOUTH_DEPRECATED).init(new Card[] { Six.of(Hearts.i()), Two.of(Hearts.i()) });

		game.setNextToPlay(Direction.SOUTH_DEPRECATED);
		DoubleDummySolver pruned = new DoubleDummySolver(game.duplicate());
		assertTrue(pruned.getConfigurator().isUseAlphaBetaPruning());
		pruned.search();
		assertEquals(2, pruned.getRoot().getTricksTaken(Player.WEST_EAST));

	}

	public void testLastTrickAutoExpands() {
		Deal g = new Deal(NoTrump.i());
		GameUtils.initializeSingleColorSuits(g, 1);
		DoubleDummySolver s = new DoubleDummySolver(g);
		s.search();
		assertEquals(1, s.getPositionsExamined());
		assertTrue(s.getStack().empty());
	}

	public void testPrunedParentNoEvaluation() {
		Node root = new Node(null);
		root.pruneAsAlpha();
		Node child = new Node(root);
		Deal g = new Deal(NoTrump.i());
		GameUtils.initializeSingleColorSuits(g, 2);
		DoubleDummySolver s = new DoubleDummySolver(g);
		s.examinePosition(child);
		assertEquals(0, child.children.size());
	}

	public void testPrunedAncestorNoEvaluation() {
		Node root = new Node(null);

		root.pruneAsAlpha();
		Node child = new Node(root);
		Node grandChild = new Node(child);
		Deal g = new Deal(NoTrump.i());
		GameUtils.initializeSingleColorSuits(g, 2);
		DoubleDummySolver s = new DoubleDummySolver(g);
		s.examinePosition(grandChild);
		assertEquals(0, grandChild.children.size());
	}

	public void testTrimRoot() {
		int maxWestTricks = 3;
		Node root = new Node(null);
		root.setPlayerTurn(Direction.WEST_DEPRECATED);
		Node child1 = new Node(root);
		child1.setTricksTaken(Player.WEST_EAST, 1);
		child1.setTricksTaken(Player.NORTH_SOUTH, 2);
		Node child2 = new Node(root);
		child2.setTricksTaken(Player.WEST_EAST, maxWestTricks);
		child2.setTricksTaken(Player.NORTH_SOUTH, 1);
		DoubleDummySolver s = new DoubleDummySolver(root);
		s.trim(root);
		//		assertEquals("Poor move not trimmed", null, root.children.get(0));
		//		assertEquals("Good move trimmed", child2, root.children.get(1));
		assertEquals(child2, root.getBestMove());
		assertEquals(maxWestTricks, root.getTricksTaken(Player.WEST_EAST));
	}

	public void testLastChildCallsParentTrim() {
		Node root = new Node(null);
		root.setPlayerTurn(Direction.WEST_DEPRECATED);

		Node child1 = new Node(root);
		child1.setPlayerTurn(Direction.NORTH_DEPRECATED);
		child1.setTricksTaken(Player.WEST_EAST, 1);
		child1.setTricksTaken(Player.NORTH_SOUTH, 2);
		Node grandChild1 = new Node(child1);
		grandChild1.setPlayerTurn(Direction.EAST_DEPRECATED);
		grandChild1.setTricksTaken(Player.WEST_EAST, 1);
		grandChild1.setTricksTaken(Player.NORTH_SOUTH, 2);

		Node child2 = new Node(root);
		child2.setPlayerTurn(Direction.NORTH_DEPRECATED);

		Node grandChild2 = new Node(child2);
		grandChild2.setPlayerTurn(Direction.EAST_DEPRECATED);
		grandChild2.setTricksTaken(Player.WEST_EAST, 1);
		grandChild2.setTricksTaken(Player.NORTH_SOUTH, 2);
		DoubleDummySolver s = new DoubleDummySolver(root);
		s.trim(child2);
		assertFalse("Trimmed parent even though another child was not visited", root.trimmed());

		s.trim(child1);
		assertTrue(root.trimmed());
	}

	public void testNotLastChildNoCallToParentTrim() {
		MockNode root = new MockNode(null);
		root.setPlayerTurn(Direction.WEST_DEPRECATED);

		Node child1 = new Node(root);
		child1.setPlayerTurn(Direction.NORTH_DEPRECATED);

		Node grandChild1 = new Node(child1);
		grandChild1.setPlayerTurn(Direction.EAST_DEPRECATED);
		grandChild1.setTricksTaken(Player.WEST_EAST, 1);
		grandChild1.setTricksTaken(Player.NORTH_SOUTH, 2);

		Node child2 = new Node(root);
		child2.setPlayerTurn(Direction.NORTH_DEPRECATED);
		child2.setTricksTaken(Player.WEST_EAST, 1);
		child2.setTricksTaken(Player.NORTH_SOUTH, 2);

		Node grandChild2 = new Node(child2);
		grandChild2.setPlayerTurn(Direction.EAST_DEPRECATED);
		grandChild2.setTricksTaken(Player.WEST_EAST, 1);
		grandChild2.setTricksTaken(Player.NORTH_SOUTH, 2);

		DoubleDummySolver s = new DoubleDummySolver(root);
		s.trim(child2);
		assertFalse(root.trimmed());
	}

	public void testMinMaxTrimmingNorthSpoilsWestPlay() {
		Node root = new Node(null);
		root.setPlayerTurn(Direction.WEST_DEPRECATED);

		MockNode child1 = new MockNode(root);
		child1.setPlayerTurn(Direction.NORTH_DEPRECATED);
		child1.setTricksTaken(Player.WEST_EAST, 4);
		child1.setTricksTaken(Player.NORTH_SOUTH, 5);
		child1.trim();

		Node child2 = new Node(root);
		child2.setPlayerTurn(Direction.NORTH_DEPRECATED);

		Node grandChild1 = new Node(child2);
		grandChild1.setPlayerTurn(Direction.EAST_DEPRECATED);
		grandChild1.setTricksTaken(Player.WEST_EAST, 3);
		grandChild1.setTricksTaken(Player.NORTH_SOUTH, 6);

		Node grandChild2 = new Node(child2);
		grandChild2.setPlayerTurn(Direction.EAST_DEPRECATED);
		grandChild2.setTricksTaken(Player.WEST_EAST, 7);
		grandChild2.setTricksTaken(Player.NORTH_SOUTH, 2);

		DoubleDummySolver s = new DoubleDummySolver(root);
		s.trim(child2);
		assertEquals(child1.getTricksTaken(root.getCurrentPair()), root.getTricksTaken(root.getCurrentPair()));
		assertEquals(child1, root.getBestMove());
		//assertNull(root.children.get(1));
	}

	public void testMinMaxTrimmingNorthLesserEvil() {
		Node root = new Node(null);
		root.setPlayerTurn(Direction.WEST_DEPRECATED);

		MockNode child1 = new MockNode(root);
		child1.setPlayerTurn(Direction.NORTH_DEPRECATED);
		child1.setTricksTaken(Player.WEST_EAST, 4);
		child1.setTricksTaken(Player.NORTH_SOUTH, 5);
		child1.trim();

		Node child2 = new Node(root);
		child2.setPlayerTurn(Direction.NORTH_DEPRECATED);

		Node grandChild1 = new Node(child2);
		grandChild1.setPlayerTurn(Direction.EAST_DEPRECATED);
		grandChild1.setTricksTaken(Player.WEST_EAST, 5);
		grandChild1.setTricksTaken(Player.NORTH_SOUTH, 4);

		Node grandChild2 = new Node(child2);
		grandChild2.setPlayerTurn(Direction.EAST_DEPRECATED);
		grandChild2.setTricksTaken(Player.WEST_EAST, 7);
		grandChild2.setTricksTaken(Player.NORTH_SOUTH, 2);

		DoubleDummySolver s = new DoubleDummySolver(root);
		s.trim(child2);
		//assertNull(root.children.get(0));
		assertEquals(child2, root.getBestMove());
		assertEquals(grandChild1.getTricksTaken(root.getCurrentPair()), root.getTricksTaken(root.getCurrentPair()));

	}

	public void testTrimTerminatesOnUnexpandedNonLeafNode() {

		MockNode root = new MockNode(null);
		root.setPlayerTurn(Direction.WEST_DEPRECATED);

		Node child1 = new Node(root);
		child1.setPlayerTurn(Direction.NORTH_DEPRECATED);
		child1.setLeaf(false);

		Node child2 = new Node(root);
		child2.setPlayerTurn(Direction.NORTH_DEPRECATED);
		child2.setTricksTaken(Player.WEST_EAST, 1);
		child2.setTricksTaken(Player.NORTH_SOUTH, 2);

		Node grandChild2 = new Node(child2);
		grandChild2.setPlayerTurn(Direction.EAST_DEPRECATED);
		grandChild2.setTricksTaken(Player.WEST_EAST, 1);
		grandChild2.setTricksTaken(Player.NORTH_SOUTH, 2);
		DoubleDummySolver s = new DoubleDummySolver(root);
		s.trim(child2);
		assertFalse(root.trimmed());

	}

	public void testChildrenOfPrunedNodeInheritStatus() {
		Node root = new Node(null);
		root.setPlayerTurn(Direction.WEST_DEPRECATED);
		root.pruneAsAlpha();
		@SuppressWarnings("unused")
		Node child1 = new Node(root);
		@SuppressWarnings("unused")
		Node child2 = new Node(root);
		DoubleDummySolver s = new DoubleDummySolver(root);
		s.trim(root);
		assertTrue(root.children.get(0).isAlphaPruned());
		assertTrue(root.children.get(1).isAlphaPruned());
	}

	public void testBestMoveWhenRootDoesNotStartTrick() {
		Deal game = new Deal(NoTrump.i());
		game.getPlayer(Direction.WEST_DEPRECATED).init(new Card[] { Nine.of(Clubs.i()), Four.of(Spades.i()) });
		game.getPlayer(Direction.NORTH_DEPRECATED).init(new Card[] { Seven.of(Spades.i()), Queen.of(Hearts.i()) });
		game.getPlayer(Direction.EAST_DEPRECATED).init(new Card[] { Three.of(Clubs.i()), Three.of(Hearts.i()) });
		game.getPlayer(Direction.SOUTH_DEPRECATED).init(new Card[] { Four.of(Clubs.i()), Two.of(Spades.i()) });
		game.doNextCard(0);
		DoubleDummySolver s = new DoubleDummySolver(game);
		s.search();
		assertEquals(1, s.getBestMoves().size());
		assertEquals(Queen.of(Hearts.i()), s.getBestMoves().get(0));

		// triangulate
		Deal game2 = new Deal(NoTrump.i());
		game2.getPlayer(Direction.WEST_DEPRECATED).init(new Card[] { Nine.of(Clubs.i()), Four.of(Spades.i()) });
		game2.getPlayer(Direction.NORTH_DEPRECATED).init(new Card[] { Queen.of(Hearts.i()), Seven.of(Spades.i()) }); // invert
		// order
		game2.getPlayer(Direction.EAST_DEPRECATED).init(new Card[] { Three.of(Clubs.i()), Three.of(Hearts.i()) });
		game2.getPlayer(Direction.SOUTH_DEPRECATED).init(new Card[] { Four.of(Clubs.i()), Two.of(Spades.i()) });
		game2.doNextCard(0);
		DoubleDummySolver s2 = new DoubleDummySolver(game2);
		s2.search();
		assertEquals(1, s2.getBestMoves().size());
		assertEquals(Queen.of(Hearts.i()), s2.getBestMoves().get(0));
	}

	public void testNorthTrumps() {
		Deal game = new Deal(Spades.i());
		game.getPlayer(Direction.WEST_DEPRECATED).init(new Card[] { Nine.of(Clubs.i()), Four.of(Spades.i()) });
		game.getPlayer(Direction.NORTH_DEPRECATED).init(new Card[] { Two.of(Spades.i()), Two.of(Hearts.i()) });
		game.getPlayer(Direction.EAST_DEPRECATED).init(new Card[] { Three.of(Clubs.i()), Three.of(Diamonds.i()) });
		game.getPlayer(Direction.SOUTH_DEPRECATED).init(new Card[] { Six.of(Clubs.i()), Five.of(Diamonds.i()) });
		game.play(Nine.of(Clubs.i()));
		DoubleDummySolver s = new DoubleDummySolver(game);
		s.search();
		s.printStats();
		s.printOptimalPath();
		assertEquals(1, s.getBestMoves().size());
		assertEquals(Two.of(Spades.i()), s.getBestMoves().get(0));

		Deal game2 = new Deal(Spades.i());
		game2.getPlayer(Direction.WEST_DEPRECATED).init(new Card[] { Nine.of(Clubs.i()), Four.of(Spades.i()) });
		game2.getPlayer(Direction.NORTH_DEPRECATED).init(new Card[] { Two.of(Hearts.i()), Two.of(Spades.i()) }); // order
		// reverted
		game2.getPlayer(Direction.EAST_DEPRECATED).init(new Card[] { Three.of(Clubs.i()), Three.of(Diamonds.i()) });
		game2.getPlayer(Direction.SOUTH_DEPRECATED).init(new Card[] { Six.of(Clubs.i()), Five.of(Diamonds.i()) });
		game2.doNextCard(0);
		DoubleDummySolver s2 = new DoubleDummySolver(game2);
		s2.search();
		assertEquals(1, s2.getBestMoves().size());
		assertEquals(Two.of(Spades.i()), s2.getBestMoves().get(0));

	}

	public void testNorthCannotTrumpBecauseHasColor() {
		Deal game = new Deal(Spades.i());
		game.getPlayer(Direction.WEST_DEPRECATED).init(new Card[] { Nine.of(Clubs.i()), Four.of(Spades.i()) });
		game.getPlayer(Direction.NORTH_DEPRECATED).init(new Card[] { Two.of(Spades.i()), Two.of(Clubs.i()) });
		game.getPlayer(Direction.EAST_DEPRECATED).init(new Card[] { Three.of(Diamonds.i()), Three.of(Hearts.i()) });
		game.getPlayer(Direction.SOUTH_DEPRECATED).init(new Card[] { Six.of(Diamonds.i()), Five.of(Hearts.i()) });
		game.doNextCard(0);
		DoubleDummySolver s = new DoubleDummySolver(game);
		s.search();
		assertEquals(1, s.getBestMoves().size());
		assertEquals(Two.of(Clubs.i()), s.getBestMoves().get(0));
	}

	public void testBestMoveForOneTrick() {
		Deal game = new Deal(NoTrump.i());
		game.getPlayer(Direction.WEST_DEPRECATED).init(new String[] { "3" });
		game.getPlayer(Direction.NORTH_DEPRECATED).init(new String[] { "2" });
		game.getPlayer(Direction.SOUTH_DEPRECATED).init(new String[] { "A" });
		game.getPlayer(Direction.EAST_DEPRECATED).init(new String[] { "K" });
		game.setNextToPlay(Direction.SOUTH_DEPRECATED);
		DoubleDummySolver s = new DoubleDummySolver(game);
		s.search();
		assertNotNull(s.getBestMoves());
		assertEquals(1, s.getBestMoves().size());
	}

	public void testBestMoveForOneTrickRootDidNotStartTrick() {
		Deal game = new Deal(NoTrump.i());
		game.getPlayer(Direction.WEST_DEPRECATED).init(new String[] { "3" });
		game.getPlayer(Direction.NORTH_DEPRECATED).init(new String[] { "2" });
		game.getPlayer(Direction.SOUTH_DEPRECATED).init(new String[] { "A" });
		game.getPlayer(Direction.EAST_DEPRECATED).init(new String[] { "K" });
		game.setNextToPlay(Direction.SOUTH_DEPRECATED);
		game.doNextCard(0);
		DoubleDummySolver s = new DoubleDummySolver(game);
		s.search();
		assertNotNull(s.getBestMoves());
		assertEquals(1, s.getBestMoves().size());
	}

	public void testIfCannotBeatPlayLowestToColorPruningNoConflictWithSequencePruning() {

		Deal game = new Deal(NoTrump.i());
		game.getPlayer(Direction.WEST_DEPRECATED).init(new Card[] { Ace.of(Spades.i()), Nine.of(Spades.i()) });
		game.getPlayer(Direction.NORTH_DEPRECATED).init(new Card[] { Five.of(Spades.i()), Four.of(Spades.i()) });
		game.getPlayer(Direction.EAST_DEPRECATED).init(new Card[] { Ten.of(Hearts.i()), Three.of(Hearts.i()) });
		game.getPlayer(Direction.SOUTH_DEPRECATED).init(new Card[] { Six.of(Hearts.i()), Two.of(Hearts.i()) });

		game.setNextToPlay(Direction.WEST_DEPRECATED);
		game.play(Ace.of(Spades.i()));
		DoubleDummySolver pruned = new DoubleDummySolver(game.duplicate());
		Node root = new Node(null);
		pruned.examinePosition(root);
		assertNotNull(root.getBestMove());

	}

	public void testShortCircuitIfRootOnlyHasOneValidMove() {

		Deal game = new Deal(NoTrump.i());
		game.getPlayer(Direction.WEST_DEPRECATED).init(new Card[] { Ace.of(Spades.i()), Nine.of(Spades.i()) });
		game.getPlayer(Direction.NORTH_DEPRECATED).init(new Card[] { Six.of(Spades.i()), Four.of(Hearts.i()) });
		game.getPlayer(Direction.EAST_DEPRECATED).init(new Card[] { Ten.of(Hearts.i()), Three.of(Hearts.i()) });
		game.getPlayer(Direction.SOUTH_DEPRECATED).init(new Card[] { Six.of(Hearts.i()), Two.of(Hearts.i()) });

		game.setNextToPlay(Direction.WEST_DEPRECATED);
		game.play(Ace.of(Spades.i()));
		DoubleDummySolver pruned = new DoubleDummySolver(game.duplicate());
		pruned.setTerminateIfRootOnlyHasOneValidMove(true);
		pruned.search();
		assertEquals(1, pruned.getPositionsExamined());

	}

	public void testIfAllMovesTheSameChooseLowestValueCard() {

		Deal game = new Deal(NoTrump.i());
		game.getPlayer(Direction.WEST_DEPRECATED).init(new Card[] { Ace.of(Spades.i()), Queen.of(Spades.i()) });
		game.getPlayer(Direction.NORTH_DEPRECATED).init(new Card[] { Six.of(Spades.i()), Four.of(Spades.i()) });
		game.getPlayer(Direction.EAST_DEPRECATED).init(new Card[] { Ten.of(Hearts.i()), Three.of(Hearts.i()) });
		game.getPlayer(Direction.SOUTH_DEPRECATED).init(new Card[] { Six.of(Hearts.i()), Two.of(Hearts.i()) });

		game.setNextToPlay(Direction.WEST_DEPRECATED);
		DoubleDummySolver pruned = new DoubleDummySolver(game.duplicate());
		//	pruned.useAlphaBetaPruning(false);
		pruned.search();
		assertEquals(Queen.of(Spades.i()), pruned.getRoot().getBestMove().getCardPlayed());

		Deal gameWithCardsFlipped = new Deal(NoTrump.i());
		gameWithCardsFlipped.getPlayer(Direction.WEST_DEPRECATED).init(
				new Card[] { Queen.of(Spades.i()), Ace.of(Spades.i()) });
		gameWithCardsFlipped.getPlayer(Direction.NORTH_DEPRECATED).init(
				new Card[] { Six.of(Spades.i()), Four.of(Spades.i()) });
		gameWithCardsFlipped.getPlayer(Direction.EAST_DEPRECATED).init(
				new Card[] { Ten.of(Hearts.i()), Three.of(Hearts.i()) });
		gameWithCardsFlipped.getPlayer(Direction.SOUTH_DEPRECATED).init(
				new Card[] { Six.of(Hearts.i()), Two.of(Hearts.i()) });

		gameWithCardsFlipped.setNextToPlay(Direction.WEST_DEPRECATED);
		DoubleDummySolver triangulate = new DoubleDummySolver(gameWithCardsFlipped.duplicate());

		//	triangulate.useAlphaBetaPruning(false);
		triangulate.search();
		assertEquals(Queen.of(Spades.i()), triangulate.getRoot().getBestMove().getCardPlayed());

	}

	public void testIfAllMovesLoseSameChooseLowestValueCard() {

		Deal game = new Deal(NoTrump.i());
		game.getPlayer(Direction.WEST_DEPRECATED).init(new Card[] { Ace.of(Spades.i()), King.of(Spades.i()) });
		game.getPlayer(Direction.NORTH_DEPRECATED).init(new Card[] { Six.of(Diamonds.i()), Four.of(Hearts.i()) });
		game.getPlayer(Direction.EAST_DEPRECATED).init(new Card[] { Ten.of(Hearts.i()), Three.of(Hearts.i()) });
		game.getPlayer(Direction.SOUTH_DEPRECATED).init(new Card[] { Six.of(Hearts.i()), Two.of(Hearts.i()) });

		game.setNextToPlay(Direction.WEST_DEPRECATED);
		game.play(Ace.of(Spades.i()));
		DoubleDummySolver pruned = new DoubleDummySolver(game.duplicate());
		pruned.search();
		assertEquals(Four.of(Hearts.i()), pruned.getRoot().getBestMove().getCardPlayed());
		Deal gameWithCardsFlipped = new Deal(NoTrump.i());
		gameWithCardsFlipped.getPlayer(Direction.WEST_DEPRECATED).init(
				new Card[] { Ace.of(Spades.i()), King.of(Spades.i()) });
		gameWithCardsFlipped.getPlayer(Direction.NORTH_DEPRECATED).init(
				new Card[] { Four.of(Hearts.i()), Six.of(Diamonds.i()) });
		gameWithCardsFlipped.getPlayer(Direction.EAST_DEPRECATED).init(
				new Card[] { Ten.of(Hearts.i()), Three.of(Hearts.i()) });
		gameWithCardsFlipped.getPlayer(Direction.SOUTH_DEPRECATED).init(
				new Card[] { Six.of(Hearts.i()), Two.of(Hearts.i()) });

		gameWithCardsFlipped.setNextToPlay(Direction.WEST_DEPRECATED);
		gameWithCardsFlipped.play(Ace.of(Spades.i()));
		DoubleDummySolver triangulate = new DoubleDummySolver(gameWithCardsFlipped.duplicate());
		//triangulate.pruneAlphaBeta = false;
		triangulate.search();
		assertEquals(Four.of(Hearts.i()), triangulate.getRoot().getBestMove().getCardPlayed());

	}

}
