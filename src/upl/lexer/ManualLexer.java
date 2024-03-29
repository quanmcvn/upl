package upl.lexer;

import upl.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static upl.lexer.TokenType.*;

public class ManualLexer implements ILexer {
	private static final Map<String, TokenType> keywords;
	
	static {
		keywords = new HashMap<>();
		keywords.put("begin",  BEGIN);
		keywords.put("end",    END);
		keywords.put("if",     IF);
		keywords.put("then",   THEN);
		keywords.put("else",   ELSE);
		keywords.put("do",     DO);
		keywords.put("while",  WHILE);
		keywords.put("print",  PRINT);
		keywords.put("int",    INT);
		keywords.put("bool",   BOOL);
	}
	private final String source;
	private final List<Token> tokens = new ArrayList<>();
	private int start = 0;
	private int current = 0;
	private int line = 1;
	private int currentStartOfLine = 0;
	public ManualLexer(String source) {
		this.source = source;
	}
	
	private boolean isAtEnd() {
		return current >= source.length();
	}
	private char peek(int ahead) {
		if (current + ahead >= source.length()) return '\0';
		return source.charAt(current + ahead);
	}
	private char peek() {
		return peek(0);
	}
	
	private char consume() {
		if (peek() == '\n') {
			line++;
			currentStartOfLine = current + 1;
		}
		return source.charAt(current++);
	}
	
	private boolean match(char expected) {
		if (isAtEnd()) return false;
		if (source.charAt(current) != expected) return false;
		
		current++;
		return true;
	}
	
	private void addToken(TokenType type) {
		String text = source.substring(start, current);
		tokens.add(new Token(type, text, line));
	}
	private boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}
	
	private boolean isAlpha(char c) {
		return (c >= 'a' && c <= 'z') ||
				(c >= 'A' && c <= 'Z');
	}
	
	@Override
	public List<Token> scanTokens() {
		while (!isAtEnd()) {
			start = current;
			scanToken();
		}
		
		return tokens;
	}
	
	private void scanToken() {
		char c = consume();
		switch (c) {
			case '(': addToken(LEFT_PAREN); break;
			case ')': addToken(RIGHT_PAREN); break;
			case '{': addToken(LEFT_BRACE); break;
			case '}': addToken(RIGHT_BRACE); break;
			case '+': addToken(PLUS); break;
			case ';': addToken(SEMICOLON); break;
			case '*': addToken(STAR); break;
			case '/': {
				StringBuilder comment = new StringBuilder();
				if (match('/')) { // single line comment
					comment.append("//");
					while (!isAtEnd() && peek() != '\n') comment.append(consume());
					// intentionally not consume '\n'
				} else if (match('*')) { // multi line comment
					comment.append("/*");
					while (!isAtEnd() && !(peek(0) == '*' && peek(1) == '/')) comment.append(consume());
					// consume '*' and '/'
					comment.append(consume());
					comment.append(consume());
				} else {
					Main.error(line, current - currentStartOfLine,"Unexpected /");
					break;
				}
				System.out.printf("Got comment: %s\n", comment);
				break;
			}
			case '=':
				addToken(match('=') ? EQUAL_EQUAL : EQUAL);
				break;
			case '>':
				addToken(match('=') ? GREATER_EQUAL : GREATER);
				break;
			case ' ':
			case '\r':
			case '\t':
			case '\n':
				// Ignore whitespace.
				break;
			default:
				if (isDigit(c)) {
					number();
				} else if(isAlpha(c)) {
					identifier();
				} else {
					Main.error(line, current - currentStartOfLine, String.format("Unexpected %c", c));
				}
		}
	}
	
	private void number() {
		while (isDigit(peek())) consume();
		addToken(NUMBER);
	}
	
	private void identifier() {
		while (isAlpha(peek())) consume();
		while (isDigit(peek())) consume();
		String text = source.substring(start, current);
		TokenType type = keywords.get(text);
		if (type == null) type = IDENTIFIER;
		addToken(type);
	}
}
