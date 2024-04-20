package upl.parser.general.statement;

import upl.parser.general.expression.Expression;

public class DoWhile extends Statement {
	public final Statements body;
	public final Expression condition;
	public DoWhile (Statements body, Expression condition) {
		this.body = body;
		this.condition = condition;
	}
	@Override
	public <R> R accept(Visitor<R> visitor) {
		return visitor.visitDoWhile(this);
	}
}
