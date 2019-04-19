package lz77grammar;

import java.util.HashSet;
import java.util.Set;

public class Terminal implements Node, Cloneable {

	private char character;
	private String name;

	@Override
	public HashSet<String> addChildren(HashSet<String> set) {
		set.add(evaluate());
		return set;
	}

	@Override
	public String evaluate() {
		return String.valueOf(character);
	}

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
		// TODO Auto-generated method stub
		return name;
	}

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
		// TODO Auto-generated method stub
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
			// Will not happen in this case
			return null;
		}
	}

	@Override
	public int getHeight() {
		return 0;
	}

	public Terminal(String name, char character) {
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRight(Node right) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLeft(Node left) {
		// TODO Auto-generated method stub

	}

	@Override
	public Node getRight() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Terminal) {
			Terminal t = (Terminal) obj;
			return (name.equals(t.getName()) && character == t.getCharacter());
		}
		return false;
	}

}
