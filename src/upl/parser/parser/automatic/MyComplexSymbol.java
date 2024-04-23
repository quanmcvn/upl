package upl.parser.parser.automatic;

import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.Symbol;

public class MyComplexSymbol extends ComplexSymbolFactory.ComplexSymbol {
	public MyComplexSymbol(String name, int id) {
		super(name, id);
	}
	
	public MyComplexSymbol(String name, int id, Object value) {
		super(name, id, value);
	}
	
	public MyComplexSymbol(String name, int id, int state) {
		super(name, id, state);
	}
	
	public MyComplexSymbol(String name, int id, Symbol left, Symbol right) {
		super(name, id, left, right);
	}
	
	public MyComplexSymbol(String name, int id, ComplexSymbolFactory.Location left, ComplexSymbolFactory.Location right) {
		super(name, id, left, right);
	}
	
	public MyComplexSymbol(String name, int id, Symbol left, Symbol right, Object value) {
		super(name, id, left, right, value);
	}
	
	public MyComplexSymbol(String name, int id, Symbol left, Object value) {
		super(name, id, left, value);
	}
	
	public MyComplexSymbol(String name, int id, ComplexSymbolFactory.Location left, ComplexSymbolFactory.Location right, Object value) {
		super(name, id, left, right, value);
	}
	
	@Override
	public String toString() {
		return String.format("symbol: %s at %s:%d:%d", getName(), getLeft().getUnit(), getLeft().getLine(), getLeft().getColumn());
	}
}
