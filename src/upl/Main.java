package upl;

import java.io.*;
import java.util.List;

import upl.lexer.Lexer;
import upl.lexer.Token;
import upl.parser.Parser;
import upl.parser.parser.automatic.CupParserWrapper;
import upl.parser.parser.manual.bottomup.BottomUpParser;
import upl.parser.parser.manual.topdown.TopDownParser;
import upl.parser.general.statement.Statements;
import upl.parser.visualize.TextBox;

public class Main{
	private static String filename = null;
	public static String getFilename() { return filename; }
	static boolean hasCompileError = false;
	public static void error(int line, int column, String message) {
		System.err.printf("Error: %s at %d:%d\n", message, line, column);
	}
	public static void error(int line, String where, String message) {
		System.err.printf("[line %d] Error %s : %s\n", line, where, message);
	}
	public static void error(int line, int column, String where, String message) {
		System.err.printf("[%d:%d] Error %s : %s\n", line, column, where, message);
	}
	public static void compileError(Token token, String message) {
		error(token.line, token.column, String.format("at '%s'", token.lexeme), message);
		hasCompileError = true;
	}
	
	public static  <T extends Comparable<T>> int compareList(List<T> lhs, List<T> rhs) {
		if (lhs == null && rhs == null) return 0;
		if (lhs == null) return -1;
		if (rhs == null) return 1;
		if (lhs.size() != rhs.size()) return Integer.compare(lhs.size(), rhs.size());
		for (int i = 0; i < lhs.size(); i++) {
			T obj1 = lhs.get(i);
			T obj2 = rhs.get(i);
			if (obj1 == null && obj2 == null) continue;
			if (obj1 == null) return -1;
			if (obj2 == null) return 1;
			int cmp = obj1.compareTo(obj2);
			if (cmp != 0) return cmp;
		}
		return 0;
	}
	public static void main(String[] args) throws Exception {
		boolean jflex = false;
		boolean colorful = false;
		boolean cup = false;
		boolean bottomup = false;
		for (String arg : args) {
			if (arg.startsWith("--")) {
				String[] part = arg.substring(2).split("=");
				if (part[0].equals("jflex")) {
					jflex = Boolean.parseBoolean(part[1]);
					continue;
				}
				if (part[0].equals("colorful")) {
					colorful = Boolean.parseBoolean(part[1]);
					continue;
				}
				if (part[0].equals("cup")) {
					cup = Boolean.parseBoolean(part[1]);
					continue;
				}
				if (part[0].equals("bottom-up")) {
					bottomup = Boolean.parseBoolean(part[1]);
					continue;
				}
			} else {
				filename = arg;
			}
		}
		if (filename == null) {
			System.err.println("Missing input file.");
			System.exit(1);
		}
		
		TextBox.setEnableVT100Mode(colorful);
		
		Lexer lexer = new Lexer(new InputStreamReader(new java.io.FileInputStream(filename)), jflex);
		
		List<Token> tokens = lexer.getTokens();
		
//		for (Token token : tokens) {
//			System.out.println(token);
//		}
		
		Parser parser;
		if (cup) {
			parser = new CupParserWrapper(tokens);
		} else if (bottomup) {
			parser = new BottomUpParser(tokens);
		} else {
			parser = new TopDownParser(tokens);
		}
		
		Statements statements = parser.parse();
		
		if (hasCompileError) {
			System.exit(1);
		}
		
//		Environment environment = parser.getEnvironment();
		
		System.out.println(new TextBox().print(statements));
		//
//		int status=1;
//
//		do {
//			status=s.yylex();
//		} while(status==0);
		
		//s.yylex();
}
	
}