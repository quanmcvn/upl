package upl.lexer;

enum TokenType {
	// Single-character tokens.
	LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
	PLUS, SEMICOLON, STAR,
	
	// One or two character tokens.
	EQUAL, EQUAL_EQUAL,
	GREATER, GREATER_EQUAL,
	
	// Literals.
	IDENTIFIER, NUMBER,
	
	// Keywords.
	BEGIN, END, IF, THEN, ELSE, DO, WHILE, PRINT,
	INT, BOOL,
}
