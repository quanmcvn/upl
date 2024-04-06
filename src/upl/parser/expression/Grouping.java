package upl.parser.expression;

import upl.lexer.Token;

public class Grouping extends Expression {
	public final Expression expression;
	public Grouping (Expression expression) {
		this.expression = expression;
	}
	@Override
	public <R> R accept(Visitor<R> visitor) {
		return visitor.visitGrouping(this);
	}
}
