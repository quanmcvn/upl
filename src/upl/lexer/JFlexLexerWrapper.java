package upl.lexer;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class JFlexLexerWrapper implements ILexer {
	JFlexLexer jFlexLexer;
	
	public JFlexLexerWrapper(Reader reader) {
		jFlexLexer = new JFlexLexer(reader);
	}
	@Override
	public List<Token> scanTokens() {
		int status=1;
		do {
			try {
				status=jFlexLexer.yylex();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} while(status==0);
		return new ArrayList<>();
	}
}
