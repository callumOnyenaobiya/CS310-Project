package lz77grammar;

import java.io.Serializable;

/**
 * SequenceNode object used to represent a compressed sequence in our SignatureStore data structure.
 * @author Callum Onyenaobiya
 * 
 */
class SequenceNode implements TreePrinterNode, Serializable {

	private static final long serialVersionUID = -8681618095183150263L;
	
	private SequenceNode left;
	private SequenceNode right;
	Element element;
	int size;
	String text;
	
	SequenceNode(SequenceNode left, SequenceNode right, Element element, int position, int size) {
		this.left = left;
		this.right = right;
		this.element = element;
		this.size = size;
	}

	@Override
	public SequenceNode getLeft() {
		return left;
	}

	@Override
	public SequenceNode getRight() {
		return right;
	}

	/**
	 * Text representation of current node, used in TreePrinter.
	 */
	@Override
	public String getText() {
		if(text != null) {
			return text;
		}
		return Integer.toString(element.getSig());
	}

}
