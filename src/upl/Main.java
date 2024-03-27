package upl;

import java.io.*;
import upl.lexer.Lexer;
import upl.lexer.Token;

public class Main{
	public static void error(int line, int column, String message) {
		System.err.printf("Error: %s at %d:%d\n", message, line, column);
	}
	
	public static void main(String[] args) throws Exception{
		Lexer lexer = new Lexer(new InputStreamReader(new java.io.FileInputStream(args[0])), Boolean.valueOf(args[1]));

		for (Token token : lexer.getTokens()) {
			System.out.println(token);
		}
		//
//		int status=1;
//
//		do {
//			status=s.yylex();
//		} while(status==0);
		
		//s.yylex();
}
}