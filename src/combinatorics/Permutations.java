package combinatorics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Permutations {
	
	private static <E extends Comparable<? super E>> boolean nextPermutation(List<E> items){
		int j = items.size() - 1;
		while(j > 0 && (items.get(j - 1).compareTo(items.get(j)) >= 0)) j--;
		int pivot = j - 1;
		if(pivot < 0) return false;
		int i = items.size() - 1;
		while(items.get(i).compareTo(items.get(pivot)) <= 0) i--;
		E temp = items.get(i);
		items.set(i, items.get(pivot));
		items.set(pivot, temp);
		i = items.size() - 1;
		while(j < i){
			temp = items.get(i);
			items.set(i, items.get(j));
			items.set(j, temp);
			j++;
			i--;
		}
		return true;
	}
	
	public static <E extends Comparable<? super E>> List<List<E>> permutations(List<E> items) {
		List<List<E>> permutations = new ArrayList<>();
		List<E> itemsCopy = new ArrayList<>(items);
		Collections.copy(itemsCopy, items);
		Collections.sort(itemsCopy);
		do {
			List<E> permutation = new ArrayList<>(itemsCopy);
			Collections.copy(permutation, itemsCopy);
			permutations.add(permutation);
		} while(nextPermutation(itemsCopy));
		return permutations;
	}
}