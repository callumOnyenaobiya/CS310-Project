package lz77grammar;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

public class SequenceStore {

	Random prior;

	String text;

	BidiMap<Signature, Character> u; // Universe
	BidiMap<Signature, Pair<Signature, Signature>> p; // Pairs
	BidiMap<Signature, Pair<Character, Signature>> r; // Power

	Map<Signature, Float> signatures;

	int max_sig;

	public SequenceStore() {
		prior = new Random();
		signatures = new HashMap<>();

		u = new DualHashBidiMap<>();
		p = new DualHashBidiMap<>();
		r = new DualHashBidiMap<>();

		max_sig = 0;
	}

	public int createSig(String text) {
		System.out.println("Creating sig: " + text);
//		String text = null;
//		try {
//			text = new String(Files.readAllBytes(Paths.get(file)));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		List<Element> list = group(text);
		return sig(list).getSig();
	}

	public List<Element> group(String text) {
		List<Element> list = new ArrayList<>();
		char current = text.charAt(0);
		int count = 1;
		for (int i = 1; i < text.length(); i++) {
			if (current == text.charAt(i)) {
				count++;
				continue;
			}
			list.add(new Power(count, current));
			current = text.charAt(i);
			count = 1;
		}
		list.add(new Power(count, current));
		return list;
	}

	public void printPairs(List<Pair<Character, Integer>> list) {
		for (Pair<Character, Integer> p : list) {
			System.out.println(p.getL() + " " + p.getR());
		}
	}

	public void printSigs(List<Pair<Character, Integer>> list) {
		// TODO
	}

	public Signature getSigU(char c) {
		if (u.inverseBidiMap().containsKey(c)) {
			return u.inverseBidiMap().get(c);
		}
		Float priority = prior.nextFloat();
		while (signatures.containsValue(priority)) {
			priority = prior.nextFloat();
		}

		Signature signature = new Signature(max_sig++);
		signatures.put(signature, priority);
		u.put(signature, c);
		System.out.println("getSigU: " + c + " - " + signature.getSig()+ ", prior: " + priority);
		return signature;
	}

	public Signature getSigR(Pair<Character, Signature> pair) {
		if (r.inverseBidiMap().containsKey(pair)) {
			return r.inverseBidiMap().get(pair);
		}
		Float priority = prior.nextFloat();
		while (signatures.containsValue(priority)) {
			priority = prior.nextFloat();
		}
		Signature signature = new Signature(max_sig++);

		signatures.put(signature, priority);
		r.put(signature, pair);
		System.out.println("getSigR: " + pair.getL() + "," + pair.getR().getSig() + " - " + signature.getSig()+ ", prior: " + priority);
		return signature;
	}

	public Signature getSigP(Pair<Signature, Signature> pair) {
		if (p.inverseBidiMap().containsKey(pair)) {
			return p.inverseBidiMap().get(pair);
		}
		Float priority = prior.nextFloat();
		while (signatures.containsValue(priority)) {
			priority = prior.nextFloat();
		}

		Signature signature = new Signature(max_sig++);
		signatures.put(signature, priority);
		p.put(signature, pair);
		System.out.println("getSigP: " + pair.getL().getSig() + "," + pair.getR().getSig() + " - " + signature.getSig() + ", prior: " + priority);
		return signature;
	}

	public Signature sig(List<Element> list) {
		if (list.size() == 1 & list.get(0).getPow() == 1) {
			return Sig(list);
		} else if (list.size() == 1 && list.get(0).getPow() > 1) {
			return getSigR(new Pair<Character, Signature>(list.get(0).getChar(), new Signature(list.get(0).getPow())));
		}
		return sig(shrink(list));
	}

	private List<Element> shrink(List<Element> list2) {
		List<Signature> signatures = new ArrayList<>();
		for (int i = 0; i < list2.size(); i++) {
			signatures.add(sig(list2.subList(i, i + 1)));
		}
		List<List<Signature>> blocks = blocksRandomized(signatures);
		List<Element> result = new ArrayList<>();
		for (List block : blocks) {
			result.add(Sig(block));
		}

		return result;
	}

	private List<List<Signature>> blocksDeterministic(List<Signature> list) {
		int logNcolor = 31 - Integer.numberOfLeadingZeros(list.size());
		int color = 0;
		List<Integer> logNcoloring = new ArrayList<>();
		for (Signature l : list) {
			logNcoloring.add(color++);
			color = (color == logNcolor) ? 0 : color;
		}

		List<String> binaryColoring = new ArrayList<>();
		for (int i : logNcoloring) {
			binaryColoring.add(Integer.toBinaryString(i));
		}

		List<Integer> coloring = threeColoring(binaryColoring);
		List<Integer> d = new ArrayList<>();
		d.add(1);
		for (int i = 1; i < coloring.size(); i++) {
			if ((coloring.get(i - 1) < coloring.get(i)) && (coloring.get(i + 1) < coloring.get(i))) {
				d.add(1);
			} else {
				d.add(0);
			}
		}

		return createBlocks(list, d);
	}

	private List<List<Signature>> createBlocks(List<Signature> list, List<Integer> markers) {
		List<List<Signature>> blocks = new ArrayList<>();
		List<Signature> currentBlock = new ArrayList<>();
		currentBlock.add(list.get(0));
		for (int i = 1; i < list.size() - 1; i++) {
			if (markers.get(i) == 1) {
				blocks.add(currentBlock);
				currentBlock = new ArrayList<>();
			}
			currentBlock.add(list.get(i));
		}
		currentBlock.add(list.get(list.size() - 1));
		blocks.add(currentBlock);
		return blocks;
	}

	private List<String> sixColoring(List<String> list) {
		int colorCount = list.size();
		char c1;
		int ji;
		char bi;
		String ciprime;
		while (colorCount > 6) {
			System.out.println(colorCount);
			list.set(0,""+list.get(0).charAt(0));
			for (int i = 1; i < list.size(); i++) {
				ji = minJ(list.get(i), list.get(i - 1));
				bi = list.get(i).charAt(ji);
				ciprime = Integer.toString(2 * ji) + bi;
				list.set(i, ciprime);
			}
			colorCount = findMax(list) + 1;
		}
		return list;
	}

	private int minJ(String a, String b) {
		for (int i = 0; i < a.length(); i++) {
			if (a.charAt(i) != b.charAt(i)) {
				System.out.println(i);
				return i;
			}
		}
		return -1;
	}
	
	private List<Integer> threeColoring(List<String> list) {
		Set<Integer> colors = new HashSet<>(Arrays.asList(0, 1, 2));
		list = sixColoring(list);
		List<Integer> intList = new ArrayList<>();
		for (String s : list)
			intList.add(Integer.valueOf(s));
		intList.set(0, Integer.MAX_VALUE);
		intList.add(Integer.MAX_VALUE);
		for (int c = 3; c <= 5; c++) {
			for (int i = 1; i < intList.size(); i++) {
				if (intList.get(i) == c) {
					intList.set(i, minDifference(colors,
							new HashSet<>(Arrays.asList(intList.get(i - 1), intList.get(i + 1)))));
				}
			}
		}
		return intList;
	}

	private int minDifference(Set<Integer> a, Set<Integer> b) {
		Set<Integer> set = new HashSet<>(a);
		set.removeAll(b);
		return Collections.min(set);
	}

	private int findMax(List<String> list) {
		List<Integer> intList = new ArrayList<>();
		for (String s : list)
			intList.add(Integer.valueOf(s));
		return Collections.max(intList);
	}


	private List<List<Signature>> blocksRandomized(List<Signature> list) {
		List<List<Signature>> blocks = new ArrayList<>();
		List<Signature> currentBlock = new ArrayList<>();
		currentBlock.add(list.get(0));
		for (int i = 1; i < list.size() - 1; i++) {
			if ((Float.compare(signatures.get(list.get(i - 1)), signatures.get(list.get(i))) > 0) 
					&& (Float.compare(signatures.get(list.get(i)), signatures.get(list.get(i+1))) < 0)) { 
				System.out.println("new block: " + list.get(i).getSig() + ", sig: " + signatures.get(list.get(i)));
				System.out.println("before: " + list.get(i-1).getSig() + ", sig: " + signatures.get(list.get(i-1)));
				System.out.println("after: " + list.get(i+1).getSig() + ", sig: " + signatures.get(list.get(i+1)));
				blocks.add(currentBlock);
				currentBlock = new ArrayList<>();
			}
			currentBlock.add(list.get(i));
		}
		currentBlock.add(list.get(list.size() - 1));
		blocks.add(currentBlock);
		System.out.println("Blocks:" );
		for(List<Signature> block : blocks) {
			System.out.println("");
			for(Signature s : block) {
				System.out.print(s.getSig() +" , ");
			}
		}
		System.out.println("");
		return blocks;
	}

	private Signature Sig(List<Element> list) {
		System.out.print("Sig: ");
		for(Element e : list) {
			System.out.print(e.getSig() + ", " + e.getChar() +", " + e.getPow());
		}
		System.out.println("");
		if (list.size() == 1 && signatures.containsKey(list.get(0))) {
			return (Signature) list.get(0);
		} else if (list.size() == 1 && list.get(0) instanceof Power) {
			return getSigU(list.get(0).getChar());
		} else if (list.size() == 2) {
			return getSigP(new Pair<Signature, Signature>(Sig(list.subList(0, 1)), Sig(list.subList(1, 2))));
		}
		List<Element> newList = new ArrayList<>(list.subList(0, 1));
		newList.add(Sig(list.subList(1, list.size())));
		return Sig(newList);
	}
}
