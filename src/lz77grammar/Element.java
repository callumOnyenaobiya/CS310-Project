package lz77grammar;

interface Element {
	public char getChar();
	public int getPow();
	public int getSig();
	public void setSig(int signature);
	public void setPow(int power);
}
