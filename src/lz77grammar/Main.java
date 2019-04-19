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
		String option;
		do {
			System.out.println("Please select an option");
			System.out.println("");
			System.out.println("l - LZ77 operations");
			System.out.println("g - Grammar operations");
			System.out.println("s - Compressed signature data structure operations");
			System.out.println("d - View current directory");
			System.out.println("q - quit");
			option = input.nextLine();
			switch (option) {
			case ("l"):
				lz77Menu();
				break;
			case ("g"):
				grammarMenu();
				break;
			case("s"):
				signatureMenu();
				break;
			case ("d"):
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
		System.out.println("Grammar successfully parsed.");
		String option;
		do {
			System.out.println("Select an option: ");
			System.out.println("t - view parse tree");
			System.out.println("b - balance grammar");
			System.out.println("g - compute gFactors");
			System.out.println("q - back");
			option = input.nextLine();
			switch(option) {
			case("t"):
				viewParseTree(cnfGrammar);
				break;
			case("b"):
				cnfGrammar = balanceGrammar(cnfGrammar);
				break;
			case("g"):
				grammarFactors(cnfGrammar);
				break;
			}
		} while(!option.equals("q"));

	}
	
	public static void viewParseTree(CnfGrammar cnfGrammar) {
		cnfGrammar.printTree();
	}

	public static CnfGrammar parseGrammar() {
		System.out.println("Enter grammar file to parse: ");
		String file = input.nextLine();
		CFG cfg = new CFG(file);
		if(cfg.map.size() == 0) {
			System.out.println("Empty grammar received");
			return null;
		}
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

	public static CnfGrammar balanceGrammar(CnfGrammar cnfGrammar) {
		System.out.println("Enter output file name: ");
		String fileOut = input.nextLine();
		cnfGrammar.balanceGrammar(0);
		productionsToFile(cnfGrammar, fileOut);
		return cnfGrammar;

	}

	public static void grammarFactors(CnfGrammar cnfGrammar) {
		cnfGrammar.loadGfactors();
		System.out.println("Grammar factors of "+cnfGrammar.evaluate()+":");
		for(String s : cnfGrammar.getgFactors()) {
			System.out.println(s);
		}
	}

	public static void lz77Menu() {
		String option;
		do {
			System.out.println("Select an option: ");
			System.out.println("c - compress a file");
			System.out.println("d - decompress a file");
			System.out.println("s - convert LZ77 file to SLP");
			System.out.println("q - back");
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
			for(Reference r : list) {
				System.out.println(r.toString());
			}
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
		productionsToFile(cnfgrammar, fileOut);
	}
	
	public static void productionsToFile(CnfGrammar cnfGrammar, String file) {
		try (PrintWriter out = new PrintWriter(file)) {
			String[] productions = cnfGrammar.getProductions().toArray(new String[0]);
			for(int i = 0; i < productions.length - 1; i++) {
				out.println(productions[i]);
			}
			out.print(productions[productions.length-1]);
			System.out.println("Grammar outputted to " + file + "successfully.");
		} catch(FileNotFoundException ex) {
			System.out.println("Unable to output grammar to file");
		}
	}
	
	public static void signatureMenu() {
		String option;
		SignatureStore sigStore;
		do {
			System.out.println("Select an option: ");
			System.out.println("l - Load existing data structure");
			System.out.println("n - Create new data structure");
			System.out.println("s - save current signature store");
			System.out.println("o - run operations");
			option = input.nextLine();
			switch (option) {
			case ("l"):
				sigStore = loadSigStore();
				if(sigStore == null) {
					System.out.println("Could not load store.");
					return;
				} 
				signatureStore = sigStore;
				System.out.println("Signature loaded successfully.");
				break;
			case ("n"):
				sigStore = newSigStore();
				if(sigStore == null) {
					System.out.println("Could not load store.");
					return;
				} 
				signatureStore = sigStore;
				System.out.println("Signature loaded successfully.");
				break;
			case ("o"):
				sigOperations();
				break;
			}
		} while (!option.equals("q"));		
	}
	
	public static SignatureStore loadSigStore() {
		System.out.println("Signature store file to load: ");
		String file = input.nextLine();
		
		SignatureStore loadedSignatureStore;
		try {
			FileInputStream fileIn = new FileInputStream(file);
			ObjectInputStream objectIn = new ObjectInputStream(fileIn);

			Object obj = objectIn.readObject();
			objectIn.close();
			loadedSignatureStore = (SignatureStore) obj;

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
		return loadedSignatureStore;
	}
	
	public static SignatureStore newSigStore() {
		SignatureStore signatureStore = new SignatureStore();
		return signatureStore;
	}
	
	public static void saveSigStore() {
		System.out.println("File name: ");
		String file = input.nextLine();
		
		try {
			FileOutputStream fileOut = new FileOutputStream(file);
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
			objectOut.writeObject(signatureStore);
			objectOut.close();
			System.out.println("Signature store successfully saved as " + file);

		} catch (Exception ex) {
			System.out.println("Unable to output to file.");
		}
	}
	
	public static void sigOperations() {
		String option;
		do {
			System.out.println("Select an option: ");
			System.out.println("c - create new signature");
			System.out.println("j - join/concatenate two sequences");
			System.out.println("s - split a sequence");
			System.out.println("comp - compare two sequences by their signature");
			option = input.nextLine();
			switch (option) {
			case ("c"):
				createSig();
				break;
			case ("j"):
				concatenateSigs();
				break;
			case ("s"):
				splitSig();
				break;
			case ("comp"):
				compareSigs();
				break;
			}
		} while (!option.equals("q"));		
	}
	
	public static void createSig() {
		System.out.println("Enter string to convert to signature: ");
		String string = input.nextLine();
		List<SequenceNode> output = signatureStore.storeSequence(string);
		System.out.println("Signature for " + string + " successfully created as: " + output.get(output.size() - 1).element.getSig());
	}
	
	public static void concatenateSigs() {
		System.out.println("Enter first string to concatenate: ");
		String first = input.nextLine();
		System.out.println("Enter second string to concatenate: ");
		String second = input.nextLine();
		
		List<SequenceNode> a = signatureStore.storeSequence(first);
		System.out.println("Signature for " + first + " successfully created as: " + a.get(a.size() - 1).element.getSig());
		List<SequenceNode> b = signatureStore.storeSequence(second);
		System.out.println("Signature for " + second + " successfully created as: " + b.get(b.size() - 1).element.getSig());
		
		System.out.println("Concatenating " +first+" and " + second +" gives "+ first+second);
		List<SequenceNode> ab = signatureStore.concatenate(a, b);
		System.out.println("Signature for " + first+second + " successfully created as: " + ab.get(ab.size() - 1).element.getSig());
	}
	
	public static void splitSig() {
		System.out.println("Enter string to split: ");
		String string = input.nextLine();
		System.out.println("Enter position to split " + string +" at:");
		int position = intInput.nextInt();
		
		String first = string.substring(0, position);
		String second = string.substring(position, string.length());
		
		System.out.println("Splitting "+string+" into: "+first+" and "+second);
		List<SequenceNode> a = signatureStore.storeSequence(first);
		System.out.println("Signature for " + first + " successfully created as: " + a.get(a.size() - 1).element.getSig());
		List<SequenceNode> b = signatureStore.storeSequence(second);
		System.out.println("Signature for " + second + " successfully created as: " + b.get(b.size() - 1).element.getSig());
	}
	
	public static void compareSigs() {
		System.out.println("Enter first string to compare: ");
		String first = input.nextLine();
		System.out.println("Enter second string to compare: ");
		String second = input.nextLine();
		
		List<SequenceNode> a = signatureStore.storeSequence(first);
		List<SequenceNode> b = signatureStore.storeSequence(second);
		
		if(a.get(a.size() - 1).element.getSig() == b.get(b.size() - 1).element.getSig()) {
			System.out.println(a.get(a.size() - 1).element.getSig()+" = "+b.get(b.size() - 1).element.getSig()+". Therefore same sequence.");
		} else {
			System.out.println(a.get(a.size() - 1).element.getSig()+" != "+b.get(b.size() - 1).element.getSig()+". Therefore sequences differ.");
		}
	}

	public static void gFactors(String file) throws Exception {
		CnfGrammar cnf = null;
		LZ77 lz77 = new LZ77(32800, 250);
		ArrayList<Reference> list = lz77.compress(file);
		String[] result = lz77.getTuples(list);
		cnf = converter.constructGrammar(result);
		treeprinter.print(cnf.getStartNode());
		System.out.println("our gfactors:");
		for (String s : cnf.getgFactors()) {
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