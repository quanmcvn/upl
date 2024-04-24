package upl.lexer;

public class Token {
	private final TokenType type;
	private final String lexeme;
	private final Object value;
	private final Location location;
	public Token(TokenType type, String lexeme, Object value, int line, int column) {
		this.type = type;
		this.lexeme = lexeme;
		this.value = value;
		this.location = new Location(line, column);
	}
	public Token(TokenType type, String lexeme, int line, int column) {
		this.type = type;
		this.lexeme = lexeme;
		this.value = null;
		this.location = new Location(line, column);
	}
	public Token(TokenType type, String lexeme, Object value) {
		this.type = type;
		this.lexeme = lexeme;
		this.value = value;
		this.location = new Location(0, 0);
	}
	public Token(TokenType type, String lexeme) {
		this.type = type;
		this.lexeme = lexeme;
		this.value = null;
		this.location = new Location(0, 0);
	}
	public TokenType getType() {
		return type;
	}
	public String getLexeme() {
		return lexeme;
	}
	
	public Object getValue() {
		return value;
	}
	public int getLine() {
		return location.line();
	}
	public int getColumn() {
		return location.column();
	}
	public Location getLocation() {
		return location;
	}
	public String toString() {
		return String.format("<%s, '%s'>", type, lexeme);
	}
}
