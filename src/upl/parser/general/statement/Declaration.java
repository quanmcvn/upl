package upl.parser.general.statement;

import upl.parser.general.expression.Expression;
import upl.parser.general.expression.Variable;

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
