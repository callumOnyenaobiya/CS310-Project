package lz77grammar;

/**
 * TreePrinterNode object used to represent a node printable by our TreePrinter class.
 * @author Callum Onyenaobiya
 *
 */
interface TreePrinterNode {
	
	public TreePrinterNode getLeft();
	public TreePrinterNode getRight();
	public String getText();
	
}
