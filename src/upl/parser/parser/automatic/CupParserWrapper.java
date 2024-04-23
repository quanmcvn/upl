package upl.parser.parser.automatic;

import upl.Main;
import upl.lexer.Lexer;
import upl.lexer.Token;
import upl.parser.context.ContextChecker;
import upl.parser.context.Environment;
import upl.parser.Parser;
import upl.parser.general.statement.Statements;
import upl.parser.visualize.TextBox;

import java.io.InputStreamReader;
import java.util.List;

public class CupParserWrapper implements Parser {
	public static final String magicKeyword = "NOT CHECKED!!!";
	private final List<Token> tokenList;
	private Environment environment = null;
	private Statements program = null;
	public CupParserWrapper(List<Token> tokenList) {
		this.tokenList = tokenList;
	}
	@Override
	public Environment getEnvironment() {
		return null;
	}
	
	@Override
	public Statements parse() {
		FakeScanner s = new FakeScanner(Main.getFilename(), tokenList);
		
		CupParser parser = new CupParser(s, new MyComplexSymbolFactory());
		
		try {
			parser.parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Statements program = parser.program;
		ContextChecker contextChecker = new ContextChecker(program);
		program = contextChecker.check();
		
		this.environment = contextChecker.getEnvironment();
		this.program = program;
		return program;
	}
	
	public static void main(String[] args) {
		try {
			String filename = "input.upl";
			Lexer lexer = new Lexer(new InputStreamReader(new java.io.FileInputStream(filename)), false);
			
			List<Token> tokens = lexer.getTokens();

			
			FakeScanner s = new FakeScanner(filename, tokens);
			
			CupParser parser = new CupParser(s, new MyComplexSymbolFactory());
			
			parser.parse();
			
			Statements program = parser.program;
			
			System.out.println("before:\n");
			System.out.println((new TextBox()).print(program));
			
			ContextChecker contextChecker = new ContextChecker(program);
			program = contextChecker.check();
			
			System.out.println("after:\n");
			System.out.println((new TextBox()).print(program));
			//parser.debug_parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
