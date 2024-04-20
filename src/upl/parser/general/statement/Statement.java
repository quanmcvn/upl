package upl.parser.general.statement;

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
