package lz77grammar;

import java.io.Serializable;
import java.util.Objects;

/**
 * Pair object used to represent a pair of signatures for SignatureStore data structure.
 * @author Callum Onyenaobiya
 * 
 */
class Pair implements Serializable {

	private static final long serialVersionUID = -1459302784569277752L;
	
	private Signature l;
	private Signature r;

	Pair(Signature l, Signature r) {
		this.l = l;
		this.r = r;
	}

	public Signature getL() {
		return l;
	}

	public Signature getR() {
		return r;
	}

	public void setL(Signature l) {
		this.l = l;
	}

	public void setR(Signature r) {
		this.r = r;
	}

	/**
	 * Overrided equals method, pair is equal if both signatures in pair are equal.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Pair) {
			Pair pair = (Pair) obj;
			return l.equals(pair.getL()) && r.equals(pair.getR());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(l, r);
	}

}
