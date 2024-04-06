package upl.parser.statement;

import java.util.List;
import upl.parser.expression.Expression;
import upl.parser.expression.Variable;

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
