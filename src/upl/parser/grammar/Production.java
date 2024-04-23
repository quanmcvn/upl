package upl.parser.grammar;

import upl.Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Production implements Comparable<Production> {
	public final NonTerminal left;
	public final List<Symbol> right;
	public Production(NonTerminal left, List<Symbol> right) {
		this.left = left;
		this.right = right;
	}
	public Production(NonTerminal left, Symbol... symbols) {
		this.left = left;
		this.right = new ArrayList<>();
		right.addAll(Arrays.asList(symbols));
	}
	@Override
	public int compareTo(Production production) {
		if (!left.equals(production.left)) {
			return left.compareTo(production.left);
		}
		
		return Main.compareList(right, production.right);
	}
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(left).append(" ->");
		for (Symbol symbol : right) {
			stringBuilder.append(' ').append(symbol);
		}
		return stringBuilder.toString();
	}
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Production production)) return false;
		
		if (!this.left.equals(production.left)) return false;
		if (this.right.size() != production.right.size()) {
			return false;
		}
		
		for (int i = 0; i < this.right.size(); i++) {
			if (!this.right.get(i).equals(production.right.get(i))) {
				return false;
			}
		}
		
		return true;
	}
	/**
	 *  Remove ε, if necessary
	 */
	public void trim() {
		right.removeIf((symbol -> symbol.equals(Grammar.epsilon)));
		if (right.isEmpty()) {
			right.add(Grammar.epsilon);
		}
	}
	
}
