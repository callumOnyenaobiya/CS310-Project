package lz77grammar;

import java.util.ArrayList;
import java.util.List;

class SequenceStore {
	private List<List<SequenceNode>> sequences;

	public SequenceStore() {
		super();
		this.sequences = new ArrayList<List<SequenceNode>>();
	}

	SequenceNode sequenceToTree(List<Element> list) {
		if (list.size() == 0) {
			return null;
		}
		return sequenceToTree(list, 0, list.size() - 1);
	}

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
