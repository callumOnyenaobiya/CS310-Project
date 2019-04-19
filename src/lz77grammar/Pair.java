package lz77grammar;

import java.util.Objects;

class Pair {
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
