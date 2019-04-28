package lz77grammar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * CFG object used to represent a parsed user grammar.
 * @author Callum Onyenaobiya
 *
 */
@SuppressWarnings("unchecked") class GrammarParser {

	/**
	 * 
	 */
	HashMap<String, NonTerminal> map;

	/**
	 * @param file
	 */
	GrammarParser(String file) {
		BufferedReader b;
		map = new HashMap<String, NonTerminal>();

		try {

			File f = new File(file);

			b = new BufferedReader(new FileReader(f));

			String readLine = "";

			String[] tokens = {};
			NonTerminal nonTerminal;
			while ((readLine = b.readLine()) != null) {
				tokens = readLine.split("\\->");
				nonTerminal = new NonTerminal(tokens[0], tokens[1]);
				map.put(nonTerminal.getName(), nonTerminal);
			}

		} catch (Exception e) {
			System.out.println("Unable to parse grammar");
			map = new HashMap<String, NonTerminal>();
		}
	}
	
	/**
	 * @return A CnfGrammar object identical to the parsed CFG
	 */
	Grammar toCnfGrammar() {
		return new Grammar(toCnfGrammar(map.get("S")));
	}
	
	/**
	 * @param nonTerminal starting Node to build CnfGrammar
	 * @return start node of CnfGrammar
	 */
	private Node toCnfGrammar(NonTerminal nonTerminal) {
		if (!isCNF()) {
			System.out.println("CNF only");
			return null;
		}
		String name = nonTerminal.getName();
		List<String> productions = new ArrayList<String>();

		char[] characters = nonTerminal.getRight().toCharArray();

		boolean isNonTerminal = false;
		String nonTerminalName = "";
		for (char c : characters) {
			if ((c == '(') && !isNonTerminal) {
				isNonTerminal = true;
			} else if ((c == ')') && isNonTerminal) {
				isNonTerminal = false;
				productions.add(nonTerminalName);
				nonTerminalName = "";
			} else if ((c == ')') && !isNonTerminal) {
				System.out.println("Should error");
			} else if (isNonTerminal) {
				nonTerminalName = nonTerminalName + c;
			} else {
				productions.add(String.valueOf(c));
			}
		}

		if (productions.size() == 1) {
			return new Terminal(name, productions.get(0).charAt(0));
		} else {
			return new Branch(name, toCnfGrammar(map.get(productions.get(0))), toCnfGrammar(map.get(productions.get(1))));
		}
	}

	/**
	 * @return Returns True of CFG is in Chomksy Normal Form.
	 */
	public boolean isCNF() {
		for (NonTerminal node : map.values()) {
			if (!node.isCNF()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @return Returns true if cycle exists in CFG.
	 */
	public boolean isCyclic() {
		return isCyclic(map.get("S"), new HashSet<String>());
	}

	/**
	 * @param node The current node
	 * @param set Set containing previously seen NonTerminals
	 * @return Returns true if cycle exists in CFG i.e the current nonterminal exists within our set.
	 */
	private boolean isCyclic(NonTerminal node, HashSet<String> set) {
		set.add(node.getName());

		char[] characters = node.getRight().toCharArray();
		boolean isNonTerminal = false;
		String nonTerminal = "";
		for (char c : characters) {
			if ((c == '(') && !isNonTerminal) {
				isNonTerminal = true;
			} else if ((c == ')') && isNonTerminal) {
				isNonTerminal = false;
				if (set.contains(nonTerminal)) {
					return true;
				} else if (isCyclic(map.get(nonTerminal), (HashSet<String>) set.clone())) {
					return true;
				}
				nonTerminal = "";
			} else if ((c == ')') && !isNonTerminal) {
				System.out.println("WHAT THE FUCK?");
			} else if (isNonTerminal) {
				nonTerminal = nonTerminal + c;
			}
		}
		return false;
	}

	/**
	 * @return Evaluated string of CFG, from starting node S.
	 */
	public String evaluate() {
		return map.get("S").evaluate();
	}

	/**
	 * @author Callum Onyenaobiya
	 * Object to represent parsed NonTerminals in CFG.
	 */
	private class NonTerminal {
		private String name;
		private String right;

		/**
		 * @return Returns True if in Chomsky Normal Form.
		 */
		public boolean isCNF() {
			int terminals = 0;
			int nonTerminals = 0;
			char[] characters = right.toCharArray();
			boolean isNonTerminal = false;
			for (char c : characters) {
				if ((c == '(') && !isNonTerminal) {
					isNonTerminal = true;
				} else if ((c == ')') && isNonTerminal) {
					isNonTerminal = false;
					nonTerminals++;
				} else if ((c == ')') && !isNonTerminal) {
					System.out.println("WHAT THE FUCK?");
				} else if (isNonTerminal) {
					continue;
				} else {
					terminals++;
				}
			}
			return ((terminals == 1 && nonTerminals == 0) || (terminals == 0 && nonTerminals == 2));
		}

		public String getName() {
			return name;
		}

		public String getRight() {
			return right;
		}

		private NonTerminal(String left, String right) {
			this.name = processLeft(left);
			this.right = right.replaceAll("\\s+", "");
		}

		/**
		 * @param left Left hand side of production, the NonTerminal.
		 * @return The name of the NonTerminal i.e (A) = "A"
		 */
		private String processLeft(String left) {
			left = left.replaceAll("\\s+", "");
			if ((left.charAt(0) != '(') || (left.charAt(left.length() - 1) != ')')) {
				System.out.println("Should error here");
				return null;
			}
			return left.substring(1, left.length() - 1);
		}

		/**
		 * @return Evaluates right hand side of production by building from left to right, recursively calling 
		 * evaluate() on any seen NonTerminals.
		 */
		public String evaluate() {
			char[] characters = right.toCharArray();
			boolean isNonTerminal = false;
			String result = "";
			String nonTerminal = "";
			for (char c : characters) {
				if ((c == '(') && !isNonTerminal) {
					isNonTerminal = true;
				} else if ((c == ')') && isNonTerminal) {
					isNonTerminal = false;
					result = result + map.get(nonTerminal).evaluate();
					nonTerminal = "";
				} else if ((c == ')') && !isNonTerminal) {
					System.out.println("Should error here");
				} else if (isNonTerminal) {
					nonTerminal = nonTerminal + c;
				} else {
					result = result + c;
				}
			}
			return result;
		}
	}

}
