package upl.parser;

import upl.CompileTimeError;
import upl.lexer.Token;
import upl.lexer.TokenType;
import upl.parser.general.expression.*;
import upl.parser.general.statement.*;

import java.util.ArrayList;
import java.util.List;

import static upl.lexer.TokenType.*;

public class Parser {
	private final List<Token> tokens;
	private Environment environment = new Environment();
	private int current = 0;
	public Parser(List<Token> tokens) {
		this.tokens = tokens;
	}
	
	public Environment getEnvironment() {
		return environment;
	}
	
	public Statements parse() {
		try {
			consume(BEGIN, "expected a 'begin'");
			List<Statement> statements = new ArrayList<>();
			while (!check(END) && !isAtEnd()) {
				statements.add(statement());
			}
			consume(END, "expected an 'end'");
			return new Statements(statements);
		} catch (CompileTimeError e) {
			return null;
		}
	}
	
	private Statements block() {
		consume(LEFT_BRACE, "expected a '{' at the begin of a block");
		environment = new Environment(environment);
		List<Statement> statements = new ArrayList<>();
		while (!check(RIGHT_BRACE) && !isAtEnd()) {
			statements.add(statement());
		}
		consume(RIGHT_BRACE, "expected a '}' after block.");
		environment = environment.enclosing;
		return new Statements(statements);
	}
	
	private Statement statement() {
		try {
			if (check(IF)) return ifThenElseStatement();
			if (check(DO)) return doWhileStatement();
			if (check(PRINT)) return printStatement();
			if (check(INT) || check(BOOL)) return declarationStatement();
			if (check(IDENTIFIER)) return assignmentStatement();
			throw error(peek(), "expected a statement");
		} catch (CompileTimeError e) {
			skipUntilNextStatement();
			return null;
		}
		
//		return null;
	}
	
	private IfThenElse ifThenElseStatement() {
		consume(IF, "expected a 'if'"); // never happen, just for extra safety
//		consume(LEFT_PAREN, "Expected '(' after 'if'");
		Expression expression = expression();
//		consume(RIGHT_PAREN, "Expected ')' after if condition");
		
		consume(THEN, "expected a 'then' after if condition");
		
		Statements thenBranch = block();
		Statements elseBranch = null;
		if (match(ELSE)) {
			elseBranch = block();
		}
		
		return new IfThenElse(expression, thenBranch, elseBranch);
	}
	
	private DoWhile doWhileStatement() {
		consume(DO, "expected 'do'"); // never happen, just for extra safety
		Statements statements = block();
		consume(WHILE, "expected a 'while' after do {}");
		consume(LEFT_PAREN, "expected a '(' after 'while'");
		Expression expression = expression();
		consume(RIGHT_PAREN, "expected a ')' after while condition");
		consume(SEMICOLON, "expected a ';' after while ()");
		
		return new DoWhile(statements, expression);
	}
	
	private Print printStatement() {
		consume(PRINT, "expected a 'print'"); // never happen, just for extra safety
		consume(LEFT_PAREN, "expected a '(' after 'print'");
		Expression expression = expression();
		consume(RIGHT_PAREN, "expected a ')' after print expression");
		consume(SEMICOLON, "expected a ';' after print ()");
		
		return new Print(expression);
	}
	
	private Declaration declarationStatement() {
		Token type = advance();
		Token identifier = consume(IDENTIFIER, "expected an identifier (declaration)");
		
		Expression initializer = null;
		if (match(EQUAL)) {
			initializer = expression();
		}
		
		consume(SEMICOLON, "expected a ';'");
		
		Variable variable = new Variable(type, identifier);
		
		environment.define(identifier, new Environment.EnvironmentEntry(type));
		
		return new Declaration(variable, initializer);
	}
	
	private Assignment assignmentStatement() {
		Token identifier = consume(IDENTIFIER, "expected an identifier (assignment)");
		Variable variable = new Variable(environment.get(identifier).type, identifier);
		consume(EQUAL, "expected a '='");
		Expression expression = expression();
		consume(SEMICOLON, "expected a ';'");
		
		return new Assignment(variable, expression);
	}
	
	public Expression parseExpression() {
		try {
			return expression();
		} catch (CompileTimeError error) {
			return null;
		}
	}
	
	private Expression expression() {
		return equality();
	}
	
	private Expression equality() {
		Expression expression = relational();
		
		while (match(EQUAL_EQUAL)) {
			Token operator = previous();
			Expression right = relational();
			expression = new BinaryExpression(expression, operator, right);
		}
		
		return expression;
	}
	
	private Expression relational() {
		Expression expression = additive();
		
		while (match(GREATER, GREATER_EQUAL)) {
			Token operator = previous();
			Expression right = additive();
			expression = new BinaryExpression(expression, operator, right);
		}
		
		return expression;
	}
	
	private Expression additive() {
		Expression expression = multiplicative();
		
		while (match(PLUS)) {
			Token operator = previous();
			Expression right = multiplicative();
			expression = new BinaryExpression(expression, operator, right);
		}
		
		return expression;
	}
	
	private Expression multiplicative() {
		Expression expression = primary();
		
		while (match(STAR)) {
			Token operator = previous();
			Expression right = primary();
			expression = new BinaryExpression(expression, operator, right);
		}
		
		return expression;
	}
	
	private Expression primary() {
		if (match(NUMBER)) {
			return new Literal(previous().value);
		}
		
		if (match(IDENTIFIER)) {
			return new Variable(environment.get(previous()).type, previous());
		}
		
		if (match(LEFT_PAREN)) {
			Expression expression = expression();
			consume(RIGHT_PAREN, "expect a ')' after expression.");
			return new Grouping(expression);
		}
		
		throw error(peek(), "expect an expression.");
	}
	
	private boolean match(TokenType... types) {
		for (TokenType type : types) {
			if (check(type)) {
				advance();
				return true;
			}
		}
		
		return false;
	}
	private Token consume(TokenType type, String message) {
		if (check(type)) return advance();
		
		throw error(peek(), message);
	}
	private boolean check(TokenType type) {
		if (isAtEnd()) return false;
		return peek().type == type;
	}
	private Token advance() {
		if (!isAtEnd()) current++;
		return previous();
	}
	private boolean isAtEnd() {
		return peek().type == EOF;
	}
	
	private Token peek() {
		return tokens.get(current);
	}
	
	private Token previous() {
		return tokens.get(current - 1);
	}
	
	private CompileTimeError error(Token token, String message) {
		return new CompileTimeError(token, message);
	}
	
	private void skipUntilNextStatement() {
		while (!isAtEnd()) {
			if (peek().type == SEMICOLON) {
				advance();
				return;
			}
			
			switch (peek().type) {
				case INT:
				case BOOL:
				case IF:
				case DO:
				case WHILE:
				case PRINT:
				case END:
					return;
			}
			
			advance();
		}
	}
}
