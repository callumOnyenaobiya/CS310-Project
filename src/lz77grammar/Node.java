package lz77grammar;

import java.util.HashSet;

public interface Node {
	public String evaluate();
	public String getText();
	public String getName();
	public int size();
	public char get(int i);
	public void setName(String name);
	public int getHeight();
	public int getBalance();
	public Node getLeft();
	public Node getRight();
	public void setRight(Node right);
	public void setLeft(Node left);
	public Node clone();
	public void printProduction();
	public HashSet<String> addChildren(HashSet<String> set);
}
