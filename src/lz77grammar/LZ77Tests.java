package lz77grammar;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Test for LZ77 related operations.
 * @author Callum Onyenaobiya
 * 
 */
class LZ77Tests {

	/**
	 * Compresses the string "aabbaaab" into the desired tuples.
	 */
	@Test
	void compressionTest1() {
		LZ77 compressor = new LZ77(32500,250);
		List<Reference> result = compressor.compress("aabbaaab");
		
		List<Reference> expected = new ArrayList<Reference>();
		expected.add(new Reference(0, 0, "a"));
		expected.add(new Reference(1, 1, "b"));
		expected.add(new Reference(1, 1, "a"));
		expected.add(new Reference(5, 2, "b"));
		assertEquals(expected,result);
	}
	
	/**
	 * Compresses the string "aaaaaaaaaaa" into the desired tuples.
	 */
	@Test
	void compressionTest2() {
		LZ77 compressor = new LZ77(32500,250);
		List<Reference> result = compressor.compress("aaaaaaaaaaa");
		
		List<Reference> expected = new ArrayList<Reference>();
		expected.add(new Reference(0, 0, "a"));
		expected.add(new Reference(1, 1, "a"));
		expected.add(new Reference(3, 3, "a"));
		expected.add(new Reference(7, 3, "a"));
		assertEquals(expected,result);
	}
	
	/**
	 * Compresses the string "thisisatestthisisatest" into the desired tuples.
	 */
	@Test
	void compressionTest3() {
		LZ77 compressor = new LZ77(32500,250);
		List<Reference> result = compressor.compress("thisisatestthisisatest");
		
		List<Reference> expected = new ArrayList<Reference>();
		expected.add(new Reference(0, 0, "t"));
		expected.add(new Reference(0, 0, "h"));
		expected.add(new Reference(0, 0, "i"));
		expected.add(new Reference(0, 0, "s"));
		expected.add(new Reference(2, 2, "a"));
		expected.add(new Reference(7, 1, "e"));
		expected.add(new Reference(6, 1, "t"));
		expected.add(new Reference(11, 10, "t"));
		assertEquals(expected,result);
	}
	
	/**
	 * Decompresses a set of tuples, expecting the result "aabbaaab".
	 */
	@Test
	void decompressionTest() {
		LZ77 compressor = new LZ77(32500,250);
		ArrayList<Reference> input = new ArrayList<Reference>();
		input.add(new Reference(0, 0, "a"));
		input.add(new Reference(1, 1, "b"));
		input.add(new Reference(1, 1, "a"));
		input.add(new Reference(5, 2, "b"));
		
		String expected = "aabbaaab";
		String result = compressor.decompress(input);
		assertEquals(expected, result);
	}
	
	/**
	 * Decompresses a set of tuples, expecting the result "aaaaaaaaaaa".
	 */
	@Test
	void decompressionTest2() {
		LZ77 compressor = new LZ77(32500,250);
		ArrayList<Reference> input = new ArrayList<Reference>();
		input.add(new Reference(0, 0, "a"));
		input.add(new Reference(1, 1, "a"));
		input.add(new Reference(3, 3, "a"));
		input.add(new Reference(7, 3, "a"));
		
		String expected = "aaaaaaaaaaa";
		String result = compressor.decompress(input);
		assertEquals(expected, result);
	}
	
	/**
	 * Decompresses a set of tuples, expecting the result "thisisatestthisisatest".
	 */
	@Test
	void decompressionTest3() {
		LZ77 compressor = new LZ77(32500,250);
		ArrayList<Reference> input = new ArrayList<Reference>();
		input.add(new Reference(0, 0, "t"));
		input.add(new Reference(0, 0, "h"));
		input.add(new Reference(0, 0, "i"));
		input.add(new Reference(0, 0, "s"));
		input.add(new Reference(2, 2, "a"));
		input.add(new Reference(7, 1, "e"));
		input.add(new Reference(6, 1, "t"));
		input.add(new Reference(11, 10, "t"));
		
		String expected = "thisisatestthisisatest";
		String result = compressor.decompress(input);
		assertEquals(expected, result);
	}
}
