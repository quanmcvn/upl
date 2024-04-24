package upl.parser.grammar.lr0;

import upl.parser.grammar.Grammar;
import upl.parser.grammar.NonTerminal;
import upl.parser.grammar.Symbol;
import upl.parser.grammar.Terminal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SLRParsingTable {
	private final Grammar augmentedGrammar;
	private final CanonicalCollection c;
	public final List<Map<Terminal, Action>> action;
	public final List<Map<NonTerminal, Integer>> go;
	
	public final int initState;
	
	/**
	 * @param augmentedGrammar an augmented grammar
	 */
	public SLRParsingTable(Grammar augmentedGrammar) {
		this.augmentedGrammar = augmentedGrammar;
		// rule 1
		this.c = new CanonicalCollection(this.augmentedGrammar);
		int n = c.itemSetList.size();
		
		// rule 5
		initState = c.itemSetList.stream()
				.filter(itemSet -> itemSet.items.stream().
								anyMatch(item -> item.production.left.equals(augmentedGrammar.start)))
				.map(c.itemSetList::indexOf)
				.findFirst().orElse(-1);
		
		if (initState == -1) {
			throw new RuntimeException(String.format("??? init is %d\n", initState));
		}
		action = new ArrayList<>(n);
		go = new ArrayList<>(n);
		for (int i = 0; i < n; ++ i) {
			action.add(new TreeMap<>());
			go.add(new TreeMap<>());
		}
		constructTable();
	}
	
	private void constructTable() {
		int n = c.itemSetList.size();
		
		for (int i = 0; i < n; ++ i) {
			ItemSet itemSet = c.itemSetList.get(i);
			for (Item item : itemSet.items) {
				if (!item.isDotAtEnd()) {
					Symbol symbol = item.getSymbolAfterDot();
					if (symbol instanceof Terminal terminal) {
						// rule 2.a
						int j = c.itemSetList.indexOf(itemSet.go(terminal));
						action.get(i).put(terminal, new Action(ActionType.SHIFT, j));
					} else {
						// rule 3
						NonTerminal nonTerminal = (NonTerminal) symbol;
						int j = c.itemSetList.indexOf(itemSet.go(nonTerminal));
						go.get(i).put(nonTerminal, j);
					}
				} else {
					if (!item.production.left.equals(augmentedGrammar.start)) {
						// rule 2.b
						int j = augmentedGrammar.productionList.indexOf(item.production);
						Action act = new Action(ActionType.REDUCE, j);
						for (Terminal terminal : augmentedGrammar.follow.get(item.production.left)) {
							if (action.get(i).containsKey(terminal)) {
								Action act2 = action.get(i).get(terminal);
								if (act2.actionType() == ActionType.SHIFT) {
									System.err.printf("shift/reduce conflict in production %s on terminal %s\n", augmentedGrammar.productionList.get(j), terminal);
								} else if (act2.actionType() == ActionType.REDUCE) {
									System.err.printf("reduce/reduce conflict in production %s on terminal %s\n", augmentedGrammar.productionList.get(j), terminal);
								}
							}
							action.get(i).put(terminal, act);
						}
					} else {
						// rule 2.c
						action.get(i).put(Grammar.eof, new Action(ActionType.ACCEPT, 0));
					}
				}
			}
		}
	}
	
	@Override
	public String toString() {
		return toTypstTable();
	}
	private String typstContent(String content) {
		content = content.replaceAll("\\*", "\\\\*");
		return "[" + content + "],";
	}
	private String toTypstTable() {
		StringBuilder res = new StringBuilder();
		
		res.append("```\n").append(c.toString()).append("```\n");
		
		res.append("#table(\n");
		res.append("\talign: center,\n");
		int terminalSize = augmentedGrammar.terminalList.size();
		int nonTerminalSize = augmentedGrammar.nonTerminalList.size() - 1;
		int columns = 1 + terminalSize + nonTerminalSize;
		res.append("\tcolumns: (").append("1.5cm,".repeat(columns)).append("),\n");
		res.append("table.cell(colspan: 1, rowspan: 2, [*State*]),\n");
		res.append(String.format("table.cell(colspan: %d, rowspan: 1, [*Action*]),\n", terminalSize));
		res.append(String.format("table.cell(colspan: %d, rowspan: 1, [*Goto*]),\n", nonTerminalSize));
		for (Terminal terminal : augmentedGrammar.terminalList) {
			res.append(typstContent(terminal.toString()));
		}
		for (NonTerminal nonTerminal : augmentedGrammar.nonTerminalList) {
			if (nonTerminal.equals(augmentedGrammar.start)) continue;
			res.append(typstContent(nonTerminal.toString()));
		}
		res.append("\n");
		int n = c.itemSetList.size();
		for (int i = 0; i < n; ++ i) {
			res.append(typstContent(String.valueOf(i)));
			for (Terminal terminal : augmentedGrammar.terminalList) {
				if (action.get(i).containsKey(terminal)) {
					Action act = action.get(i).get(terminal);
					res.append(typstContent(act.toString()));
				} else {
					res.append(typstContent(""));
				}
			}
			for (NonTerminal nonTerminal : augmentedGrammar.nonTerminalList) {
				if (nonTerminal.equals(augmentedGrammar.start)) continue;
				if (go.get(i).containsKey(nonTerminal)) {
					int number = go.get(i).get(nonTerminal);
					res.append(typstContent(String.valueOf(number)));
				} else {
					res.append(typstContent(""));
				}
			}
			res.append("\n");
		}
		res.append(")\n");
		return res.toString();
	}
}
