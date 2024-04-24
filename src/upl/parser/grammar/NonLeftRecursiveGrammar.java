package upl.parser.grammar;

import java.util.*;

/**
 * Its sole purpose is to calculate FIRST and FOLLOW,
 * everything else is destroyed right away
 */
public class NonLeftRecursiveGrammar {
	private final char prime = '_';
	private final List<NonTerminal> addedNonTerminal = new ArrayList<>();
	private final NonTerminal start;
	private final List<NonTerminal> nonTerminalList;
	private final List<Production> origin;
	private final List<Production> productionList;
	private final Map<Symbol, Set<Terminal>> ourFirst = new HashMap<>();
	private final Map<NonTerminal, Set<Terminal>> ourFollow = new HashMap<>();
	private void removeImmediateLeftRecursion(NonTerminal nonTerminal) {
		List<Production> toRemove = Grammar.allCurrentNonTerminalAiProduction(productionList, nonTerminal);
		boolean haveImmediateLeftRecursion = false;
		for (Production production : toRemove) {
			if (production.right.get(0).equals(nonTerminal)) {
				haveImmediateLeftRecursion = true;
				break;
			}
		}
		
		if (!haveImmediateLeftRecursion) return;
		
		NonTerminal newNonTerminal = new NonTerminal(nonTerminal.toString() + prime);
		addedNonTerminal.add(newNonTerminal);
		
		for (Production production : toRemove) {
			NonTerminal left;
			List<Symbol> newSymbols = new ArrayList<>();
			if (production.right.get(0).equals(nonTerminal)) {
				left = newNonTerminal;
				for (int i = 1; i < production.right.size(); ++ i) {
					newSymbols.add(production.right.get(i));
				}
			} else {
				left = nonTerminal;
				newSymbols.addAll(production.right);
			}
			newSymbols.add(newNonTerminal);
			productionList.add(new Production(left, newSymbols));
		}
		
		productionList.add(new Production(newNonTerminal, Grammar.epsilon));
		
		for (Production production : toRemove) {
			productionList.remove(production);
		}
	}
	
	private void clean(NonTerminal high, NonTerminal low) {
		// if high -> low ...
		// then must be replaced
		List<Production> aiProductions = Grammar.allCurrentNonTerminalAiProduction(productionList, high);
		List<Production> toRemove = new ArrayList<>();
		List<Production> toAdd = new ArrayList<>();
		for (Production production : aiProductions) {
			if (production.right.get(0).equals(low)) {
				toRemove.add(production);
				
				List<Production> ajProductions = Grammar.allCurrentNonTerminalAiProduction(productionList, low);
				
				for (Production ajProduction : ajProductions) {
					for (int i = 1; i < production.right.size(); ++ i) {
						ajProduction.right.add(production.right.get(i));
					}
					toAdd.add(new Production(high, ajProduction.right));
				}
			}
		}
		
		productionList.removeAll(toRemove);
		productionList.addAll(toAdd);
	}
	private void removeLeftRecursion() {
		int size = nonTerminalList.size();
		for (int i = 0; i < size; ++ i) {
			for (int j = 0; j < i; ++ j) {
				clean(nonTerminalList.get(i), nonTerminalList.get(j));
			}
			removeImmediateLeftRecursion(nonTerminalList.get(i));
		}
		
		for (Production production : productionList) {
			production.trim();
		}
		Map<NonTerminal, Integer> order = new TreeMap<>();
		for (int i = 0; i < nonTerminalList.size(); ++ i) {
			order.put(nonTerminalList.get(i), i * 2);
			order.put(new NonTerminal(String.format("%s" + prime, nonTerminalList.get(i))), i * 2 + 1);
		}
		productionList.sort(Comparator.comparingInt(production -> order.get(production.left)));
	}
	private void setupFirst(Symbol symbol) {
		if (!ourFirst.containsKey(symbol)) {
			ourFirst.put(symbol, new HashSet<>());
		}
	}
	private void setupFollow(NonTerminal nonTerminal) {
		if (!ourFollow.containsKey(nonTerminal)) {
			ourFollow.put(nonTerminal, new HashSet<>());
		}
	}
	private void addToFirst(Symbol symbol, Terminal terminal) {
		setupFirst(symbol);
		ourFirst.get(symbol).add(terminal);
	}
	private void addToFollow(NonTerminal nonTerminal, Terminal terminal) {
		setupFollow(nonTerminal);
		ourFollow.get(nonTerminal).add(terminal);
	}
	private void calculateFirst() {
		for (NonTerminal nonTerminal : nonTerminalList) {
			calculateFirstDFS(nonTerminal);
		}
		for (NonTerminal nonTerminal : addedNonTerminal) {
			calculateFirstDFS(nonTerminal);
		}
	}
	
	private void calculateFirstDFS(Symbol symbol) {
		if (ourFirst.containsKey(symbol)) {
			return;
		}
		
		if (symbol instanceof Terminal terminal) {
			addToFirst(symbol, terminal);
			// first rule 1
			return;
		}
		NonTerminal nonTerminal = (NonTerminal) symbol;
		List<Production> aiProductions = Grammar.allCurrentNonTerminalAiProduction(productionList, nonTerminal);
		
		for (Production production : aiProductions) {
			setupFirst(nonTerminal);
			ourFirst.get(nonTerminal).addAll(calculateFirst(production.right));
		}
	}
	
	private Set<Terminal> calculateFirst(List<Symbol> symbols) {
		Set<Terminal> res = new HashSet<>();
		boolean allEpsilon = true;
		for (Symbol symbol : symbols) {
			boolean hasEpsilon = false;
			calculateFirstDFS(symbol);
			for (Terminal terminal : ourFirst.get(symbol)) {
				// first rule 2
				if (!terminal.equals(Grammar.epsilon)) {
					res.add(terminal);
				} else {
					hasEpsilon = true;
				}
			}
			// first rule 2
			if (!hasEpsilon) {
				allEpsilon = false;
				break;
			}
		}
		// first rule 3
		if (allEpsilon) {
			res.add(Grammar.epsilon);
		}
		
		return res;
	}
	
	private void calculateFollow() {
		// follow seems to be calculated on normal production
		productionList.clear();
		productionList.addAll(origin);
		
		for (NonTerminal nonTerminal : nonTerminalList) {
			calculateFollowDFS(nonTerminal);
		}
		for (NonTerminal nonTerminal : addedNonTerminal) {
			calculateFollowDFS(nonTerminal);
		}
	}
	
	private void calculateFollowDFS(NonTerminal nonTerminal) {
		if (ourFollow.containsKey(nonTerminal)) {
			return;
		}
		// follow rule 1
		if (nonTerminal.equals(start)) {
			addToFollow(start, Grammar.eof);
		}
		
		for (Production production : productionList) {
			for (int i = 0; i < production.right.size(); ++ i) {
				if (!production.right.get(i).equals(nonTerminal)) {
					continue;
				}
				if (i + 1 < production.right.size()) {
					// follow rule 2
					List<Symbol> beta = new ArrayList<>();
					for (int j = i + 1; j < production.right.size(); ++ j) {
						beta.add(production.right.get(j));
					}
					Set<Terminal> firstBeta = calculateFirst(beta);
					for (Terminal terminal : firstBeta) {
						if (!terminal.equals(Grammar.epsilon)) {
							addToFollow(nonTerminal, terminal);
						}
					}
					// follow rule 3.2
					if (firstBeta.contains(Grammar.epsilon)) {
						if (!nonTerminal.equals(production.left)) {
							calculateFollowDFS(production.left);
							setupFollow(nonTerminal);
							ourFollow.get(nonTerminal).addAll(ourFollow.get(production.left));
						}
					}
				} else {
					// follow rule 3.1
					if (!nonTerminal.equals(production.left)) {
						calculateFollowDFS(production.left);
						setupFollow(nonTerminal);
						ourFollow.get(nonTerminal).addAll(ourFollow.get(production.left));
					}}
			}
		}
	}
	
	public void calculateFirstAndFollow(Map<NonTerminal, Set<Terminal>> theirFirst, Map<NonTerminal, Set<Terminal>> theirFollow) {
		removeLeftRecursion();
		
//		System.out.println("```\n");
//		for (Production production : productionList) {
//			System.out.println(production);
//		}
//		System.out.println("```\n");
		
		// calculate first and follow real
		calculateFirst();
		calculateFollow();
		
//		System.out.println("```\n");
//		System.out.println("FIRST:\n");
//		ourFirst.forEach(((symbol, terminals) -> {
//			System.out.printf("first(%s): ", symbol);
//			for (Terminal terminal : terminals) {
//				System.out.printf("%s ", terminal);
//			}
//			System.out.println();
//		}));
//		System.out.println();
//
//		System.out.println("FOLLOW:\n");
//		ourFollow.forEach(((nonTerminal, terminals) -> {
//			System.out.printf("follow(%s): ", nonTerminal);
//			for (Terminal terminal : terminals) {
//				System.out.printf("%s ", terminal);
//			}
//			System.out.println();
//		}));
//		System.out.println();
//		System.out.println("```\n");
		
		// adding non-terminal to real
		for (NonTerminal nonTerminal : nonTerminalList) {
			theirFirst.put(nonTerminal, ourFirst.get(nonTerminal));
			theirFollow.put(nonTerminal, ourFollow.get(nonTerminal));
		}
	}
	
	public NonLeftRecursiveGrammar(NonTerminal start, List<NonTerminal> nonTerminalList, List<Production> productionList) {
		this.start = start;
		this.nonTerminalList = new ArrayList<>();
		this.nonTerminalList.addAll(nonTerminalList);
		this.origin = new ArrayList<>();
		this.origin.addAll(productionList);
		this.productionList = new ArrayList<>();
		this.productionList.addAll(productionList);
	}
}
