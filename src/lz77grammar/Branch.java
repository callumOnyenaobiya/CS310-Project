package lz77grammar;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a branch of a CnfGrammar i.e. (A)-{@literal >}(B)(C) or (A)-{@literal >}(B)c or (A)-{@literal >}bc
 * @author Callum Onyenaobiya
 *
 */
class Branch implements Node, Cloneable {

	private Node left;
	private Node right;
	private String name;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Recursively calculates size of tree rooted at this Branch.
	 */
	@Override
	public int size() {
		return left.size() + right.size();
	}

	/**
	 * Recursively locates the ith element rooted at this Branch.
	 */
	@Override
	public char get(int i) {
		if(left.size() > i) {
			return left.get(i);
		}
		return right.get(i - left.size());
	}

	/**
	 * Recursively builds set of all children using an in order traversal.
	 */
	@Override
	public HashSet<String> addChildren(HashSet<String> set) {
		set = left.addChildren(set);
		set = right.addChildren(set);
		set.add(evaluate());
		return set;
	}
	
	

	/**
	 * Recursively builds set of all productions rooted from this Branch, unique productions are maintained
	 * through use of a set.
	 */
	@Override
	public Set<String> getProductions(Set<String> productions) {
		Set<String> newProductions = productions;
		newProductions.add("(" + getName() + ") -> " + "(" + left.getName() + ")" + "(" + right.getName() + ")");
		newProductions = left.getProductions(newProductions);
		newProductions = right.getProductions(newProductions);
		return newProductions;
	}

	/**
	 * Prints this Branch's production in desired format.
	 */
	@Override
	public void printProduction() {
		System.out.println("(" + getName() + ") -> " + "(" + left.getName() + ")" + "(" + right.getName() + ")");
	}

	/**
	 * Implemented for TreePrinter, prints in desired TreePrinter format.
	 */
	@Override
	public String getText() {
		return this.name;
	}

	/**
	 * Clone Override
	 */
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

	/**
	 * Recursively evaluates branch by concatenating left and right evaluations.
	 */
	@Override
	public String evaluate() {
		return left.evaluate() + right.evaluate();
	}

	/**
	 * @param name The name of this Branch
	 * @param left Left Node
	 * @param right Right Node
	 */
	Branch(String name, Node left, Node right) {
		super();
		this.name = name;
		this.left = left;
		this.right = right;
	}

	/**
	 * 
	 */
	public Branch() {
		super();
	}

	/**
	 * Recursively calculates height of tree rooted at this branch by incrementing 1 to the max of the left and
	 * right subtree.
	 */
	@Override
	public int getHeight() {
		return 1 + Math.max(left.getHeight(), right.getHeight());
	}

	@Override
	public Node getLeft() {
		return left;
	}

	@Override
	public void setLeft(Node left) {
		this.left = left;
	}

	@Override
	public Node getRight() {
		return right;
	}

	@Override
	public void setRight(Node right) {
		this.right = right;
	}

	/**
	 * Recursively calculates height of tree rooted at this branch, by calculating difference in height of left and
	 * right subtree.
	 */
	@Override
	public int getBalance() {
		return left.getHeight() - right.getHeight();
	}
	
	/**
	 * Override equals. Equality is defined by two branch's names being equal, as well as their left and right subtrees.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Branch) {
			Branch branch = (Branch) obj;
			return (name.equals(branch.name) && left.equals(branch.getLeft()) && right.equals(branch.getRight()));
		}
		return false;
	}

}
