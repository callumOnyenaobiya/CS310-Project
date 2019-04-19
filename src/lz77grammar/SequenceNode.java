package lz77grammar;

class SequenceNode implements TreePrinterNode {

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

	@Override
	public String getText() {
		if(text != null) {
			return text;
		}
		return Integer.toString(element.getSig());
	}

}
