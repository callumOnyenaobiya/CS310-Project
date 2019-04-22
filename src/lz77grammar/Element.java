package lz77grammar;

/**
 * Element interface for SignatureStore data structure.
 * @author Callum Onyenaobiya
 * 
 */
interface Element {
	/**
	 * @return The character an element produces (if applicable)
	 */
	public char getChar();
	/**
	 * @return The power an element produces (if applicable)
	 */
	public int getPow();
	/**
	 * @return The signature an element produces (if applicable)
	 */
	public int getSig();

	public void setSig(int signature);

	public void setPow(int power);
}
