package upl.parser.parser.manual.bottomup;

import upl.CompileTimeError;
import upl.lexer.Lexer;
import upl.lexer.Token;
import upl.lexer.TokenType;
import upl.parser.context.ContextChecker;
import upl.parser.context.Environment;
import upl.parser.Parser;
import upl.parser.context.TypeChecker;
import upl.parser.general.expression.*;
import upl.parser.general.statement.*;
import upl.parser.grammar.*;
import upl.parser.grammar.lr0.Action;
import upl.parser.grammar.lr0.ActionType;
import upl.parser.grammar.lr0.SLRParsingTable;
import upl.parser.visualize.TextBox;

import javax.swing.*;
import java.io.InputStreamReader;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class BottomUpParser implements Parser {
	private static final Grammar grammar;
	private static final SLRParsingTable table;
	static {
		// hardcode the grammar
		GrammarBuilderHelper helper = new GrammarBuilderHelper();
		helper.defineNonTerminal("Program");
		helper.defineNonTerminal("Statements");
		helper.defineNonTerminal("Statement");
		helper.defineNonTerminal("IfThenElseStatement");
		helper.defineNonTerminal("DoWhileStatement");
		helper.defineNonTerminal("PrintStatement");
		helper.defineNonTerminal("DeclarationStatement");
		helper.defineNonTerminal("AssignmentStatement");
		helper.defineNonTerminal("MaybeElse"); // more trolling
		helper.defineNonTerminal("TypeSpecifier");
		helper.defineNonTerminal("InitDeclarator"); // a little bit of trolling
		helper.defineNonTerminal("Expression");
		helper.defineNonTerminal("EqualityExpression");
		helper.defineNonTerminal("RelationalExpression");
		helper.defineNonTerminal("AdditiveExpression");
		helper.defineNonTerminal("MultiplicativeExpression");
		helper.defineNonTerminal("PrimaryExpression");
		helper.defineNonTerminal("Identifier");
		helper.defineTerminal("EOF");
		helper.defineTerminal("LEFT_PAREN");
		helper.defineTerminal("RIGHT_PAREN");
		helper.defineTerminal("LEFT_BRACE");
		helper.defineTerminal("RIGHT_BRACE");
		helper.defineTerminal("PLUS");
		helper.defineTerminal("SEMICOLON");
		helper.defineTerminal("STAR");
		helper.defineTerminal("EQUAL");
		helper.defineTerminal("EQUAL_EQUAL");
		helper.defineTerminal("GREATER");
		helper.defineTerminal("GREATER_EQUAL");
		helper.defineTerminal("IDENTIFIER");
		helper.defineTerminal("NUMBER");
		helper.defineTerminal("BEGIN");
		helper.defineTerminal("END");
		helper.defineTerminal("IF");
		helper.defineTerminal("THEN");
		helper.defineTerminal("ELSE");
		helper.defineTerminal("DO");
		helper.defineTerminal("WHILE");
		helper.defineTerminal("PRINT");
		helper.defineTerminal("INT");
		helper.defineTerminal("BOOL");
		helper.defineTerminal("ε");
		helper.defineProduction("Program -> BEGIN Statements END", (symbolValues -> {
			System.out.println((new TextBox()).print((Statements) symbolValues[1])) ;
			return symbolValues[1];
		}));
		// manually eliminate ε
		helper.defineProduction("Program -> BEGIN END", (symbolValues -> new Statements(new ArrayList<>())));
		helper.defineProduction("Statements -> Statement", (symbolValues -> {
			List<Statement> statementList = new ArrayList<>();
			statementList.add((Statement) symbolValues[0]);
			return new Statements(statementList);
		}));
//		helper.defineProduction("Statements -> ε");
		helper.defineProduction("Statements -> Statements Statement", (symbolValues -> {
			Statements statements = (Statements) symbolValues[0];
			Statement statement = (Statement) symbolValues[1];
			statements.statements.add(statement);
			return statements;
		}));
		helper.defineProduction("Statement -> IfThenElseStatement", (symbolValues -> symbolValues[0]));
		helper.defineProduction("Statement -> DoWhileStatement", (symbolValues -> symbolValues[0]));
		helper.defineProduction("Statement -> PrintStatement", (symbolValues -> symbolValues[0]));
		helper.defineProduction("Statement -> DeclarationStatement", (symbolValues -> symbolValues[0]));
		helper.defineProduction("Statement -> AssignmentStatement", (symbolValues -> symbolValues[0]));
		helper.defineProduction("IfThenElseStatement -> IF Expression THEN LEFT_BRACE Statements RIGHT_BRACE MaybeElse", symbolValues -> {
			Expression condition = (Expression) symbolValues[1];
			Statements thenBranch = (Statements) symbolValues[4];
			Statements elseBranch = (Statements) symbolValues[6];
			return new IfThenElse(condition, thenBranch, elseBranch);
		});
		// manually eliminate ε
		helper.defineProduction("IfThenElseStatement -> IF Expression THEN LEFT_BRACE Statements RIGHT_BRACE", symbolValues -> {
			Expression condition = (Expression) symbolValues[1];
			Statements thenBranch = (Statements) symbolValues[4];
			return new IfThenElse(condition, thenBranch, null);
		});
		helper.defineProduction("IfThenElseStatement -> IF Expression THEN LEFT_BRACE RIGHT_BRACE MaybeElse", symbolValues -> {
			Expression condition = (Expression) symbolValues[1];
			Statements elseBranch = (Statements) symbolValues[5];
			return new IfThenElse(condition, new Statements(new ArrayList<>()), elseBranch);
		});
		helper.defineProduction("IfThenElseStatement -> IF Expression THEN LEFT_BRACE RIGHT_BRACE", symbolValues -> {
			Expression condition = (Expression) symbolValues[1];
			return new IfThenElse(condition, new Statements(new ArrayList<>()), null);
		});
//		helper.defineProduction("MaybeElse -> ε");
		helper.defineProduction("MaybeElse -> ELSE LEFT_BRACE RIGHT_BRACE", (symbolValues -> new Statements(new ArrayList<>())));
		helper.defineProduction("MaybeElse -> ELSE LEFT_BRACE Statements RIGHT_BRACE", (symbolValues -> symbolValues[2]));
		// manually eliminate ε
		helper.defineProduction("DoWhileStatement -> DO LEFT_BRACE RIGHT_BRACE WHILE LEFT_PAREN Expression RIGHT_PAREN SEMICOLON", symbolValues -> {
			Expression condition = (Expression) symbolValues[5];
			return new DoWhile(new Statements(new ArrayList<>()), condition);
		});
		helper.defineProduction("DoWhileStatement -> DO LEFT_BRACE Statements RIGHT_BRACE WHILE LEFT_PAREN Expression RIGHT_PAREN SEMICOLON", symbolValues -> {
			Expression condition = (Expression) symbolValues[6];
			Statements body = (Statements) symbolValues[2];
			return new DoWhile(body, condition);
		});
		helper.defineProduction("PrintStatement -> PRINT LEFT_PAREN Expression RIGHT_PAREN SEMICOLON", (symbolValues -> new Print((Expression) symbolValues[2])));
		helper.defineProduction("DeclarationStatement -> TypeSpecifier InitDeclarator SEMICOLON", (symbolValues -> {
			Token type = (Token) symbolValues[0];
			Entry<Token, Expression> decl = (Entry<Token, Expression>) symbolValues[1];
			return new Declaration(new Variable(type, decl.getKey()), decl.getValue());
		}));
		helper.defineProduction("TypeSpecifier -> INT", (symbolValues -> symbolValues[0]));
		helper.defineProduction("TypeSpecifier -> BOOL", (symbolValues -> symbolValues[0]));
		helper.defineProduction("InitDeclarator -> IDENTIFIER", (symbolValues -> new SimpleEntry<>((Token) symbolValues[0], null)));
		helper.defineProduction("InitDeclarator -> IDENTIFIER EQUAL Expression", (symbolValues -> new SimpleEntry<>((Token) symbolValues[0], (Expression) symbolValues[2])));
		helper.defineProduction("AssignmentStatement -> Identifier EQUAL Expression SEMICOLON", (symbolValues -> {
			Variable variable = (Variable) symbolValues[0];
			Expression expression = (Expression) symbolValues[2];
			return new Assignment(variable, expression);
		}));
		helper.defineProduction("Expression -> EqualityExpression", (symbolValues -> symbolValues[0]));
		helper.defineProduction("EqualityExpression -> RelationalExpression", (symbolValues -> symbolValues[0]));
		helper.defineProduction("EqualityExpression -> EqualityExpression EQUAL_EQUAL RelationalExpression", (symbolValues -> {
			Expression left = (Expression) symbolValues[0];
			Token op = (Token) symbolValues[1];
			Expression right = (Expression) symbolValues[2];
			return new BinaryExpression(left, op, right);
		}));
		helper.defineProduction("RelationalExpression -> AdditiveExpression", (symbolValues -> symbolValues[0]));
		helper.defineProduction("RelationalExpression -> RelationalExpression GREATER AdditiveExpression", (symbolValues -> {
			Expression left = (Expression) symbolValues[0];
			Token op = (Token) symbolValues[1];
			Expression right = (Expression) symbolValues[2];
			return new BinaryExpression(left, op, right);
		}));
		helper.defineProduction("RelationalExpression -> RelationalExpression GREATER_EQUAL AdditiveExpression", (symbolValues -> {
			Expression left = (Expression) symbolValues[0];
			Token op = (Token) symbolValues[1];
			Expression right = (Expression) symbolValues[2];
			return new BinaryExpression(left, op, right);
		}));
		helper.defineProduction("AdditiveExpression -> MultiplicativeExpression", (symbolValues -> symbolValues[0]));
		helper.defineProduction("AdditiveExpression -> AdditiveExpression PLUS MultiplicativeExpression", (symbolValues -> {
			Expression left = (Expression) symbolValues[0];
			Token op = (Token) symbolValues[1];
			Expression right = (Expression) symbolValues[2];
			return new BinaryExpression(left, op, right);
		}));
		helper.defineProduction("MultiplicativeExpression -> PrimaryExpression", (symbolValues -> symbolValues[0]));
		helper.defineProduction("MultiplicativeExpression -> MultiplicativeExpression STAR PrimaryExpression", (symbolValues -> {
			Expression left = (Expression) symbolValues[0];
			Token op = (Token) symbolValues[1];
			Expression right = (Expression) symbolValues[2];
			return new BinaryExpression(left, op, right);
		}));
		helper.defineProduction("PrimaryExpression -> Identifier", (symbolValues -> symbolValues[0]));
		helper.defineProduction("PrimaryExpression -> NUMBER", (symbolValues -> {
			Token num = (Token) symbolValues[0];
			return new Literal(num.getValue(), num.getLocation());
		}));
		helper.defineProduction("PrimaryExpression -> LEFT_PAREN Expression RIGHT_PAREN", (symbolValues -> new Grouping((Expression) symbolValues[0])));
		helper.defineProduction("Identifier -> IDENTIFIER", (symbolValues -> {
			Token id = (Token) symbolValues[0];
			return new Variable(new Token(TokenType.IDENTIFIER, Parser.magicKeyword, id.getLine(), id.getLine()), id);
		}));
//		helper.defineNonTerminal("E");
//		helper.defineNonTerminal("T");
//		helper.defineNonTerminal("F");
//		helper.defineTerminalFromTokenType(TokenType.IDENTIFIER,"id");
//		helper.defineTerminalFromTokenType(TokenType.PLUS,"+");
//		helper.defineTerminalFromTokenType(TokenType.STAR,"*");
//
//		helper.addProduction("E -> E + T");
//		helper.addProduction("E -> T");
//		helper.addProduction("T -> T * F");
//		helper.addProduction("T -> F");
//		helper.addProduction("F -> id");
//
		grammar = helper.getGrammar("Program").getAugmentedGrammar();
		grammar.calculateFirstAndFollow();
		table = new SLRParsingTable(grammar);
//		System.out.println(table);
	}
//	private Environment environment = new Environment();
	private Environment environment;
	@Override
	public Environment getEnvironment() {
//		return environment;
		return environment;
	}
	
	@Override
	public Statements parse() {
		try {
			Statements program = doParse();
			ContextChecker contextChecker = new ContextChecker(program);
			program = contextChecker.check();
			new TypeChecker().check(program);
			this.environment = contextChecker.getEnvironment();
			return program;
		} catch (CompileTimeError ignore) {
			return new Statements(new ArrayList<>());
		}
	}
	
	private final List<Token> tokenList;
	
	private Statements doParse() {
		Stack<Integer> states = new Stack<>();
		Stack<Symbol> symbols = new Stack<>();
		Stack<Token> input = new Stack<>();
		for (int i = tokenList.size() - 1; i >= 0; -- i) {
			input.add(tokenList.get(i));
		}
		
		states.add(table.initState);
		symbols.add(grammar.start);
		while (true) {
			if (states.isEmpty()) {
				throw Parser.error(input.peek(), "stack empty?");
			}
			if (input.empty()) {
				throw Parser.error(input.peek(), "input empty?");
			}
			Terminal terminal = new Terminal(input.peek());
			int state = states.peek();
			Action action = table.action.get(state).get(terminal);
			if (action == null) { // error
				throw Parser.error(input.peek(), String.format("unexpected '%s'\n", input.peek().getLexeme()));
			}
			if (action.actionType() == ActionType.SHIFT) {
				int j = action.number();
				states.add(j);
				symbols.add(terminal);
				input.pop();
//				System.out.printf("shift %s\n", terminal);
				continue;
			}
			if (action.actionType() == ActionType.REDUCE) {
				int j = action.number();
				Production production = grammar.productionList.get(j);
				Symbol[] symbolArr = new Symbol[production.right.size()];
				for (int k = production.right.size() - 1; k >= 0; -- k) {
					symbolArr[k] = symbols.pop();
					states.pop();
				}
				int t = states.peek();
				states.push(table.go.get(t).get(production.left));
				NonTerminal nonTerminal = production.reduce(symbolArr);
//				if (nonTerminal.object instanceof Statement statement) {
//					System.out.println((new TextBox()).print(statement));
//				}
				symbols.add(new NonTerminal(production.left.toString(), nonTerminal.object));
//				System.out.printf("reduce by %s\n", production);
				continue;
			}
			if (action.actionType() == ActionType.ACCEPT) {
//				System.out.println("accept!!!");
				return (Statements) symbols.peek().object;
			}
			throw new RuntimeException(String.format("?? state %s", action.actionType()));
		}
	}
	
	public BottomUpParser(List<Token> tokenList) {
		this.tokenList = new ArrayList<>();
		this.tokenList.addAll(tokenList);
		if (this.tokenList.get(this.tokenList.size() - 1).getType() != TokenType.EOF) {
			this.tokenList.add(Grammar.eof.token);
		}
	}
	
	public static void main(String[] args) {
		try {
			String filename = "input.upl";
			Lexer lexer = new Lexer(new InputStreamReader(new java.io.FileInputStream(filename)), false);
			
			List<Token> tokens = lexer.getTokens();
			
			BottomUpParser bottomUpParser = new BottomUpParser(tokens);
			
			bottomUpParser.doParse();
			//parser.debug_parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
