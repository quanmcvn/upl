package upl.parser.parser.automatic;

import java_cup.runtime.ComplexSymbolFactory;

public class MyLocation extends ComplexSymbolFactory.Location {
	
	public MyLocation(ComplexSymbolFactory.Location other) {
		super(other);
	}
	
	public MyLocation(String unit, int line, int column, int offset) {
		super(unit, line, column, offset);
	}
	
	public MyLocation(String unit, int line, int column) {
		super(unit, line, column);
	}
	
	public MyLocation(int line, int column, int offset) {
		super(line, column, offset);
	}
	
	public MyLocation(int line, int column) {
		super(line, column);
	}
	
	@Override
	public String toString() {
		return String.format("%s:%d:%d", getUnit(), getLine(), getColumn());
	}
}
