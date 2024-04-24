package upl.lexer;

public record Location(int line, int column) {
	@Override
	public String toString() {
		return String.format("%d:%d", line, column);
	}
}
