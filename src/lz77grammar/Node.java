package lz77grammar;

import java.util.HashSet;
import java.util.Set;

/**
 * Node interface used to represent grammars.
 * @author Callum Onyenaobiya
 * 
 */
interface Node extends TreePrinterNode {
	/**
	 * @return Evaluation of tree rooted at current Node.
	 */
	public String evaluate();
	/**
	 * @return The name of current Node.
	 */
	public String getName();
	/**
	 * @return Size of tree rooted at current Node.
	 */
	public int size();
	/**
	 * @param i Ith character.
	 * @return The ith character of evaluated string rooted at current Node.
	 */
	public char get(int i);
	
	public void setName(String name);
	/**
	 * @return Height of tree rooted at Current Node.
	 */
	public int getHeight();
	/**
	 * @return Balance of tree rooted at Current Node.
	 */
	public int getBalance();
	/**
	 * Returns left subtree (if applicable)
	 */
	@Override
	public Node getLeft();
	/**
	 * Returns right subtree (if applicable)
	 */
	@Override
	public Node getRight();

	public void setRight(Node right);

	public void setLeft(Node left);
	/**
	 * @return Identical node.
	 */
	public Node clone();
	/**
	 * Prints production representation of current Node.
	 */
	public void printProduction();
	/**
	 * @param set Set of existing Node evaluations.
	 * @return Set of existing Node evaluations, including this Node's children.
	 */
	public HashSet<String> addChildren(HashSet<String> set);
	/**
	 * @param productions Existing set of productions
	 * @return Set of productions including current Node.
	 */
	public Set<String> getProductions(Set<String> productions);
}
