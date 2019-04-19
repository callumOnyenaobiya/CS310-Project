package lz77grammar;

import java.util.Objects;

class Signature implements Element {

	private int signature;
	
	Signature(int signature) {
		this.signature = signature;
	}
	
	@Override
	public char getChar() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPow() {
		// TODO Auto-generated method stub
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
