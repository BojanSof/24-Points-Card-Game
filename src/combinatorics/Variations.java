package combinatorics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Variations {
	
	private static <E extends Comparable<? super E>> void variations(List<E> items, int k, int[] current, int currentIndex, List<List<E>> variations) {
		if(currentIndex == k) {
			List<E> variation = new ArrayList<>(k);
			for(int i = 0; i < current.length; i++)
				variation.add(items.get(current[i]));
			variations.add(variation);
			return;
		}
		for(int i = 0; i < items.size(); i++) {
			current[currentIndex] = i;
			variations(items, k, current, currentIndex + 1, variations);
		}
	}
	
	public static <E extends Comparable<? super E>> List<List<E>> variations(List<E> items, int k) throws IllegalArgumentException {
		if(k < 1 || k > items.size()) throw new IllegalArgumentException("The order of the variation must be between 1 and " + items.size());
		List<List<E>> variations = new ArrayList<>();
		int[] current = new int[k];
		List<E> itemsCopy = new ArrayList<>(items);
		Collections.copy(itemsCopy, items);
		Collections.sort(itemsCopy);
		variations(itemsCopy, k, current, 0, variations);
		return variations;
	}
}
