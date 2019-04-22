package lz77grammar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * SequenceStore used to store compressed sequences of our SignatureStore data structure.
 * @author Callum Onyenaobiya
 * 
 */
class SequenceStore implements Serializable {
	
	private static final long serialVersionUID = 1L;
	/**
	 * List of stored Sequences, stored as SequenceNodes (A binary tree).
	 */
	private List<List<SequenceNode>> sequences;

	public SequenceStore() {
		super();
		this.sequences = new ArrayList<List<SequenceNode>>();
	}

	/**
	 * Converts a sequence into a binary tree.
	 * @param list The Sequence
	 * @return Root node of converted binary tree.
	 */
	SequenceNode sequenceToTree(List<Element> list) {
		if (list.size() == 0) {
			return null;
		}
		return sequenceToTree(list, 0, list.size() - 1);
	}

	/**
	 * Flattens a binary tree into its sequence.
	 * @param node Root node of binary tree.
	 * @return Flattened binary tree.
	 */
	List<Element> treeToSequence(SequenceNode node) {
		List<Element> result = new ArrayList<>();
		if (node.getLeft() != null) {
			result.addAll(treeToSequence(node.getLeft()));
		}
		result.add(node.element);
		if (node.getRight() != null) {
			result.addAll(treeToSequence(node.getRight()));
		}
		return result;
	}

	/**
	 * Recursively builds sequence into binary tree such that an in order traversal gives the sequence.
	 * @param elements
	 * @param start
	 * @param end
	 * @return Root node of converted binary tree.
	 */
	private SequenceNode sequenceToTree(List<Element> elements, int start, int end) {
		if (start > end) {
			return null;
		}
		int mid = (start + end) / 2;
		return new SequenceNode(sequenceToTree(elements, start, mid - 1), sequenceToTree(elements, mid + 1, end),
				elements.get(mid), mid, end-start);
	}
	
	void addSequence(List<SequenceNode> list) {
		sequences.add(list);
	}

}
