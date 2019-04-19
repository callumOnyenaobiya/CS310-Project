package lz77grammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

class Converter {
	private TreePrinter treeprinter;
	private Map<String, Node> nodes;
	private int index;
	private int tutorialMode;
	
	Converter(int tutorialMode) {
		this.tutorialMode = tutorialMode;
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
	
	private void addNode(String string, Node node) {
		nodes.put(string, node);
	}

	CnfGrammar constructGrammar(String[] factors) {
		Node node = create(factors[0]);
		for (int i = 1; i < factors.length; i++) {
			node = concatenate(node, create(factors[i]));
		}
		CnfGrammar cnfGrammar = new CnfGrammar(node);
		cnfGrammar.getStartNode().setName("S");
		return cnfGrammar;
	}
	
//	public Map<String, Integer> gFactors(Node root, Map<String, Integer> map){
//		if(root instanceof Terminal) {
//			tutorialMode(("G factor:" + root.evaluate());
//			map.put(root.evaluate(), zeroIfNull(map.get(root.evaluate()))+1);
//			return map;
//		}
//		if(map.containsKey(root.evaluate())) {
//			tutorialMode(("G factor:" + root.evaluate());
//			map.put(root.evaluate(), zeroIfNull(map.get(root.evaluate()))+1);
//			return map;
//		} else {
//			map.put(root.evaluate(), 0);
//		}
//		map = gFactors(root.getLeft(), map);
//		map = gFactors(root.getRight(), map);
//		return map;
//	}
	
//	public int zeroIfNull(Integer a) {
//		if(a != null) {
//			return a;
//		}
//		return 0;
//	}
	
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

	private Node leftBalance(Node node) {
		if(node.getLeft().getHeight() == node.getRight().getHeight() + 2) {
			tutorialMode("Left balance required");
			if(node.getLeft().getLeft().getHeight() > node.getLeft().getRight().getHeight()) {
				return leftRotation1(node);
			} else if (node.getLeft().getRight().getHeight() > node.getLeft().getLeft().getHeight()) {
				return leftRotation2(node);
			}
			return node;
		}
		return node;
	}
	
	private Node leftRotation1(Node node) {
		Node newNodeRight = new Branch("X" + index++, node.getLeft().getRight().clone(),  node.getRight().clone());
		addNode(newNodeRight.evaluate(), newNodeRight);
		return new Branch("X" + index++, node.getLeft().getLeft().clone(), newNodeRight);
	}
	
	private Node leftRotation2(Node node) {
		Node newNodeRight = new Branch("X" + index++, node.getLeft().getRight().getRight().clone(),  node.getRight().clone());
		addNode(newNodeRight.evaluate(), newNodeRight);
		Node newNodeLeft = new Branch("X" + index++, node.getLeft().getLeft().clone(), node.getLeft().getRight().getLeft().clone());
		addNode(newNodeLeft.evaluate(), newNodeLeft);
		return new Branch("X" + index++, newNodeLeft, newNodeRight);
	}
	
	private Node rightBalance(Node node) {
		if(node.getRight().getHeight() == node.getLeft().getHeight() + 2) {
			tutorialMode("Right balance required");
			if(node.getRight().getRight().getHeight() > node.getRight().getLeft().getHeight()) {
				return rightRotation1(node);
			} else if (node.getRight().getLeft().getHeight() > node.getRight().getRight().getHeight()) {
				return rightRotation2(node);
			}
			return node;
		}
		return node;
	}
	
	private Node rightRotation1(Node node) {
		Node newNodeLeft = new Branch("X" + index++, node.getLeft().clone(), node.getRight().getLeft().clone());
		addNode(newNodeLeft.evaluate(), newNodeLeft);
		return new Branch("X" + index++, newNodeLeft, node.getRight().getRight().clone());
	}
	
	private Node rightRotation2(Node node) {
		Node newNodeLeft = new Branch("X" + index++, node.getLeft().clone(), node.getRight().getLeft().getLeft().clone());
		addNode(newNodeLeft.evaluate(), newNodeLeft);
		Node newNodeRight = new Branch("X" + index++, node.getRight().getLeft().getRight().clone(), node.getRight().getRight().clone());
		addNode(newNodeRight.evaluate(), newNodeRight);
		return new Branch("X" + index++, newNodeLeft, newNodeRight);
	}
	
	private Node create(String word) {
		Node node = null;
		if(nodes.containsKey(word)) {
			tutorialMode(word + " found");
			return nodes.get(word);
		}
		tutorialMode(word + " not found");
		List<List<String>> splits = decompose(word);
		for(List<String> list : splits) {
			if(hasNodes(list, nodes)) {
				tutorialMode("Can get from: ");
				for(String s : list) {
					tutorialMode(s + ",");
				}
				tutorialMode("");
				node = nodes.get(list.get(0));
				for(int i = 1; i < list.size(); i++) {
					node = concatenate(node, nodes.get(list.get(i)));
				}
				break;
			}
		}
		tutorialTree(node);
		return node;
	}
	
	private boolean hasNodes(List<String> list, Map<String, Node> nodes) {
		for(String s : list) {
			if(!nodes.containsKey(s)) {
				return false;
			}
		}
		return true;
	}
	
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
	
	private void tutorialMode(String string) {
		if(tutorialMode == 1) {
			System.out.println(string);
		}
	}
	
	private void tutorialTree(Node node) {
		if(tutorialMode == 1) {
			treeprinter.print(node);
		}
	}
}