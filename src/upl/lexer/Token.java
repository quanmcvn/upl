package upl.lexer;

public class Token {
	public final TokenType type;
	public final String lexeme;
	public final Object value;
	public final int line;
	public final int column;
	public Token(TokenType type, String lexeme, Object value, int line, int column) {
		this.type = type;
		this.lexeme = lexeme;
		this.value = value;
		this.line = line;
		this.column = column;
	}
	public Token(TokenType type, String lexeme, int line, int column) {
		this.type = type;
		this.lexeme = lexeme;
		this.value = null;
		this.line = line;
		this.column = column;
	}
	public Token(TokenType type, String lexeme, Object value) {
		this.type = type;
		this.lexeme = lexeme;
		this.value = value;
		this.line = 0;
		this.column = 0;
		
	}
	public Token(TokenType type, String lexeme) {
		this.type = type;
		this.lexeme = lexeme;
		this.value = null;
		this.line = 0;
		this.column = 0;
		
	}
	
	public String toString() {
		return String.format("<%s, '%s'>", type, lexeme);
	}
}
