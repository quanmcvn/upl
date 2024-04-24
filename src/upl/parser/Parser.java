package upl.parser;

import upl.CompileTimeError;
import upl.lexer.Location;
import upl.lexer.Token;
import upl.parser.context.Environment;
import upl.parser.general.statement.Statements;

public interface Parser {
	String magicKeyword = "NOT CHECKED!!!";
	Environment getEnvironment();
	
	Statements parse();
	
	static CompileTimeError error(Token token, String message) {
		return new CompileTimeError(token, message);
	}
	static CompileTimeError error(Location location, String message) {
		return new CompileTimeError(location, message);
	}
}
