package upl.parser.general.expression;

import upl.lexer.Token;
import upl.lexer.Location;

public class Variable extends Expression {
	public final Token type;
	public final Token identifier;
	public Variable (Token type, Token identifier) {
		this.type = type;
		this.identifier = identifier;
	}
	@Override
	public <R> R accept(Visitor<R> visitor) {
		return visitor.visitVariable(this);
	}
}
