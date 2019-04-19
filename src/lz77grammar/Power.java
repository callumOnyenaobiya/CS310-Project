package lz77grammar;

import java.util.Objects;

class Power implements Element {

	private int power;
	private char character;
	private int signature;

	Power(int power, char character) {
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
	public void setPow(int power) {
		this.power = power;
	}

	@Override	
	public void setSig(int signature) {
		this.signature = signature;
	}
	@Override
	public int getSig() {
		// TODO Auto-generated method stub
		return this.signature;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return character + "^" + power;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Power) {
			Power p = (Power) obj;
			return (power == p.getPow() && character == p.getChar());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(character, power);
	}
}
