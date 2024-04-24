package upl.parser.general.statement;

import java.util.List;
import upl.lexer.Token;
import upl.parser.general.expression.Expression;
import upl.parser.general.expression.Variable;

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
