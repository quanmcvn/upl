package upl.parser.statement;

import java.util.List;
import upl.parser.expression.Expression;
import upl.parser.expression.Variable;

public class IfThenElse extends Statement {
	public final Expression condition;
	public final Statements thenBranch;
	public final Statements elseBranch;
	public IfThenElse (Expression condition, Statements thenBranch, Statements elseBranch) {
		this.condition = condition;
		this.thenBranch = thenBranch;
		this.elseBranch = elseBranch;
	}
	@Override
	public <R> R accept(Visitor<R> visitor) {
		return visitor.visitIfThenElse(this);
	}
}
