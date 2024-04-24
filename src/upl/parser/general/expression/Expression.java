package upl.parser.general.expression;

import upl.lexer.Token;
import upl.lexer.Location;

public abstract class Expression {
	public interface Visitor<R> {
		R visitBinaryExpression(BinaryExpression expression);
		R visitUnaryExpression(UnaryExpression expression);
		R visitGrouping(Grouping expression);
		R visitLiteral(Literal expression);
		R visitVariable(Variable expression);
	}
	public abstract <R> R accept(Visitor<R> visitor);
}
