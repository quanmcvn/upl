package upl.parser.expression;

import upl.lexer.Token;

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
