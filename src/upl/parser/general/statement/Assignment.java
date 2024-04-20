package upl.parser.general.statement;

import upl.parser.general.expression.Expression;
import upl.parser.general.expression.Variable;

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
