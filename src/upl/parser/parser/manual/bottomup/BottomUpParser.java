package upl.parser.parser.manual.bottomup;

import upl.lexer.Token;
import upl.lexer.TokenType;
import upl.parser.context.Environment;
import upl.parser.Parser;
import upl.parser.general.statement.Statements;
import upl.parser.grammar.Grammar;
import upl.parser.grammar.GrammarBuilderHelper;
import upl.parser.grammar.Production;
import upl.parser.grammar.Terminal;
import upl.parser.grammar.lr0.Action;
import upl.parser.grammar.lr0.ActionType;
import upl.parser.grammar.lr0.SLRParsingTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class BottomUpParser implements Parser {
	private static final Grammar grammar;
	private static final SLRParsingTable table;
	static {
		// hardcode the grammar
		GrammarBuilderHelper helper = new GrammarBuilderHelper();
		helper.defineNonTerminal("E");
		helper.defineNonTerminal("T");
		helper.defineNonTerminal("F");
		helper.defineTerminalFromTokenType(TokenType.IDENTIFIER,"id");
		helper.defineTerminalFromTokenType(TokenType.PLUS,"+");
		helper.defineTerminalFromTokenType(TokenType.STAR,"*");
		
		helper.addProduction("E -> E + T");
		helper.addProduction("E -> T");
		helper.addProduction("T -> T * F");
		helper.addProduction("T -> F");
		helper.addProduction("F -> id");
		
		grammar = helper.getGrammar("E").getAugmentedGrammar();
		grammar.calculateFirstAndFollow();
		table = new SLRParsingTable(grammar);
		System.out.println(table);
	}
//	private Environment environment = new Environment();
	@Override
	public Environment getEnvironment() {
//		return environment;
		return null;
	}
	
	@Override
	public Statements parse() {
		return null;
	}
	
	private final List<Token> tokenList;
	
	private void doParse() {
		Stack<Integer> stack = new Stack<>();
		Stack<Token> input = new Stack<>();
		for (int i = tokenList.size() - 1; i >= 0; -- i) {
			input.add(tokenList.get(i));
		}
		
		stack.add(table.initState);
		while (true) {
			if (stack.isEmpty()) {
				throw Parser.error(input.peek(), "stack empty?");
			}
			if (input.empty()) {
				throw Parser.error(input.peek(), "input empty?");
			}
			Terminal terminal = new Terminal(input.peek());
			int state = stack.peek();
			Action action = table.action.get(state).get(terminal);
			if (action == null) {
				throw Parser.error(input.peek(), String.format("unexpected '%s'", input.peek()));
			}
			if (action.actionType() == ActionType.SHIFT) {
				int j = action.number();
				stack.add(j);
				input.pop();
				System.out.println("shift");
				continue;
			}
			if (action.actionType() == ActionType.REDUCE) {
				int j = action.number();
				Production production = grammar.productionList.get(j);
				for (int k = 0; k < production.right.size(); ++ k) {
					stack.pop();
				}
				int t = stack.peek();
				stack.push(table.go.get(t).get(production.left));
				System.out.printf("reduce by %s\n", production);
				continue;
			}
			if (action.actionType() == ActionType.ACCEPT) {
				System.out.println("accept!!!");
				break;
			}
			throw new RuntimeException(String.format("?? state %s", action.actionType()));
		}
	}
	
	public BottomUpParser(List<Token> tokenList) {
		this.tokenList = new ArrayList<>();
		this.tokenList.addAll(tokenList);
		if (this.tokenList.get(this.tokenList.size() - 1).type != TokenType.EOF) {
			this.tokenList.add(Grammar.eof.token);
		}
	}
	
	public static void main(String[] args) {
		List<Token> tokens = Arrays.asList(
				new Token(TokenType.IDENTIFIER, "a"),
				new Token(TokenType.PLUS, "+"),
				new Token(TokenType.IDENTIFIER, "b"),
				new Token(TokenType.STAR, "*"),
				new Token(TokenType.IDENTIFIER, "a")
		);
		
		BottomUpParser bottomUpParser = new BottomUpParser(tokens);
		bottomUpParser.doParse();
	}
}
