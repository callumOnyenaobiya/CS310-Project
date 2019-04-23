package lz77grammar;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Tests for all grammar related operations.
 * @author Callum Onyenaobiya
 * 
 */
class grammarTests {

	/**
	 * Parses the file "grammarTest.txt".
	 */
	@Test
	void parseGrammarTest() {
		GrammarParser cfg = new GrammarParser("grammarTest.txt");
		Grammar grammar = cfg.toCnfGrammar();
		
		
		Terminal G = new Terminal("G", 'g');
		Terminal F = new Terminal ("F", 'f');
		Terminal E = new Terminal("E", 'e');
		Branch D = new Branch("D",F,G);
		Terminal C = new Terminal("C", 'c');
		Branch B = new Branch("B", D,E);
		Branch S = new Branch("S",B,C);
		Grammar expectedCnfGrammar = new Grammar(S);
		

		assertEquals(expectedCnfGrammar, grammar);
	}

	/**
	 * Parses "grammarTest.txt" containing no cycles.
	 */
	@Test
	void slpTest1() {
		GrammarParser cfg = new GrammarParser("grammarTest.txt");
		assertFalse(cfg.isCyclic());
	}
	
	/**
	 * Parses "grammarTest.txt" in Chomsky Normal Form.
	 */
	@Test
	void cnfTest1() {
		GrammarParser cfg = new GrammarParser("grammarTest.txt");
		assertTrue(cfg.isCNF());
	}
	
	/**
	 * Parses "grammarTest2.txt" containing cycles.
	 */
	@Test
	void slpTest2() {
		GrammarParser cfg = new GrammarParser("grammarTest2.txt");
		assertTrue(cfg.isCyclic());
	}
	
	/**
	 * Parses "grammarTest2.txt" not in Chomsky Normal Form.
	 */
	@Test
	void cnfTest2() {
		GrammarParser cfg = new GrammarParser("grammarTest2.txt");
		assertFalse(cfg.isCNF());
	}
	
	
	
	/**
	 * Manually builds a balanced grammar, which our function should identify. 
	 */
	@Test
	void balanceTest1() {
		Terminal terminal1 = new Terminal("terminal1", 'a');
		Terminal terminal2 = new Terminal("terminal2", 'b');
		Branch branch1 = new Branch("branch1", terminal1, terminal2);
		Grammar grammar = new Grammar(branch1);
		assertTrue(grammar.isBalanced());
	}
	
	/**
	 * Manually builds an unbalanced grammar, which our function should identify.
	 */
	@Test
	void balanceTest2() {
		Terminal terminal1 = new Terminal("terminal1", 'a');
		Terminal terminal2 = new Terminal("terminal2", 'b');

		Branch branch1 = new Branch("branch1", terminal1, terminal2);
		Branch branch2 = new Branch("branch2", branch1, terminal2);
		Branch branch3 = new Branch("branch3", branch2, terminal2);
		
		Grammar grammar = new Grammar(branch3);
		assertFalse(grammar.isBalanced());
	}
	
	/**
	 * Takes an unbalanced grammar and correctly balances it.
	 */
	@Test
	void rebalanceTest() {
		Terminal terminal1 = new Terminal("terminal1", 'a');
		Terminal terminal2 = new Terminal("terminal2", 'b');

		Branch branch1 = new Branch("branch1", terminal1, terminal2);
		Branch branch2 = new Branch("branch2", branch1, terminal2);
		Branch branch3 = new Branch("branch3", branch2, terminal2);
		
		Grammar grammar = new Grammar(branch3);
		grammar.balanceGrammar(0);
		assertTrue(grammar.isBalanced());
	}

}
