package lz77grammar;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

class signatureStoreTests {

	@Test
	void equalityTest1() {
		SignatureStore signatureStore = new SignatureStore();
		List<SequenceNode> a = signatureStore.storeSequence("test");
		List<SequenceNode> b = signatureStore.storeSequence("test");
		assertTrue(a.get(a.size() - 1).element.getSig() == b.get(b.size() - 1).element.getSig());
	}
	
	@Test
	void equalityTest2() {
		SignatureStore signatureStore = new SignatureStore();
		List<SequenceNode> a = signatureStore.storeSequence("test1");
		List<SequenceNode> b = signatureStore.storeSequence("ing");
		assertFalse(a.get(a.size() - 1).element.getSig() == b.get(b.size() - 1).element.getSig());
	}
	
	@Test
	void concatEqualityTest() {
		SignatureStore signatureStore = new SignatureStore();
		List<SequenceNode> a = signatureStore.storeSequence("test");
		List<SequenceNode> b = signatureStore.storeSequence("ing");
		List<SequenceNode> expected = signatureStore.storeSequence("testing");
		
		List<SequenceNode> ab = signatureStore.concatenate(a, b);
		
		assertTrue(expected.get(expected.size() - 1).element.getSig() == ab.get(ab.size() - 1).element.getSig());
	}
	
	@Test
	void concatEqualityTes2t() {
		SignatureStore signatureStore = new SignatureStore();
		List<SequenceNode> a = signatureStore.storeSequence("test");
		List<SequenceNode> b = signatureStore.storeSequence("ing");
		List<SequenceNode> expected = signatureStore.storeSequence("testing different");
		
		List<SequenceNode> ab = signatureStore.concatenate(a, b);
		
		assertFalse(expected.get(expected.size() - 1).element.getSig() == ab.get(ab.size() - 1).element.getSig());
	}
}
