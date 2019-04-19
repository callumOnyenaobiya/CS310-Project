package lz77grammar;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Branch implements Node, Cloneable {

	private Node left;
	private Node right;
	private String name;

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return left.size() + right.size();
	}

	@Override
	public char get(int i) {
		if(left.size() > i) {
			return left.get(i);
		}
		return right.get(i - left.size());
	}

	@Override
	public HashSet<String> addChildren(HashSet<String> set) {
		set = left.addChildren(set);
		set = right.addChildren(set);
		set.add(evaluate());
		return set;
	}
	
	

	@Override
	public Set<String> getProductions(Set<String> productions) {
		Set<String> newProductions = productions;
		newProductions.add("(" + getName() + ") -> " + "(" + left.getName() + ")" + "(" + right.getName() + ")");
		newProductions = left.getProductions(newProductions);
		newProductions = right.getProductions(newProductions);
		return newProductions;
	}

	@Override
	public void printProduction() {
		System.out.println("(" + getName() + ") -> " + "(" + left.getName() + ")" + "(" + right.getName() + ")");
	}

	@Override
	public String getText() {
		return this.name;
	}

	@Override
	public Branch clone() {
		try {
			Branch b = (Branch) super.clone();
			b.left = left.clone();
			b.right = right.clone();
			return b;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	@Override
	public String evaluate() {
		return left.evaluate() + right.evaluate();
	}

	public Branch(String name, Node left, Node right) {
		super();
		this.name = name;
		this.left = left;
		this.right = right;
	}

	public Branch() {
		super();
	}

	@Override
	public int getHeight() {
		return 1 + Math.max(left.getHeight(), right.getHeight());
	}

	public Node getLeft() {
		return left;
	}

	public void setLeft(Node left) {
		this.left = left;
	}

	public Node getRight() {
		return right;
	}

	public void setRight(Node right) {
		this.right = right;
	}

	@Override
	public int getBalance() {
		// TODO Auto-generated method stub
		return left.getHeight() - right.getHeight();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Branch) {
			Branch branch = (Branch) obj;
			return (name.equals(branch.name) && left.equals(branch.getLeft()) && right.equals(branch.getRight()));
		}
		return false;
	}

}
