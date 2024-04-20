package upl.parser.general.statement;

import upl.parser.general.expression.Expression;

public class Print extends Statement {
	public final Expression expression;
	public Print (Expression expression) {
		this.expression = expression;
	}
	@Override
	public <R> R accept(Visitor<R> visitor) {
		return visitor.visitPrint(this);
	}
}
