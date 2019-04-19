package lz77grammar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@SuppressWarnings("unchecked")
public class CFG {

	HashMap<String, NonTerminal> map;

	public CFG(String file) {
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
	
	public CnfGrammar toCnfGrammar() {
		return new CnfGrammar(toCnfGrammar(map.get("S")));
	}
	
	public Node toCnfGrammar(NonTerminal nonTerminal) {
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

	public boolean isCNF() {
		for (NonTerminal node : map.values()) {
			if (!node.isCNF()) {
				return false;
			}
		}
		return true;
	}

	public boolean isCyclic() {
		return isCyclic(map.get("S"), new HashSet<String>());
	}

	public boolean isCyclic(String nonTerminal) {
		return isCyclic(map.get(nonTerminal), new HashSet<String>());
	}

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

	public String evaluate() {
		return map.get("S").evaluate();
	}

	class NonTerminal {
		private String name;
		private String right;

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

		public NonTerminal(String left, String right) {
			this.name = processLeft(left);
			this.right = right.replaceAll("\\s+", "");
		}

		private String processLeft(String left) {
			left = left.replaceAll("\\s+", "");
			if ((left.charAt(0) != '(') || (left.charAt(left.length() - 1) != ')')) {
				System.out.println("Should error here");
				return null;
			}
			return left.substring(1, left.length() - 1);
		}

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
