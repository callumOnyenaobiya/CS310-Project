package lz77grammar;

import java.util.ArrayList;
import java.util.List;

class TreePrinter {

	/**
	 * Print a tree Inspired by https://stackoverflow.com/a/29704252
	 * 
	 * @param root Root of printed tree.
	 */
	void print(TreePrinterNode root) {

		List<List<String>> lines = new ArrayList<List<String>>();
		List<TreePrinterNode> level = new ArrayList<TreePrinterNode>();
		List<TreePrinterNode> next = new ArrayList<TreePrinterNode>();
		level.add(root);

		int nextNode = 1;
		int MaxWidth = 0;

		while (nextNode != 0) {
			List<String> line = new ArrayList<String>();
			nextNode = 0;
			for (TreePrinterNode n : level) {
				if (n == null) {
					line.add(null);
					next.add(null);
					next.add(null);
				} else {
					String aa = n.getText();
					line.add(aa);
					if (aa.length() > MaxWidth) {
						MaxWidth = aa.length();
					}
					next.add(n.getLeft());
					next.add(n.getRight());

					if (n.getLeft() != null) {
						nextNode++;
					}
					if (n.getRight() != null) {
						nextNode++;
					}
				}
			}
			if (MaxWidth % 2 == 1) {
				MaxWidth++;
			}
			lines.add(line);
			List<TreePrinterNode> tmp = level;
			level = next;
			next = tmp;
			next.clear();
		}

		int perpiece = lines.get(lines.size() - 1).size() * (MaxWidth + 2);
		for (int i = 0; i < lines.size(); i++) {
			List<String> line = lines.get(i);
			int hpw = (int) Math.floor(perpiece / 2f) - 1;

			if (i > 0) {
				for (int j = 0; j < line.size(); j++) {

					char c = ' ';
					if (j % 2 == 1) {
						if (line.get(j - 1) != null) {
							c = (line.get(j) != null) ? '┴' : '┘';
						} else {
							if (j < line.size() && line.get(j) != null)
								c = '└';
						}
					}
					System.out.print(c);

					if (line.get(j) == null) {
						for (int k = 0; k < perpiece - 1; k++) {
							System.out.print(" ");
						}
					} else {
						for (int k = 0; k < hpw; k++) {
							if (j % 2 == 0) {
								System.out.print(" ");
							} else {
								System.out.print("─");
							}
						}
						System.out.print(j % 2 == 0 ? "┌" : "┐");
						for (int k = 0; k < hpw; k++) {
							if (j % 2 == 0) {
								System.out.print("─");
							} else {
								System.out.print(" ");
							}
						}
					}
				}
				System.out.println();
			}

			for (int j = 0; j < line.size(); j++) {

				String f = line.get(j);
				if (f == null)
					f = "";
				int gap1 = (int) Math.ceil(perpiece / 2f - f.length() / 2f);
				int gap2 = (int) Math.floor(perpiece / 2f - f.length() / 2f);

				for (int k = 0; k < gap1; k++) {
					System.out.print(" ");
				}
				System.out.print(f);
				for (int k = 0; k < gap2; k++) {
					System.out.print(" ");
				}
			}
			System.out.println();

			perpiece /= 2;
		}
	}
}