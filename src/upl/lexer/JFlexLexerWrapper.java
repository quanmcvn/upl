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
		List<Token> tokenList = new ArrayList<>();
		while (true) {
			try {
				Token lastToken = jFlexLexer.yylex();
				if (lastToken == null) continue;
				tokenList.add(lastToken);
				if (lastToken.type == TokenType.EOF) break;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return tokenList;
	}
}
