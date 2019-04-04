package lz77grammar;

import java.io.Serializable;
import java.util.ArrayList;

public class LZ77file implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private ArrayList<Reference> list;
	private int searchWindowLen;
	private int lookAheadWindowLen;
	private int signature;
	
	public LZ77file(ArrayList<Reference> list, int searchWindowLen,int lookAheadWindowLen, int signature) {
		this.list = list;
		this.searchWindowLen = searchWindowLen;
		this.lookAheadWindowLen = lookAheadWindowLen;
		this.signature = signature;
	}

	public ArrayList<Reference> getList() {
		return list;
	}

	public void setList(ArrayList<Reference> list) {
		this.list = list;
	}

	public int getSearchWindowLen() {
		return searchWindowLen;
	}

	public void setSearchWindowLen(int searchWindowLen) {
		this.searchWindowLen = searchWindowLen;
	}

	public int getLookAheadWindowLen() {
		return lookAheadWindowLen;
	}

	public void setLookAheadWindowLen(int lookAheadWindowLen) {
		this.lookAheadWindowLen = lookAheadWindowLen;
	}

	public int getSignature() {
		return signature;
	}

	public void setSignature(int signature) {
		this.signature = signature;
	}
}
