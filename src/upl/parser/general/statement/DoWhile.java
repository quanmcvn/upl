package upl.parser.general.statement;

import java.util.List;
import upl.lexer.Token;
import upl.parser.general.expression.Expression;
import upl.parser.general.expression.Variable;

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
