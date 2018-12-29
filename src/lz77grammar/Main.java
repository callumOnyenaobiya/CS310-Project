package lz77grammar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Main {
// NEED TO CREATE G FACTORS. SEE PAPER FOR DETAILS!
	public static void main(String[] args) throws Exception {
		Converter converter = new Converter();

		TreePrinter treeprinter = new TreePrinter();
		Node node = null;
		
		LZ77 lz77 = new LZ77(32800, 250);
		ArrayList<Reference> list = lz77.compress(args[0]);
		System.out.println("String to factorise: ");
		lz77.decompress(list);
		String[] result = lz77.getTuples(list);
		System.out.println("LZ77 factorization: ");
		for(String s : result) {
			System.out.println(s);
		}

		System.out.println("Computing grammar...");
		node = converter.create(result[0]);
		for(int i = 1; i < result.length; i++) {
			node = converter.concatenate(node, converter.create(result[i]));
		}
		
		System.out.println("Parse tree: ");
		treeprinter.print(node);

		System.out.println("Balance = " + node.getBalance());
		
		System.out.println("Production Rules: ");
		Set<String> set = node.addChildren(new HashSet<String>());
		for(String s: set) {
			converter.getNodes().get(s).printProduction();
		}
		System.out.println("Done");
		System.out.println("LZ77 factors: " + result.length);
		System.out.println("Grammar factors: " + set.size());
	}
}
/*
 * 		String word = "testing";
		converter.create("testing");
		converter.create("a");
		for(int i = 0; i < 20; i++) {
			converter.concatenate(converter.getNodes().get(Character.toString((char) i)), converter.getNodes().get(word));
			word = Character.toString((char) i) + word;
		}
		System.out.println(converter.getNodes().get(word).getBalance());
		System.out.println(word);
		System.out.println(converter.getNodes().get(word).evaluate());
		System.out.println(word.equals(converter.getNodes().get(word)));
		TreePrinter treeprinter = new TreePrinter();
		treeprinter.print(converter.getNodes().get(word));
		
		converter = new Converter();
		String word2 = "testing";
		converter.create("testing");
		converter.create("a");
		for(int i = 0; i < 20; i++) {
			converter.concatenate(converter.getNodes().get(word2), converter.getNodes().get(Character.toString((char) i)));
			word2 = word2 + Character.toString((char) i);
		}
		System.out.println(converter.getNodes().get(word2).getBalance());
		System.out.println(converter.getNodes().get(word2).evaluate());
 */
/*		Node newNode = converter.concatenate(converter.getNodes().get("TEST"), converter.getNodes().get("TEST"));
	converter.getNodes().put(newNode.evaluate(), newNode);
	System.out.println(converter.getNodes().get("TESTTEST").getHeight());
	Node bigNode = converter.concatenate(converter.getNodes().get("TESTTEST"), converter.getNodes().get("B"));
	converter.getNodes().put(bigNode.evaluate(), bigNode);
	System.out.println(converter.concatenate(converter.getNodes().get("TESTTESTB"), converter.getNodes().get("S")).evaluate());
	System.out.println(converter.concatenate(converter.getNodes().get("S"), converter.getNodes().get("TESTTESTB")).evaluate());
	System.out.println(converter.getNodes().get("TESTTEST").evaluate());
	System.out.println(converter.getNodes().get("TESTTESTB").getBalance());

	
	for(List<String> list : converter.recursive("hello")) {
		for(String s : list) {
			System.out.print(s);
			System.out.print(" ");
		}
		System.out.println("");
	}
}*/