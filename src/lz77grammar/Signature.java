package lz77grammar;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

public class Signature {

	Random prior;
	
	String text;

	BidiMap<Integer, Character> u;
	BidiMap<Integer, Pair<Integer, Integer>> p;
	BidiMap<Integer, Pair<Character, Integer>> r;

	Map<Integer, Float> signatures;

	int max_sig;


	public Signature(String file) {
		prior = new Random();
		signatures = new HashMap<>();

		u = new DualHashBidiMap<>();
		p = new DualHashBidiMap<>();
		r = new DualHashBidiMap<>();

		max_sig = 0;

		try {
			text = new String(Files.readAllBytes(Paths.get(file)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<Integer> g() {
		List<Pair<Character, Integer>> list = group();
		List<Integer> signatures = new ArrayList<>();
		for(int i = 0; i < list.size(); i++) {
			signatures.add(sig(list.subList(i, i+1)));
		}
		return signatures;
	}
	
	public List<Pair<Character, Integer>> group() {
		List<Pair<Character, Integer>> list = new ArrayList<>();
		char current = text.charAt(0);
		int count = 1;
		for (int i = 1; i < text.length(); i++) {
			if (current == text.charAt(i)) {
				count++;
				continue;
			}
			list.add(new Pair<Character, Integer>(current, count));
			current = text.charAt(i);
			count = 1;
		}
		list.add(new Pair<Character, Integer>(current, count));
		return list;
	}

	public void printPairs(List<Pair<Character, Integer>> list) {
		for (Pair<Character, Integer> p : list) {
			System.out.println(p.getL() + " " + p.getR());
		}
	}

	public void printSigs(List<Pair<Character, Integer>> list) {
		List<Pair<Character, Float>> sigs = blockDecomposition(list);
		for (Pair p : sigs) {
			System.out.println(p.getL() + " : " + p.getR());
		}
	}

	public List<Pair<Character, Float>> blockDecomposition(List<Pair<Character, Integer>> list) {
		Random rand = new Random();
		List<Pair<Character, Float>> sigs = new ArrayList<>();
		for (Pair<Character, Integer> p : list) {
			sigs.add(new Pair<Character, Float>(p.getL(), rand.nextFloat()));
		}
		return sigs;
	}

	public int sig(List<Pair<Character, Integer>> list) {
		if (list.size() == 1 & list.get(0).getR() == 1) {
			return Sig(list);
		} else if (list.size() == 1 && list.get(0).getR() > 1) {
			return getSigR(new Pair<Character, Integer>(list.get(0).getL(), list.get(0).getR()));
		}
		return sig(shrink(list));
	}

	public int getSigU(char c) {
		if (u.inverseBidiMap().containsKey(c)) {
			return u.inverseBidiMap().get(c);
		}
		Float priority = prior.nextFloat();
		while(signatures.containsValue(priority)) {
			priority = prior.nextFloat();
		}
		signatures.put(max_sig, priority);
		u.put(max_sig++, c);
		return max_sig-1;
	}

	public int getSigR(Pair<Character, Integer> pair) {
		if (r.inverseBidiMap().containsKey(pair)) {
			return r.inverseBidiMap().get(pair);
		}
		Float priority = prior.nextFloat();
		while(signatures.containsValue(priority)) {
			priority = prior.nextFloat();
		}
		signatures.put(max_sig, priority);
		r.put(max_sig++, pair);
		return max_sig - 1;
	}

	public int getSigP(Pair<Integer, Integer> pair) {
		if (p.inverseBidiMap().containsKey(pair)) {
			return p.inverseBidiMap().get(pair);
		}
		Float priority = prior.nextFloat();
		while(signatures.containsValue(priority)) {
			priority = prior.nextFloat();
		}
		signatures.put(max_sig, priority);
		p.put(max_sig++, pair);
		return max_sig - 1;
	}

	private List<Pair<Character, Integer>> shrink(List<Pair<Character, Integer>> list2) {
		// TODO Auto-generated method stub
		return null;
	}

	private int Sig(List<Pair<Character, Integer>> list) {
		return -1;
	}
}
