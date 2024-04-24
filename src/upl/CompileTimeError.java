package upl;

import upl.lexer.Location;
import upl.lexer.Token;

public class CompileTimeError extends RuntimeException {
	public CompileTimeError(Token token, String message) {
		Main.compileError(token, message);
	}
	public CompileTimeError(Location location, String message) {
		Main.compileError(location, message);
	}
}
