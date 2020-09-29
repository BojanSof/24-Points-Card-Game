package expression;

@SuppressWarnings("serial")
public class InvalidExpression extends Exception {
	
	private Expression expression;

	public InvalidExpression(Expression expression) {
		super("Invalid expression: " + expression);
		this.expression = expression;
	}

	public Expression getExpression() {
		return this.expression;
	}
}