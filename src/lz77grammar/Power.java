package lz77grammar;

import java.util.Objects;

/**
 * Power object used to represent compressed run of characters in SignatureStore data structure.
 * @author Callum Onyenaobiya
 * 
 */
class Power implements Element {

	private int power;
	private char character;
	private int signature;

	/**
	 * Creates a power object of compressed run of characters i.e. "bbb" = b^3.
	 * @param power
	 * @param character
	 */
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

	/**
	 * Returns string representation of power object.
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return character + "^" + power;
	}

	/**
	 * Overrided equals method. Power objects are equal if their power and character are equal.
	 */
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
