package upl.parser.grammar;

import upl.lexer.Token;
import upl.lexer.TokenType;

import java.util.*;

public class GrammarBuilderHelper {
	private final List<NonTerminal> nonTerminalList = new ArrayList<>();
	private final List<Terminal> terminalList = new ArrayList<>();
	private final List<Production> productionList = new ArrayList<>();
	private final Map<String, NonTerminal> toNonTerminal = new TreeMap<>();
	private final Map<String, Terminal> toTerminal = new TreeMap<>();
	private NonTerminal getNonTerminal(String name) {
		return toNonTerminal.get(name);
	}
	private Terminal getTerminal(String name) {
		return toTerminal.get(name);
	}
	private boolean hasNonTerminal(String name) {
		return toNonTerminal.containsKey(name);
	}
	private boolean hasTerminal(String name) {
		return toTerminal.containsKey(name);
	}
	public void defineNonTerminal(String name) {
		if (!hasNonTerminal(name)) {
			NonTerminal newNonTerminal = new NonTerminal(name);
			nonTerminalList.add(newNonTerminal);
			toNonTerminal.put(name, newNonTerminal);
		}
	}
	public void defineTerminal(String name) {
		if (!hasTerminal(name)) {
			Terminal newTerminal;
			if (name.equals("ε")) {
				newTerminal = Grammar.epsilon;
			} else {
				newTerminal = new Terminal(name);
			}
			terminalList.add(newTerminal);
			toTerminal.put(name, newTerminal);
		}
	}
	public void defineTerminalFromTokenType(TokenType tokenType, String name) {
		if (!hasTerminal(name)) {
			Terminal newTerminal;
			if (name.equals("ε")) {
				newTerminal = Grammar.epsilon;
			} else {
				newTerminal = new Terminal(new Token(tokenType, tokenType.name()));
			}
			terminalList.add(newTerminal);
			toTerminal.put(name, newTerminal);
		}
	}
	/**
	 *  Add production
	 *  <br>
	 *  eg, A -> Ab then call addProduction("A", "A b");
	 *  <br>
	 *  Tries to make sense by guessing "A" is a non-terminal first, then terminal
	 *  <br>
	 *  If both wrong then error ig
	 * @param left is a non-terminal
	 * @param right is a string of non-terminal and terminal
	 * @param nonTerminalValueReducer is a reducer, more in Production.NonTerminalReducer
	 */
	public void defineProduction(String left, String right, Production.NonTerminalValueReducer nonTerminalValueReducer) {
		String[] parts = right.split("\\s+");
		if (!hasNonTerminal(left)) {
			throw new RuntimeException(String.format("left of production: ??? %s is ???\n", left));
		}
		List<Symbol> symbols = new ArrayList<>();
		for (String name : parts) {
			if (hasNonTerminal(name)) {
				symbols.add(getNonTerminal(name));
				continue;
			}
			if (hasTerminal(name)) {
				symbols.add(getTerminal(name));
				continue;
			}
			throw new RuntimeException(String.format("right of production: ??? %s is ???\n", name));
		}
		productionList.add(new Production(getNonTerminal(left), symbols, nonTerminalValueReducer));
	}
	
	/**
	 * Add production in the form of "A -> B d"
	 */
	public void defineProduction(String production) {
		String[] parts = production.split("\\s*->\\s*");
		defineProduction(parts[0], parts[1], Production.nullReducer);
	}
	
	/**
	 * Add production in the form of "A -> B d"
	 */
	public void defineProduction(String production, Production.NonTerminalValueReducer nonTerminalValueReducer) {
		String[] parts = production.split("\\s*->\\s*");
		defineProduction(parts[0], parts[1], nonTerminalValueReducer);
	}
	
	public Grammar getGrammar(String start) {
		// eof is defined implicitly
		defineTerminalFromTokenType(Grammar.eof.token.getType(), "$");
		return new Grammar(getNonTerminal(start), nonTerminalList, terminalList, productionList);
	}
}
