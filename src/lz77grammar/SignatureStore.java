package lz77grammar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

public class SignatureStore implements Serializable {

	Random prior;

	String text;

	BidiMap<Signature, Character> u; // Universe
	BidiMap<Signature, Pair> p; // Pairs
	BidiMap<Signature, Power> r; // Power

	Map<Signature, Float> signatures;
	
	SequenceStore sequenceStore;

	int max_sig;

	public SignatureStore() {
		prior = new Random();
		signatures = new HashMap<>();

		u = new DualHashBidiMap<>();
		p = new DualHashBidiMap<>();
		r = new DualHashBidiMap<>();

		sequenceStore = new SequenceStore();
		
		max_sig = 0;
	}

	public int createSig(String text) {
		//System.out.println("Creating sig: " + text);
		List<Element> list = group(text);
		return sig(list).getSig();
	}
	
	public SequenceNode viewTree(Signature s) {
		SequenceNode node = null;
		if(u.containsKey(s)) {
			node = new SequenceNode(null, null, s, 0, 0);
			node.text = Character.toString(u.get(s));
		} else if(p.containsKey(s)) {
			node = new SequenceNode(viewTree(p.get(s).getL()), viewTree(p.get(s).getR()), s, 0, 0);
		} else if(r.containsKey(s)) {
			node = new SequenceNode(null, null,s,0,0);
			node.text = r.get(s).toString();
		}
		return node;
	}
	
	
	public List<SequenceNode> storeSequence(String text) {
		List<SequenceNode> sequence = new ArrayList<SequenceNode>();
		sequence.add(sequenceStore.sequenceToTree(group(text)));
		//System.out.println(sequence.size());
		int i = 1;
		while(true) {
			//System.out.println("SIZE = " + sequence.get(i-1).size);
			sequence.add(2*(i)-1, sequenceStore.sequenceToTree(elpow(sequenceStore.treeToSequence(sequence.get(2*(i)-2)))));
			if(sequence.get(2*(i)-1).size == 0) {
				break;
			}
			sequence.add(2*(i), sequenceStore.sequenceToTree(shrink(sequenceStore.treeToSequence(sequence.get(2*(i)-2)))));
			if(sequence.get(2*(i)).size == 0) {
				break;
			}
			i++;
		}
		TreePrinter treeprinter = new TreePrinter();
		treeprinter.print(viewTree(new Signature(sequence.get(sequence.size() - 1).element.getSig())));
		sequenceStore.addSequence(sequence);
		return sequence;
	}
	
	public List<SequenceNode> concatenate(List<SequenceNode> a, List<SequenceNode> b) {
		List<Element> groups = fixGroups(ListUtils.union(sequenceStore.treeToSequence(a.get(0)),
				sequenceStore.treeToSequence(b.get(0))));
		List<Element> elpowgroups = elpow(groups);
		
		SequenceNode s = sequenceStore.sequenceToTree(groups);
		SequenceNode elpows = sequenceStore.sequenceToTree(elpowgroups);
		
		List<SequenceNode> sequence = new ArrayList<SequenceNode>();
		if(s.size <= 1) {
			sequence.add(s);
			sequence.add(elpows);
			return sequence;
		}
		while (s.size >= 1) {
			sequence.add(s);
			sequence.add(sequenceStore.sequenceToTree(elpow(sequenceStore.treeToSequence(s))));
			s = sequenceStore.sequenceToTree(shrink(sequenceStore.treeToSequence(s)));
		}
		sequence.add(s);
		TreePrinter treeprinter = new TreePrinter();
		treeprinter.print(viewTree(new Signature(sequence.get(sequence.size() - 1).element.getSig())));
		sequenceStore.addSequence(sequence);
		return sequence;
	}
	
//	public List<SequenceNode> concatenate(List<SequenceNode> a, List<SequenceNode> b) {
//		List<Element> groups = ListUtils.union(sequenceStore.treeToSequence(a.get(0)),
//				sequenceStore.treeToSequence(b.get(0)));
//		
//		SequenceNode s = sequenceStore.sequenceToTree(groups);
//		
//		List<SequenceNode> sequence = new ArrayList<SequenceNode>();
//		while (s.size >= 1) {
//			sequence.add(s);
//			sequence.add(sequenceStore.sequenceToTree(elpow(sequenceStore.treeToSequence(s))));
//			s = sequenceStore.sequenceToTree(shrink(sequenceStore.treeToSequence(s)));
//		}
//		sequence.add(s);
//		TreePrinter treeprinter = new TreePrinter();
//		treeprinter.print(viewTree(new Signature(sequence.get(sequence.size() - 1).element.getSig())));
//		sequenceStore.addSequence(sequence);
//		return sequence;
//	}

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
	
	public List<Element> fixGroups(List<Element> list) {
		List<Element> newList = list;
		//System.out.println("pld list");
		for(Element e : newList) {
			//System.out.println(e.getChar() + " " + e.getPow());
		}
		for(int i = 0; i < newList.size()-1; i++) {
			if(newList.get(i).getChar() == newList.get(i+1).getChar()) {
				newList.get(i).setPow(newList.get(i).getPow()+newList.get(i+1).getPow());
				newList.remove(i+1);
			}
		}
		//System.out.println("new list");
		for(Element e : newList) {
			//System.out.println(e.getChar() + " " + e.getPow());
		}
		return newList;
	}

	public List<Element> elpow(List<Element> list) {
		List<Element> result = new ArrayList<Element>();
		for (Element p : list) {
			result.add(sig(Collections.singletonList(p)));
		}
		return result;
	}

//	public void printPairs(List<Pair<Character, Integer>> list) {
//		for (Pair<Character, Integer> p : list) {
//			//System.out.println(p.getL() + " " + p.getR());
//		}
//	}
//
//	public void printSigs(List<Pair<Character, Integer>> list) {
//		// TODO
//	}

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
		//System.out.println("getSigU: " + c + " - " + signature.getSig() + ", prior: " + priority);
		return signature;
	}

	public Signature getSigR(Power power) {
		if (r.inverseBidiMap().containsKey(power)) {
			return r.inverseBidiMap().get(power);
		}
		Float priority = prior.nextFloat();
		while (signatures.containsValue(priority)) {
			priority = prior.nextFloat();
		}
		Signature signature = new Signature(max_sig++);
		signatures.put(signature, priority);
		r.put(signature, power);
		//System.out.println("getSigR: " + power.getChar() + "," + power.getPow() + " - " + signature.getSig()+ ", prior: " + priority);
		return signature;
	}

	public Signature getSigP(Pair pair) {
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
		//System.out.println("getSigP: " + pair.getL().getSig() + "," + pair.getR().getSig() + " - " + signature.getSig() + ", prior: " + priority);
		return signature;
	}

	public Signature sig(List<Element> list) {
		if (list.size() == 1 & list.get(0).getPow() == 1) {
			return Sig(list);
		} else if (list.size() == 1 && list.get(0).getPow() > 1) {
			return getSigR(new Power(list.get(0).getPow(), list.get(0).getChar()));
		}
		return sig(shrink(list));
	}

	private List<Element> shrink(List<Element> list) {
		List<Element> signatures = elpow(list);
		List<List<Element>> blocks = blocksRandomized(signatures);
		List<Element> result = new ArrayList<>();
		for (List<Element> block : blocks) {
			result.add(Sig(block));
		}

		return result;
	}

//	private List<List<Signature>> blocksDeterministic(List<Signature> list) {
//		int logNcolor = 31 - Integer.numberOfLeadingZeros(list.size());
//		int color = 0;
//		List<Integer> logNcoloring = new ArrayList<>();
//		for (Signature l : list) {
//			logNcoloring.add(color++);
//			color = (color == logNcolor) ? 0 : color;
//		}
//
//		List<String> binaryColoring = new ArrayList<>();
//		for (int i : logNcoloring) {
//			binaryColoring.add(Integer.toBinaryString(i));
//		}
//
//		List<Integer> coloring = threeColoring(binaryColoring);
//		List<Integer> d = new ArrayList<>();
//		d.add(1);
//		for (int i = 1; i < coloring.size(); i++) {
//			if ((coloring.get(i - 1) < coloring.get(i)) && (coloring.get(i + 1) < coloring.get(i))) {
//				d.add(1);
//			} else {
//				d.add(0);
//			}
//		}
//
//		return createBlocks(list, d);
//	}
//
//	private List<List<Signature>> createBlocks(List<Signature> list, List<Integer> markers) {
//		List<List<Signature>> blocks = new ArrayList<>();
//		List<Signature> currentBlock = new ArrayList<>();
//		currentBlock.add(list.get(0));
//		for (int i = 1; i < list.size() - 1; i++) {
//			if (markers.get(i) == 1) {
//				blocks.add(currentBlock);
//				currentBlock = new ArrayList<>();
//			}
//			currentBlock.add(list.get(i));
//		}
//		currentBlock.add(list.get(list.size() - 1));
//		blocks.add(currentBlock);
//		return blocks;
//	}
//
//	private List<String> sixColoring(List<String> list) {
//		int colorCount = list.size();
//		char c1;
//		int ji;
//		char bi;
//		String ciprime;
//		while (colorCount > 6) {
//			//System.out.println(colorCount);
//			list.set(0,""+list.get(0).charAt(0));
//			for (int i = 1; i < list.size(); i++) {
//				ji = minJ(list.get(i), list.get(i - 1));
//				bi = list.get(i).charAt(ji);
//				ciprime = Integer.toString(2 * ji) + bi;
//				list.set(i, ciprime);
//			}
//			colorCount = findMax(list) + 1;
//		}
//		return list;
//	}
//
//	private int minJ(String a, String b) {
//		for (int i = 0; i < a.length(); i++) {
//			if (a.charAt(i) != b.charAt(i)) {
//				//System.out.println(i);
//				return i;
//			}
//		}
//		return -1;
//	}
//	
//	private List<Integer> threeColoring(List<String> list) {
//		Set<Integer> colors = new HashSet<>(Arrays.asList(0, 1, 2));
//		list = sixColoring(list);
//		List<Integer> intList = new ArrayList<>();
//		for (String s : list)
//			intList.add(Integer.valueOf(s));
//		intList.set(0, Integer.MAX_VALUE);
//		intList.add(Integer.MAX_VALUE);
//		for (int c = 3; c <= 5; c++) {
//			for (int i = 1; i < intList.size(); i++) {
//				if (intList.get(i) == c) {
//					intList.set(i, minDifference(colors,
//							new HashSet<>(Arrays.asList(intList.get(i - 1), intList.get(i + 1)))));
//				}
//			}
//		}
//		return intList;
//	}
//
//	private int minDifference(Set<Integer> a, Set<Integer> b) {
//		Set<Integer> set = new HashSet<>(a);
//		set.removeAll(b);
//		return Collections.min(set);
//	}
//
//	private int findMax(List<String> list) {
//		List<Integer> intList = new ArrayList<>();
//		for (String s : list)
//			intList.add(Integer.valueOf(s));
//		return Collections.max(intList);
//	}

	private List<List<Element>> blocksRandomized(List<Element> list) {
		List<List<Element>> blocks = new ArrayList<>();
		List<Element> currentBlock = new ArrayList<>();
		currentBlock.add(list.get(0));
		for (int i = 1; i < list.size() - 1; i++) {
			if ((Float.compare(signatures.get(list.get(i - 1)), signatures.get(list.get(i))) > 0)
					&& (Float.compare(signatures.get(list.get(i)), signatures.get(list.get(i + 1))) < 0)) {
				//System.out.println("new block: " + list.get(i).getSig() + ", sig: " + signatures.get(list.get(i)));
				//System.out.println("before: " + list.get(i - 1).getSig() + ", sig: " + signatures.get(list.get(i - 1)));
				//System.out.println("after: " + list.get(i + 1).getSig() + ", sig: " + signatures.get(list.get(i + 1)));
				blocks.add(currentBlock);
				currentBlock = new ArrayList<>();
			}
			currentBlock.add(list.get(i));
		}
		currentBlock.add(list.get(list.size() - 1));
		blocks.add(currentBlock);
		//System.out.println("Blocks:");
		for (List<Element> block : blocks) {
			//System.out.println("");
			for (Element s : block) {
				//System.out.print(s.getSig() + " , ");
			}
		}
		//System.out.println("");
		return blocks;
	}

	private Signature Sig(List<Element> list) {
		//System.out.print("Sig: ");
		for (Element e : list) {
			//System.out.print(e.getSig() + ", " + e.getChar() + ", " + e.getPow());
		}
		//System.out.println("");
		if (list.size() == 1 && signatures.containsKey(list.get(0))) {
			return (Signature) list.get(0);
		} else if (list.size() == 1 && list.get(0) instanceof Power) {
			return getSigU(list.get(0).getChar());
		} else if (list.size() == 2) {
			return getSigP(new Pair(Sig(list.subList(0, 1)), Sig(list.subList(1, 2))));
		}
		List<Element> newList = new ArrayList<>(list.subList(0, 1));
		newList.add(Sig(list.subList(1, list.size())));
		return Sig(newList);
	}
}
