package upl.parser.statement;

import upl.parser.expression.Expression;
import upl.parser.expression.Variable;

public class Assignment extends Statement {
	public final Variable variable;
	public final Expression expression;
	public Assignment (Variable variable, Expression expression) {
		this.variable = variable;
		this.expression = expression;
	}
	@Override
	public <R> R accept(Visitor<R> visitor) {
		return visitor.visitAssignment(this);
	}
}
