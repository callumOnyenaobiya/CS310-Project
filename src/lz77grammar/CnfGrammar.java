package lz77grammar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class CnfGrammar {

	Node startNode;
	List<String> gFactors;
	TreePrinter treeprinter;

	public CnfGrammar(Node startNode) {
		this.startNode = startNode;
		gFactors = new LinkedList<String>();
		gFactors(startNode, new HashSet<String>());
		this.treeprinter = new TreePrinter();
	}

	public void loadGfactors() {
		gFactors = new LinkedList<String>();
		gFactors(startNode, new HashSet<String>());
	}

	public Set<String> gFactors(Node root, Set<String> map) {
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

	public void balanceGrammar(int tutorialMode) {
		loadGfactors();
		Converter converter = new Converter(tutorialMode);
		startNode = converter.constructGrammar(gFactors.toArray(new String[0])).startNode;
	}
	
	public Set<String> getProductions() {
		return startNode.getProductions(new HashSet<String>());
	}

	public void printTree() {
		treeprinter.print(startNode);
	}
}
