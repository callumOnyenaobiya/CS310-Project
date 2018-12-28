package lz77grammar;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class LZ77 implements Serializable {
	
	int searchWindowLen;
	int lookAheadWindowLen;
	
	public LZ77(int searchWindowLen, int lookAheadWindowLen){
		this.searchWindowLen = searchWindowLen;
		this.lookAheadWindowLen = lookAheadWindowLen;
	}

	public ArrayList<Reference> compress(String file) throws Exception{
		Reference thisReference;
		String searchSubstring;
		int matchLen;
		int matchLoc;
		int headPos;
		int searchWindowStart;
		int lookAheadWindowEnd;
		ArrayList<Reference>encodedData = new ArrayList<Reference>();
		String rawData = new String(Files.readAllBytes(Paths.get(file)));
		//System.out.println(rawData);
		//System.out.println();//blank line

		//Start attempting to compress from first character
		headPos = 0;
		while(headPos < rawData.length()){
			// Create start of search window
			searchWindowStart = 0;
			if(headPos-searchWindowLen >=0) {
				searchWindowStart = headPos-searchWindowLen;
			}
			//Establish the end of the lookAhead window.
			lookAheadWindowEnd = rawData.length();
			if(headPos+lookAheadWindowLen < rawData.length()) {
				lookAheadWindowEnd = headPos + lookAheadWindowLen;
			}
			//Get a substring from the search window to search 
			// for a match.
			searchSubstring = rawData.substring(searchWindowStart, headPos);
			//Search the search window for a match to the next
			// character in the lookAhead window.
			matchLen = 1;
			if(searchSubstring.indexOf(rawData.substring(headPos,headPos + matchLen)) != -1){
				//A match was found for the one-character string.
				// See if the match extends beyond one character.
				//Limit the length of the possible match to
				// lookAheadWindowLen
				while(matchLen <= lookAheadWindowLen){
					//Keep testing and extending the length of the
					// string being tested for a match until the
					// test fails.
					matchLoc = searchSubstring.indexOf(rawData.substring(headPos,headPos+matchLen));
					//Be careful to avoid searching beyond the end
					// of the raw data.
					if((matchLoc != -1) && ((headPos + matchLen) < rawData.length())){
						matchLen++;
					}else{
						//The matching test failed.  Break out of the
						// loop.
						break;
					}//end else
				}//end while
				//Reduce matchLen to the longest length that
				// matched.
				matchLen--;

				//We went one step too far increasing matchLen. Go
				// back and get the location in the search window
				// where the last match occurred.
				matchLoc = searchSubstring.indexOf(
				rawData.substring(headPos,headPos+matchLen));

				//Increase the character counter to cause the
				// outer loop to skip the matching characters.
				headPos += matchLen;
				//1. Offset distance back to the location of the
				//   match in the search window.
				//2. Length of the match.
				//3. First non-matching character in the lookAhead
				//   window
				//Calculate the offset
				int offset;
				if(headPos < searchWindowLen + matchLen) {
					offset =  headPos - matchLoc - matchLen;
				} else {
					offset = searchWindowLen - matchLoc;
				}
				//Get and save the next non-matching character in
				// the lookAhead window.
				String nextChar = rawData.substring(headPos,headPos+1);
				//Instantiate and populate the Reference object.
				thisReference = new Reference(offset,matchLen,nextChar);
				//Save the Reference object in a Collection object of
				// type ArrayList.
				encodedData.add(thisReference);
			}else{
				//A match was not found for the next character.
				//1. 0
				//2. 0
				//3. The non-matching character.
				String nextChar = rawData.substring(headPos,headPos+1);
				thisReference = new Reference(0,0,nextChar);
				encodedData.add(thisReference);
			}//end else

			//Increment the character counter that controls the
			// outer loop.
			headPos++;
		}//end while loop
		/*for(Reference r: encodedData) {
			System.out.println(r.offset + " " + r.stringLen + " " + r.nextChar);
		}*/
		return encodedData;
	}

	void decompress(ArrayList<Reference> encodedData) {
		StringBuffer reconData = new StringBuffer();
		for (Reference next : encodedData) {
			if(next.stringLen == 0) {
				reconData.append(next.nextChar);
			} else {
				for(int i = 0; i < next.stringLen; i++){
					char currentChar = reconData.charAt(reconData.length() - next.offset);
					reconData.append(currentChar);
				}
				reconData.append(next.nextChar);
			}
		}
		System.out.println(new String(reconData));
	}
	
	String[] getTuples(ArrayList<Reference> encodedData) {
		StringBuffer reconData = new StringBuffer();
		String[] result = new String[encodedData.size()];
		StringBuffer nextWord = new StringBuffer();
		int x = 0;
		for(Reference next : encodedData) {
			if(next.stringLen == 0) {
				reconData.append(next.nextChar);
				result[x++] = next.nextChar;
			} else {
				nextWord = new StringBuffer();
				for(int i = 0; i < next.stringLen; i++) {
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

	String toBitStream(ArrayList<Reference> References) {
		StringBuffer bitStream = new StringBuffer();
		for(Reference Reference : References) {
			bitStream.append(offsetToBinary(Reference.offset));
			bitStream.append(stringLenToBinary(Reference.stringLen));
			bitStream.append(nextCharToBinary(Reference.nextChar));
		}
		System.out.println("Number of References: " + References.size());
		String result = new String(bitStream);
		System.out.println("Length of bitstream: " + result.length());
		return result;
	}
	String offsetToBinary(int offset) {
		return String.format("%" + Integer.toBinaryString(this.searchWindowLen).length() + "s", Integer.toBinaryString(offset)).replace(' ', '0');
	}
	String stringLenToBinary(int stringLen) {
		return String.format("%" + Integer.toBinaryString(this.lookAheadWindowLen).length() + "s", Integer.toBinaryString(stringLen)).replace(' ', '0');
	}
	String nextCharToBinary(String nextChar) {
		return String.format("%8s", Integer.toBinaryString((int) nextChar.charAt(0))).replace(' ', '0');
	}

	void bitStreamToString(String bitStream) {
		ArrayList<Reference> output = new ArrayList<Reference>();
		int headPos = 0;
		int offset;
		int stringLen;
		String nextChar;
		while(headPos < bitStream.length()) {
			offset = Integer.parseInt(bitStream.substring(headPos, headPos + Integer.toBinaryString(searchWindowLen).length()), 2);
			headPos +=  Integer.toBinaryString(searchWindowLen).length();
			stringLen = Integer.parseInt(bitStream.substring(headPos, headPos + Integer.toBinaryString(lookAheadWindowLen).length()), 2);
			headPos += Integer.toBinaryString(lookAheadWindowLen).length();
			nextChar = Character.toString((char)Integer.parseInt(bitStream.substring(headPos, headPos + 8), 2));
			headPos += 8;
			output.add(new Reference(offset, stringLen, nextChar));
		}
		decompress(output);
	}
	
	static int getBitsRequired(int value) {
		return Integer.SIZE-Integer.numberOfLeadingZeros(value);
	}
}
