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
import java.util.List;
import java.util.Scanner;
/**
 * Initialises CLI program.
 * 
 *   Copyright 2019 Callum Onyenaobiya

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
   
 * @author Callum Onyenaobiya
 */
@SuppressWarnings("unchecked")
public class Main {

	static Converter converter;
	static TreePrinter treeprinter;
	static SignatureStore signatureStore;
	static LZ77 lz77Compressor;

	static Scanner input;
	static Scanner intInput;

	/**
	 * Initialises program main menu.
	 * @param args
	 * 
	 */
	public static void main(String[] args) {
		input = new Scanner(System.in);
		intInput = new Scanner(System.in);
		converter = new Converter();

		treeprinter = new TreePrinter();

		signatureStore = new SignatureStore();

		lz77Compressor = new LZ77(32800, 250);

		mainMenu();
	}

	/**
	 * Displays the main menu of the program.
	 * 
	 */
	public static void mainMenu() {
		String option;
		do {
			System.out.println("Please select an option:");
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

	/**
	 * Displays grammar menu for the program.
	 * 
	 */
	public static void grammarMenu() {
		Grammar grammar = parseGrammar();
		if(grammar == null) {
			return;
		}
		System.out.println("Grammar successfully parsed.");
		System.out.println("Balanced = " + grammar.getBalance());
		String option;
		do {
			System.out.println("Select an option: ");
			System.out.println("t - view parse tree");
			System.out.println("b - balance grammar");
			System.out.println("g - compute gFactors");
			System.out.println("get - retrieve character at given position");
			System.out.println("q - back");
			option = input.nextLine();
			switch(option) {
			case("t"):
				viewParseTree(grammar);
				break;
			case("b"):
				grammar = balanceGrammar(grammar);
				break;
			case("g"):
				grammarFactors(grammar);
				break;
			case("get"):
				getCharacter(grammar);
				break;
			}
		} while(!option.equals("q"));
	}
	
	public static void getCharacter(Grammar grammar) {
		System.out.println("Enter position: ");
		int position = intInput.nextInt();
		if(position > grammar.size()) {
			System.out.println("Position must be <= grammar size.");
			return;
		}
		long startTime = System.nanoTime();
		String result = Character.toString(grammar.get(position));
		long endTime = System.nanoTime();
		System.out.println("The character at position " + position + " is: " + result +", in time: " + (endTime - startTime) +" nanoseconds.");
	}
	
	/**
	 * Displays the parse tree of a given CnfGrammar.
	 * @param grammar CnfGrammar to be displayed as parse tree.
	 */
	public static void viewParseTree(Grammar grammar) {
		grammar.printTree();
	}

	/**
	 * Takes use input file to parse.
	 * @return CnfGrammar object of parsed grammar.
	 */
	public static Grammar parseGrammar() {
		System.out.println("Enter grammar file to parse: ");
		String file = input.nextLine();
		GrammarParser cfg = new GrammarParser(file);
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

	/**
	 * @param grammar
	 * @return
	 */
	public static Grammar balanceGrammar(Grammar grammar) {
		System.out.println("Enter output file name: ");
		String fileOut = input.nextLine();
		grammar.balanceGrammar(0);
		productionsToFile(grammar, fileOut);
		return grammar;

	}

	/**
	 * @param grammar
	 */
	public static void grammarFactors(Grammar grammar) {
		grammar.loadGfactors();
		System.out.println("Grammar factors of "+grammar.evaluate()+":");
		for(String s : grammar.getgFactors()) {
			System.out.println(s);
		}
	}

	/**
	 * Displays menu options for LZ77 related functions.
	 */
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

	/**
	 * Takes user input file and outputs compressed file to desired location.
	 */
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

	/**
	 * Takes user input lz77 file and outputs decompressed file to desired location.
	 */
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

	/**
	 * Takes user input lz77 file and outputs grammar to file.
	 */
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
		
		Grammar cnfgrammar = converter.constructGrammar(lz77Compressor.getTuples(encodedData));
		cnfgrammar.printTree();
		productionsToFile(cnfgrammar, fileOut);
	}
	
	/**
	 * @param grammar CnfGrammar to be stored.
	 * @param file File location for CnfGrammar
	 */
	public static void productionsToFile(Grammar grammar, String file) {
		try (PrintWriter out = new PrintWriter(file)) {
			String[] productions = grammar.getProductions().toArray(new String[0]);
			for(int i = 0; i < productions.length - 1; i++) {
				out.println(productions[i]);
			}
			out.print(productions[productions.length-1]);
			System.out.println("Grammar outputted to " + file + " successfully.");
		} catch(FileNotFoundException ex) {
			System.out.println("Unable to output grammar to file");
		}
	}
	
	/**
	 * Displays menu for sequence data structure related operations.
	 */
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
			case("s"):
				saveSigStore();
				break;
			case ("o"):
				sigOperations();
				break;
			}
		} while (!option.equals("q"));		
	}
	
	/**
	 * Loads a SignatureStore object from user input file.
	 * @return a SignatureStore object.
	 */
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
	
	/**
	 * Creates a new SignatureStore object
	 * @return a SignatureStore object.
	 */
	public static SignatureStore newSigStore() {
		SignatureStore signatureStore = new SignatureStore();
		return signatureStore;
	}
	
	/**
	 * Saves current SignatureStore to user defined file.
	 */
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
			ex.printStackTrace();
			System.out.println("Unable to output to file.");
		}
	}
	
	/**
	 * Displays menu for list of Sequence Data Structure Operations.
	 */
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
	
	/**
	 * Takes user input string and creates new signature using loaded SignatureStore, storing in SignatureStore.
	 */
	public static void createSig() {
		System.out.println("Enter string to convert to signature: ");
		String string = input.nextLine();
		List<SequenceNode> output = signatureStore.storeSequence(string);
		System.out.println("Signature for " + string + " successfully created as: " + output.get(output.size() - 1).element.getSig());
	}
	
	/**
	 * Takes two user input strings and concatenates their signatures using loaded SignatureStore, storing
	 * new signature in SignatureStore.
	 */
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
	
	/**
	 * Takes user input string and position, splitting sequence at given position creating two new signatures using loaded SignatureStore. Stores new
	 * signatures in SignatureStore.
	 */
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
	
	/**
	 * Takes two user input strings and compares their signatures.
	 */
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

	/**
	 * Prints files from current directory.
	 */
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