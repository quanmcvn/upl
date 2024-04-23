package upl.lexer;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class Lexer {
	ILexer lexer;
	
	public Lexer(java.io.Reader reader, boolean isJflex) {
		if (isJflex) {
			lexer = new JFlexLexerWrapper(reader);
		} else {
			StringBuilder content = new StringBuilder();
	
			try (BufferedReader br = new BufferedReader(reader)) {
				String line;
				while ((line = br.readLine()) != null) {
					content.append(line).append("\n"); // Append the line to the content
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
	
			lexer = new ManualLexer(content.toString());
		}
	}
	
	public List<Token> getTokens() {
		return lexer.scanTokens();
	}
}
