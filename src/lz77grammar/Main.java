package lz77grammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Main {

	static Converter converter;
	static TreePrinter treeprinter;
	static SequenceStore signature;

	public static void main(String[] args) throws Exception {

		converter = new Converter();

		treeprinter = new TreePrinter();

		switch (args[1]) {
		case "c":
			compressAndBuild(args[0]);
			break;
		case "p":
			parseAndBuild(args[0]);
			break;
		case "s":
			createSignature(args[0]);
			break;
		default:
			System.out.println("Please enter h for details");
		}

	}

	public static void createSignature(String file) throws Exception {
		SequenceStore signature = new SequenceStore();
		SequenceStore signature2 = new SequenceStore();
		System.out.println(signature.createSig("aabc"));
		System.out.println(signature.createSig("aab"));
		System.out.println(signature.createSig("c"));
		System.out.println(signature.createSig("aa"));
		System.out.println(signature.createSig("bc"));
//		SequenceStore signature2 = new SequenceStore("test.txt");
//		List<Element> list2 = signature2.group();
//		System.out.println(signature2.sig(list2).getSig());
		
//		Random prior = new Random();
//		Float priority = prior.nextFloat();
//		Float priority2 = prior.nextFloat();
//		
//		System.out.println(priority);
//		System.out.println(priority2);
//		System.out.println(priority<priority2);

	}
	
	public static void compressAndBuild(String file) throws Exception {
		Node node = null;

		LZ77 lz77 = new LZ77(32800, 250);
		ArrayList<Reference> list = lz77.compress(file);
		for(Reference r : list) {
			System.out.println(r.toString());
		}
		System.out.println("String to factorise: ");
		lz77.decompress(list);
		String[] result = lz77.getTuples(list);
		System.out.println("LZ77 factorisation: ");
		for(String s : result) {
			System.out.println(s);
		}
		System.out.println("Computing grammar...");
		node = converter.create(result[0]);
		treeprinter.print(node);
		for (int i = 1; i < result.length; i++) {
			node = converter.concatenate(node, converter.create(result[i]));
			treeprinter.print(node);
		}
		node.setName("S");
		
		System.out.println("LZ77 factors: " + result.length);
		for (String s : result) {
			System.out.println("- " + s);
		}
		System.out.println("Parse tree: ");
		treeprinter.print(node);

		System.out.println("Balance = " + node.getBalance());
		System.out.println("Height = " + node.getHeight());
		System.out.println("Size = " + node.size());
		//System.out.println(4 +"th element = " + node.get(3));

		System.out.println("Production Rules: ");
		Set<String> set = node.addChildren(new HashSet<String>());
		for (String s : set) {
			converter.getNodes().get(s).printProduction();
		}
	}

	public static void parseAndBuild(String file) {
		CFG cfg = new CFG(file);
		System.out.println("Is in CNF: " + cfg.isCNF());
		System.out.println("Evaluation: " + cfg.evaluate());
		System.out.println("Is cyclic: " + cfg.isCyclic());
		Node node = cfg.toTree();
		treeprinter.print(node);
		converter.gFactors(node, new HashMap<String, Integer>());
	}

	public static Node generateTree() {
		Node nodex2 = new Terminal("X2", 'a');
		Node nodex1 = new Terminal("X1", 'b');

		Node nodex3 = new Branch("X3", nodex2, nodex1);
		Node nodex4 = new Branch("X4", nodex3, nodex2);
		Node nodex5 = new Branch("X5", nodex4, nodex3);
		Node nodex6 = new Branch("X6", nodex5, nodex4);
		Node nodex7 = new Branch("X7", nodex6, nodex5);
		return nodex7;
	}
}