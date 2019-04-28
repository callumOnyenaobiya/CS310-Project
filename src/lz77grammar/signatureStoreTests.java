package lz77grammar;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests relating to all signature store data structure operations.
 * @author Callum Onyenaobiya
 * 
 */
class signatureStoreTests {

	/**
	 * Take two identical strings and confirm identical signatures.
	 */
	@Test
	void equalityTest1() {
		SignatureStore signatureStore = new SignatureStore();
		List<SequenceNode> a = signatureStore.storeSequence("test");
		List<SequenceNode> b = signatureStore.storeSequence("test");
		assertTrue(a.get(a.size() - 1).element.getSig() == b.get(b.size() - 1).element.getSig());
	}
	
	/**
	 * Take two differing strings and confirm differing signatures.
	 */
	@Test
	void equalityTest2() {
		SignatureStore signatureStore = new SignatureStore();
		List<SequenceNode> a = signatureStore.storeSequence("test1");
		List<SequenceNode> b = signatureStore.storeSequence("ing");
		assertFalse(a.get(a.size() - 1).element.getSig() == b.get(b.size() - 1).element.getSig());
	}
	
	/**
	 * Take two strings, concatenate and obtain identical signature to building from scratch."
	 */
	@Test
	void concatEqualityTest() {
		SignatureStore signatureStore = new SignatureStore();
		List<SequenceNode> a = signatureStore.storeSequence("test");
		List<SequenceNode> b = signatureStore.storeSequence("ing");
		List<SequenceNode> expected = signatureStore.storeSequence("testing");
		
		List<SequenceNode> ab = signatureStore.concatenate(a, b);
		
		assertTrue(expected.get(expected.size() - 1).element.getSig() == ab.get(ab.size() - 1).element.getSig());
	}
	
	/**
	 * Take two strings, concatenate and obtain differing signature to building different string"
	 */
	@Test
	void concatEqualityTest3() {
		SignatureStore signatureStore = new SignatureStore();
		List<SequenceNode> a = signatureStore.storeSequence("test");
		List<SequenceNode> b = signatureStore.storeSequence("ing");
		List<SequenceNode> expected = signatureStore.storeSequence("testing different");
		
		List<SequenceNode> ab = signatureStore.concatenate(a, b);
		
		assertFalse(expected.get(expected.size() - 1).element.getSig() == ab.get(ab.size() - 1).element.getSig());
	}
	
	/**
	 * Concatenate 3 strings and confirm signature equality to building from scratch.
	 */
	@Test
	void concatEqualityTest4() {
		SignatureStore signatureStore = new SignatureStore();
		List<SequenceNode> a = signatureStore.storeSequence("test");
		List<SequenceNode> b = signatureStore.storeSequence("ing");
		List<SequenceNode> c = signatureStore.storeSequence("aaa");
		List<SequenceNode> expected = signatureStore.storeSequence("testingingaaa");
		
		List<SequenceNode> ab = signatureStore.concatenate(a, b);
		List<SequenceNode> abc = signatureStore.concatenate(ab, c);
		
		assertFalse(expected.get(expected.size() - 1).element.getSig() == abc.get(abc.size() - 1).element.getSig());
	}
	
	/**
	 * Build the sequence "testing", two different ways, confirm both results return same signature.
	 */
	@Test
	void concatEqualityTest5() {
		SignatureStore signatureStore = new SignatureStore();
		List<SequenceNode> a = signatureStore.storeSequence("test");
		List<SequenceNode> b = signatureStore.storeSequence("ing");
		List<SequenceNode> c = signatureStore.storeSequence("testi");
		List<SequenceNode> d = signatureStore.storeSequence("ng");
		
		List<SequenceNode> ab = signatureStore.concatenate(a, b);
		List<SequenceNode> cd = signatureStore.concatenate(c, d);
		
		assertTrue(cd.get(cd.size() - 1).element.getSig() == ab.get(ab.size() - 1).element.getSig());
	}
}
