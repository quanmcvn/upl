package upl.parser.statement;

import java.util.List;
import upl.parser.expression.Expression;
import upl.parser.expression.Variable;

public abstract class Statement {
	public interface Visitor<R> {
		R visitStatements(Statements statement);
		R visitIfThenElse(IfThenElse statement);
		R visitDoWhile(DoWhile statement);
		R visitPrint(Print statement);
		R visitDeclaration(Declaration statement);
		R visitAssignment(Assignment statement);
	}
	public abstract <R> R accept(Visitor<R> visitor);
}
