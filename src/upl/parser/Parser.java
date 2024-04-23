package upl.parser;

import upl.CompileTimeError;
import upl.lexer.Token;
import upl.parser.context.Environment;
import upl.parser.general.statement.Statements;

public interface Parser {
	Environment getEnvironment();
	
	Statements parse();
	
	static CompileTimeError error(Token token, String message) {
		return new CompileTimeError(token, message);
	}
}
