package lz77grammar;

import java.util.HashSet;
import java.util.Set;

/**
 * Terminal object represents a terminal production in CnfGrammar i.e. (A) -{@literal >} a
 * @author Callum Onyenaobiya
 * 
 */
class Terminal implements Node, Cloneable {

	private char character;
	private String name;

	/**
	 * Terminal objects have no children aside from the terminal they produce.
	 */
	@Override
	public HashSet<String> addChildren(HashSet<String> set) {
		set.add(evaluate());
		return set;
	}

	/**
	 * Evaluation of a terminal object simply produces the character.
	 */
	@Override
	public String evaluate() {
		return String.valueOf(character);
	}

	/**
	 * Adds production (A)-{@literal >}a to set of productions.
	 */
	@Override
	public Set<String> getProductions(Set<String> productions) {
		Set<String> newProductions = productions;
		newProductions.add("(" + name + ")" + " -> " + evaluate());
		return newProductions;
	}

	@Override
	public String getText() {
		return ("(" + name + " -> " + evaluate() + ")");
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	public char getCharacter() {
		return character;
	}

	public void setCharacter(char character) {
		this.character = character;
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public char get(int i) {
		return this.character;
	}

	@Override
	public void printProduction() {
		System.out.println("(" + name + ")" + " -> " + evaluate());
	}

	@Override
	public Terminal clone() {
		try {
			Terminal t = (Terminal) super.clone();
			t.character = character;
			return t;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	@Override
	public int getHeight() {
		return 0;
	}

	Terminal(String name, char character) {
		super();
		this.name = name;
		this.character = character;
	}

	@Override
	public int getBalance() {
		return 0;
	}

	@Override
	public Node getLeft() {
		return null;
	}

	@Override
	public void setRight(Node right) {

	}

	@Override
	public void setLeft(Node left) {
		
	}

	@Override
	public Node getRight() {
		return null;
	}

	/**
	 * Overrided equals method. Terminal Objects are equal if their name and character are equal.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Terminal) {
			Terminal t = (Terminal) obj;
			return (name.equals(t.getName()) && character == t.getCharacter());
		}
		return false;
	}

}
