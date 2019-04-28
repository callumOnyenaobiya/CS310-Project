package lz77grammar;

import java.io.Serializable;

/**
 * Reference object used to represent an LZ77-Compressed tuple.
 * @author Callum Onyenaobiya
 * 
 */
class Reference implements Serializable {
	private static final long serialVersionUID = 1L;

	int offset;

	int stringLen;

	String nextChar;

	Reference(int offset, int stringLen, String nextChar) {
		this.offset = offset;
		this.stringLen = stringLen;
		this.nextChar = nextChar;
	}

	/**
	 * Desired format for string representation of reference.
	 */
	@Override
	public String toString() {
		return "Offset: " + offset + ", stringLen :" + stringLen + ", nextChar: " + nextChar;
	}

	/**
	 * Overrided equals. Two references are equal of their offset, stringLen and nextChar values are equal.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Reference) {
			Reference r = (Reference) obj;
			return (offset == r.offset && stringLen == r.stringLen && nextChar.equals(r.nextChar));
		}
		return false;
	}
	
	

}
