package upl.parser.grammar;

import upl.lexer.Token;
import upl.lexer.TokenType;
import upl.parser.grammar.lr0.SLRParsingTable;

import java.util.*;

public class Grammar {
	public static List<Production> allCurrentNonTerminalAiProduction(List<Production> productionList, NonTerminal nonTerminal) {
		List<Production> res = new ArrayList<>();
		for (Production production : productionList) {
			if (production.left.equals(nonTerminal)) {
				List<Symbol> right = new ArrayList<>(production.right);
				res.add(new Production(production.left, right));
			}
		}
		return res;
	}
	/**
	 * Sort production based on order in nonTerminalList
	 */
	public static void sortProduction(List<Production> productionList, List<NonTerminal> nonTerminalList) {
		Map<NonTerminal, Integer> order = new TreeMap<>();
		for (int i = 0; i < nonTerminalList.size(); ++ i) {
			order.put(nonTerminalList.get(i), i);
		}
		productionList.sort(Comparator.comparingInt(production -> order.get(production.left)));
	}
	public static final Terminal epsilon = new Terminal("ε");
	public static final Terminal eof = new Terminal(new Token(TokenType.EOF, "$"));
	public static final String dot = "·";
	public final NonTerminal start;
	public final List<NonTerminal> nonTerminalList;
	public final List<Terminal> terminalList;
	public final List<Production> productionList;
	public final Map<NonTerminal, Set<Terminal>> first = new HashMap<>();
	public final Map<NonTerminal, Set<Terminal>> follow = new HashMap<>();
	public Grammar(NonTerminal start, List<NonTerminal> nonTerminalList, List<Terminal> terminalList, List<Production> productionList) {
		this.start = start;
		this.nonTerminalList = nonTerminalList;
		this.terminalList = terminalList;
		this.productionList = productionList;
		sortProduction(productionList, nonTerminalList);
	}
	@Override
	public String toString() {
		StringBuilder res = new StringBuilder();
		res.append(String.format("start: %s\n", start));
		res.append("terminals:");
		for (Terminal terminal : terminalList) res.append(String.format(" %s", terminal));
		res.append("\n");
		res.append("non-terminals:");
		for (NonTerminal nonTerminal : nonTerminalList) res.append(String.format(" %s", nonTerminal));
		res.append("\n");
		res.append("productions:\n");
		for (Production production : productionList) res.append(String.format("%s\n", production));
		
		res.append("FIRST:\n");
		first.forEach(((symbol, terminals) -> {
			res.append(String.format("first(%s): ", symbol));
			for (Terminal terminal : terminals) {
				res.append(String.format("%s ", terminal));
			}
			res.append("\n");
		}));
		
		res.append("FOLLOW:\n");
		follow.forEach(((nonTerminal, terminals) -> {
			res.append(String.format("follow(%s): ", nonTerminal));
			for (Terminal terminal : terminals) {
				res.append(String.format("%s ", terminal));
			}
			res.append("\n");
		}));
		res.append("\n");
		
		return res.toString();
	}
	
	public void calculateFirstAndFollow() {
		new NonLeftRecursiveGrammar(start, nonTerminalList, productionList).calculateFirstAndFollow(first, follow);
	}
	
	public Grammar getEpsilonFreeGrammar() {
		List<Production> newProductionList = new ArrayList<>();
		List<Terminal> newTerminalList = new ArrayList<>();
		for (Terminal terminal : terminalList) {
			if (!terminal.equals(Grammar.epsilon)) newTerminalList.add(terminal);
		}
		
		for (Production production : productionList) {
			newProductionList.add(production);
			
			if (!production.isEpsilonProduction()) {
				newProductionList.add(production);
			} else {
				NonTerminal left = production.left;
				for (Production production1 : productionList) {
					boolean hasLeftInRight = false;
					List<Symbol> right = new ArrayList<>();
					for (Symbol symbol : production1.right) {
						if (symbol instanceof NonTerminal nonTerminal) {
							if (left.equals(nonTerminal)) {
								hasLeftInRight = true;
								continue;
							}
						}
						right.add(symbol);
					}
					if (hasLeftInRight) {
						Production newProduction = new Production(production1.left, right);
						System.out.printf("new production: %s\n", newProduction);
						newProductionList.add(newProduction);
					}
				}
			}
		}
		
		return new Grammar(start, nonTerminalList, newTerminalList, newProductionList);
	}
	
	public Grammar getAugmentedGrammar() {
		NonTerminal newStart = new NonTerminal(String.format("%s'", start));
		
		List<NonTerminal> newNonTerminalList = new ArrayList<>();
		newNonTerminalList.add(newStart);
		newNonTerminalList.addAll(nonTerminalList);
		
		List<Terminal> newTerminalList = new ArrayList<>(terminalList);
		
		List<Production> newProductionList = new ArrayList<>();
		newProductionList.add(new Production(newStart, start));
		newProductionList.addAll(productionList);
		
		return new Grammar(newStart, newNonTerminalList, newTerminalList, newProductionList);
	}
	
	public static void main(String[] args) {
		GrammarBuilderHelper helper = new GrammarBuilderHelper();
//		1 example
//		helper.defineNonTerminal("S");
//		helper.defineNonTerminal("A");
//		helper.defineTerminal("a");
//		helper.defineTerminal("b");
//		helper.defineTerminal("c");
//		helper.defineTerminal("d");
//		helper.defineTerminal("ε");
//		helper.addProduction("S -> A a");
//		helper.addProduction("S -> b");
//		helper.addProduction("A -> A c");
//		helper.addProduction("A -> S d");
//		helper.addProduction("A -> ε");
//		Grammar grammar = helper.getGrammar("S");
		
		helper.defineNonTerminal("E");
		helper.defineNonTerminal("T");
		helper.defineNonTerminal("F");
		helper.defineTerminal("id");
		helper.defineTerminal("+");
		helper.defineTerminal("*");
		helper.defineTerminal("(");
		helper.defineTerminal(")");
		
		helper.defineProduction("E -> E + T");
		helper.defineProduction("E -> T");
		helper.defineProduction("T -> T * F");
		helper.defineProduction("T -> F");
		helper.defineProduction("F -> ( E )");
		helper.defineProduction("F -> id");
		
		Grammar grammar = helper.getGrammar("E");
		
		Grammar aug = grammar.getAugmentedGrammar();
		
		aug.calculateFirstAndFollow();
		
		System.out.println("```\n");
		System.out.println(aug);
		System.out.println("```\n");
		
		SLRParsingTable table = new SLRParsingTable(aug);
		
		System.out.println(table);
	}
}
