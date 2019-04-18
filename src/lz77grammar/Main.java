package lz77grammar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class Main {

	static Converter converter;
	static TreePrinter treeprinter;
	static SignatureStore signatureStore;
	static LZ77 lz77Compressor;
	static int tutorialMode;

	static Scanner input;
	static Scanner intInput;

	public static void main(String[] args) {
		input = new Scanner(System.in);
		intInput = new Scanner(System.in);
		tutorialMode = 0;
		converter = new Converter(tutorialMode);

		treeprinter = new TreePrinter();

		signatureStore = new SignatureStore();

		lz77Compressor = new LZ77(32800, 250);

		mainMenu();



//        File curDir = new File(".");
//        getAllFiles(curDir);

	}

	public static void mainMenu() {
		String option = "NA";
		do {
			System.out.println("Please select an option");
			System.out.println("");
			System.out.println("L - LZ77 operations");
			System.out.println("G - Grammar operations");
			System.out.println("D - View curent directory");
			option = input.nextLine();
			switch (option) {
			case ("L"):
				lz77Menu();
				break;
			case ("G"):
				grammarMenu();
				break;
			case ("D"):
				getAllFiles();
				break;
			}
		} while (!option.equals("q"));
	}

	public static void grammarMenu() {
		CnfGrammar cnfGrammar = parseGrammar();
		if(cnfGrammar == null) {
			return;
		}
		cnfGrammar.printTree();
		System.out.println("Grammar successfully parsed, select an option: ");
	}

	public static CnfGrammar parseGrammar() {
		System.out.println("Enter grammar file to parse: ");
		String file = input.nextLine();
		CFG cfg = new CFG(file);
		if(cfg.isCyclic()) {
			System.out.println("Grammar not an SLP, please enter an SLP.");
			return null;
		}
		if(!cfg.isCNF()) {
			System.out.println("Please enter a grammar in CNF.");
			return null;
		}
		System.out.println("Evaluation: " + cfg.evaluate());
		return cfg.toCnfGrammar();
	}

	public static void balanceGrammar() {

	}

	public static void grammarFactors() {

	}

	public static void lz77Menu() {
		String option = "NA";
		do {
			System.out.println("Select an option: ");
			System.out.println("c - compress a file");
			System.out.println("d - decompress a file");
			System.out.println("s - convert LZ77 file to SLP");
			option = input.nextLine();
			switch (option) {
			case ("c"):
				lz77Compression();
				break;
			case ("d"):
				lz77Decompression();
				break;
			case ("s"):
				lz77Slp();
				break;
			}
		} while (!option.equals("q"));
	}

	public static void lz77Compression() {
		System.out.println("File to compress: ");
		String file = input.nextLine();
		System.out.println("Search window size: ");
		int searchWindow = intInput.nextInt();
		System.out.println("Lookahead window size: ");
		int lookAheadWindow = intInput.nextInt();
		
		lz77Compressor = new LZ77(searchWindow, lookAheadWindow);
		ArrayList<Reference> list = new ArrayList<Reference>();
		try {
			list = lz77Compressor.compress(new String(Files.readAllBytes(Paths.get(file))));
		}
		catch (IOException ex) {
			System.out.println("Unable to read file.");
		}
		
		try {
			FileOutputStream fileOut = new FileOutputStream(file + ".lz77");
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
			objectOut.writeObject(list);
			objectOut.close();
			System.out.println("File successfully compressed as " + file+".lz77");

		} catch (Exception ex) {
			System.out.println("Unable to output to file.");
		}
	}

	public static void lz77Decompression() {
		System.out.println("File to decompress: ");
		String file = input.nextLine();
		System.out.println("Output file name: ");
		String fileOut = input.nextLine();
		
		ArrayList<Reference> encodedData;
		try {
			FileInputStream fileIn = new FileInputStream(file);
			ObjectInputStream objectIn = new ObjectInputStream(fileIn);

			Object obj = objectIn.readObject();
			objectIn.close();
			encodedData = (ArrayList<Reference>) obj;

		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}

		try (PrintWriter out = new PrintWriter(fileOut)) {
		    out.println(lz77Compressor.decompress(encodedData));
			System.out.println("File decompressed successfully.");
		} catch(FileNotFoundException ex) {
			System.out.println("Unable to decompress file.");
		}
	}

	public static void lz77Slp() {
		System.out.println("File to convert: ");
		String file = input.nextLine();
		System.out.println("Output file name: ");
		String fileOut = input.nextLine();
		
		ArrayList<Reference> encodedData;
		try {
			FileInputStream fileIn = new FileInputStream(file);
			ObjectInputStream objectIn = new ObjectInputStream(fileIn);

			Object obj = objectIn.readObject();
			objectIn.close();
			encodedData = (ArrayList<Reference>) obj;

		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
		
		CnfGrammar cnfgrammar = converter.constructGrammar(lz77Compressor.getTuples(encodedData));
		cnfgrammar.printTree();
		try (PrintWriter out = new PrintWriter(fileOut)) {
			String[] productions = cnfgrammar.getProductions().toArray(new String[0]);
			for(int i = 0; i < productions.length - 1; i++) {
				out.println(productions[i]);
			}
			out.print(productions[productions.length-1]);
			System.out.println("LZ77 file converted successfully");
		} catch(FileNotFoundException ex) {
			System.out.println("Unable to output grammar to file");
		}

		
	}

	public static void createSignature(String file) throws Exception {
		List<SequenceNode> a = signatureStore.storeSequence("test");
		List<SequenceNode> b = signatureStore.storeSequence("ing");
		List<SequenceNode> c = signatureStore.storeSequence("testi");
		List<SequenceNode> d = signatureStore.storeSequence("ng");

		System.out.println("test = " + a.get(a.size() - 1).element.getSig());
		System.out.println("ing = " + b.get(b.size() - 1).element.getSig());
		System.out.println("testi = " + c.get(d.size() - 1).element.getSig());
		System.out.println("ng = " + d.get(d.size() - 1).element.getSig());
//		System.out.println("testi = " + c.get(d.size() - 1).element.getSig());
//		System.out.println("ng = " + d.get(d.size() - 1).element.getSig());

		List<SequenceNode> ab = signatureStore.concatenate(a, b);
		List<SequenceNode> cd = signatureStore.concatenate(c, d);

		System.out.println("ABtesting = " + ab.get(ab.size() - 1).element.getSig());
		System.out.println("CDtesting = " + cd.get(cd.size() - 1).element.getSig());
		System.out.println("testing = " + signatureStore.createSig("testing"));
	}

	public static void gFactors(String file) throws Exception {
		CnfGrammar cnf = null;
		LZ77 lz77 = new LZ77(32800, 250);
		ArrayList<Reference> list = lz77.compress(file);
		String[] result = lz77.getTuples(list);
		cnf = converter.constructGrammar(result);
		treeprinter.print(cnf.startNode);
		System.out.println("our gfactors:");
		for (String s : cnf.gFactors) {
			System.out.println(s);
		}
	}

	public static void compressAndBuild(String file) throws Exception {
		Node node = null;

		LZ77 lz77 = new LZ77(32800, 250);
		ArrayList<Reference> list = lz77.compress(file);
		for (Reference r : list) {
			System.out.println(r.toString());
		}
		System.out.println("String to factorise: ");
		lz77.decompress(list);
		String[] result = lz77.getTuples(list);
		System.out.println("LZ77 factorgsation: ");
		for (String s : result) {
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
		// System.out.println(4 +"th element = " + node.get(3));

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
		CnfGrammar cnfGrammar = cfg.toCnfGrammar();
		cnfGrammar.printTree();
		cnfGrammar.balanceGrammar(tutorialMode);
		cnfGrammar.printTree();
	}

	// We can now write this in a grammar file.
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

	private static void getAllFiles() {
		File curDir = new File(".");
		File[] filesList = curDir.listFiles();
		for (File f : filesList) {
			if (f.isDirectory())
				System.out.println(f.getName());
			if (f.isFile()) {
				System.out.println(f.getName());
			}
		}

	}
	
}