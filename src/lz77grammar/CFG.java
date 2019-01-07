package lz77grammar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class CFG {

	HashMap<String, NonTerminal> map;
	
	public CFG(String file) {
	
		map = new HashMap<String, NonTerminal>();
		
        try {

            File f = new File(file);

            BufferedReader b = new BufferedReader(new FileReader(f));

            String readLine = "";

            String[] tokens = {};
            NonTerminal nonTerminal;
            while ((readLine = b.readLine()) != null) {
                tokens = readLine.split("\\=>");
                nonTerminal = new NonTerminal(tokens[0], tokens[1]);
                map.put(nonTerminal.getName(), nonTerminal);
            }
                        

        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	class NonTerminal {
		private String name;
		private String right;
		
		public String getName() {
			return name;
		}

		public String getRight() {
			return right;
		}

		public boolean isCyclic(HashSet<String> set) {
			set.add(name);
			
			char[] characters = right.toCharArray();
			boolean isNonTerminal = false;
			String nonTerminal = "";
			for(char c : characters) {
				if((c == '(') && !isNonTerminal) {
					isNonTerminal = true;
				} else if((c == ')') && isNonTerminal) {
					isNonTerminal = false;
					if(set.contains(nonTerminal)) {
						return true;
					} else if(map.get(nonTerminal).isCyclic((HashSet<String>)set.clone())) {
						return true;
					}
					nonTerminal = "";
				} else if((c == ')') && !isNonTerminal) {
					System.out.println("WHAT THE FUCK?");
				} else if(isNonTerminal) {
					nonTerminal = nonTerminal + c;
				} 
			}
			return false;
		}
		public NonTerminal(String left, String right) {
			this.name = processLeft(left);
			this.right = right.replaceAll("\\s+", "");
		}
		
		private String processLeft(String left) {
			left = left.replaceAll("\\s+", "");
			if((left.charAt(0) != '(') || (left.charAt(left.length()-1) != ')')) {
				System.out.println("WHAT?");
				return null;
			}
			return left.substring(1, left.length()-1);
		}
		
		public String evaluate() {
			char[] characters = right.toCharArray();
			boolean isNonTerminal = false;
			String result = "";
			String nonTerminal = "";
			for(char c : characters) {
				if((c == '(') && !isNonTerminal) {
					isNonTerminal = true;
				} else if((c == ')') && isNonTerminal) {
					isNonTerminal = false;
					result = result + map.get(nonTerminal).evaluate();
					nonTerminal = "";
				} else if((c == ')') && !isNonTerminal) {
					System.out.println("WHAT THE FUCK?");
				} else if(isNonTerminal) {
					nonTerminal = nonTerminal + c;
				} else {
					result = result + c;
				}
			}
			return result;
		}
	}
	
}
