package upl.parser.expression;

import upl.lexer.Token;

public class UnaryExpression extends Expression {
	public final Token operator;
	public final Expression expression;
	public UnaryExpression (Token operator, Expression expression) {
		this.operator = operator;
		this.expression = expression;
	}
	@Override
	public <R> R accept(Visitor<R> visitor) {
		return visitor.visitUnaryExpression(this);
	}
}
