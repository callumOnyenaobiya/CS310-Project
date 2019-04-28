package lz77grammar;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Object representing a grammar in CNF.
 * @author Callum Onyenaobiya
 * 
 */
class Grammar {

	private Node startNode;
	private List<String> gFactors;
	private TreePrinter treeprinter;

	/**
	 * Build new CnfGrammar from starting node.
	 * @param startNode
	 */
	Grammar(Node startNode) {
		this.startNode = startNode;
		gFactors = new LinkedList<String>();
		gFactors(startNode, new HashSet<String>());
		this.treeprinter = new TreePrinter();
	}

	public Node getStartNode() {
		return startNode;
	}
	
	/**
	 * @return String evaluated from the start node.
	 */
	String evaluate() {
		return this.startNode.evaluate();
	}

	public void setStartNode(Node startNode) {
		this.startNode = startNode;
	}
	
	/**
	 * @return return True if tree rooted at start node is balanced.
	 */
	Boolean isBalanced() {
		return this.startNode.getBalance() == 0 || this.startNode.getBalance() == 1;
	}
	
	/**
	 * 
	 * @return The balance of the subtree rooted at start node.
	 */
	int getBalance() {
		return this.startNode.getBalance();
	}

	List<String> getgFactors() {
		return gFactors;
	}

	/**
	 * Recursively builds gFactors from current node, given a set of current NonTerminals.
	 */
	void loadGfactors() {
		gFactors = new LinkedList<String>();
		gFactors(startNode, new HashSet<String>());
	}

	/**
	 * Builds objects gFactors list by running a pre order traversal on the grammar.
	 * @param root current Node
	 * @param map set of NonTerminal evaluations already processed before this node.
	 * @return set of NonTerminal evaluations already processed, including this node and its children.
	 */
	private Set<String> gFactors(Node root, Set<String> map) {
		if (root instanceof Terminal) {
			gFactors.add(root.evaluate());
			map.add(root.evaluate());
			return map;
		}
		if (map.contains(root.evaluate())) {
			gFactors.add(root.evaluate());
			map.add(root.evaluate());
			return map;
		} else {
			map.add(root.evaluate());
		}
		map = gFactors(root.getLeft(), map);
		map = gFactors(root.getRight(), map);
		return map;
	}

	/**
	 * Balances grammar using gFactors and LZ77-SLP construction methods.
	 */
	void balanceGrammar(int tutorialMode) {
		loadGfactors();
		Converter converter = new Converter();
		startNode = converter.constructGrammar(gFactors.toArray(new String[0])).startNode;
	}

	/**
	 * @return builds a set of productions recursively from start node, loading all unique productions using a set.
	 */
	public Set<String> getProductions() {
		return startNode.getProductions(new HashSet<String>());
	}

	/**
	 * View CnfGrammar as a parse tree, rooted from start node.
	 */
	void printTree() {
		treeprinter.print(startNode);
	}
	
	/**
	 * 
	 * @param i The desired position.
	 * @return The character at position i.
	 */
	char get(int i) {
		return startNode.get(i);
	}
	
	/**
	 * Calculates the size of the grammar rooted at the start node.
	 * @return The size of the grammar rooted at startNode.
	 */
	int size() {
		return startNode.size();
	}

	/**
	 * Overrided implementation of equals. Equality based on Equality of starting nodes.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Grammar) {
			Grammar grammar = (Grammar) obj;
			return (startNode.equals(grammar.getStartNode()));
		}
		return false;
	}

	
	
}
