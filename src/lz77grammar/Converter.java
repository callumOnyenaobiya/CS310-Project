package lz77grammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Converter methods used to convert LZ77 compressed tuples into a balanced SLP.
 * @author Callum Onyenaobiya
 * 
 */
class Converter {
	private TreePrinter treeprinter;
	private Map<String, Node> nodes;
	private int index;
	
	/**
	 * Initialised alphabet with all ascii characters.
	 */
	Converter() {
		this.index = 0;
		nodes = new HashMap<String, Node>();
		for (int i = 0; i <= 255; i++) {
			nodes.put(String.valueOf((char) i), new Terminal("X" + index, (char) i));
			index++;
		}
		treeprinter = new TreePrinter();
	}
	
	public Map<String, Node> getNodes() {
		return nodes;
	}
	
	/**
	 * Adds a new node to our map.
	 * @param string
	 * @param node
	 */
	private void addNode(String string, Node node) {
		nodes.put(string, node);
	}

	/**
	 * Constructs a balanced SLP from a set of LZ77-factors or G-factors.
	 * @param factors LZ77-factors or G-factors.
	 * @return a balanced SLP.
	 */
	Grammar constructGrammar(String[] factors) {
		Node node = create(factors[0]);
		for (int i = 1; i < factors.length; i++) {
			node = concatenate(node, create(factors[i]));
		}
		Grammar grammar = new Grammar(node);
		grammar.getStartNode().setName("S");
		return grammar;
	}
	
	/**
	 * Concatenate two nodes
	 * @param a
	 * @param b
	 * @return a balanced SLP, ab.
	 */
	private Node concatenate(Node a, Node b) {
		Node newNode = null;
		if(Math.abs(a.getHeight() - b.getHeight()) <= 1) {
			newNode = new Branch("X" + index++, a, b);
		}
		else if(a.getHeight() > b.getHeight()) {
			newNode =  rightConcatenate(a.clone(), b.clone());
		}
		else {
			newNode =  leftConcatenate(a.clone(), b.clone());
		}
		if(nodes.containsKey(newNode.evaluate())) {
			return nodes.get(newNode.evaluate());
		}
		addNode(newNode.evaluate(), newNode);
		return newNode;
	}
	
	/**
	 * Concatenating b to the left most branch of a.
	 * @param a
	 * @param b
	 * @return 
	 */
	private Node leftConcatenate(Node a, Node b) {
		Node current = b;
		Node newNode = null;
		while(current.getLeft().getHeight() - a.getHeight() > 1) {
			current = current.getLeft();
		}
		newNode = new Branch("X" + index++, a, current.getLeft());
		if(!nodes.containsKey(newNode.evaluate())) {
			addNode(newNode.evaluate(), newNode);
		} else {
			newNode = nodes.get(newNode.evaluate());
		}
		current.setLeft(newNode);
		Node change = b;
		Stack<Node> stack = new Stack<Node>();
		while(change.getName() != current.getName()) {
			change.setName("X" + index++);
			addNode(change.evaluate(), change);
			stack.push(change);
			change = change.getLeft();
		}
		Node next;
		while(!stack.isEmpty()) {
			next = stack.pop();
			next = leftBalance(next);
			addNode(next.evaluate(), next);
		}
		current.setName("X" + index++);
		addNode(current.evaluate(), current);
		return b;
	}
	
	/**
	 * Concatenating a to the right most branch of b.
	 * @param a
	 * @param b
	 * @return
	 */
	private Node rightConcatenate(Node a, Node b) {
		Node current = a;
		Node newNode = null;
		while(current.getRight().getHeight() - b.getHeight() > 1) {
			current = current.getRight();
		}
		newNode = new Branch("X" + index++, current.getRight(), b);
		if(!nodes.containsKey(newNode.evaluate())) {
			addNode(newNode.evaluate(), newNode);
		} else {
			newNode = nodes.get(newNode.evaluate());
		}
		current.setRight(newNode);
		Node change = a;
		Stack<Node> stack = new Stack<Node>();
		while(!change.evaluate().equals(current.evaluate())) {
			change.setName("X" + index++);
			addNode(change.evaluate(), change);
			stack.push(change);
			change = change.getRight();
		}
		Node next;
		while(!stack.isEmpty()) {
			next = stack.pop();
			next = rightBalance(next);
			addNode(next.evaluate(), next);
		}
		current.setName("X" + index++);
		current = rightBalance(current);
		addNode(current.evaluate(), current);
		return a;
	}

	/**
	 * Rebalancing helper function if tree is left heavy.
	 * @param node Root node of tree.
	 * @return A balanced SLP.
	 */
	private Node leftBalance(Node node) {
		if(node.getLeft().getHeight() == node.getRight().getHeight() + 2) {
			if(node.getLeft().getLeft().getHeight() > node.getLeft().getRight().getHeight()) {
				return leftRotation1(node);
			} else if (node.getLeft().getRight().getHeight() > node.getLeft().getLeft().getHeight()) {
				return leftRotation2(node);
			}
			return node;
		}
		return node;
	}
	
	/**
	 * Helper function for rebalancing.
	 * @param node Root node of tree.
	 * @return left rotated SLP.
	 */
	private Node leftRotation1(Node node) {
		Node newNodeRight = new Branch("X" + index++, node.getLeft().getRight().clone(),  node.getRight().clone());
		addNode(newNodeRight.evaluate(), newNodeRight);
		return new Branch("X" + index++, node.getLeft().getLeft().clone(), newNodeRight);
	}
	
	/**
	 * Helper function for rebalancing.
	 * @param node Root node of tree.
	 * @return left rotated SLP.
	 */
	private Node leftRotation2(Node node) {
		Node newNodeRight = new Branch("X" + index++, node.getLeft().getRight().getRight().clone(),  node.getRight().clone());
		addNode(newNodeRight.evaluate(), newNodeRight);
		Node newNodeLeft = new Branch("X" + index++, node.getLeft().getLeft().clone(), node.getLeft().getRight().getLeft().clone());
		addNode(newNodeLeft.evaluate(), newNodeLeft);
		return new Branch("X" + index++, newNodeLeft, newNodeRight);
	}
	
	/**
	 * Rebalancing helper function if tree is right heavy.
	 * @param node Root node of tree.
	 * @return A balanced SLP.
	 */
	private Node rightBalance(Node node) {
		if(node.getRight().getHeight() == node.getLeft().getHeight() + 2) {
			if(node.getRight().getRight().getHeight() > node.getRight().getLeft().getHeight()) {
				return rightRotation1(node);
			} else if (node.getRight().getLeft().getHeight() > node.getRight().getRight().getHeight()) {
				return rightRotation2(node);
			}
			return node;
		}
		return node;
	}
	
	/**
	 * Helper function for rebalancing.
	 * @param node Root node of tree.
	 * @return right rotated SLP.
	 */
	private Node rightRotation1(Node node) {
		Node newNodeLeft = new Branch("X" + index++, node.getLeft().clone(), node.getRight().getLeft().clone());
		addNode(newNodeLeft.evaluate(), newNodeLeft);
		return new Branch("X" + index++, newNodeLeft, node.getRight().getRight().clone());
	}
	
	/**
	 * Helper function for rebalancing.
	 * @param node Root node of tree.
	 * @return right rotated SLP.
	 */
	private Node rightRotation2(Node node) {
		Node newNodeLeft = new Branch("X" + index++, node.getLeft().clone(), node.getRight().getLeft().getLeft().clone());
		addNode(newNodeLeft.evaluate(), newNodeLeft);
		Node newNodeRight = new Branch("X" + index++, node.getRight().getLeft().getRight().clone(), node.getRight().getRight().clone());
		addNode(newNodeRight.evaluate(), newNodeRight);
		return new Branch("X" + index++, newNodeLeft, newNodeRight);
	}
	
	/**
	 * Builds an SLP for given word from existing NonTerminals
	 * @param word word requiring an SLP.
	 * @return SLP generating word.
	 */
	private Node create(String word) {
		Node node = null;
		if(nodes.containsKey(word)) {
			return nodes.get(word);
		}
		List<List<String>> splits = decompose(word);
		for(List<String> list : splits) {
			if(hasNodes(list, nodes)) {
				node = nodes.get(list.get(0));
				for(int i = 1; i < list.size(); i++) {
					node = concatenate(node, nodes.get(list.get(i)));
				}
				break;
			}
		}
		return node;
	}
	
	/**
	 * Check if NonTerminals exist for given strings.
	 * @param list List of strings requiring NonTerminals
	 * @param nodes current map of NonTerminals
	 * @return True of all strings have NonTerminals existing in map.
	 */
	private boolean hasNodes(List<String> list, Map<String, Node> nodes) {
		for(String s : list) {
			if(!nodes.containsKey(s)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Decomposes a string into all possible permutations.
	 * @param str
	 * @return List of all possible permutations of a string.
	 */
	private List<List<String>> decompose(String str) {
	    List<List<String>> result = new ArrayList<>();

	    if (str.length() == 1) {
	        result.add(new ArrayList<>());
	        result.get(0).add(str);
	        return result;
	    }

	    for (List<String> list : decompose(str.substring(1))) {
	        List<String> append = new ArrayList<>(list);
	        append.set(0, str.substring(0, 1) + append.get(0));
	        List<String> add = new ArrayList<>(list);
	        add.add(0, str.substring(0, 1));
	        result.add(append);
	        result.add(add);
	    }
		return result;
	}
}