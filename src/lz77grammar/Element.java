package lz77grammar;

public interface Element {
	public char getChar();
	public int getPow();
	public int getSig();
	public void setSig(int signature);
	public void setPow(int power);
}
