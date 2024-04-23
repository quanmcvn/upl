package upl.parser.parser.automatic;

import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.ComplexSymbolFactory.Location;
import java_cup.runtime.Symbol;
import java_cup.runtime.SymbolFactory;

public class MyComplexSymbolFactory implements SymbolFactory {
	public Symbol newSymbol(String name, int id, Location left, Location right, Object value) {
		return new MyComplexSymbol(name, id, left, right, value);
	}
	
	public Symbol newSymbol(String name, int id, ComplexSymbolFactory.Location left, Location right) {
		return new MyComplexSymbol(name, id, left, right);
	}
	
	public Symbol newSymbol(String name, int id, Symbol left, Object value) {
		return new MyComplexSymbol(name, id, left, value);
	}
	
	public Symbol newSymbol(String name, int id, Symbol left, Symbol right, Object value) {
		return new MyComplexSymbol(name, id, left, right, value);
	}
	
	public Symbol newSymbol(String name, int id, Symbol left, Symbol right) {
		return new MyComplexSymbol(name, id, left, right);
	}
	
	public Symbol newSymbol(String name, int id) {
		return new MyComplexSymbol(name, id);
	}
	
	public Symbol newSymbol(String name, int id, Object value) {
		return new MyComplexSymbol(name, id, value);
	}
	
	public Symbol startSymbol(String name, int id, int state) {
		return new MyComplexSymbol(name, id, state);
	}
	
}
