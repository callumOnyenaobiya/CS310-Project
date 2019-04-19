package lz77grammar;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

class CnfGrammar {

	private Node startNode;
	private List<String> gFactors;
	private TreePrinter treeprinter;

	CnfGrammar(Node startNode) {
		this.startNode = startNode;
		gFactors = new LinkedList<String>();
		gFactors(startNode, new HashSet<String>());
		this.treeprinter = new TreePrinter();
	}

	public Node getStartNode() {
		return startNode;
	}
	
	String evaluate() {
		return this.startNode.evaluate();
	}

	public void setStartNode(Node startNode) {
		this.startNode = startNode;
	}
	
	Boolean isBalanced() {
		return this.startNode.getBalance() == 0 || this.startNode.getBalance() == 1;
	}

	List<String> getgFactors() {
		return gFactors;
	}

	void loadGfactors() {
		gFactors = new LinkedList<String>();
		gFactors(startNode, new HashSet<String>());
	}

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

	void balanceGrammar(int tutorialMode) {
		loadGfactors();
		Converter converter = new Converter(tutorialMode);
		startNode = converter.constructGrammar(gFactors.toArray(new String[0])).startNode;
	}

	public Set<String> getProductions() {
		return startNode.getProductions(new HashSet<String>());
	}

	void printTree() {
		treeprinter.print(startNode);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CnfGrammar) {
			CnfGrammar cnfGrammar = (CnfGrammar) obj;
			return (startNode.equals(cnfGrammar.getStartNode()));
		}
		return false;
	}

	
	
}
