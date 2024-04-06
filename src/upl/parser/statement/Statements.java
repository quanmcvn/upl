package upl.parser.statement;

import java.util.List;
import upl.parser.expression.Expression;
import upl.parser.expression.Variable;

public class Statements extends Statement {
	public final List<Statement> statements;
	public Statements (List<Statement> statements) {
		this.statements = statements;
	}
	@Override
	public <R> R accept(Visitor<R> visitor) {
		return visitor.visitStatements(this);
	}
}
