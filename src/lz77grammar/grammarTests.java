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
		CFG cfg = new CFG("grammarTest.txt");
		CnfGrammar cnfGrammar = cfg.toCnfGrammar();
		
		
		Terminal G = new Terminal("G", 'g');
		Terminal F = new Terminal ("F", 'f');
		Terminal E = new Terminal("E", 'e');
		Branch D = new Branch("D",F,G);
		Terminal C = new Terminal("C", 'c');
		Branch B = new Branch("B", D,E);
		Branch S = new Branch("S",B,C);
		CnfGrammar expectedCnfGrammar = new CnfGrammar(S);
		

		assertEquals(expectedCnfGrammar, cnfGrammar);
	}

	/**
	 * Parses "grammarTest.txt" containing no cycles.
	 */
	@Test
	void slpTest1() {
		CFG cfg = new CFG("grammarTest.txt");
		assertFalse(cfg.isCyclic());
	}
	
	/**
	 * Parses "grammarTest.txt" in Chomsky Normal Form.
	 */
	@Test
	void cnfTest1() {
		CFG cfg = new CFG("grammarTest.txt");
		assertTrue(cfg.isCNF());
	}
	
	/**
	 * Parses "grammarTest2.txt" containing cycles.
	 */
	@Test
	void slpTest2() {
		CFG cfg = new CFG("grammarTest2.txt");
		assertTrue(cfg.isCyclic());
	}
	
	/**
	 * Parses "grammarTest2.txt" not in Chomsky Normal Form.
	 */
	@Test
	void cnfTest2() {
		CFG cfg = new CFG("grammarTest2.txt");
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
		CnfGrammar cnfGrammar = new CnfGrammar(branch1);
		assertTrue(cnfGrammar.isBalanced());
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
		
		CnfGrammar cnfGrammar = new CnfGrammar(branch3);
		assertFalse(cnfGrammar.isBalanced());
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
		
		CnfGrammar cnfGrammar = new CnfGrammar(branch3);
		cnfGrammar.balanceGrammar(0);
		assertTrue(cnfGrammar.isBalanced());
	}

}
