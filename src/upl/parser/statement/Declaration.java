package upl.parser.statement;

import upl.parser.expression.Expression;
import upl.parser.expression.Variable;

public class Declaration extends Statement {
	public final Variable variable;
	public final Expression initializer;
	public Declaration (Variable variable, Expression initializer) {
		this.variable = variable;
		this.initializer = initializer;
	}
	@Override
	public <R> R accept(Visitor<R> visitor) {
		return visitor.visitDeclaration(this);
	}
}
