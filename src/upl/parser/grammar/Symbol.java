package upl.parser.grammar;

public abstract class Symbol implements Comparable<Symbol> {
	public final String value;
	public Symbol(String value) {
		this.value = value;
	}
	@Override
	public int compareTo(Symbol symbol) {
		return value.compareTo(symbol.value);
	}
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Symbol x)) return false;
		return this.value.equals(x.value);
	}
	@Override
	public int hashCode() {
		return value.hashCode();
	}
	
	@Override
	public String toString() {
		return value;
	}
}
