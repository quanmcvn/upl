package upl.parser.parser.automatic;

import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.Scanner;
import java_cup.runtime.Symbol;
import upl.lexer.Token;
import upl.lexer.TokenType;

import java.util.Arrays;
import java.util.List;

/**
 * Act like a scanner, but actually not
 */
public class FakeScanner implements Scanner {
	private final String filename;
	private int current = 0;
	private final List<Token> tokens;
	private final MyComplexSymbolFactory sf = new MyComplexSymbolFactory();
	public FakeScanner(String filename, List<Token> tokens) {
		this.filename = filename;
		this.tokens = tokens;
	}
	@Override
	public java_cup.runtime.Symbol next_token() {
		if (current >= tokens.size()) {
			current -= 1; // don't know why but cup somehow needing 2 EOFs in a row, is my grammar broken
		}
		Token token = tokens.get(current++);
		
//		System.out.printf("feeding %s...\n", token);
		
		ComplexSymbolFactory.Location left = new ComplexSymbolFactory.Location(filename, token.getLine(), token.getColumn());
		ComplexSymbolFactory.Location right = new ComplexSymbolFactory.Location(filename, token.getLine(), token.getColumn() + token.getLexeme().length() - 1);
		
		int id = -1;
		for (int i = 0; i < CupParserSym.terminalNames.length; ++ i) {
			if (CupParserSym.terminalNames[i].equals(token.getType().name())) {
				id = i;
				break;
			}
		}
		
		if (token.getValue() != null) {
			return sf.newSymbol(token.getType().name(), id, left, right, token.getValue());
		} else if (token.getType() == TokenType.IDENTIFIER) {
			return sf.newSymbol(token.getType().name(), id, left, right, token.getLexeme());
		} else {
			return sf.newSymbol(token.getType().name(), id, left, right);
		}
	}
}
