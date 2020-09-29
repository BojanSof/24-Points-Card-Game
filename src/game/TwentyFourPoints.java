package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import combinatorics.Permutations;
import combinatorics.Variations;
import expression.Expression;
import expression.ExpressionType;
import expression.InvalidExpression;

public class TwentyFourPoints {
	private final static Random rng = new Random();
	private final static double EPS = 1E-7;
	private final static String[] solutionsPostfixForms = new String[] {"NNNNOOO", "NNNONOO", "NNONNOO", "NNONONO", "NNNOONO"};
	private final static Character[] operators = new Character[] {'+', '-', '*', '/'};
	private final List<Integer> numbers = new ArrayList<>();
	private final List<String> solutions = new ArrayList<>();
	private int solutionsIndex = 0;
	private int currentSolution = 0;
	private int n;
	
	public TwentyFourPoints() {
		this(4);
	}
	
	public TwentyFourPoints(int n) {
		if(n < 3) this.n = 4;
		else this.n = n;
		generateNumbers();
	}
	
	public void generateNumbers() {
		this.numbers.clear();
		this.solutions.clear();
		this.solutionsIndex = 0;
		this.currentSolution = 0;
		int num;
		for(int i = 0; i < this.n; i++) {
			do {
				num = rng.nextInt(13) + 1;
			} while(this.numbers.contains(num));
			this.numbers.add(num);
		}
	}
	
	public List<Integer> getNumbers() {
		return this.numbers;
	}
	
	public int getNumberOfNumbers() {
		return this.n;
	}
	
	public String verifyExpression(String exp) {
		if(exp.equalsIgnoreCase("no solutions") || exp.equalsIgnoreCase("no solution")) {
			if(findSolution().equals("No solution exists")) return "Correct!";
			else return "Incorrect.";
		}
		Expression expression = new Expression(exp);
		List<Integer> operands = new ArrayList<>();
		try {
			for(Double val: expression.getOperands())
				operands.add(val.intValue());
		} catch(InvalidExpression ex) {
			return "Invalid expression";
		}
		
		if(operands.size() != this.numbers.size()) {
			return "Invalid numbers in the expression";
		}
		
		List<Integer> numbersSorted = new ArrayList<>(this.numbers);
		Collections.copy(numbersSorted, this.numbers);
		Collections.sort(operands);
		Collections.sort(numbersSorted);
		for(int i = 0; i < numbersSorted.size(); i++) {
			if(operands.get(i) != numbersSorted.get(i)) {
				return "Invalid numbers in the expression";
			}
		}
		try {
			if(Math.abs(expression.evaluate() - 24.0) < EPS) {
				return "Correct!";
			} else {
				return "Incorrect result";				
			}
		} catch(InvalidExpression ex) {
			return "Invalid expression";
		}
	}
	
	public String findSolution() {
		if(solutions.size() == 0) {
			for(List<Integer> numbersPermutation: Permutations.permutations(this.numbers)) {
				for(List<Character> operatorsVariation: Variations.variations(Arrays.asList(operators), this.n - 1)) {
					for(String template: solutionsPostfixForms) {
						StringBuilder postfix = new StringBuilder();
						int numberIndex = 0, operatorIndex = 0;
						for(int i = 0; i < template.length(); i++) {
							if(template.charAt(i) == 'N') postfix.append(numbersPermutation.get(numberIndex++));
							else if(template.charAt(i) == 'O') postfix.append(operatorsVariation.get(operatorIndex++));
							postfix.append(" ");
						}
						Expression expression = new Expression(postfix.toString(), ExpressionType.POSTFIX);
						try {
							if(Math.abs(expression.evaluate() - 24.0) < EPS) {
								String sol = expression.convert(ExpressionType.INFIX).getExpression();
								if(!solutions.contains(sol)) solutions.add(sol);
							}
						} catch(InvalidExpression ex) {
							continue;
						}
					}
				}
			}
		}
		if(solutions.size() == 0) return "No solution exists";
		else {
			String sol = solutions.get(solutionsIndex);
			currentSolution = solutionsIndex + 1;
			if(currentSolution > solutions.size()) currentSolution = 1;
			solutionsIndex = (solutionsIndex + 1) % solutions.size();
			return sol;
		}
	}
	
	public int getNumberSolutions() {
		return this.solutions.size();
	}
	
	public int getCurrentSolution() {
		return currentSolution;		
	}
}