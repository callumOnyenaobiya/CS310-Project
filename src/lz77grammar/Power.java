package lz77grammar;

public class Power implements Element {

	private int power;
	private char character;
	
	public Power(int power, char character) {
		this.power = power;
		this.character = character;
	}
	
	@Override
	public char getChar() {
		// TODO Auto-generated method stub
		return character;
	}

	@Override
	public int getPow() {
		// TODO Auto-generated method stub
		return power;
	}

	@Override
	public int getSig() {
		// TODO Auto-generated method stub
		return -1;
	}

}
