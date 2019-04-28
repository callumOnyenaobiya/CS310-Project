package lz77grammar;

import java.io.Serializable;
import java.util.Objects;

/**
 * Signature object used to represent a signature in our SignatureStore data structure.
 * @author Callum Onyenaobiya
 * 
 */
class Signature implements Element, Serializable {

	private static final long serialVersionUID = -1513135206877381389L;
	
	private int signature;
	
	Signature(int signature) {
		this.signature = signature;
	}
	
	@Override
	public char getChar() {
		return 0;
	}

	@Override
	public int getPow() {
		return 1;
	}

	@Override
	public void setPow(int power) {
		
	}

	@Override
	public void setSig(int signature) {
		this.signature = signature;
	}

	@Override
	public int getSig() {
		return this.signature;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(signature);
	}

	/**
	 * Overrided equals method, returns true if both signature values are equal.
	 */
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(obj == null || getClass() != obj.getClass()) {
			return false;
		}
		Signature sig = (Signature) obj;
		return this.signature == sig.signature;
	}

}
