package upl.parser.general.expression;

import upl.lexer.Token;
import upl.lexer.Location;

public class Literal extends Expression {
	public final Object value;
	public final Location location;
	public Literal (Object value, Location location) {
		this.value = value;
		this.location = location;
	}
	@Override
	public <R> R accept(Visitor<R> visitor) {
		return visitor.visitLiteral(this);
	}
}
