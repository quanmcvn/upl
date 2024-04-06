package upl.parser.statement;

import java.util.List;
import upl.parser.expression.Expression;
import upl.parser.expression.Variable;

public class DoWhile extends Statement {
	public final Statements body;
	public final Expression expression;
	public DoWhile (Statements body, Expression expression) {
		this.body = body;
		this.expression = expression;
	}
	@Override
	public <R> R accept(Visitor<R> visitor) {
		return visitor.visitDoWhile(this);
	}
}
