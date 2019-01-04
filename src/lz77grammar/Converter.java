package lz77grammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Converter {
	private Map<String, Node> nodes;
	int index;
	
	public Map<String, Node> getNodes() {
		return nodes;
	}
	
	public void addNode(String string, Node node) {
		nodes.put(string, node);
	}
	
	public Converter() {
		this.index = 0;
		nodes = new HashMap<String, Node>();
		for (int i = 0; i <= 255; i++) {
			nodes.put(String.valueOf((char) i), new Terminal("X" + index, (char) i));
			index++;
		}
		//System.out.println(index);
	}

	public HashMap<String, Integer> gFactors(Node root, HashMap<String, Integer> map){
		if(root instanceof Terminal) {
			map.put(root.evaluate(), zeroIfNull(map.get(root.evaluate()))+1);
			return map;
		}
		if(map.containsKey(root.evaluate())) {
			map.put(root.evaluate(), zeroIfNull(map.get(root.evaluate()))+1);
			return map;
		} else {
			map.put(root.evaluate(), 0);
		}
		map = gFactors(root.getLeft(), map);
		map = gFactors(root.getRight(), map);
		return map;
	}
	
	public int zeroIfNull(Integer a) {
		if(a != null) {
			return a;
		}
		return 0;
	}
	public Node concatenate(Node a, Node b) {
		TreePrinter treeprinter = new TreePrinter();
		Node newNode = null;
		if(Math.abs(a.getHeight() - b.getHeight()) <= 1) {
			//System.out.println("CONCAT: "+ index);
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
		//System.out.println(newNode.evaluate() + " = " + (index-1));
		//treeprinter.print(newNode);
		return newNode;
	}
	
	public Node leftConcatenate(Node a, Node b) {
		Node current = b;
		Node newNode = null;
		while(current.getLeft().getHeight() - a.getHeight() > 1) {
			current = current.getLeft();
		}
		//System.out.println("LCONCAT: "+ index);
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
	
	public Node rightConcatenate(Node a, Node b) {
		Node current = a;
		Node newNode = null;
		while(current.getRight().getHeight() - b.getHeight() > 1) {
			current = current.getRight();
		}
		//System.out.println("RCONCAT: "+ index);
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

	public Node leftBalance(Node node) {
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
	
	public Node leftRotation1(Node node) {
		Node newNodeRight = new Branch("X" + index++, node.getLeft().getRight().clone(),  node.getRight().clone());
		addNode(newNodeRight.evaluate(), newNodeRight);
		return new Branch("X" + index++, node.getLeft().getLeft().clone(), newNodeRight);
	}
	
	public Node leftRotation2(Node node) {
		Node newNodeRight = new Branch("X" + index++, node.getLeft().getRight().getRight().clone(),  node.getRight().clone());
		addNode(newNodeRight.evaluate(), newNodeRight);
		Node newNodeLeft = new Branch("X" + index++, node.getLeft().getLeft().clone(), node.getLeft().getRight().getLeft().clone());
		addNode(newNodeLeft.evaluate(), newNodeLeft);
		return new Branch("X" + index++, newNodeLeft, newNodeRight);
	}
	
	public Node rightBalance(Node node) {
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
	
	public Node rightRotation1(Node node) {
		Node newNodeLeft = new Branch("X" + index++, node.getLeft().clone(), node.getRight().getLeft().clone());
		addNode(newNodeLeft.evaluate(), newNodeLeft);
		return new Branch("X" + index++, newNodeLeft, node.getRight().getRight().clone());
	}
	
	public Node rightRotation2(Node node) {
		Node newNodeLeft = new Branch("X" + index++, node.getLeft().clone(), node.getRight().getLeft().getLeft().clone());
		addNode(newNodeLeft.evaluate(), newNodeLeft);
		Node newNodeRight = new Branch("X" + index++, node.getRight().getLeft().getRight().clone(), node.getRight().getRight().clone());
		addNode(newNodeRight.evaluate(), newNodeRight);
		return new Branch("X" + index++, newNodeLeft, newNodeRight);
	}
	
	public Node create(String word) {
		Node node = null;
		if(nodes.containsKey(word)) {
			//System.out.println(word + " found");
			return nodes.get(word);
		}
		//System.out.println(word + " not found");
		List<List<String>> splits = decompose(word);
		for(List<String> list : splits) {
			if(hasNodes(list, nodes)) {
				//System.out.println("Can get from: ");
				for(String s : list) {
					//System.out.print(s + ",");
				}
				//System.out.println("");
				node = nodes.get(list.get(0));
				for(int i = 1; i < list.size(); i++) {
					node = concatenate(node, nodes.get(list.get(i)));
				}
				break;
			}
		}
		return node;
	}
	
	public boolean hasNodes(List<String> list, Map<String, Node> nodes) {
		for(String s : list) {
			if(!nodes.containsKey(s)) {
				return false;
			}
		}
		return true;
	}
	
	public List<List<String>> decompose(String str) {
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
	    //result.sort((xs1, xs2) -> xs1.size() - xs2.size());
	    return result;
	}

}