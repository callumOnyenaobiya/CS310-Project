package lz77grammar;

import java.util.HashSet;
import java.util.Set;

interface Node extends TreePrinterNode {
	public String evaluate();
	public String getName();
	public int size();
	public char get(int i);
	public void setName(String name);
	public int getHeight();
	public int getBalance();
	@Override
	public Node getLeft();
	@Override
	public Node getRight();
	public void setRight(Node right);
	public void setLeft(Node left);
	public Node clone();
	public void printProduction();
	public HashSet<String> addChildren(HashSet<String> set);
	public Set<String> getProductions(Set<String> productions);
}
