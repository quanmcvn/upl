package upl.parser.expression;

import upl.lexer.Token;

public class Literal extends Expression {
	public final Object value;
	public Literal (Object value) {
		this.value = value;
	}
	@Override
	public <R> R accept(Visitor<R> visitor) {
		return visitor.visitLiteral(this);
	}
}
