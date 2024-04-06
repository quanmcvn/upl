package upl;

import java.io.*;
import java.util.List;

import upl.lexer.Lexer;
import upl.lexer.Token;
import upl.parser.ASTPrinter;
import upl.parser.Environment;
import upl.parser.Parser;
import upl.parser.statement.Statements;

public class Main{
	static boolean hasCompileError = false;
	public static void error(int line, int column, String message) {
		System.err.printf("Error: %s at %d:%d\n", message, line, column);
	}
	public static void error(int line, String where, String message) {
		System.err.printf("[line %d] Error %s : %s\n", line, where, message);
	}
	public static void compileError(Token token, String message) {
		error(token.line, String.format("at '%s'", token.lexeme), message);
		hasCompileError = true;
	}
	
	public static void main(String[] args) throws Exception{
		Lexer lexer;
		if (args.length == 1) {
			lexer = new Lexer(new InputStreamReader(new java.io.FileInputStream(args[0])), true);
		} else {
			lexer = new Lexer(new InputStreamReader(new java.io.FileInputStream(args[0])), Boolean.valueOf(args[1]));
		}
		
		List<Token> tokens = lexer.getTokens();
		
		Parser parser = new Parser(tokens);
		
		Statements statements = parser.parse();
		
		if (hasCompileError) {
			System.exit(1);
		}
		
		Environment environment = parser.getEnvironment();
		
		System.out.println(new ASTPrinter().print(statements));
		//
//		int status=1;
//
//		do {
//			status=s.yylex();
//		} while(status==0);
		
		//s.yylex();
}
	
}