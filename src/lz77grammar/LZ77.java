package lz77grammar;

import java.util.ArrayList;

/**
 * Functions required to compress a string into LZ77-compressed tuples.
 * @author Callum Onyenaobiya
 * 
 */
@SuppressWarnings("unused") class LZ77 {

	private int searchWindowLen;
	private int lookAheadWindowLen;

	
	/**
	 * Creates an instance of our LZ77 converter
	 * @param searchWindowLen Number of characters in our search window
	 * @param lookAheadWindowLen Number of characters in our lookAhead window.
	 */
	LZ77(int searchWindowLen, int lookAheadWindowLen) {
		this.searchWindowLen = searchWindowLen;
		this.lookAheadWindowLen = lookAheadWindowLen;
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


	/**
	 * Compress a string into Tuples using LZ77 algorithm.
	 * @param rawData String to compress.
	 * @return List of LZ77-Compressed tuples.
	 */
	ArrayList<Reference> compress(String rawData) {
		Reference thisReference;
		String searchSubstring;
		int matchLen;
		int matchLoc;
		int headPos;
		int searchWindowStart;
		int lookAheadWindowEnd;
		ArrayList<Reference> encodedData = new ArrayList<Reference>();
		headPos = 0;
		while (headPos < rawData.length()) {
			searchWindowStart = 0;
			if (headPos - searchWindowLen >= 0) {
				searchWindowStart = headPos - searchWindowLen;
			}
			lookAheadWindowEnd = rawData.length();
			if (headPos + lookAheadWindowLen < rawData.length()) {
				lookAheadWindowEnd = headPos + lookAheadWindowLen;
			}
			searchSubstring = rawData.substring(searchWindowStart, headPos);
			matchLen = 1;
			if (searchSubstring.indexOf(rawData.substring(headPos, headPos + matchLen)) != -1) {
				while (matchLen <= lookAheadWindowLen) {
					matchLoc = searchSubstring.indexOf(rawData.substring(headPos, headPos + matchLen));
					if ((matchLoc != -1) && ((headPos + matchLen) < rawData.length())) {
						matchLen++;
					} else {
						break;
					}
				}
				matchLen--;
				matchLoc = searchSubstring.indexOf(rawData.substring(headPos, headPos + matchLen));
				headPos += matchLen;
				int offset;
				if (headPos < searchWindowLen + matchLen) {
					offset = headPos - matchLoc - matchLen;
				} else {
					offset = searchWindowLen - matchLoc;
				}
				if(matchLen == 0) {
					offset = 0;
				}
				String nextChar = rawData.substring(headPos, headPos + 1);
				thisReference = new Reference(offset, matchLen, nextChar);
				encodedData.add(thisReference);
			} else {
				String nextChar = rawData.substring(headPos, headPos + 1);
				thisReference = new Reference(0, 0, nextChar);
				encodedData.add(thisReference);
			} 
			headPos++;
		}
		return encodedData;
	}

	/**
	 * Decompress a list of LZ77-Compressed tuples into the output string using LZ77 algorithm.
	 * @param encodedData List of LZ77-Compressed tuples.
	 * @return Decompressed message.
	 */
	String decompress(ArrayList<Reference> encodedData) {
		StringBuffer reconData = new StringBuffer();
		for (Reference next : encodedData) {
			if (next.stringLen == 0) {
				reconData.append(next.nextChar);
			} else {
				for (int i = 0; i < next.stringLen; i++) {
					char currentChar = reconData.charAt(reconData.length() - next.offset);
					reconData.append(currentChar);
				}
				reconData.append(next.nextChar);
			}
		}
		return new String(reconData);
	}

	/**
	 * Convert a list of LZ77-Compressed tuples into their respective strings.
	 * @param encodedData List of LZ77-Compressed tuples
	 * @return Array of strings represented by tuples, used to build balanced SLPs.
	 */
	String[] getTuples(ArrayList<Reference> encodedData) {
		StringBuffer reconData = new StringBuffer();
		String[] result = new String[encodedData.size()];
		StringBuffer nextWord = new StringBuffer();
		int x = 0;
		for (Reference next : encodedData) {
			if (next.stringLen == 0) {
				reconData.append(next.nextChar);
				result[x++] = next.nextChar;
			} else {
				nextWord = new StringBuffer();
				for (int i = 0; i < next.stringLen; i++) {
					char currentChar = reconData.charAt(reconData.length() - next.offset);
					nextWord.append(currentChar);
					reconData.append(currentChar);
				}
				nextWord.append(next.nextChar);
				reconData.append(next.nextChar);
				result[x++] = nextWord.toString();
			}
		}
		return result;
	}

	/**
	 * Research purposes only.
	 * Convert Offset into binary representation
	 * @param offset
	 * @return Binary representation of offset value.
	 */
	private String offsetToBinary(int offset) {
		return String.format("%" + Integer.toBinaryString(this.searchWindowLen).length() + "s",
				Integer.toBinaryString(offset)).replace(' ', '0');
	}

	/**
	 * Research puropses only.
	 * Convert stringLen into binary representation
	 * @param stringLen
	 * @return Binary representation of stringLen value.
	 */
	private String stringLenToBinary(int stringLen) {
		return String.format("%" + Integer.toBinaryString(this.lookAheadWindowLen).length() + "s",
				Integer.toBinaryString(stringLen)).replace(' ', '0');
	}

	/**
	 * Research purposes only.
	 * Convert nextChar into binary representation.
	 * @param nextChar
	 * @return Binary representation of nextChar.
	 */
	private String nextCharToBinary(String nextChar) {
		return String.format("%8s", Integer.toBinaryString((int) nextChar.charAt(0))).replace(' ', '0');
	}

	/**
	 * Research purposes only.
	 * @param bitStream bitStream representation of LZ77-Compressed tuples.
	 * Prints decompressed bitstream.
	 */
	void bitStreamToString(String bitStream) {
		ArrayList<Reference> output = new ArrayList<Reference>();
		int headPos = 0;
		int offset;
		int stringLen;
		String nextChar;
		while (headPos < bitStream.length()) {
			offset = Integer.parseInt(
					bitStream.substring(headPos, headPos + Integer.toBinaryString(searchWindowLen).length()), 2);
			headPos += Integer.toBinaryString(searchWindowLen).length();
			stringLen = Integer.parseInt(
					bitStream.substring(headPos, headPos + Integer.toBinaryString(lookAheadWindowLen).length()), 2);
			headPos += Integer.toBinaryString(lookAheadWindowLen).length();
			nextChar = Character.toString((char) Integer.parseInt(bitStream.substring(headPos, headPos + 8), 2));
			headPos += 8;
			output.add(new Reference(offset, stringLen, nextChar));
		}
		decompress(output);
	}

	/**
	 * Research purposes only.
	 * @param value 
	 * @return Bits required to represent value.
	 */
	static int getBitsRequired(int value) {
		return Integer.SIZE - Integer.numberOfLeadingZeros(value);
	}
	
	/**
	 * Research purposes only.
	 * Converts list of LZ77-Compressed tuples into a bitstream.
	 * @param References
	 * @return Bitstream of LZ77-Compressed tuples.
	 */
	String toBitStream(ArrayList<Reference> References) {
		StringBuffer bitStream = new StringBuffer();
		for (Reference Reference : References) {
			bitStream.append(offsetToBinary(Reference.offset));
			bitStream.append(stringLenToBinary(Reference.stringLen));
			bitStream.append(nextCharToBinary(Reference.nextChar));
		}
		System.out.println("Number of References: " + References.size());
		String result = new String(bitStream);
		System.out.println("Length of bitstream: " + result.length());
		return result;
	}
}
