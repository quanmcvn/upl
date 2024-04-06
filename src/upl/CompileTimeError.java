package upl;

import upl.lexer.Token;

public class CompileTimeError extends RuntimeException {
	public final Token token;
	
	public CompileTimeError(Token token, String message) {
		Main.compileError(token, message);
		this.token = token;
	}
}
