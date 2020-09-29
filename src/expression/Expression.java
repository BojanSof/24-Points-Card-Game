package expression;

import java.util.Stack;
import java.util.EmptyStackException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Expression {
	
	private static final char[] operators = new char[] {'+', '-', '*', '/'};

	private final ExpressionType type;
	private final String expression;

	public Expression() {
		this.type = ExpressionType.EMPTY;
		this.expression = "";
	}

	public Expression(String expression) {
		this.expression = expression;
		this.type = ExpressionType.INFIX;
	}

	public Expression(String expression, ExpressionType type) {
		this.type = type;
		this.expression = expression;
	}

	private static boolean isBracket(char c) {
		switch(c) {
			case '(':
			case ')':
			case '[':
			case ']':
			case '{':
			case '}':
				return true;
			default: return false;
		}
	}

	private static boolean isOpeningBracket(char c) {
		switch(c) {
			case '(':
			case '[':
			case '{':
				return true;
			default: return false;
		}
	}

	private static boolean isClosingBracket(char c) {
		switch(c) {
			case ')':
			case ']':
			case '}':
				return true;
			default: return false;
		}
	}

	private static char matchingBracket(char c) {
		switch(c) {
			case '(': return ')';
			case ')': return '(';
			case '[': return ']';
			case ']': return '[';
			case '{': return '}';
			case '}': return '{';
			default: return '.';
		}

	}

	private static boolean isOperator(char c) {
		for(int i = 0; i < operators.length; i++)
			if(c == operators[i]) return true;
		return false;
	}

	private static int operatorPrecedence(char op) {
		switch(op) {
			case '*':
			case '/':
				return 2;
			case '+':
			case '-':
				return 1;
			case '`': return Integer.MAX_VALUE;
			default: return 0;
		}
	}

	private static String insertWhitespaces(String expression) {
		StringBuilder modifiedExpression = new StringBuilder();
		for(int i = 0; i < expression.length(); i++) {
			char c = expression.charAt(i);
			if(isOperator(c) || isBracket(c)) {
				if(
					!(
						c == '-' && 
						(
							i == 0 || 
							isOpeningBracket(expression.charAt(i-1)) || isOperator(expression.charAt(i-1))
						)
					)
				) {
					modifiedExpression.append(" " + c + " ");
				} else modifiedExpression.append(c);
			}
			else modifiedExpression.append(c);
		}
		return modifiedExpression.toString().trim();
	}

	private static String[] tokenizeExpression(String expression) {
		return insertWhitespaces(expression).split("\\s+");
	}
	
	public List<Double> getOperands() throws InvalidExpression {
		List<Double> operands = new ArrayList<>();
		String[] tokens = tokenizeExpression(this.expression);
		for(String token: tokens) {
			if(token.length() == 0) throw new InvalidExpression(this);
			if(!isBracket(token.charAt(0)) && !(token.length() == 1 && isOperator(token.charAt(0)))) {
				try {
					operands.add(Double.parseDouble(token));
				} catch(NumberFormatException ex) {
					throw new InvalidExpression(this);
				}
			}
		}
		return operands;
	}
	
	public List<Character> getOperators() throws InvalidExpression {
		List<Character> operators = new ArrayList<>();
		String[] tokens = tokenizeExpression(this.expression);
		for(String token: tokens) {
			if(token.length() == 0) throw new InvalidExpression(this);
			if(isBracket(token.charAt(0)) || (token.length() == 1 && isOperator(token.charAt(0)))) {
				operators.add(token.charAt(0));
			}
		}
		return operators;
	}

	private double evaluateOperator(double a, double b, char operator) {
		switch(operator) {
			case '+': return a + b;
			case '-': return a - b;
			case '*': return a * b;
			case '/': return a / b;
			default: return Double.NaN;
		}
	}

	private double evaluateInfix() throws InvalidExpression {
		if(this.type != ExpressionType.INFIX) return Double.NaN;
		Stack<Double> operandStack = new Stack<>();
		Stack<Character> operatorStack = new Stack<>();
		String[] tokens = tokenizeExpression(this.expression);
		try {
			for(String token: tokens) {
				if(isOpeningBracket(token.charAt(0))) operatorStack.push(token.charAt(0));
				else if(isClosingBracket(token.charAt(0))) {
					while(operatorStack.peek() != matchingBracket(token.charAt(0))) {
						double b = operandStack.pop();
						double a = operandStack.pop();
						operandStack.push(evaluateOperator(a, b, operatorStack.pop()));
					}
					operatorStack.pop();
				} else if(token.length() == 1 && isOperator(token.charAt(0))) {
					while(	
							!operatorStack.empty() && 
							operatorPrecedence(operatorStack.peek()) >= operatorPrecedence(token.charAt(0))
						) {
							double b = operandStack.pop();
							double a = operandStack.pop();
							operandStack.push(evaluateOperator(a, b, operatorStack.pop()));
						}
						operatorStack.push(token.charAt(0));
				} else {
					double operand = Double.parseDouble(token);
					operandStack.push(operand);
				}
			}
			while(!operatorStack.empty()) {
				double b = operandStack.pop();
				double a = operandStack.pop();
				operandStack.push(evaluateOperator(a, b, operatorStack.pop()));
			} 
		} catch(EmptyStackException|NumberFormatException ex) {
			throw new InvalidExpression(this);
		}
		if(operandStack.size() > 1) throw new InvalidExpression(this);
		return operandStack.pop();
	}

	private double evaluatePrefix() throws InvalidExpression {
		if(this.type != ExpressionType.PREFIX) return Double.NaN;
		Stack<Double> operandStack = new Stack<>();
		String[] tokens = tokenizeExpression(this.expression);
		List<String> tokensList = Arrays.asList(tokens);
		Collections.reverse(tokensList);
		tokens = tokensList.toArray(new String[0]);
		try{
			for(String token: tokens) {
				if(isOperator(token.charAt(0))) {
					double a = operandStack.pop();
					double b = operandStack.pop();
					operandStack.push(evaluateOperator(a, b, token.charAt(0)));
				} else {
					double operand = Double.parseDouble(token);
					operandStack.push(operand);
				}
			}
		} catch(EmptyStackException|NumberFormatException ex) {
			throw new InvalidExpression(this);
		}
		if(operandStack.size() > 1) throw new InvalidExpression(this);
		return operandStack.pop();
	}

	private double evaluatePostfix() throws InvalidExpression {
		if(this.type != ExpressionType.POSTFIX) return Double.NaN;
		Stack<Double> operandStack = new Stack<>();
		String[] tokens = tokenizeExpression(this.expression);
		try{
			for(String token: tokens) {
				if(isOperator(token.charAt(0))) {
					double b = operandStack.pop();
					double a = operandStack.pop();
					operandStack.push(evaluateOperator(a, b, token.charAt(0)));
				} else {
					double operand = Double.parseDouble(token);
					operandStack.push(operand);
				}
			}
		} catch(EmptyStackException|NumberFormatException ex) {
			throw new InvalidExpression(this);
		}
		if(operandStack.size() > 1) throw new InvalidExpression(this);
		return operandStack.pop();
	}

	public double evaluate() throws InvalidExpression {
		switch(this.type) {
			case INFIX: return this.evaluateInfix();
			case PREFIX: return this.evaluatePrefix();
			case POSTFIX: return this.evaluatePostfix();
			default: return Double.NaN;
		}
	} 

	private Expression infixToPrefix() throws InvalidExpression {
		if(this.type != ExpressionType.INFIX) return null;
		String[] tokens = tokenizeExpression(this.expression);
		StringBuilder prefixExpression = new StringBuilder();
		Stack<Character> operatorStack = new Stack<>();
		for(int i = tokens.length - 1; i >= 0; i--) {
			String token = tokens[i];
			try {
				if(isClosingBracket(token.charAt(0))) {
					operatorStack.push(token.charAt(0));
				} else if(isOpeningBracket(token.charAt(0))) {
					while(operatorStack.peek() != matchingBracket(token.charAt(0))) {
						prefixExpression.append(operatorStack.pop() + " ");
					}
					operatorStack.pop();
				} else if(isOperator(token.charAt(0))) {
					while(	
							!operatorStack.empty() && 
							operatorPrecedence(operatorStack.peek()) > operatorPrecedence(token.charAt(0))
						) {
						prefixExpression.append(operatorStack.pop() + " ");
					}
					operatorStack.push(token.charAt(0));
				} else {
					prefixExpression.append(token + " ");
				}
			} catch(EmptyStackException ex) {
				throw new InvalidExpression(this);
			}
		}
		while(!operatorStack.empty()) prefixExpression.append(operatorStack.pop() + " ");
		return new Expression(prefixExpression.reverse().toString(), ExpressionType.PREFIX);
	}

	private Expression infixToPostfix() throws InvalidExpression {
		if(this.type != ExpressionType.INFIX) return null;
		String[] tokens = tokenizeExpression(this.expression);
		StringBuilder postfixExpression = new StringBuilder();
		Stack<Character> operatorStack = new Stack<>();
		for(String token: tokens) {
			try {
				if(isOpeningBracket(token.charAt(0))) {
					operatorStack.push(token.charAt(0));
				} else if(isClosingBracket(token.charAt(0))) {
					while(operatorStack.peek() != matchingBracket(token.charAt(0))) {
						postfixExpression.append(operatorStack.pop() + " ");
					}
					operatorStack.pop();
				} else if(isOperator(token.charAt(0))) {
					while(	
							!operatorStack.empty() && 
							operatorPrecedence(operatorStack.peek()) >= operatorPrecedence(token.charAt(0))
						) {
						postfixExpression.append(operatorStack.pop() + " ");
					}
					operatorStack.push(token.charAt(0));
				} else {
					postfixExpression.append(token + " ");
				}
			} catch(EmptyStackException ex) {
				throw new InvalidExpression(this);
			}
		}
		while(!operatorStack.empty()) postfixExpression.append(operatorStack.pop() + " ");
		return new Expression(postfixExpression.toString().trim(), ExpressionType.POSTFIX);
	}

	private Expression prefixToInfix() throws InvalidExpression {
		if(this.type != ExpressionType.PREFIX) return null;
		Expression revPostfix = new Expression((new StringBuilder(this.expression)).reverse().toString(), ExpressionType.POSTFIX);
		Expression revInfix = revPostfix.postfixToInfix();
		String[] tokens = tokenizeExpression(revInfix.expression);
		StringBuilder infixExpression = new StringBuilder();
		for(int i = tokens.length - 1; i >= 0; i--) {
			if(isBracket(tokens[i].charAt(0))) infixExpression.append(matchingBracket(tokens[i].charAt(0)));
			else infixExpression.append(tokens[i]);
		}
		return new Expression(infixExpression.toString(), ExpressionType.INFIX);
	}

	private Expression prefixToPostfix() throws InvalidExpression {
		if(this.type != ExpressionType.PREFIX) return null;
		String[] tokens = tokenizeExpression(this.expression);
		Stack<String> operandStack = new Stack<>();
		for(int i = tokens.length - 1; i >= 0; i--) {
			String token = tokens[i];
			try {
				if(isOperator(token.charAt(0))) {
					String a = operandStack.pop();
					String b = operandStack.pop();
					operandStack.push(a + " " + b + " " + token.charAt(0));
				} else {
					operandStack.push(token);
				}
			} catch(EmptyStackException ex) {
				throw new InvalidExpression(this);
			}
		}
		if(operandStack.size() > 1) throw new InvalidExpression(this);
		return new Expression(operandStack.pop(), ExpressionType.POSTFIX);
	}

	private Expression postfixToInfix() throws InvalidExpression {
		if(this.type != ExpressionType.POSTFIX) return null;
		String[] tokens = tokenizeExpression(this.expression);
		Stack<String> operandStack = new Stack<>();
		Stack<Character> operatorStack = new Stack<>();
		char leftOperator, rightOperator;
		for(String token: tokens) {
			try{
				if(isOperator(token.charAt(0))) {
					char currentOperator = token.charAt(0);
					String b = operandStack.pop();
					String a = operandStack.pop();
					if(operatorStack.size() > 0) rightOperator = operatorStack.pop();
					else rightOperator = '`';
					if(operatorStack.size() > 0) leftOperator = operatorStack.pop();
					else leftOperator = '`';
					StringBuilder newOperand = new StringBuilder();
					if(operatorPrecedence(leftOperator) < operatorPrecedence(currentOperator))
						newOperand.append("( " + a + " ) " + currentOperator);
					else
						newOperand.append(a + " " + currentOperator);
					if(	
						operatorPrecedence(rightOperator) < operatorPrecedence(currentOperator) ||
						(operatorPrecedence(rightOperator) == operatorPrecedence(currentOperator) && (currentOperator == '/' || currentOperator == '-'))
					) newOperand.append(" ( " + b + " )");
					else newOperand.append(" " + b);
					operandStack.push(newOperand.toString());
					operatorStack.push(currentOperator);
				} else {
					operandStack.push(token);
					operatorStack.push('`');
				}
			} catch(EmptyStackException ex) {
				throw new InvalidExpression(this);
			}
		}
		if(operandStack.size() > 1) throw new InvalidExpression(this);
		return new Expression(operandStack.pop(), ExpressionType.INFIX);
	}

	private Expression postfixToPrefix() throws InvalidExpression {
		if(this.type != ExpressionType.POSTFIX) return null;
		String[] tokens = tokenizeExpression(this.expression);
		Stack<String> operandStack = new Stack<>();
		for(String token: tokens) {
			try {
				if(isOperator(token.charAt(0))) {
					String a = operandStack.pop();
					String b = operandStack.pop();
					operandStack.push(token.charAt(0) + " " + b + " " + a);
				} else {
					operandStack.push(token);
				}
			} catch(EmptyStackException ex) {
				throw new InvalidExpression(this);
			}
		}
		if(operandStack.size() > 1) throw new InvalidExpression(this);
		return new Expression(operandStack.pop(), ExpressionType.PREFIX);
	}

	public Expression convert(ExpressionType type) throws InvalidExpression {
		if(this.type == type) return this;
		if(this.type == ExpressionType.INFIX) {
			if(type == ExpressionType.PREFIX) return infixToPrefix();
			if(type == ExpressionType.POSTFIX) return infixToPostfix();
		} else if(this.type == ExpressionType.PREFIX) {
			if(type == ExpressionType.INFIX) return prefixToInfix();
			if(type == ExpressionType.POSTFIX) return prefixToPostfix();
		} else if(this.type == ExpressionType.POSTFIX) {
			if(type == ExpressionType.INFIX) return postfixToInfix();
			if(type == ExpressionType.PREFIX) return postfixToPrefix();
		}
		return new Expression();
	}

	public String getExpression() {
		return this.expression;
	}

	public ExpressionType getExpressionType() {
		return this.type;
	}

	@Override
	public String toString() {
		switch(this.type) {
			case EMPTY: return "empty expression";
			case INFIX: return "infix expression: " + this.expression;
			case PREFIX: return "prefix expression: " + this.expression;
			case POSTFIX: return "postfix expression: " + this.expression;
			default: return "invalid expression";
		}
	}
}